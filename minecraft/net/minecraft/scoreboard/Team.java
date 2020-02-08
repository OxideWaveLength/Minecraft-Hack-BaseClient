package net.minecraft.scoreboard;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public abstract class Team {
	/**
	 * Same as ==
	 */
	public boolean isSameTeam(Team other) {
		return other == null ? false : this == other;
	}

	/**
	 * Retrieve the name by which this team is registered in the scoreboard
	 */
	public abstract String getRegisteredName();

	public abstract String formatString(String input);

	public abstract boolean getSeeFriendlyInvisiblesEnabled();

	public abstract boolean getAllowFriendlyFire();

	public abstract Team.EnumVisible getNameTagVisibility();

	public abstract Collection<String> getMembershipCollection();

	public abstract Team.EnumVisible getDeathMessageVisibility();

	public static enum EnumVisible {
		ALWAYS("always", 0), NEVER("never", 1), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2), HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

		private static Map<String, Team.EnumVisible> field_178828_g = Maps.<String, Team.EnumVisible>newHashMap();
		public final String field_178830_e;
		public final int field_178827_f;

		public static String[] func_178825_a() {
			return (String[]) field_178828_g.keySet().toArray(new String[field_178828_g.size()]);
		}

		public static Team.EnumVisible func_178824_a(String p_178824_0_) {
			return (Team.EnumVisible) field_178828_g.get(p_178824_0_);
		}

		private EnumVisible(String p_i45550_3_, int p_i45550_4_) {
			this.field_178830_e = p_i45550_3_;
			this.field_178827_f = p_i45550_4_;
		}

		static {
			for (Team.EnumVisible team$enumvisible : values()) {
				field_178828_g.put(team$enumvisible.field_178830_e, team$enumvisible);
			}
		}
	}
}
