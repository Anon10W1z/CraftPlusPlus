package io.github.anon10w1z.craftPP.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * The class transformer for Craft++
 */
@MCVersion("1.8")
@SortingIndex(2000)
public class CppClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String className, String string, byte[] bytes) {
		if (className.equals("atr"))
			return this.transformBlock(bytes, true);
		if (className.equals("net.minecraft.block.Block"))
			return this.transformBlock(bytes, false);
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
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		String targetMethodName = obfuscated ? "c" : "onBlockAdded";
		String targetMethodName2 = obfuscated ? "a" : "onNeighborBlockChange";
		String targetMethodName3 = obfuscated ? "b" : "updateTick";
		String targetMethodName4 = obfuscated ? "a" : "tickRate";
		String delegateMethodDescriptor = obfuscated ? "(Laqu;Ldt;Latr;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)V";
		String delegateMethodDescriptor2 = obfuscated ? "(Latr;)I" : "(Lnet/minecraft/block/Block;)I";
		for (MethodNode methodNode : classNode.methods) {
			if (methodNode.name.equals(targetMethodName) && (!obfuscated || (methodNode.desc.equals("(Laqu;Ldt;Lbec;)V")))) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getDelegationClassName(), "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodNode.instructions.insert(injectionList);
			} else if (methodNode.name.equals(targetMethodName2) && (!obfuscated || (methodNode.desc.equals("(Laqu;Ldt;Lbec;Latr;)V")))) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getDelegationClassName(), "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodNode.instructions.insert(injectionList);
			} else if (methodNode.name.equals(targetMethodName3) && (!obfuscated || methodNode.desc.equals("(Laqu;Ldt;Lbec;Ljava/util/Random;)V"))) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 1));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 2));
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getDelegationClassName(), "onTick", delegateMethodDescriptor, false));
				methodNode.instructions.insert(injectionList);
			} else if (methodNode.name.equals(targetMethodName4) && (!obfuscated || methodNode.desc.equals("(Laqu;)I"))) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				injectionList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, this.getDelegationClassName(), "tickRate", delegateMethodDescriptor2, false));
				injectionList.add(new InsnNode(Opcodes.IRETURN));
				methodNode.instructions.insert(injectionList);
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private String getDelegationClassName() {
		return "io/github/anon10w1z/craftPP/coremod/CppBlockDelegate";
	}
}
