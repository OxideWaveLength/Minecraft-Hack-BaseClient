package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.EnumChatFormatting;

public class ScorePlayerTeam extends Team
{
    private final Scoreboard theScoreboard;
    private final String registeredName;
    private final Set<String> membershipSet = Sets.<String>newHashSet();
    private String teamNameSPT;
    private String namePrefixSPT = "";
    private String colorSuffix = "";
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = true;
    private Team.EnumVisible nameTagVisibility = Team.EnumVisible.ALWAYS;
    private Team.EnumVisible deathMessageVisibility = Team.EnumVisible.ALWAYS;
    private EnumChatFormatting chatFormat = EnumChatFormatting.RESET;

    public ScorePlayerTeam(Scoreboard theScoreboardIn, String name)
    {
        this.theScoreboard = theScoreboardIn;
        this.registeredName = name;
        this.teamNameSPT = name;
    }

    /**
     * Retrieve the name by which this team is registered in the scoreboard
     */
    public String getRegisteredName()
    {
        return this.registeredName;
    }

    public String getTeamName()
    {
        return this.teamNameSPT;
    }

    public void setTeamName(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else
        {
            this.teamNameSPT = name;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    public Collection<String> getMembershipCollection()
    {
        return this.membershipSet;
    }

    /**
     * Returns the color prefix for the player's team name
     */
    public String getColorPrefix()
    {
        return this.namePrefixSPT;
    }

    public void setNamePrefix(String prefix)
    {
        if (prefix == null)
        {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        else
        {
            this.namePrefixSPT = prefix;
            this.theScoreboard.sendTeamUpdate(this);
        }
    }

    /**
     * Returns the color suffix for the player's team name
     */
    public String getColorSuffix()
    {
        return this.colorSuffix;
    }

    public void setNameSuffix(String suffix)
    {
        this.colorSuffix = suffix;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public String formatString(String input)
    {
        return this.getColorPrefix() + input + this.getColorSuffix();
    }

    /**
     * Returns the player name including the color prefixes and suffixes
     */
    public static String formatPlayerName(Team p_96667_0_, String p_96667_1_)
    {
        return p_96667_0_ == null ? p_96667_1_ : p_96667_0_.formatString(p_96667_1_);
    }

    public boolean getAllowFriendlyFire()
    {
        return this.allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean friendlyFire)
    {
        this.allowFriendlyFire = friendlyFire;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public boolean getSeeFriendlyInvisiblesEnabled()
    {
        return this.canSeeFriendlyInvisibles;
    }

    public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles)
    {
        this.canSeeFriendlyInvisibles = friendlyInvisibles;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public Team.EnumVisible getNameTagVisibility()
    {
        return this.nameTagVisibility;
    }

    public Team.EnumVisible getDeathMessageVisibility()
    {
        return this.deathMessageVisibility;
    }

    public void setNameTagVisibility(Team.EnumVisible p_178772_1_)
    {
        this.nameTagVisibility = p_178772_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public void setDeathMessageVisibility(Team.EnumVisible p_178773_1_)
    {
        this.deathMessageVisibility = p_178773_1_;
        this.theScoreboard.sendTeamUpdate(this);
    }

    public int func_98299_i()
    {
        int i = 0;

        if (this.getAllowFriendlyFire())
        {
            i |= 1;
        }

        if (this.getSeeFriendlyInvisiblesEnabled())
        {
            i |= 2;
        }

        return i;
    }

    public void func_98298_a(int p_98298_1_)
    {
        this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
        this.setSeeFriendlyInvisiblesEnabled((p_98298_1_ & 2) > 0);
    }

    public void setChatFormat(EnumChatFormatting p_178774_1_)
    {
        this.chatFormat = p_178774_1_;
    }

    public EnumChatFormatting getChatFormat()
    {
        return this.chatFormat;
    }
}
