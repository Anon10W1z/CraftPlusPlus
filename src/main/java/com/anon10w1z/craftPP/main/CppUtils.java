package com.anon10w1z.craftPP.main;

import com.google.common.collect.Iterables;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Contains some utility functions used by Craft++
 */
@SuppressWarnings("unchecked")
public class CppUtils {
	/**
	 * The way Minecraft Forge handles this is odd. <br>
	 * Getting a fake player for the first time returns a new fake player. <br>
	 * Subsequent calls to get a new fake player will return the first fake player created.
	 *
	 * @param world The world to store this fake player in
	 * @return A fake player
	 */
	public static FakePlayer getFakePlayer(World world) {
		try {
			return new WeakReference<FakePlayer>(FakePlayerFactory.getMinecraft(null)).get();
		} catch (Exception ignored) {
			return !world.isRemote ? new WeakReference<FakePlayer>(FakePlayerFactory.getMinecraft((WorldServer) world)).get() : null;
		}
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
	 * Finds an object by the specified name(s) in the specified instance
	 *
	 * @param instance    The instance to find the object in
	 * @param objectNames A list of all possible names for the object
	 * @param <T>         The data type of the object
	 * @return An object of the specified type with the first possible of the passed names
	 */
	public static <T> T findObject(Object instance, String... objectNames) {
		try {
			Field field = ReflectionHelper.findField(instance.getClass(), objectNames);
			return (T) field.get(instance);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
