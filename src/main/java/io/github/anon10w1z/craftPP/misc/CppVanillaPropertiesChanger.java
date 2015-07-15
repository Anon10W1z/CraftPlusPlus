package io.github.anon10w1z.craftPP.misc;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.This;
import net.minecraft.block.*;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraftforge.common.FishingHooks;
import net.minecraftforge.fml.common.Loader;

import java.util.Random;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;

/**
 * The vanilla properties changer for Craft++
 */
public class CppVanillaPropertiesChanger {
	/**
	 * Initializes the vanilla properties changer
	 */
	@SuppressWarnings("unchecked")
	public static void init() {
		//Modifying block step sounds
		Iterable<Block> blocks = Block.blockRegistry;
		for (Block block : blocks) {
			if (block instanceof BlockStem || block instanceof BlockNetherWart)
				block.setStepSound(Block.soundTypeGrass);
			else if (block instanceof BlockFire) {
				block.setStepSound(new SoundType(null, 1.5F, 0.65F) {
					@Override
					public String getStepSound() {
						return "fire.fire";
					}
				});
			}
		}
		//Modifying block creative tabs
		if (CppConfigHandler.commandBlockInRedstoneTab)
			Blocks.command_block.setCreativeTab(CreativeTabs.tabRedstone);
		Blocks.dragon_egg.setCreativeTab(CreativeTabs.tabDecorations);
		if (Loader.isModLoaded("NotEnoughItems") || Loader.isModLoaded("TooManyItems"))
			Blocks.mob_spawner.setCreativeTab(CreativeTabs.tabMisc);
		//Modifying block names
		if (CppConfigHandler.renameButtons) {
			Blocks.stone_button.setUnlocalizedName("buttonStone");
			Blocks.wooden_button.setUnlocalizedName("buttonWood");
		}
		//Adding fishables
		FishingHooks.addJunk(new WeightedRandomFishable(new ItemStack(Items.paper), 10));
		//Enable falling dirt and ability to use item and block IDs instead of names in commands
		ByteBuddyAgent.installOnOpenJDK();
		boolean deobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		String onBlockAddedName = deobfuscated ? "onBlockAdded" : "func_176213_c";
		String onNeighborBlockChangeName = deobfuscated ? "onNeighborBlockChange" : "func_176204_a";
		String updateTickName = deobfuscated ? "updateTick" : "func_180650_b";
		String tickRateName = deobfuscated ? "tickRate" : "func_149738_a";
		new ByteBuddy().redefine(Block.class).method(named(onBlockAddedName).or(named(onNeighborBlockChangeName)).or(named(updateTickName)).or(named(tickRateName))).intercept(MethodDelegation.to(CppDirtInterceptor.class)).make().load(BlockDirt.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
		new ByteBuddy().redefine(CommandBase.class).method(returns(Item.class).or(returns(Block.class))).intercept(MethodDelegation.to(CppCommandInterceptor.class)).make().load(CommandBase.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
	}

	@SuppressWarnings("unused")
	public static class CppDirtInterceptor {
		public static void onBlockAdded(World world, BlockPos blockPos, IBlockState blockState, @This Block block) {
			if (CppConfigHandler.additionalFallingBlocks.contains(block))
				world.scheduleUpdate(blockPos, block, block.tickRate(world));
		}

		public static void onNeighborBlockChange(World world, BlockPos blockPos, IBlockState blockState, Block neighborBlock, @This Block block) {
			if (CppConfigHandler.additionalFallingBlocks.contains(block))
				world.scheduleUpdate(blockPos, block, block.tickRate(world));
		}

		public static void updateTick(World world, BlockPos blockPos, IBlockState blockState, Random random, @This Block block) {
			if (!world.isRemote && CppConfigHandler.additionalFallingBlocks.contains(block)) {
				boolean canFallInto;
				if (world.isAirBlock(blockPos.down()))
					canFallInto = true;
				else {
					Block block1 = world.getBlockState(blockPos.down()).getBlock();
					Material material = block1.getMaterial();
					canFallInto = block1 == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
				}
				if (canFallInto && blockPos.getY() >= 0) {
					byte b = 32;
					if (!BlockFalling.fallInstantly && world.isAreaLoaded(blockPos.add(-b, -b, -b), blockPos.add(b, b, b))) {
						EntityFallingBlock fallingBlock = new EntityFallingBlock(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, world.getBlockState(blockPos));
						world.spawnEntityInWorld(fallingBlock);
					} else {
						world.setBlockToAir(blockPos);
						BlockPos blockPos1;
						blockPos1 = blockPos.down();
						while (canFallInto && blockPos1.getY() > 0) {
							blockPos1 = blockPos1.down();
							if (!world.isAirBlock(blockPos1)) {
								Block block1 = world.getBlockState(blockPos).getBlock();
								Material material = block1.getMaterial();
								canFallInto = block1 == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
							}
						}
						if (blockPos1.getY() > 0)
							world.setBlockState(blockPos1.up(), block.getDefaultState());
					}
				}
			}
		}

		public static int tickRate(World world, @This Block block) {
			if (CppConfigHandler.additionalFallingBlocks.contains(block))
				return 2;
			return 10;
		}
	}

	public static class CppCommandInterceptor {
		@SuppressWarnings("unused")
		public static Item getItemByText(ICommandSender commandSender, String text) throws CommandException {
			Item item = Item.getByNameOrId(text);
			if (item == null)
				throw new NumberInvalidException("commands.give.notFound", new ResourceLocation(text));
			return item;
		}

		@SuppressWarnings("unused")
		public static Block getBlockByText(ICommandSender commandSender, String text) throws CommandException {
			Block block = Block.getBlockFromName(text);
			if (block == null)
				try {
					block = Block.getBlockById(Integer.parseInt(text));
				} catch (NumberFormatException ignored) {

				}
			if (block == null)
				throw new NumberInvalidException("commands.give.notFound", new ResourceLocation(text));
			return block;
		}
	}
}
