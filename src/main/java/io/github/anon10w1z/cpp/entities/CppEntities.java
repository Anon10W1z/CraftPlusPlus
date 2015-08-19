package io.github.anon10w1z.cpp.entities;

import io.github.anon10w1z.cpp.main.CraftPlusPlus;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Registers Craft++'s entities
 */
public class CppEntities {
	private static int currentEntityID = 0;
	private static CraftPlusPlus currentMod;

	/**
	 * Registers the entities for Craft++
	 *
	 * @param mod The Craft++ instance
	 */
	public static void registerEntities(CraftPlusPlus mod) {
		currentMod = mod;
		registerEntity(EntityDynamite.class, "Dynamite");
		registerEntity(EntityObsidianBoat.class, "ObsidianBoat");
		registerEntity(EntitySitPoint.class, "SitPoint");
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName) {
		EntityRegistry.registerModEntity(entityClass, entityName, ++currentEntityID, currentMod, 64, 10, true);
	}
}
