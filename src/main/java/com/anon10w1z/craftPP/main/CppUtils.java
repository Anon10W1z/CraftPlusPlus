package com.anon10w1z.craftPP.main;

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
public final class CppUtils {
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
