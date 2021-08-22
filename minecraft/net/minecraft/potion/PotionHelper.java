package net.minecraft.potion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.IntegerCache;

public class PotionHelper {
	public static final String field_77924_a = null;
	public static final String sugarEffect = "-0+1-2-3&4-4+13";
	public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
	public static final String spiderEyeEffect = "-0-1+2-3&4-4+13";
	public static final String fermentedSpiderEyeEffect = "-0+3-4+13";
	public static final String speckledMelonEffect = "+0-1+2-3&4-4+13";
	public static final String blazePowderEffect = "+0-1-2+3&4-4+13";
	public static final String magmaCreamEffect = "+0+1-2-3&4-4+13";
	public static final String redstoneEffect = "-5+6-7";
	public static final String glowstoneEffect = "+5-6-7";
	public static final String gunpowderEffect = "+14&13-13";
	public static final String goldenCarrotEffect = "-0+1+2-3+13&4-4";
	public static final String pufferfishEffect = "+0-1+2+3+13&4-4";
	public static final String rabbitFootEffect = "+0+1-2+3&4-4+13";
	private static final Map<Integer, String> potionRequirements = Maps.<Integer, String>newHashMap();
	private static final Map<Integer, String> potionAmplifiers = Maps.<Integer, String>newHashMap();
	private static final Map<Integer, Integer> DATAVALUE_COLORS = Maps.<Integer, Integer>newHashMap();

	/** An array of possible potion prefix names, as translation IDs. */
	private static final String[] potionPrefixes = new String[] { "potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky" };

	/**
	 * Checks if the bit at 1 << j is on in i.
	 */
	public static boolean checkFlag(int p_77914_0_, int p_77914_1_) {
		return (p_77914_0_ & 1 << p_77914_1_) != 0;
	}

	/**
	 * Returns 1 if the flag is set, 0 if it is not set.
	 */
	private static int isFlagSet(int p_77910_0_, int p_77910_1_) {
		return checkFlag(p_77910_0_, p_77910_1_) ? 1 : 0;
	}

	/**
	 * Returns 0 if the flag is set, 1 if it is not set.
	 */
	private static int isFlagUnset(int p_77916_0_, int p_77916_1_) {
		return checkFlag(p_77916_0_, p_77916_1_) ? 0 : 1;
	}

	/**
	 * Given a potion data value, get its prefix index number.
	 */
	public static int getPotionPrefixIndex(int dataValue) {
		return func_77908_a(dataValue, 5, 4, 3, 2, 1);
	}

	/**
	 * Given a {@link Collection}<{@link PotionEffect}> will return an Integer
	 * color.
	 */
	public static int calcPotionLiquidColor(Collection<PotionEffect> p_77911_0_) {
		int i = 3694022;

		if (p_77911_0_ != null && !p_77911_0_.isEmpty()) {
			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			float f3 = 0.0F;

			for (PotionEffect potioneffect : p_77911_0_) {
				if (potioneffect.getIsShowParticles()) {
					int j = Potion.potionTypes[potioneffect.getPotionID()].getLiquidColor();

					for (int k = 0; k <= potioneffect.getAmplifier(); ++k) {
						f += (float) (j >> 16 & 255) / 255.0F;
						f1 += (float) (j >> 8 & 255) / 255.0F;
						f2 += (float) (j >> 0 & 255) / 255.0F;
						++f3;
					}
				}
			}

			if (f3 == 0.0F) {
				return 0;
			} else {
				f = f / f3 * 255.0F;
				f1 = f1 / f3 * 255.0F;
				f2 = f2 / f3 * 255.0F;
				return (int) f << 16 | (int) f1 << 8 | (int) f2;
			}
		} else {
			return i;
		}
	}

	/**
	 * Check whether a {@link Collection}<{@link PotionEffect}> are all ambient.
	 */
	public static boolean getAreAmbient(Collection<PotionEffect> potionEffects) {
		for (PotionEffect potioneffect : potionEffects) {
			if (!potioneffect.getIsAmbient()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Given a potion data value, get the associated liquid color (optionally
	 * bypassing the cache)
	 */
	public static int getLiquidColor(int dataValue, boolean bypassCache) {
		Integer integer = IntegerCache.func_181756_a(dataValue);

		if (!bypassCache) {
			if (DATAVALUE_COLORS.containsKey(integer)) {
				return ((Integer) DATAVALUE_COLORS.get(integer)).intValue();
			} else {
				int i = calcPotionLiquidColor(getPotionEffects(integer.intValue(), false));
				DATAVALUE_COLORS.put(integer, Integer.valueOf(i));
				return i;
			}
		} else {
			return calcPotionLiquidColor(getPotionEffects(integer.intValue(), true));
		}
	}

	/**
	 * Given a potion data value, get its prefix as a translation ID.
	 */
	public static String getPotionPrefix(int dataValue) {
		int i = getPotionPrefixIndex(dataValue);
		return potionPrefixes[i];
	}

	private static int func_77904_a(boolean p_77904_0_, boolean p_77904_1_, boolean p_77904_2_, int p_77904_3_, int p_77904_4_, int p_77904_5_, int p_77904_6_) {
		int i = 0;

		if (p_77904_0_) {
			i = isFlagUnset(p_77904_6_, p_77904_4_);
		} else if (p_77904_3_ != -1) {
			if (p_77904_3_ == 0 && countSetFlags(p_77904_6_) == p_77904_4_) {
				i = 1;
			} else if (p_77904_3_ == 1 && countSetFlags(p_77904_6_) > p_77904_4_) {
				i = 1;
			} else if (p_77904_3_ == 2 && countSetFlags(p_77904_6_) < p_77904_4_) {
				i = 1;
			}
		} else {
			i = isFlagSet(p_77904_6_, p_77904_4_);
		}

		if (p_77904_1_) {
			i *= p_77904_5_;
		}

		if (p_77904_2_) {
			i *= -1;
		}

		return i;
	}

	/**
	 * Returns the number of 1 bits in the given integer.
	 */
	private static int countSetFlags(int p_77907_0_) {
		int i;

		for (i = 0; p_77907_0_ > 0; ++i) {
			p_77907_0_ &= p_77907_0_ - 1;
		}

		return i;
	}

	private static int parsePotionEffects(String p_77912_0_, int p_77912_1_, int p_77912_2_, int p_77912_3_) {
		if (p_77912_1_ < p_77912_0_.length() && p_77912_2_ >= 0 && p_77912_1_ < p_77912_2_) {
			int i = p_77912_0_.indexOf(124, p_77912_1_);

			if (i >= 0 && i < p_77912_2_) {
				int l1 = parsePotionEffects(p_77912_0_, p_77912_1_, i - 1, p_77912_3_);

				if (l1 > 0) {
					return l1;
				} else {
					int j2 = parsePotionEffects(p_77912_0_, i + 1, p_77912_2_, p_77912_3_);
					return j2 > 0 ? j2 : 0;
				}
			} else {
				int j = p_77912_0_.indexOf(38, p_77912_1_);

				if (j >= 0 && j < p_77912_2_) {
					int i2 = parsePotionEffects(p_77912_0_, p_77912_1_, j - 1, p_77912_3_);

					if (i2 <= 0) {
						return 0;
					} else {
						int k2 = parsePotionEffects(p_77912_0_, j + 1, p_77912_2_, p_77912_3_);
						return k2 <= 0 ? 0 : (i2 > k2 ? i2 : k2);
					}
				} else {
					boolean flag = false;
					boolean flag1 = false;
					boolean flag2 = false;
					boolean flag3 = false;
					boolean flag4 = false;
					int k = -1;
					int l = 0;
					int i1 = 0;
					int j1 = 0;

					for (int k1 = p_77912_1_; k1 < p_77912_2_; ++k1) {
						char c0 = p_77912_0_.charAt(k1);

						if (c0 >= 48 && c0 <= 57) {
							if (flag) {
								i1 = c0 - 48;
								flag1 = true;
							} else {
								l = l * 10;
								l = l + (c0 - 48);
								flag2 = true;
							}
						} else if (c0 == 42) {
							flag = true;
						} else if (c0 == 33) {
							if (flag2) {
								j1 += func_77904_a(flag3, flag1, flag4, k, l, i1, p_77912_3_);
								flag3 = false;
								flag4 = false;
								flag = false;
								flag1 = false;
								flag2 = false;
								i1 = 0;
								l = 0;
								k = -1;
							}

							flag3 = true;
						} else if (c0 == 45) {
							if (flag2) {
								j1 += func_77904_a(flag3, flag1, flag4, k, l, i1, p_77912_3_);
								flag3 = false;
								flag4 = false;
								flag = false;
								flag1 = false;
								flag2 = false;
								i1 = 0;
								l = 0;
								k = -1;
							}

							flag4 = true;
						} else if (c0 != 61 && c0 != 60 && c0 != 62) {
							if (c0 == 43 && flag2) {
								j1 += func_77904_a(flag3, flag1, flag4, k, l, i1, p_77912_3_);
								flag3 = false;
								flag4 = false;
								flag = false;
								flag1 = false;
								flag2 = false;
								i1 = 0;
								l = 0;
								k = -1;
							}
						} else {
							if (flag2) {
								j1 += func_77904_a(flag3, flag1, flag4, k, l, i1, p_77912_3_);
								flag3 = false;
								flag4 = false;
								flag = false;
								flag1 = false;
								flag2 = false;
								i1 = 0;
								l = 0;
								k = -1;
							}

							if (c0 == 61) {
								k = 0;
							} else if (c0 == 60) {
								k = 2;
							} else if (c0 == 62) {
								k = 1;
							}
						}
					}

					if (flag2) {
						j1 += func_77904_a(flag3, flag1, flag4, k, l, i1, p_77912_3_);
					}

					return j1;
				}
			}
		} else {
			return 0;
		}
	}

	public static List<PotionEffect> getPotionEffects(int p_77917_0_, boolean p_77917_1_) {
		List<PotionEffect> list = null;

		for (Potion potion : Potion.potionTypes) {
			if (potion != null && (!potion.isUsable() || p_77917_1_)) {
				String s = (String) potionRequirements.get(Integer.valueOf(potion.getId()));

				if (s != null) {
					int i = parsePotionEffects(s, 0, s.length(), p_77917_0_);

					if (i > 0) {
						int j = 0;
						String s1 = (String) potionAmplifiers.get(Integer.valueOf(potion.getId()));

						if (s1 != null) {
							j = parsePotionEffects(s1, 0, s1.length(), p_77917_0_);

							if (j < 0) {
								j = 0;
							}
						}

						if (potion.isInstant()) {
							i = 1;
						} else {
							i = 1200 * (i * 3 + (i - 1) * 2);
							i = i >> j;
							i = (int) Math.round((double) i * potion.getEffectiveness());

							if ((p_77917_0_ & 16384) != 0) {
								i = (int) Math.round((double) i * 0.75D + 0.5D);
							}
						}

						if (list == null) {
							list = Lists.<PotionEffect>newArrayList();
						}

						PotionEffect potioneffect = new PotionEffect(potion.getId(), i, j);

						if ((p_77917_0_ & 16384) != 0) {
							potioneffect.setSplashPotion(true);
						}

						list.add(potioneffect);
					}
				}
			}
		}

		return list;
	}

	/**
	 * Manipulates the specified bit of the potion damage value according to the
	 * rules passed from applyIngredient.
	 */
	private static int brewBitOperations(int p_77906_0_, int p_77906_1_, boolean p_77906_2_, boolean p_77906_3_, boolean p_77906_4_) {
		if (p_77906_4_) {
			if (!checkFlag(p_77906_0_, p_77906_1_)) {
				return 0;
			}
		} else if (p_77906_2_) {
			p_77906_0_ &= ~(1 << p_77906_1_);
		} else if (p_77906_3_) {
			if ((p_77906_0_ & 1 << p_77906_1_) == 0) {
				p_77906_0_ |= 1 << p_77906_1_;
			} else {
				p_77906_0_ &= ~(1 << p_77906_1_);
			}
		} else {
			p_77906_0_ |= 1 << p_77906_1_;
		}

		return p_77906_0_;
	}

	/**
	 * Returns the new potion damage value after the specified ingredient info is
	 * applied to the specified potion.
	 */
	public static int applyIngredient(int p_77913_0_, String p_77913_1_) {
		int i = 0;
		int j = p_77913_1_.length();
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		int k = 0;

		for (int l = i; l < j; ++l) {
			char c0 = p_77913_1_.charAt(l);

			if (c0 >= 48 && c0 <= 57) {
				k = k * 10;
				k = k + (c0 - 48);
				flag = true;
			} else if (c0 == 33) {
				if (flag) {
					p_77913_0_ = brewBitOperations(p_77913_0_, k, flag2, flag1, flag3);
					flag3 = false;
					flag1 = false;
					flag2 = false;
					flag = false;
					k = 0;
				}

				flag1 = true;
			} else if (c0 == 45) {
				if (flag) {
					p_77913_0_ = brewBitOperations(p_77913_0_, k, flag2, flag1, flag3);
					flag3 = false;
					flag1 = false;
					flag2 = false;
					flag = false;
					k = 0;
				}

				flag2 = true;
			} else if (c0 == 43) {
				if (flag) {
					p_77913_0_ = brewBitOperations(p_77913_0_, k, flag2, flag1, flag3);
					flag3 = false;
					flag1 = false;
					flag2 = false;
					flag = false;
					k = 0;
				}
			} else if (c0 == 38) {
				if (flag) {
					p_77913_0_ = brewBitOperations(p_77913_0_, k, flag2, flag1, flag3);
					flag3 = false;
					flag1 = false;
					flag2 = false;
					flag = false;
					k = 0;
				}

				flag3 = true;
			}
		}

		if (flag) {
			p_77913_0_ = brewBitOperations(p_77913_0_, k, flag2, flag1, flag3);
		}

		return p_77913_0_ & 32767;
	}

	public static int func_77908_a(int p_77908_0_, int p_77908_1_, int p_77908_2_, int p_77908_3_, int p_77908_4_, int p_77908_5_) {
		return (checkFlag(p_77908_0_, p_77908_1_) ? 16 : 0) | (checkFlag(p_77908_0_, p_77908_2_) ? 8 : 0) | (checkFlag(p_77908_0_, p_77908_3_) ? 4 : 0) | (checkFlag(p_77908_0_, p_77908_4_) ? 2 : 0) | (checkFlag(p_77908_0_, p_77908_5_) ? 1 : 0);
	}

	static {
		potionRequirements.put(Integer.valueOf(Potion.regeneration.getId()), "0 & !1 & !2 & !3 & 0+6");
		potionRequirements.put(Integer.valueOf(Potion.moveSpeed.getId()), "!0 & 1 & !2 & !3 & 1+6");
		potionRequirements.put(Integer.valueOf(Potion.fireResistance.getId()), "0 & 1 & !2 & !3 & 0+6");
		potionRequirements.put(Integer.valueOf(Potion.heal.getId()), "0 & !1 & 2 & !3");
		potionRequirements.put(Integer.valueOf(Potion.poison.getId()), "!0 & !1 & 2 & !3 & 2+6");
		potionRequirements.put(Integer.valueOf(Potion.weakness.getId()), "!0 & !1 & !2 & 3 & 3+6");
		potionRequirements.put(Integer.valueOf(Potion.harm.getId()), "!0 & !1 & 2 & 3");
		potionRequirements.put(Integer.valueOf(Potion.moveSlowdown.getId()), "!0 & 1 & !2 & 3 & 3+6");
		potionRequirements.put(Integer.valueOf(Potion.damageBoost.getId()), "0 & !1 & !2 & 3 & 3+6");
		potionRequirements.put(Integer.valueOf(Potion.nightVision.getId()), "!0 & 1 & 2 & !3 & 2+6");
		potionRequirements.put(Integer.valueOf(Potion.invisibility.getId()), "!0 & 1 & 2 & 3 & 2+6");
		potionRequirements.put(Integer.valueOf(Potion.waterBreathing.getId()), "0 & !1 & 2 & 3 & 2+6");
		potionRequirements.put(Integer.valueOf(Potion.jump.getId()), "0 & 1 & !2 & 3 & 3+6");
		potionAmplifiers.put(Integer.valueOf(Potion.moveSpeed.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.digSpeed.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.damageBoost.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.regeneration.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.harm.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.heal.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.resistance.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.poison.getId()), "5");
		potionAmplifiers.put(Integer.valueOf(Potion.jump.getId()), "5");
	}
}
