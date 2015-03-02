package com.anon10w1z.craftPP.main;

import com.google.common.collect.Iterables;
import net.minecraft.block.Block;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

/**
 * Contains some utility functions used by Craft++
 */
@SuppressWarnings("unchecked")
public class CppUtils {
	/**
	 * Prevents CppUtils from being instantiated
	 */
	private CppUtils() {

	}

	/**
	 * Gets a fake player. <br>
	 * Getting a fake player for the first time returns a new fake player in the specified world. <br>
	 * Subsequent calls to get a new fake player will return the first fake player created.
	 *
	 * @param world The (semi-optional) world to store this fake player in
	 * @return A fake player
	 */
	public static FakePlayer getFakePlayer(World world) {
		if (world instanceof WorldServer)
			return FakePlayerFactory.getMinecraft((WorldServer) world);
		return null;
	}

	/**
	 * Returns an array containing all of the specified iterable's elements, with the specified type
	 *
	 * @param iterable    The iterable object to convert into an array
	 * @param elementType The class type of elements in the iterable
	 * @param <T>         The data type of elements in the iterable
	 * @return An array containing all of the passed iterable's elements
	 */
	public static <T> T[] getArray(Iterable<? extends T> iterable, Class<T> elementType) {
		return Iterables.toArray(iterable, elementType);
	}

	/**
	 * @return An array containing all blocks registered with the game
	 */
	public static Block[] getBlockArray() {
		return getArray(Block.blockRegistry, Block.class);
	}

	/**
	 * @return An array containing all recipes registered with the game
	 */
	public static IRecipe[] getRecipeArray() {
		return getArray(CraftingManager.getInstance().getRecipeList(), IRecipe.class);
	}

	/**
	 * Finds an object by the specified name(s) in the specified instance
	 *
	 * @param instance    The instance to find the object in
	 * @param objectNames A list of all possible names for the object
	 * @param <T>         The data type of the object to return
	 * @return An object of the specified type with the first possible of the passed names
	 */
	public static <T> T findObject(Object instance, String... objectNames) {
		try {
			Field field = ReflectionHelper.findField(instance.getClass(), objectNames);
			return (T) field.get(instance);
		} catch (Exception e) {
			return null;
		}
	}
}
