package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;

public interface IAttributeInstance {
	/**
	 * Get the Attribute this is an instance of
	 */
	IAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double baseValue);

	Collection<AttributeModifier> getModifiersByOperation(int operation);

	Collection<AttributeModifier> func_111122_c();

	boolean hasModifier(AttributeModifier modifier);

	/**
	 * Returns attribute modifier, if any, by the given UUID
	 */
	AttributeModifier getModifier(UUID uuid);

	void applyModifier(AttributeModifier modifier);

	void removeModifier(AttributeModifier modifier);

	void removeAllModifiers();

	double getAttributeValue();
}
