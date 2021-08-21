package net.minecraft.scoreboard;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria {
	private final String goalName;

	public GoalColor(String p_i45549_1_, EnumChatFormatting p_i45549_2_) {
		this.goalName = p_i45549_1_ + p_i45549_2_.getFriendlyName();
		IScoreObjectiveCriteria.INSTANCES.put(this.goalName, this);
	}

	public String getName() {
		return this.goalName;
	}

	public int func_96635_a(List<EntityPlayer> p_96635_1_) {
		return 0;
	}

	public boolean isReadOnly() {
		return false;
	}

	public IScoreObjectiveCriteria.EnumRenderType getRenderType() {
		return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
	}
}
