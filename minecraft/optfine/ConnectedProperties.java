package optfine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedProperties {
	public String name = null;
	public String basePath = null;
	public int[] matchBlocks = null;
	public String[] matchTiles = null;
	public int method = 0;
	public String[] tiles = null;
	public int connect = 0;
	public int faces = 63;
	public int[] metadatas = null;
	public BiomeGenBase[] biomes = null;
	public int minHeight = 0;
	public int maxHeight = 1024;
	public int renderPass = 0;
	public boolean innerSeams = false;
	public int width = 0;
	public int height = 0;
	public int[] weights = null;
	public int symmetry = 1;
	public int[] sumWeights = null;
	public int sumAllWeights = 1;
	public TextureAtlasSprite[] matchTileIcons = null;
	public TextureAtlasSprite[] tileIcons = null;
	public static final int METHOD_NONE = 0;
	public static final int METHOD_CTM = 1;
	public static final int METHOD_HORIZONTAL = 2;
	public static final int METHOD_TOP = 3;
	public static final int METHOD_RANDOM = 4;
	public static final int METHOD_REPEAT = 5;
	public static final int METHOD_VERTICAL = 6;
	public static final int METHOD_FIXED = 7;
	public static final int METHOD_HORIZONTAL_VERTICAL = 8;
	public static final int METHOD_VERTICAL_HORIZONTAL = 9;
	public static final int CONNECT_NONE = 0;
	public static final int CONNECT_BLOCK = 1;
	public static final int CONNECT_TILE = 2;
	public static final int CONNECT_MATERIAL = 3;
	public static final int CONNECT_UNKNOWN = 128;
	public static final int FACE_BOTTOM = 1;
	public static final int FACE_TOP = 2;
	public static final int FACE_NORTH = 4;
	public static final int FACE_SOUTH = 8;
	public static final int FACE_WEST = 16;
	public static final int FACE_EAST = 32;
	public static final int FACE_SIDES = 60;
	public static final int FACE_ALL = 63;
	public static final int FACE_UNKNOWN = 128;
	public static final int SYMMETRY_NONE = 1;
	public static final int SYMMETRY_OPPOSITE = 2;
	public static final int SYMMETRY_ALL = 6;
	public static final int SYMMETRY_UNKNOWN = 128;

	public ConnectedProperties(Properties p_i29_1_, String p_i29_2_) {
		this.name = parseName(p_i29_2_);
		this.basePath = parseBasePath(p_i29_2_);
		String s = p_i29_1_.getProperty("matchBlocks");
		IBlockState iblockstate = this.parseBlockState(s);

		if (iblockstate != null) {
			this.matchBlocks = new int[] { Block.getIdFromBlock(iblockstate.getBlock()) };
			this.metadatas = new int[] { iblockstate.getBlock().getMetaFromState(iblockstate) };
		}

		if (this.matchBlocks == null) {
			this.matchBlocks = parseBlockIds(s);
		}

		if (this.metadatas == null) {
			this.metadatas = parseInts(p_i29_1_.getProperty("metadata"));
		}

		this.matchTiles = this.parseMatchTiles(p_i29_1_.getProperty("matchTiles"));
		this.method = parseMethod(p_i29_1_.getProperty("method"));
		this.tiles = this.parseTileNames(p_i29_1_.getProperty("tiles"));
		this.connect = parseConnect(p_i29_1_.getProperty("connect"));
		this.faces = parseFaces(p_i29_1_.getProperty("faces"));
		this.biomes = parseBiomes(p_i29_1_.getProperty("biomes"));
		this.minHeight = parseInt(p_i29_1_.getProperty("minHeight"), -1);
		this.maxHeight = parseInt(p_i29_1_.getProperty("maxHeight"), 1024);
		this.renderPass = parseInt(p_i29_1_.getProperty("renderPass"));
		this.innerSeams = parseBoolean(p_i29_1_.getProperty("innerSeams"));
		this.width = parseInt(p_i29_1_.getProperty("width"));
		this.height = parseInt(p_i29_1_.getProperty("height"));
		this.weights = parseInts(p_i29_1_.getProperty("weights"));
		this.symmetry = parseSymmetry(p_i29_1_.getProperty("symmetry"));
	}

	private String[] parseMatchTiles(String p_parseMatchTiles_1_) {
		if (p_parseMatchTiles_1_ == null) {
			return null;
		} else {
			String[] astring = Config.tokenize(p_parseMatchTiles_1_, " ");

			for (int i = 0; i < astring.length; ++i) {
				String s = astring[i];

				if (s.endsWith(".png")) {
					s = s.substring(0, s.length() - 4);
				}

				s = TextureUtils.fixResourcePath(s, this.basePath);
				astring[i] = s;
			}

			return astring;
		}
	}

	private static String parseName(String p_parseName_0_) {
		String s = p_parseName_0_;
		int i = p_parseName_0_.lastIndexOf(47);

		if (i >= 0) {
			s = p_parseName_0_.substring(i + 1);
		}

		int j = s.lastIndexOf(46);

		if (j >= 0) {
			s = s.substring(0, j);
		}

		return s;
	}

	private static String parseBasePath(String p_parseBasePath_0_) {
		int i = p_parseBasePath_0_.lastIndexOf(47);
		return i < 0 ? "" : p_parseBasePath_0_.substring(0, i);
	}

	private static BiomeGenBase[] parseBiomes(String p_parseBiomes_0_) {
		if (p_parseBiomes_0_ == null) {
			return null;
		} else {
			String[] astring = Config.tokenize(p_parseBiomes_0_, " ");
			List list = new ArrayList();

			for (int i = 0; i < astring.length; ++i) {
				String s = astring[i];
				BiomeGenBase biomegenbase = findBiome(s);

				if (biomegenbase == null) {
					Config.warn("Biome not found: " + s);
				} else {
					list.add(biomegenbase);
				}
			}

			BiomeGenBase[] abiomegenbase = (BiomeGenBase[]) ((BiomeGenBase[]) list.toArray(new BiomeGenBase[list.size()]));
			return abiomegenbase;
		}
	}

	private static BiomeGenBase findBiome(String p_findBiome_0_) {
		p_findBiome_0_ = p_findBiome_0_.toLowerCase();
		BiomeGenBase[] abiomegenbase = BiomeGenBase.getBiomeGenArray();

		for (int i = 0; i < abiomegenbase.length; ++i) {
			BiomeGenBase biomegenbase = abiomegenbase[i];

			if (biomegenbase != null) {
				String s = biomegenbase.biomeName.replace(" ", "").toLowerCase();

				if (s.equals(p_findBiome_0_)) {
					return biomegenbase;
				}
			}
		}

		return null;
	}

	private String[] parseTileNames(String p_parseTileNames_1_) {
		if (p_parseTileNames_1_ == null) {
			return null;
		} else {
			List list = new ArrayList();
			String[] astring = Config.tokenize(p_parseTileNames_1_, " ,");
			label32:

			for (int i = 0; i < astring.length; ++i) {
				String s = astring[i];

				if (s.contains("-")) {
					String[] astring1 = Config.tokenize(s, "-");

					if (astring1.length == 2) {
						int j = Config.parseInt(astring1[0], -1);
						int k = Config.parseInt(astring1[1], -1);

						if (j >= 0 && k >= 0) {
							if (j > k) {
								Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseTileNames_1_);
								continue;
							}

							int l = j;

							while (true) {
								if (l > k) {
									continue label32;
								}

								list.add(String.valueOf(l));
								++l;
							}
						}
					}
				}

				list.add(s);
			}

			String[] astring2 = (String[]) ((String[]) list.toArray(new String[list.size()]));

			for (int i1 = 0; i1 < astring2.length; ++i1) {
				String s1 = astring2[i1];
				s1 = TextureUtils.fixResourcePath(s1, this.basePath);

				if (!s1.startsWith(this.basePath) && !s1.startsWith("textures/") && !s1.startsWith("mcpatcher/")) {
					s1 = this.basePath + "/" + s1;
				}

				if (s1.endsWith(".png")) {
					s1 = s1.substring(0, s1.length() - 4);
				}

				String s2 = "textures/blocks/";

				if (s1.startsWith(s2)) {
					s1 = s1.substring(s2.length());
				}

				if (s1.startsWith("/")) {
					s1 = s1.substring(1);
				}

				astring2[i1] = s1;
			}

			return astring2;
		}
	}

	private static int parseInt(String p_parseInt_0_) {
		if (p_parseInt_0_ == null) {
			return -1;
		} else {
			int i = Config.parseInt(p_parseInt_0_, -1);

			if (i < 0) {
				Config.warn("Invalid number: " + p_parseInt_0_);
			}

			return i;
		}
	}

	private static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
		if (p_parseInt_0_ == null) {
			return p_parseInt_1_;
		} else {
			int i = Config.parseInt(p_parseInt_0_, -1);

			if (i < 0) {
				Config.warn("Invalid number: " + p_parseInt_0_);
				return p_parseInt_1_;
			} else {
				return i;
			}
		}
	}

	private static boolean parseBoolean(String p_parseBoolean_0_) {
		return p_parseBoolean_0_ == null ? false : p_parseBoolean_0_.toLowerCase().equals("true");
	}

	private static int parseSymmetry(String p_parseSymmetry_0_) {
		if (p_parseSymmetry_0_ == null) {
			return 1;
		} else if (p_parseSymmetry_0_.equals("opposite")) {
			return 2;
		} else if (p_parseSymmetry_0_.equals("all")) {
			return 6;
		} else {
			Config.warn("Unknown symmetry: " + p_parseSymmetry_0_);
			return 1;
		}
	}

	private static int parseFaces(String p_parseFaces_0_) {
		if (p_parseFaces_0_ == null) {
			return 63;
		} else {
			String[] astring = Config.tokenize(p_parseFaces_0_, " ,");
			int i = 0;

			for (int j = 0; j < astring.length; ++j) {
				String s = astring[j];
				int k = parseFace(s);
				i |= k;
			}

			return i;
		}
	}

	private static int parseFace(String p_parseFace_0_) {
		p_parseFace_0_ = p_parseFace_0_.toLowerCase();

		if (!p_parseFace_0_.equals("bottom") && !p_parseFace_0_.equals("down")) {
			if (!p_parseFace_0_.equals("top") && !p_parseFace_0_.equals("up")) {
				if (p_parseFace_0_.equals("north")) {
					return 4;
				} else if (p_parseFace_0_.equals("south")) {
					return 8;
				} else if (p_parseFace_0_.equals("east")) {
					return 32;
				} else if (p_parseFace_0_.equals("west")) {
					return 16;
				} else if (p_parseFace_0_.equals("sides")) {
					return 60;
				} else if (p_parseFace_0_.equals("all")) {
					return 63;
				} else {
					Config.warn("Unknown face: " + p_parseFace_0_);
					return 128;
				}
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	private static int parseConnect(String p_parseConnect_0_) {
		if (p_parseConnect_0_ == null) {
			return 0;
		} else if (p_parseConnect_0_.equals("block")) {
			return 1;
		} else if (p_parseConnect_0_.equals("tile")) {
			return 2;
		} else if (p_parseConnect_0_.equals("material")) {
			return 3;
		} else {
			Config.warn("Unknown connect: " + p_parseConnect_0_);
			return 128;
		}
	}

	private static int[] parseInts(String p_parseInts_0_) {
		if (p_parseInts_0_ == null) {
			return null;
		} else {
			List list = new ArrayList();
			String[] astring = Config.tokenize(p_parseInts_0_, " ,");

			for (int i = 0; i < astring.length; ++i) {
				String s = astring[i];

				if (s.contains("-")) {
					String[] astring1 = Config.tokenize(s, "-");

					if (astring1.length != 2) {
						Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseInts_0_);
					} else {
						int k = Config.parseInt(astring1[0], -1);
						int l = Config.parseInt(astring1[1], -1);

						if (k >= 0 && l >= 0 && k <= l) {
							for (int i1 = k; i1 <= l; ++i1) {
								list.add(Integer.valueOf(i1));
							}
						} else {
							Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseInts_0_);
						}
					}
				} else {
					int j = Config.parseInt(s, -1);

					if (j < 0) {
						Config.warn("Invalid number: " + s + ", when parsing: " + p_parseInts_0_);
					} else {
						list.add(Integer.valueOf(j));
					}
				}
			}

			int[] aint = new int[list.size()];

			for (int j1 = 0; j1 < aint.length; ++j1) {
				aint[j1] = ((Integer) list.get(j1)).intValue();
			}

			return aint;
		}
	}

	private static int[] parseBlockIds(String p_parseBlockIds_0_) {
		if (p_parseBlockIds_0_ == null) {
			return null;
		} else {
			List list = new ArrayList();
			String[] astring = Config.tokenize(p_parseBlockIds_0_, " ,");

			for (int i = 0; i < astring.length; ++i) {
				String s = astring[i];

				if (s.contains("-")) {
					String[] astring1 = Config.tokenize(s, "-");

					if (astring1.length != 2) {
						Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseBlockIds_0_);
					} else {
						int k = parseBlockId(astring1[0]);
						int l = parseBlockId(astring1[1]);

						if (k >= 0 && l >= 0 && k <= l) {
							for (int i1 = k; i1 <= l; ++i1) {
								list.add(Integer.valueOf(i1));
							}
						} else {
							Config.warn("Invalid interval: " + s + ", when parsing: " + p_parseBlockIds_0_);
						}
					}
				} else {
					int j = parseBlockId(s);

					if (j < 0) {
						Config.warn("Invalid block ID: " + s + ", when parsing: " + p_parseBlockIds_0_);
					} else {
						list.add(Integer.valueOf(j));
					}
				}
			}

			int[] aint = new int[list.size()];

			for (int j1 = 0; j1 < aint.length; ++j1) {
				aint[j1] = ((Integer) list.get(j1)).intValue();
			}

			return aint;
		}
	}

	private static int parseBlockId(String p_parseBlockId_0_) {
		int i = Config.parseInt(p_parseBlockId_0_, -1);

		if (i >= 0) {
			return i;
		} else {
			Block block = Block.getBlockFromName(p_parseBlockId_0_);
			return block != null ? Block.getIdFromBlock(block) : -1;
		}
	}

	private IBlockState parseBlockState(String p_parseBlockState_1_) {
		if (p_parseBlockState_1_ == null) {
			return null;
		} else {
			String[] astring = Config.tokenize(p_parseBlockState_1_, ":");

			if (astring.length < 2) {
				return null;
			} else {
				String s = astring[0];
				String s1 = astring[1];
				String s2 = s + ":" + s1;
				Block block = Block.getBlockFromName(s2);

				if (block == null) {
					return null;
				} else {
					int i = -1;
					IBlockState iblockstate = null;

					for (int j = 2; j < astring.length; ++j) {
						String s3 = astring[j];

						if (s3.length() >= 1) {
							if (Character.isDigit(s3.charAt(0))) {
								if (s3.indexOf(45) < 0 && s3.indexOf(44) < 0) {
									int k = Config.parseInt(s3, -1);

									if (k >= 0) {
										i = k;
									}
								}
							} else {
								String[] astring1 = Config.tokenize(s3, "=");

								if (astring1.length >= 2) {
									String s4 = astring1[0];
									String s5 = astring1[1];

									if (s5.indexOf(44) < 0) {
										if (iblockstate == null) {
											iblockstate = block.getDefaultState();
										}

										Collection collection = iblockstate.getPropertyNames();
										IProperty iproperty = getProperty(s4, collection);

										if (iproperty == null) {
											String s6 = "\"";
											Config.warn("Block " + s6 + s2 + s6 + " has no property " + s6 + s4 + s6);
										} else {
											Class oclass = iproperty.getValueClass();
											Object object = ConnectedParser.parseValue(s5, oclass);

											if (object == null) {
												Collection collection1 = iproperty.getAllowedValues();
												object = ConnectedParser.getPropertyValue(s5, collection1);
											}

											if (object == null) {
												Config.warn("Invalid value: " + s5 + ", for property: " + iproperty);
											} else if (!(object instanceof Comparable)) {
												Config.warn("Value is not Comparable: " + s5 + ", for property: " + iproperty);
											} else {
												Comparable comparable = (Comparable) object;
												iblockstate = iblockstate.withProperty(iproperty, comparable);
											}
										}
									}
								}
							}
						}
					}

					if (iblockstate == null) {
						if (i < 0) {
							return null;
						} else {
							return block.getStateFromMeta(i);
						}
					} else {
						return iblockstate;
					}
				}
			}
		}
	}

	public static IProperty getProperty(String p_getProperty_0_, Collection p_getProperty_1_) {
		for (Object iproperty : p_getProperty_1_) {
			if (p_getProperty_0_.equals(((IProperty) iproperty).getName())) {
				return (IProperty) iproperty;
			}
		}

		return null;
	}

	private static int parseMethod(String p_parseMethod_0_) {
		if (p_parseMethod_0_ == null) {
			return 1;
		} else if (!p_parseMethod_0_.equals("ctm") && !p_parseMethod_0_.equals("glass")) {
			if (!p_parseMethod_0_.equals("horizontal") && !p_parseMethod_0_.equals("bookshelf")) {
				if (p_parseMethod_0_.equals("vertical")) {
					return 6;
				} else if (p_parseMethod_0_.equals("top")) {
					return 3;
				} else if (p_parseMethod_0_.equals("random")) {
					return 4;
				} else if (p_parseMethod_0_.equals("repeat")) {
					return 5;
				} else if (p_parseMethod_0_.equals("fixed")) {
					return 7;
				} else if (!p_parseMethod_0_.equals("horizontal+vertical") && !p_parseMethod_0_.equals("h+v")) {
					if (!p_parseMethod_0_.equals("vertical+horizontal") && !p_parseMethod_0_.equals("v+h")) {
						Config.warn("Unknown method: " + p_parseMethod_0_);
						return 0;
					} else {
						return 9;
					}
				} else {
					return 8;
				}
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}

	public boolean isValid(String p_isValid_1_) {
		if (this.name != null && this.name.length() > 0) {
			if (this.basePath == null) {
				Config.warn("No base path found: " + p_isValid_1_);
				return false;
			} else {
				if (this.matchBlocks == null) {
					this.matchBlocks = this.detectMatchBlocks();
				}

				if (this.matchTiles == null && this.matchBlocks == null) {
					this.matchTiles = this.detectMatchTiles();
				}

				if (this.matchBlocks == null && this.matchTiles == null) {
					Config.warn("No matchBlocks or matchTiles specified: " + p_isValid_1_);
					return false;
				} else if (this.method == 0) {
					Config.warn("No method: " + p_isValid_1_);
					return false;
				} else if (this.tiles != null && this.tiles.length > 0) {
					if (this.connect == 0) {
						this.connect = this.detectConnect();
					}

					if (this.connect == 128) {
						Config.warn("Invalid connect in: " + p_isValid_1_);
						return false;
					} else if (this.renderPass > 0) {
						Config.warn("Render pass not supported: " + this.renderPass);
						return false;
					} else if ((this.faces & 128) != 0) {
						Config.warn("Invalid faces in: " + p_isValid_1_);
						return false;
					} else if ((this.symmetry & 128) != 0) {
						Config.warn("Invalid symmetry in: " + p_isValid_1_);
						return false;
					} else {
						switch (this.method) {
						case 1:
							return this.isValidCtm(p_isValid_1_);

						case 2:
							return this.isValidHorizontal(p_isValid_1_);

						case 3:
							return this.isValidTop(p_isValid_1_);

						case 4:
							return this.isValidRandom(p_isValid_1_);

						case 5:
							return this.isValidRepeat(p_isValid_1_);

						case 6:
							return this.isValidVertical(p_isValid_1_);

						case 7:
							return this.isValidFixed(p_isValid_1_);

						case 8:
							return this.isValidHorizontalVertical(p_isValid_1_);

						case 9:
							return this.isValidVerticalHorizontal(p_isValid_1_);

						default:
							Config.warn("Unknown method: " + p_isValid_1_);
							return false;
						}
					}
				} else {
					Config.warn("No tiles specified: " + p_isValid_1_);
					return false;
				}
			}
		} else {
			Config.warn("No name found: " + p_isValid_1_);
			return false;
		}
	}

	private int detectConnect() {
		return this.matchBlocks != null ? 1 : (this.matchTiles != null ? 2 : 128);
	}

	private int[] detectMatchBlocks() {
		if (!this.name.startsWith("block")) {
			return null;
		} else {
			int i = "block".length();
			int j;

			for (j = i; j < this.name.length(); ++j) {
				char c0 = this.name.charAt(j);

				if (c0 < 48 || c0 > 57) {
					break;
				}
			}

			if (j == i) {
				return null;
			} else {
				String s = this.name.substring(i, j);
				int k = Config.parseInt(s, -1);
				return k < 0 ? null : new int[] { k };
			}
		}
	}

	private String[] detectMatchTiles() {
		TextureAtlasSprite textureatlassprite = getIcon(this.name);
		return textureatlassprite == null ? null : new String[] { this.name };
	}

	private static TextureAtlasSprite getIcon(String p_getIcon_0_) {
		TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
		TextureAtlasSprite textureatlassprite = texturemap.getSpriteSafe(p_getIcon_0_);

		if (textureatlassprite != null) {
			return textureatlassprite;
		} else {
			textureatlassprite = texturemap.getSpriteSafe("blocks/" + p_getIcon_0_);
			return textureatlassprite;
		}
	}

	private boolean isValidCtm(String p_isValidCtm_1_) {
		if (this.tiles == null) {
			this.tiles = this.parseTileNames("0-11 16-27 32-43 48-58");
		}

		if (this.tiles.length < 47) {
			Config.warn("Invalid tiles, must be at least 47: " + p_isValidCtm_1_);
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidHorizontal(String p_isValidHorizontal_1_) {
		if (this.tiles == null) {
			this.tiles = this.parseTileNames("12-15");
		}

		if (this.tiles.length != 4) {
			Config.warn("Invalid tiles, must be exactly 4: " + p_isValidHorizontal_1_);
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidVertical(String p_isValidVertical_1_) {
		if (this.tiles == null) {
			Config.warn("No tiles defined for vertical: " + p_isValidVertical_1_);
			return false;
		} else if (this.tiles.length != 4) {
			Config.warn("Invalid tiles, must be exactly 4: " + p_isValidVertical_1_);
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidHorizontalVertical(String p_isValidHorizontalVertical_1_) {
		if (this.tiles == null) {
			Config.warn("No tiles defined for horizontal+vertical: " + p_isValidHorizontalVertical_1_);
			return false;
		} else if (this.tiles.length != 7) {
			Config.warn("Invalid tiles, must be exactly 7: " + p_isValidHorizontalVertical_1_);
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidVerticalHorizontal(String p_isValidVerticalHorizontal_1_) {
		if (this.tiles == null) {
			Config.warn("No tiles defined for vertical+horizontal: " + p_isValidVerticalHorizontal_1_);
			return false;
		} else if (this.tiles.length != 7) {
			Config.warn("Invalid tiles, must be exactly 7: " + p_isValidVerticalHorizontal_1_);
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidRandom(String p_isValidRandom_1_) {
		if (this.tiles != null && this.tiles.length > 0) {
			if (this.weights != null) {
				if (this.weights.length > this.tiles.length) {
					Config.warn("More weights defined than tiles, trimming weights: " + p_isValidRandom_1_);
					int[] aint = new int[this.tiles.length];
					System.arraycopy(this.weights, 0, aint, 0, aint.length);
					this.weights = aint;
				}

				if (this.weights.length < this.tiles.length) {
					Config.warn("Less weights defined than tiles, expanding weights: " + p_isValidRandom_1_);
					int[] aint1 = new int[this.tiles.length];
					System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
					int i = ConnectedUtils.getAverage(this.weights);

					for (int j = this.weights.length; j < aint1.length; ++j) {
						aint1[j] = i;
					}

					this.weights = aint1;
				}

				this.sumWeights = new int[this.weights.length];
				int k = 0;

				for (int l = 0; l < this.weights.length; ++l) {
					k += this.weights[l];
					this.sumWeights[l] = k;
				}

				this.sumAllWeights = k;

				if (this.sumAllWeights <= 0) {
					Config.warn("Invalid sum of all weights: " + k);
					this.sumAllWeights = 1;
				}
			}

			return true;
		} else {
			Config.warn("Tiles not defined: " + p_isValidRandom_1_);
			return false;
		}
	}

	private boolean isValidRepeat(String p_isValidRepeat_1_) {
		if (this.tiles == null) {
			Config.warn("Tiles not defined: " + p_isValidRepeat_1_);
			return false;
		} else if (this.width > 0 && this.width <= 16) {
			if (this.height > 0 && this.height <= 16) {
				if (this.tiles.length != this.width * this.height) {
					Config.warn("Number of tiles does not equal width x height: " + p_isValidRepeat_1_);
					return false;
				} else {
					return true;
				}
			} else {
				Config.warn("Invalid height: " + p_isValidRepeat_1_);
				return false;
			}
		} else {
			Config.warn("Invalid width: " + p_isValidRepeat_1_);
			return false;
		}
	}

	private boolean isValidFixed(String p_isValidFixed_1_) {
		if (this.tiles == null) {
			Config.warn("Tiles not defined: " + p_isValidFixed_1_);
			return false;
		} else if (this.tiles.length != 1) {
			Config.warn("Number of tiles should be 1 for method: fixed.");
			return false;
		} else {
			return true;
		}
	}

	private boolean isValidTop(String p_isValidTop_1_) {
		if (this.tiles == null) {
			this.tiles = this.parseTileNames("66");
		}

		if (this.tiles.length != 1) {
			Config.warn("Invalid tiles, must be exactly 1: " + p_isValidTop_1_);
			return false;
		} else {
			return true;
		}
	}

	public void updateIcons(TextureMap p_updateIcons_1_) {
		if (this.matchTiles != null) {
			this.matchTileIcons = registerIcons(this.matchTiles, p_updateIcons_1_);
		}

		if (this.tiles != null) {
			this.tileIcons = registerIcons(this.tiles, p_updateIcons_1_);
		}
	}

	private static TextureAtlasSprite[] registerIcons(String[] p_registerIcons_0_, TextureMap p_registerIcons_1_) {
		if (p_registerIcons_0_ == null) {
			return null;
		} else {
			List list = new ArrayList();

			for (int i = 0; i < p_registerIcons_0_.length; ++i) {
				String s = p_registerIcons_0_[i];
				ResourceLocation resourcelocation = new ResourceLocation(s);
				String s1 = resourcelocation.getResourceDomain();
				String s2 = resourcelocation.getResourcePath();

				if (!s2.contains("/")) {
					s2 = "textures/blocks/" + s2;
				}

				String s3 = s2 + ".png";
				ResourceLocation resourcelocation1 = new ResourceLocation(s1, s3);
				boolean flag = Config.hasResource(resourcelocation1);

				if (!flag) {
					Config.warn("File not found: " + s3);
				}

				String s4 = "textures/";
				String s5 = s2;

				if (s2.startsWith(s4)) {
					s5 = s2.substring(s4.length());
				}

				ResourceLocation resourcelocation2 = new ResourceLocation(s1, s5);
				TextureAtlasSprite textureatlassprite = p_registerIcons_1_.registerSprite(resourcelocation2);
				list.add(textureatlassprite);
			}

			TextureAtlasSprite[] atextureatlassprite = (TextureAtlasSprite[]) ((TextureAtlasSprite[]) list.toArray(new TextureAtlasSprite[list.size()]));
			return atextureatlassprite;
		}
	}

	public boolean matchesBlock(int p_matchesBlock_1_) {
		if (this.matchBlocks != null && this.matchBlocks.length > 0) {
			for (int i = 0; i < this.matchBlocks.length; ++i) {
				int j = this.matchBlocks[i];

				if (j == p_matchesBlock_1_) {
					return true;
				}
			}

			return false;
		} else {
			return true;
		}
	}

	public boolean matchesIcon(TextureAtlasSprite p_matchesIcon_1_) {
		if (this.matchTileIcons != null && this.matchTileIcons.length > 0) {
			for (int i = 0; i < this.matchTileIcons.length; ++i) {
				if (this.matchTileIcons[i] == p_matchesIcon_1_) {
					return true;
				}
			}

			return false;
		} else {
			return true;
		}
	}

	public String toString() {
		return "CTM name: " + this.name + ", basePath: " + this.basePath + ", matchBlocks: " + Config.arrayToString(this.matchBlocks) + ", matchTiles: " + Config.arrayToString((Object[]) this.matchTiles);
	}
}
