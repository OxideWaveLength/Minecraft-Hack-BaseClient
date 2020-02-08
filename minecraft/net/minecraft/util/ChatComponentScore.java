package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
	private final String name;
	private final String objective;

	/** The value displayed instead of the real score (may be null) */
	private String value = "";

	public ChatComponentScore(String nameIn, String objectiveIn) {
		this.name = nameIn;
		this.objective = objectiveIn;
	}

	public String getName() {
		return this.name;
	}

	public String getObjective() {
		return this.objective;
	}

	/**
	 * Sets the value displayed instead of the real score.
	 */
	public void setValue(String valueIn) {
		this.value = valueIn;
	}

	/**
	 * Gets the text of this component, without any special formatting codes added,
	 * for chat. TODO: why is this two different methods?
	 */
	public String getUnformattedTextForChat() {
		MinecraftServer minecraftserver = MinecraftServer.getServer();

		if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
			Scoreboard scoreboard = minecraftserver.worldServerForDimension(0).getScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);

			if (scoreboard.entityHasObjective(this.name, scoreobjective)) {
				Score score = scoreboard.getValueFromObjective(this.name, scoreobjective);
				this.setValue(String.format("%d", new Object[] { Integer.valueOf(score.getScorePoints()) }));
			} else {
				this.value = "";
			}
		}

		return this.value;
	}

	/**
	 * Creates a copy of this component. Almost a deep copy, except the style is
	 * shallow-copied.
	 */
	public ChatComponentScore createCopy() {
		ChatComponentScore chatcomponentscore = new ChatComponentScore(this.name, this.objective);
		chatcomponentscore.setValue(this.value);
		chatcomponentscore.setChatStyle(this.getChatStyle().createShallowCopy());

		for (IChatComponent ichatcomponent : this.getSiblings()) {
			chatcomponentscore.appendSibling(ichatcomponent.createCopy());
		}

		return chatcomponentscore;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof ChatComponentScore)) {
			return false;
		} else {
			ChatComponentScore chatcomponentscore = (ChatComponentScore) p_equals_1_;
			return this.name.equals(chatcomponentscore.name) && this.objective.equals(chatcomponentscore.objective) && super.equals(p_equals_1_);
		}
	}

	public String toString() {
		return "ScoreComponent{name=\'" + this.name + '\'' + "objective=\'" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
	}
}
