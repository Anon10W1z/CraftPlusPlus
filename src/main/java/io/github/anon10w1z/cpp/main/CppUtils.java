package io.github.anon10w1z.cpp.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
	 * Gets a fake player and returns it. <br>
	 * Calling this method for the first time returns a new fake player in the specified world. <br>
	 * Subsequent calls to this method will return the first fake player created.
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
	 * Finds an object by the specified name(s) in the specified object, and returns it
	 *
	 * @param fieldContainer The object to find the object in
	 * @param fieldNames     A list of all possible names for the object
	 * @param <T>            The data type of the object to return
	 * @return An object of the specified type with the first possible of the passed names
	 */
	public static <T> T findObject(Object fieldContainer, String... fieldNames) {
		Class fieldClass = fieldContainer.getClass();
		while (fieldClass != null) {
			try {
				Field field = ReflectionHelper.findField(fieldContainer.getClass(), fieldNames);
				return (T) field.get(fieldContainer);
			} catch (Exception exception) {
				fieldClass = fieldClass.getSuperclass();
			}
		}
		return null;
	}

	/**
	 * Copies a list and returns the copy
	 *
	 * @param list The list to copy
	 * @param <T>  The type of the list
	 * @return A copy of the given list
	 */
	public static <T> List<T> copyList(List<T> list) {
		try {
			Constructor constructor = list.getClass().getConstructor(Collection.class);
			return (List<T>) constructor.newInstance(list);
		} catch (Exception exception) {
			return new ArrayList<>(list);
		}
	}
	
	public static final String STEP_SOUND_FIELD ="stepSound";
	//TODO: find obfuscated name
	public static final String OBFUSCATED_STEP_SOUND_FIELD ="";
	
	
	public static void setStepSound(Block block, SoundType type) {
		ReflectionHelper.setPrivateValue(Block.class, block, SoundType.SAND, STEP_SOUND_FIELD, OBFUSCATED_STEP_SOUND_FIELD);
	}
	
	public static void consumeItem(Item item, IInventory inventory){
		ItemStack stack;
		for(int i=0; i<inventory.getSizeInventory(); i++) {
			if((stack=inventory.getStackInSlot(i))!=null && stack.getItem()==item) {
				if(stack.stackSize>1) 
					stack.stackSize--;
				else
					inventory.setInventorySlotContents(i, null);
				return;
			}
		}
	}
}
