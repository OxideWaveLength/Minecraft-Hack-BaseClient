#!/usr/bin/env perl
#
# git-shift - shifts timestamps of commits after the fact
#
# Copyright (c) 2010-2015 Akinori MUSHA
#
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in the
#    documentation and/or other materials provided with the distribution.
#
# THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
# OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
# HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
# OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
#

use strict;
use warnings;
use Getopt::Long;
use IO::Select;
use IPC::Open3;
use POSIX qw(tzset tzname);
use Symbol 'gensym';
use Time::Local;
use Time::Piece 1.16;	# timezone support is required
use Time::Seconds;

my($opt_v, $opt_n, $opt_k);
my $exit_status = 0;

sub usage {
    print STDERR <<'EOS';
usage: git shift [options] {[<time>][<timediff>][timezone]|<datetime>} <rev-list>...

    -v, --verbose         be verbose
    -n, --dry-run         dry run
    -k                    skip errors

    <timediff>            add this time span to current commit time(s)
                          in regexp: /^[-+]([0-9]+[wdhms])+$/
                          e.g.
                            +1d -12h 30m -1h30m -600s

    <time>                substitute this value for the time part(s) of
                          current commit time(s)
                          one of:
                            - "HH:MM:SS"
                            - "HH:MM" (= "HH:MM:00")

    <timezone>            specify the time zone offset
                          in regexp: /^[-+][01][0-9][0-5][0-9]$/

    <datetime>            set this date time as commit time(s)
                          one of:
                            - ISO-8601 date time string
                            - RFC 2822 date time format
                            - date(1) format in C locale
                            - Git's pretty format
                            - number of seconds since the Unix epoch

    <rev-list>            speficy commits to modify which must be on the
                          current branch; a single commit or a range of
                          commits: <committish1>..<committish2> denotes a
                          *closed* range, and <committish1>...<committish2>
                          denotes a *left-open* range
EOS
}

sub main {
    Getopt::Long::Configure qw(no_getopt_compat require_order pass_through);
    GetOptions("h|help"    => sub { usage(); exit },
               "v|verbose" => \$opt_v,
               "n|dry-run" => \$opt_n,
               "k"         => \$opt_k) or exit 64;
    pop @ARGV if @ARGV && $ARGV[$#ARGV] eq '--';
    $opt_v = 1  if $opt_n;

    if (!@ARGV) {
        usage();
        exit 129;
    }

    my($timespec, @revspec) = @ARGV;
    my $function = parse_timespec($timespec);

    if (!defined($function)) {
        print STDERR "$0: error in time spec.\n";
        usage();
        exit 129;
    }

    git_shift($function, @revspec);

    exit $exit_status;
}

sub may_exit {
    my($status) = @_ ? @_ : ($? >> 8);
    exit $status unless $opt_k;
    $exit_status = $status;
}

sub git_shift {
    my($function, @revspec) = @_;

    my %revs = map { ($_ => 0) } map { expand_revspec($_) } @revspec;

    if (!%revs) {
        print STDERR "$0: no revision specified\n";
        exit 64;
    }

    my(%rev_info, @all_revs, $start, $found);

    my $git = open3(my $in, my $out, '>&2',
                    'git', 'log', '--reverse',
                    '--date=raw', '--pretty=format:%H %ad %cd') or die $!;
    for my $line (<$out>) {
        my($rev, @rev_info) = split(' ', $line);
        if (exists($revs{$rev})) {
            $revs{$rev}++;
            $found = 1;
        }
        if ($found) {
            push @all_revs, $rev;
            $rev_info{$rev} = \@rev_info;
        }
    }
    waitpid $git, 0;
    exit($? >> 8) if $?;

    for my $rev (keys(%revs)) {
        if (!$revs{$rev}) {
            print STDERR "$0: can only modify commits on the current branch.\n";
            exit 1;
        }
    }

    my (%commit_map, $last_rev);

    for my $rev (@all_revs) {
        my($atime, $atz, $ctime, $ctz) = @{$rev_info{$rev}};
        my $changed;
        if (exists($revs{$rev})) {
            my($ntime, $ntz) = $function->($atime, $atz);

            my($atimestr, $ctimestr, $ntimestr) = map {
                stringify_git_time(@$_);
            } ([$atime, $atz], [$ctime, $ctz], [$ntime, $ntz]);

            print "AuthorDate: $atimestr\n";
            print "CommitDate: $ctimestr\n";
            print "         => $ntimestr\n";

            $atime = $ctime = $ntime;
            $atz   = $ctz   = $ntz;
            $changed = 1;
        }

        $last_rev = git_rewrite_commit($rev, $atime, $atz, $ctime, $ctz, \%commit_map);
    }

    unless ($opt_n) {
        system 'git', 'reset', '--soft', $last_rev;
        exit($? >> 8) if $?;
    }
}

sub git_revlist {
    my $git = open3(my $in, my $out, '>&2',
                    'git', 'rev-list', @_) or die $!;
    my @out = <$out>;
    waitpid $git, 0;
    exit($? >> 8) if $?;
    chomp(@out);
    @out;
}

sub git_revparse {
    my($revspec) = @_;
    my $git = open3(my $in, my $out, '>&2',
                    'git', 'rev-parse', $revspec) or die $!;
    my($rev, @rest) = <$out>;
    waitpid $git, 0;
    exit($? >> 8) if $?;
    if (@rest || $rev !~ /^[0-9a-f]{40}$/) {
        print STDERR "$0: $revspec does not specify a valid commit.\n";
        exit 64;
    }
    chomp($rev);
    $rev;
}

sub git_rewrite_commit {
    my($rev, $atime, $atz, $ctime, $ctz, $commit_map) = @_;

    return if $opt_n;

    my $git_log = open3(my $in1, my $out1, '>&2',
                    'git', 'log', $rev . '^!', '--pretty=format:%P%x00%an%x00%ae%x00%cn%x00%ce%x00%B') or die $!;
    my $log_output = do { local $/; <$out1> };
    waitpid $git_log, 0;
    exit($? >> 8) if $?;

    my ($parents, $author_name, $author_email, $committer_name, $committer_email, $message) = split(/\000/, $log_output);

    my @parent_revs = split(' ', $parents);
    my @parent_opts;
    for (@parent_revs) {
        push @parent_opts, '-p';
        if (defined (my $new_parent = $commit_map->{$_})) {
            push @parent_opts, $new_parent;
        } else {
            push @parent_opts, $_;
        }
    }

    local $ENV{GIT_COMMITTER_DATE} = "$ctime $ctz";
    local $ENV{GIT_AUTHOR_DATE} = "$atime $atz";
    local $ENV{GIT_COMMITTER_NAME} = $committer_name;
    local $ENV{GIT_AUTHOR_NAME} = $author_name;
    local $ENV{GIT_COMMITTER_EMAIL} = $committer_email;
    local $ENV{GIT_AUTHOR_EMAIL} = $author_email;
    my $git_commit_tree = open3(my $in2, my $out2, '>&2',
                    'git', 'commit-tree', @parent_opts, '-m', $message, $rev . '^{tree}') or die $!;
    my $new_rev = do { local $/; <$out2> };
    chomp $new_rev;
    waitpid $git_commit_tree, 0;
    exit($? >> 8) if $?;

    $commit_map->{$rev} = $new_rev;

    return $new_rev;
}

sub expand_revspec {
    my($revspec, $branch) = @_;
    @_ == 1 or die;

    if ($revspec =~ /\.\.(\.)?/) {
        my($from_spec, $to_spec, $exclude_from) = ($`, $', $1);
        if ($to_spec eq '') {
            $to_spec = 'HEAD';
        }
        my @revs = git_revlist($from_spec eq '' ?
                                   $to_spec :
                                   sprintf('%s..%s', $from_spec, $to_spec));
        push @revs, git_revparse($from_spec) unless $from_spec eq '' || $exclude_from;
        return @revs;
    }

    my @revs = (git_revparse($revspec));

    return @revs;
}

my $re_epoch	= qr/(?:0|[1-9][0-9]*)/;
my $re_tz	= qr/[-+][01][0-9][0-5][0-9]/;
my $re_span	= qr/[-+](?:[0-9]+[wdhms])+/;

sub parse_timespec {
    my($timespec) = @_;
    if ($timespec =~ /\A($re_epoch)($re_tz)?\z/o) {
        my($ntime, $ntz) = ($1, $2);
        return sub {
            my($otime, $otz) = @_;
            ($ntime, defined($ntz) ? $ntz : $otz);
        };
    } elsif ($timespec =~ /\A(?=.)(([0-9]{2}):([0-9]{2})(?::([0-9]{2}))?)?($re_span)?($re_tz)?\z/o) {
        my($hhmmss, $hh, $mm, $ss, $span, $ntz) = ($1, $2, $3, $4, $5, $6);
        return sub {
            my($otime, $otz) = @_;
            if (defined($span) && defined(my $seconds = parse_timediff($span))) {
                $otime += $seconds;
            }
            if (defined($hhmmss)) {
                $otime -= ($otime + parse_tz($otz)) % ONE_DAY;
                $otime += $hh * ONE_HOUR + $mm * ONE_MINUTE + ($ss || 0);
            }
            ($otime, defined($ntz) ? $ntz : $otz);
        };
    } else {
        my($time, $tzoffset) = eval {
            parse_date($timespec);
        };
        return sub {
            ($time->epoch, stringify_tzoffset($tzoffset));
        } unless $@;
    }

    return undef;
}

sub parse_date {
    my($datetime) = @_;
    my($time, $tzoffset);

    if ($datetime =~ /\A([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2})(Z|[-+][0-9]{4})?\z/){
        my($date, $tzsuffix) = ($1, $2);
        eval {
            if (!defined($tzsuffix)) {
                $time = localtime(Time::Piece->strptime($date, '%Y-%m-%dT%T'));
                $tzoffset = $time->tzoffset;
            } elsif ($tzsuffix eq 'Z') {
                $time = Time::Piece->strptime($date, '%Y-%m-%dT%T');
                $tzoffset = 0;
            } else {
                $time = Time::Piece->strptime($datetime, '%Y-%m-%dT%T%z');
                $tzoffset = parse_tz($tzsuffix);
            }
        };
        return ($time, $tzoffset) unless $@;
    };

    if ($datetime =~ qr{
      \A(?:
        # date(1) format in C locale
        (?<w>[A-Z][a-z]+) \s+ (?<m>[A-Z][a-z]+) \s+ (?<d>[0-9]{1,2}) \s+
        (?<T>[0-9]{2}:[0-9]{2}:[0-9]{2}) \s+
        (?:UTC|GMT|(?<z>[A-Z]+)) \s+
        (?<Y>[0-9]{4,})
        |
        # RFC 2822
        (?<w>[A-Z][a-z]+) , \s+ (?<d>[0-9]{1,2}) \s+ (?<m>[A-Z][a-z]+) \s+
        (?<Y>[0-9]{2,}) \s+
        (?<T>[0-9]{2}:[0-9]{2}:[0-9]{2}) \s+
        (?:UTC|GMT|(?<z>[A-Z]+|[-+][0-9]{2}:?[0-9]{2}))
        |
        # Git's pretty format
        (?<w>[A-Z][a-z]+) \s+ (?<m>[A-Z][a-z]+) \s+ (?<d>[0-9]{1,2}) \s+
        (?<T>[0-9]{2}:[0-9]{2}:[0-9]{2}) \s+
        (?<Y>[0-9]{4,}) \s+
        (?:UTC|GMT|(?<z>[-+][0-9]{4}))
      )\z
    }x) {
        my($year, $mon, $day, $wday, $time, $tzname) = @+{qw(Y m d w T z)};
        eval {
            $time = Time::Piece->strptime("$wday $mon $day $time $year", '%a %b %d %T %Y');
            if (defined($tzname)) {
                if ($tzname =~ /\A[-+]/) {
                    $tzname =~ s/://;
                    $time = Time::Piece->strptime($time->strftime('%Y-%m-%dT%T') . $tzname, '%Y-%m-%dT%T%z');
                    $tzoffset = parse_tz($tzname);
                } elsif (grep { $_ eq $tzname } do { POSIX::tzset(); POSIX::tzname() }) {
                    $time = localtime(
                        timelocal($time->sec, $time->min, $time->hour,
                                  $time->mday, $time->_mon, $time->year)
                    );
                    $tzoffset = $time->tzoffset;
                } else {
                    die 'unrecognized time zone name';
                }
            } else {
                $tzoffset = 0;
            }
        };
        return ($time, $tzoffset) unless $@;
    }

    die 'unrecognized date format';
}


sub parse_timediff {
    my($spec) = @_;
    my $value = 0;
    my $sign = ($spec =~ s/\A([-+])//) && $1;
    until ($spec eq '') {
        if ($spec =~ s/\A([0-9]+)([wdhms])//) {
            my $seconds = $1 * (
                $2 eq 'w' ? ONE_WEEK :
                $2 eq 'd' ? ONE_DAY :
                $2 eq 'h' ? ONE_HOUR :
                $2 eq 'm' ? ONE_MINUTE :
                1
            );
            if ($sign eq '-') {
                $value -= $seconds;
            } else {
                $value += $seconds;
            }
        } else {
            return undef;
        }
    }
    $value;
}

sub parse_tz {
    my($tz) = @_;
    my $t = gmtime();
    my $u = Time::Piece->strptime(
        $t->strftime("%Y-%m-%dT%H:%M:%S$tz"),
        '%Y-%m-%dT%H:%M:%S%z'
    );
    $t - $u;
}

sub stringify_tzoffset {
    my($tzo) = @_;
    my $min = $tzo % ONE_HOUR;
    my $hour = ($tzo - $min) / ONE_HOUR;
    sprintf('%+03d%02d', $hour, $min);
}

sub stringify_git_time {
    my($time, $tz) = @_;
    my $t = gmtime($time) + parse_tz($tz);
    $t->strftime("%a %b %d %T %Y") . " $tz";
}

main();