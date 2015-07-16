package io.github.anon10w1z.craftPP.coremod;

import io.github.anon10w1z.craftPP.handlers.CppConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * The class transformer for Craft++
 */
@IFMLLoadingPlugin.MCVersion("1.8")
public class CppClassTransformer implements IClassTransformer {
	/**
	 * Schedules a block update if the block is a Craft++ falling blocks
	 *
	 * @param world    The world
	 * @param blockPos The block position
	 * @param block    The block
	 */
	@SuppressWarnings("unused")
	public static void scheduleBlockUpdate(World world, BlockPos blockPos, Block block) {
		if (CppConfigHandler.additionalFallingBlocks.contains(block))
			world.scheduleUpdate(blockPos, block, block.tickRate(world));
	}

	/**
	 * Called every tick on every ticking block in the world (edited to make Craft++'s falling blocks fall)
	 *
	 * @param world    The world
	 * @param blockPos The block position
	 * @param block    The block
	 */
	@SuppressWarnings("unused")
	public static void onTick(World world, BlockPos blockPos, Block block) {
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

	/**
	 * Gets the tick rate for the block (edited to tick Craft++'s falling blocks 10 times a second instead of 2)
	 *
	 * @param block The block
	 * @return The tick rate of the block
	 */
	@SuppressWarnings("unused")
	public static int tickRate(Block block) {
		if (CppConfigHandler.additionalFallingBlocks.contains(block))
			return 2;
		return 10;
	}

	@Override
	public byte[] transform(String className, String string, byte[] bytes) {
		if (className.equals("atr"))
			return transformBlock(bytes, true);
		if (className.equals("net.minecraft.block.Block"))
			return transformBlock(bytes, false);
		return bytes;
	}

	/**
	 * Modifies the block class to allow Craft++'s falling blocks to fall
	 *
	 * @param bytes      The bytes of the block class
	 * @param obfuscated Whether or not we are in an obfuscated environment
	 * @return The modified bytes of the block class
	 */
	private byte[] transformBlock(byte[] bytes, boolean obfuscated) {
		String targetMethodName = obfuscated ? "func_176213_c" : "onBlockAdded";
		String targetMethodName2 = obfuscated ? "func_176204_a" : "onNeighborBlockChange";
		String targetMethodName3 = obfuscated ? "func_180650_b" : "updateTick";
		String targetMethodName4 = obfuscated ? "func_149738_a" : "tickRate";
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		for (MethodNode methodNode : classNode.methods)
			if (methodNode.name.equals(targetMethodName) || methodNode.name.equals(targetMethodName2)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getClassName(), "scheduleBlockUpdate", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)V", false));
				methodNode.instructions.insert(injectionList);

			} else if (methodNode.name.equals(targetMethodName3)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getClassName(), "onTick", "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)V", false));
				methodNode.instructions.insert(injectionList);
			} else if (methodNode.name.equals(targetMethodName4)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getClassName(), "tickRate", "(Lnet/minecraft/block/Block;)I", false));
				injectionList.add(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(injectionList);
			}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private String getClassName() {
		return this.getClass().getName().replace('.', '/');
	}
}
