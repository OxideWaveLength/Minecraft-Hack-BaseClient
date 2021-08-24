package net.minecraft.stats;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter {
	protected final Map<StatBase, TupleIntJsonSerializable> statsData = Maps.<StatBase, TupleIntJsonSerializable>newConcurrentMap();

	/**
	 * Returns true if the achievement has been unlocked.
	 */
	public boolean hasAchievementUnlocked(Achievement achievementIn) {
		return this.readStat(achievementIn) > 0;
	}

	/**
	 * Returns true if the parent has been unlocked, or there is no parent
	 */
	public boolean canUnlockAchievement(Achievement achievementIn) {
		return achievementIn.parentAchievement == null || this.hasAchievementUnlocked(achievementIn.parentAchievement);
	}

	public int func_150874_c(Achievement p_150874_1_) {
		if (this.hasAchievementUnlocked(p_150874_1_)) {
			return 0;
		} else {
			int i = 0;

			for (Achievement achievement = p_150874_1_.parentAchievement; achievement != null && !this.hasAchievementUnlocked(achievement); ++i) {
				achievement = achievement.parentAchievement;
			}

			return i;
		}
	}

	public void increaseStat(EntityPlayer player, StatBase stat, int amount) {
		if (!stat.isAchievement() || this.canUnlockAchievement((Achievement) stat)) {
			this.unlockAchievement(player, stat, this.readStat(stat) + amount);
		}
	}

	/**
	 * Triggers the logging of an achievement and attempts to announce to server
	 */
	public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_) {
		TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.statsData.get(statIn);

		if (tupleintjsonserializable == null) {
			tupleintjsonserializable = new TupleIntJsonSerializable();
			this.statsData.put(statIn, tupleintjsonserializable);
		}

		tupleintjsonserializable.setIntegerValue(p_150873_3_);
	}

	/**
	 * Reads the given stat and returns its value as an int.
	 */
	public int readStat(StatBase stat) {
		TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.statsData.get(stat);
		return tupleintjsonserializable == null ? 0 : tupleintjsonserializable.getIntegerValue();
	}

	public <T extends IJsonSerializable> T func_150870_b(StatBase p_150870_1_) {
		TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.statsData.get(p_150870_1_);
		return (T) (tupleintjsonserializable != null ? tupleintjsonserializable.getJsonSerializableValue() : null);
	}

	public <T extends IJsonSerializable> T func_150872_a(StatBase p_150872_1_, T p_150872_2_) {
		TupleIntJsonSerializable tupleintjsonserializable = (TupleIntJsonSerializable) this.statsData.get(p_150872_1_);

		if (tupleintjsonserializable == null) {
			tupleintjsonserializable = new TupleIntJsonSerializable();
			this.statsData.put(p_150872_1_, tupleintjsonserializable);
		}

		tupleintjsonserializable.setJsonSerializableValue(p_150872_2_);
		return (T) p_150872_2_;
	}
}
