package io.github.anon10w1z.craftPP.misc;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockStem;
import net.minecraft.command.CommandBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraftforge.common.FishingHooks;
import net.minecraftforge.fml.common.Loader;

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
		//Adds ability for certain additional blocks to fall as well as the ability to use item and block IDs instead of names in commands
		boolean deobfuscated = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		String onBlockAddedName = deobfuscated ? "onBlockAdded" : "func_176213_c";
		String onNeighborBlockChangeName = deobfuscated ? "onNeighborBlockChange" : "func_176204_a";
		String updateTickName = deobfuscated ? "updateTick" : "func_180650_b";
		String tickRateName = deobfuscated ? "tickRate" : "func_149738_a";
		installMethodInterceptor(Block.class, named(onBlockAddedName).or(named(onNeighborBlockChangeName)).or(named(updateTickName)).or(named(tickRateName)), CppBlockInterceptor.class);
		installMethodInterceptor(CommandBase.class, returns(Item.class).or(returns(Block.class)), CppCommandInterceptor.class);
	}

	/**
	 * Installs a method interceptor's functionality into all methods matching the given method matcher
	 *
	 * @param methodContainer   The class that contains the method(s) to be redefined
	 * @param methodMatcher     The method matcher used to find methods
	 * @param methodInterceptor The method interceptor class
	 */
	private static void installMethodInterceptor(Class methodContainer, ElementMatcher<? super MethodDescription> methodMatcher, Class methodInterceptor) {
		new ByteBuddy().redefine(methodContainer).method(methodMatcher).intercept(MethodDelegation.to(methodInterceptor)).make().load(methodContainer.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
	}
}
