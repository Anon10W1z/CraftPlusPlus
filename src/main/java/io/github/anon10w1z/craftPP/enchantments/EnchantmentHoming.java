package io.github.anon10w1z.craftPP.enchantments;

import com.google.common.collect.Maps;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.*;

/**
 * Makes arrows shot out of bows track down their targets
 */
@SuppressWarnings("unused")
@EntityTickingEnchantment
public class EnchantmentHoming extends CppEnchantmentBase {
	public static Map<UUID, Integer> shooterToHomingValue = Maps.newHashMap();
	private static Random random = new Random();
	private static Map<UUID, UUID> arrowToTarget = Maps.newHashMap();

	public EnchantmentHoming() {
		super("homing", 1, EnumEnchantmentType.BOW);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void performAction(Entity entity, Event baseEvent) {
		if (entity instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) entity;
			UUID arrowID = arrow.getPersistentID();
			EntityLivingBase shooter = (EntityLivingBase) arrow.shootingEntity;
			if (shooter != null && this.getEnchantmentLevel(shooter.getHeldItem()) > 0) {
				shooterToHomingValue.put(shooter.getPersistentID(), getEnchantmentLevel(shooter.getHeldItem()));
				World world = arrow.worldObj;
				List<EntityLivingBase> livingEntities = world.getEntities(EntityLivingBase.class, IEntitySelector.selectAnything);
				Optional<EntityLivingBase> optionalTarget = livingEntities.stream().filter(entity1 -> entity1.getPersistentID().equals(arrowToTarget.get(arrowID))).findFirst();
				EntityLivingBase target = optionalTarget.isPresent() ? optionalTarget.get() : null;
				if (target == null || target.velocityChanged || !target.canEntityBeSeen(arrow) || target.isDead) {
					double distance = Math.pow(2, shooterToHomingValue.get(shooter.getUniqueID())) * 32;
					EntityLivingBase newTarget = target;
					for (EntityLivingBase livingEntity : livingEntities) {
						double distanceToArrow = livingEntity.getDistanceToEntity(arrow);
						if (distanceToArrow < distance && livingEntity.canEntityBeSeen(arrow) && !livingEntity.getPersistentID().equals(shooter.getPersistentID())) {
							distance = distanceToArrow;
							newTarget = livingEntity;
						}
					}
					target = newTarget;
					arrowToTarget.put(arrowID, newTarget == null ? null : newTarget.getPersistentID());
				}
				if (target != null) {
					double x = target.posX - arrow.posX;
					double y = target.getEntityBoundingBox().minY + target.height / 2 - (arrow.posY + arrow.height / 2);
					double z = target.posZ - arrow.posZ;
					arrow.setThrowableHeading(x, y, z, (float) shooterToHomingValue.get(shooter.getUniqueID()) / 2, 0);
					arrowToTarget.remove(arrowID);
				}
			}
		}
	}

	@Override
	public int getMinimumEnchantability(int enchantmentLevel) {
		return (enchantmentLevel - 1) * 10 + 10;
	}

	@Override
	public int getMaximumEnchantability(int enchantmentLevel) {
		return enchantmentLevel * 10 + 51;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return super.canApplyTogether(enchantment) && !Optional.of(enchantment).equals(CppEnchantments.getByName("quickdraw"));
	}
}
