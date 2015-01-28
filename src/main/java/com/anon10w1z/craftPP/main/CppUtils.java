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
	 * @param world The world to store this fake player
	 * @return A new fake player
	 */
	public static FakePlayer getFakePlayer(World world) {
		return !world.isRemote ? new WeakReference<FakePlayer>(FakePlayerFactory.getMinecraft((WorldServer) world)).get() : null;
	}

	/**
	 * @param iterable    The iterable object to convert into an array
	 * @param elementType The class type of elements in the iterable
	 * @param <T>         The data type of elements in the iterable
	 * @return An array containing all of the passed iterable's elements
	 */
	public static <T> T[] getArray(Iterable<? extends T> iterable, Class<T> elementType) {
		return Iterables.toArray(iterable, elementType);
	}

	/**
	 * @param fieldContainer The class that contains this field
	 * @param fieldNames     All possible names of the field to find
	 * @return The field with the first possible of the specified names
	 */
	public static Field findField(Class<?> fieldContainer, String... fieldNames) {
		try {
			return ReflectionHelper.findField(fieldContainer, fieldNames);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
