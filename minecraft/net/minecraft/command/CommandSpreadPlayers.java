package net.minecraft.command;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {
	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "spreadplayers";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.spreadplayers.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 6) {
			throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
		} else {
			int i = 0;
			BlockPos blockpos = sender.getPosition();
			double d0 = parseDouble((double) blockpos.getX(), args[i++], true);
			double d1 = parseDouble((double) blockpos.getZ(), args[i++], true);
			double d2 = parseDouble(args[i++], 0.0D);
			double d3 = parseDouble(args[i++], d2 + 1.0D);
			boolean flag = parseBoolean(args[i++]);
			List<Entity> list = Lists.<Entity>newArrayList();

			while (i < args.length) {
				String s = args[i++];

				if (PlayerSelector.hasArguments(s)) {
					List<Entity> list1 = PlayerSelector.<Entity>matchEntities(sender, s, Entity.class);

					if (list1.size() == 0) {
						throw new EntityNotFoundException();
					}

					list.addAll(list1);
				} else {
					EntityPlayer entityplayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(s);

					if (entityplayer == null) {
						throw new PlayerNotFoundException();
					}

					list.add(entityplayer);
				}
			}

			sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());

			if (list.isEmpty()) {
				throw new EntityNotFoundException();
			} else {
				sender.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.spreading." + (flag ? "teams" : "players"), new Object[] { Integer.valueOf(list.size()), Double.valueOf(d3), Double.valueOf(d0), Double.valueOf(d1), Double.valueOf(d2) }));
				this.func_110669_a(sender, list, new CommandSpreadPlayers.Position(d0, d1), d2, d3, ((Entity) list.get(0)).worldObj, flag);
			}
		}
	}

	private void func_110669_a(ICommandSender p_110669_1_, List<Entity> p_110669_2_, CommandSpreadPlayers.Position p_110669_3_, double p_110669_4_, double p_110669_6_, World worldIn, boolean p_110669_9_) throws CommandException {
		Random random = new Random();
		double d0 = p_110669_3_.field_111101_a - p_110669_6_;
		double d1 = p_110669_3_.field_111100_b - p_110669_6_;
		double d2 = p_110669_3_.field_111101_a + p_110669_6_;
		double d3 = p_110669_3_.field_111100_b + p_110669_6_;
		CommandSpreadPlayers.Position[] acommandspreadplayers$position = this.func_110670_a(random, p_110669_9_ ? this.func_110667_a(p_110669_2_) : p_110669_2_.size(), d0, d1, d2, d3);
		int i = this.func_110668_a(p_110669_3_, p_110669_4_, worldIn, random, d0, d1, d2, d3, acommandspreadplayers$position, p_110669_9_);
		double d4 = this.func_110671_a(p_110669_2_, worldIn, acommandspreadplayers$position, p_110669_9_);
		notifyOperators(p_110669_1_, this, "commands.spreadplayers.success." + (p_110669_9_ ? "teams" : "players"), new Object[] { Integer.valueOf(acommandspreadplayers$position.length), Double.valueOf(p_110669_3_.field_111101_a), Double.valueOf(p_110669_3_.field_111100_b) });

		if (acommandspreadplayers$position.length > 1) {
			p_110669_1_.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (p_110669_9_ ? "teams" : "players"), new Object[] { String.format("%.2f", new Object[] { Double.valueOf(d4) }), Integer.valueOf(i) }));
		}
	}

	private int func_110667_a(List<Entity> p_110667_1_) {
		Set<Team> set = Sets.<Team>newHashSet();

		for (Entity entity : p_110667_1_) {
			if (entity instanceof EntityPlayer) {
				set.add(((EntityPlayer) entity).getTeam());
			} else {
				set.add((Team) null);
			}
		}

		return set.size();
	}

	private int func_110668_a(CommandSpreadPlayers.Position p_110668_1_, double p_110668_2_, World worldIn, Random p_110668_5_, double p_110668_6_, double p_110668_8_, double p_110668_10_, double p_110668_12_, CommandSpreadPlayers.Position[] p_110668_14_, boolean p_110668_15_) throws CommandException {
		boolean flag = true;
		double d0 = 3.4028234663852886E38D;
		int i;

		for (i = 0; i < 10000 && flag; ++i) {
			flag = false;
			d0 = 3.4028234663852886E38D;

			for (int j = 0; j < p_110668_14_.length; ++j) {
				CommandSpreadPlayers.Position commandspreadplayers$position = p_110668_14_[j];
				int k = 0;
				CommandSpreadPlayers.Position commandspreadplayers$position1 = new CommandSpreadPlayers.Position();

				for (int l = 0; l < p_110668_14_.length; ++l) {
					if (j != l) {
						CommandSpreadPlayers.Position commandspreadplayers$position2 = p_110668_14_[l];
						double d1 = commandspreadplayers$position.func_111099_a(commandspreadplayers$position2);
						d0 = Math.min(d1, d0);

						if (d1 < p_110668_2_) {
							++k;
							commandspreadplayers$position1.field_111101_a += commandspreadplayers$position2.field_111101_a - commandspreadplayers$position.field_111101_a;
							commandspreadplayers$position1.field_111100_b += commandspreadplayers$position2.field_111100_b - commandspreadplayers$position.field_111100_b;
						}
					}
				}

				if (k > 0) {
					commandspreadplayers$position1.field_111101_a /= (double) k;
					commandspreadplayers$position1.field_111100_b /= (double) k;
					double d2 = (double) commandspreadplayers$position1.func_111096_b();

					if (d2 > 0.0D) {
						commandspreadplayers$position1.func_111095_a();
						commandspreadplayers$position.func_111094_b(commandspreadplayers$position1);
					} else {
						commandspreadplayers$position.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
					}

					flag = true;
				}

				if (commandspreadplayers$position.func_111093_a(p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_)) {
					flag = true;
				}
			}

			if (!flag) {
				for (CommandSpreadPlayers.Position commandspreadplayers$position3 : p_110668_14_) {
					if (!commandspreadplayers$position3.func_111098_b(worldIn)) {
						commandspreadplayers$position3.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
						flag = true;
					}
				}
			}
		}

		if (i >= 10000) {
			throw new CommandException("commands.spreadplayers.failure." + (p_110668_15_ ? "teams" : "players"), new Object[] { Integer.valueOf(p_110668_14_.length), Double.valueOf(p_110668_1_.field_111101_a), Double.valueOf(p_110668_1_.field_111100_b), String.format("%.2f", new Object[] { Double.valueOf(d0) }) });
		} else {
			return i;
		}
	}

	private double func_110671_a(List<Entity> p_110671_1_, World worldIn, CommandSpreadPlayers.Position[] p_110671_3_, boolean p_110671_4_) {
		double d0 = 0.0D;
		int i = 0;
		Map<Team, CommandSpreadPlayers.Position> map = Maps.<Team, CommandSpreadPlayers.Position>newHashMap();

		for (int j = 0; j < p_110671_1_.size(); ++j) {
			Entity entity = (Entity) p_110671_1_.get(j);
			CommandSpreadPlayers.Position commandspreadplayers$position;

			if (p_110671_4_) {
				Team team = entity instanceof EntityPlayer ? ((EntityPlayer) entity).getTeam() : null;

				if (!map.containsKey(team)) {
					map.put(team, p_110671_3_[i++]);
				}

				commandspreadplayers$position = (CommandSpreadPlayers.Position) map.get(team);
			} else {
				commandspreadplayers$position = p_110671_3_[i++];
			}

			entity.setPositionAndUpdate((double) ((float) MathHelper.floor_double(commandspreadplayers$position.field_111101_a) + 0.5F), (double) commandspreadplayers$position.func_111092_a(worldIn), (double) MathHelper.floor_double(commandspreadplayers$position.field_111100_b) + 0.5D);
			double d2 = Double.MAX_VALUE;

			for (int k = 0; k < p_110671_3_.length; ++k) {
				if (commandspreadplayers$position != p_110671_3_[k]) {
					double d1 = commandspreadplayers$position.func_111099_a(p_110671_3_[k]);
					d2 = Math.min(d1, d2);
				}
			}

			d0 += d2;
		}

		d0 = d0 / (double) p_110671_1_.size();
		return d0;
	}

	private CommandSpreadPlayers.Position[] func_110670_a(Random p_110670_1_, int p_110670_2_, double p_110670_3_, double p_110670_5_, double p_110670_7_, double p_110670_9_) {
		CommandSpreadPlayers.Position[] acommandspreadplayers$position = new CommandSpreadPlayers.Position[p_110670_2_];

		for (int i = 0; i < acommandspreadplayers$position.length; ++i) {
			CommandSpreadPlayers.Position commandspreadplayers$position = new CommandSpreadPlayers.Position();
			commandspreadplayers$position.func_111097_a(p_110670_1_, p_110670_3_, p_110670_5_, p_110670_7_, p_110670_9_);
			acommandspreadplayers$position[i] = commandspreadplayers$position;
		}

		return acommandspreadplayers$position;
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length >= 1 && args.length <= 2 ? func_181043_b(args, 0, pos) : null;
	}

	static class Position {
		double field_111101_a;
		double field_111100_b;

		Position() {
		}

		Position(double p_i1358_1_, double p_i1358_3_) {
			this.field_111101_a = p_i1358_1_;
			this.field_111100_b = p_i1358_3_;
		}

		double func_111099_a(CommandSpreadPlayers.Position p_111099_1_) {
			double d0 = this.field_111101_a - p_111099_1_.field_111101_a;
			double d1 = this.field_111100_b - p_111099_1_.field_111100_b;
			return Math.sqrt(d0 * d0 + d1 * d1);
		}

		void func_111095_a() {
			double d0 = (double) this.func_111096_b();
			this.field_111101_a /= d0;
			this.field_111100_b /= d0;
		}

		float func_111096_b() {
			return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
		}

		public void func_111094_b(CommandSpreadPlayers.Position p_111094_1_) {
			this.field_111101_a -= p_111094_1_.field_111101_a;
			this.field_111100_b -= p_111094_1_.field_111100_b;
		}

		public boolean func_111093_a(double p_111093_1_, double p_111093_3_, double p_111093_5_, double p_111093_7_) {
			boolean flag = false;

			if (this.field_111101_a < p_111093_1_) {
				this.field_111101_a = p_111093_1_;
				flag = true;
			} else if (this.field_111101_a > p_111093_5_) {
				this.field_111101_a = p_111093_5_;
				flag = true;
			}

			if (this.field_111100_b < p_111093_3_) {
				this.field_111100_b = p_111093_3_;
				flag = true;
			} else if (this.field_111100_b > p_111093_7_) {
				this.field_111100_b = p_111093_7_;
				flag = true;
			}

			return flag;
		}

		public int func_111092_a(World worldIn) {
			BlockPos blockpos = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

			while (blockpos.getY() > 0) {
				blockpos = blockpos.down();

				if (worldIn.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
					return blockpos.getY() + 1;
				}
			}

			return 257;
		}

		public boolean func_111098_b(World worldIn) {
			BlockPos blockpos = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

			while (blockpos.getY() > 0) {
				blockpos = blockpos.down();
				Material material = worldIn.getBlockState(blockpos).getBlock().getMaterial();

				if (material != Material.air) {
					return !material.isLiquid() && material != Material.fire;
				}
			}

			return false;
		}

		public void func_111097_a(Random p_111097_1_, double p_111097_2_, double p_111097_4_, double p_111097_6_, double p_111097_8_) {
			this.field_111101_a = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_2_, p_111097_6_);
			this.field_111100_b = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_4_, p_111097_8_);
		}
	}
}
