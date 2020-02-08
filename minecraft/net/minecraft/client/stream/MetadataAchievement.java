package net.minecraft.client.stream;

import net.minecraft.stats.Achievement;

public class MetadataAchievement extends Metadata {
	public MetadataAchievement(Achievement p_i1032_1_) {
		super("achievement");
		this.func_152808_a("achievement_id", p_i1032_1_.statId);
		this.func_152808_a("achievement_name", p_i1032_1_.getStatName().getUnformattedText());
		this.func_152808_a("achievement_description", p_i1032_1_.getDescription());
		this.func_152807_a("Achievement \'" + p_i1032_1_.getStatName().getUnformattedText() + "\' obtained!");
	}
}
