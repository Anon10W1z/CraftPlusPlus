package io.github.anon10w1z.cpp.coremod;

import io.github.anon10w1z.cpp.main.CppModInfo;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * The class transformer for Craft++
 */
public class CppClassTransformer implements IClassTransformer {
	/**
	 * The class name to delegate the methods to
	 */
	private static final String DELEGATE_CLASS_NAME = CppModInfo.PACKAGE_LOCATION.replace('.', '/') + "/coremod/CppBlockDelegate";

	/**
	 * Returns whether or not the given class name represents a vanilla block class
	 *
	 * @param className The class name
	 * @return Whether or not the class name represents a vanilla block class
	 */
	private static boolean isVanillaBlockClass(String className) {
		return className != null && (className.startsWith("net.minecraft.block.Block") || className.startsWith("net/minecraft/block/Block")) && !className.contains("$") && !className.endsWith("EventData");
	}

	@Override
	public byte[] transform(String className, String deobfuscatedClassName, byte[] bytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		if (isVanillaBlockClass(deobfuscatedClassName) || isVanillaBlockClass(classNode.superName))
			return this.transformBlock(bytes, !className.equals(deobfuscatedClassName), deobfuscatedClassName);
		return bytes;
	}

	/**
	 * Modifies the block class to allow Craft++'s falling blocks to fall
	 *
	 * @param bytes      The bytes of the block class
	 * @param obfuscated Whether or not we are in an obfuscated environment
	 * @return The modified bytes of the block class
	 */
	private byte[] transformBlock(byte[] bytes, boolean obfuscated, String deobfuscatedClassName) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		String targetMethodName = obfuscated ? "c" : "onBlockAdded";
		String targetMethodName1 = obfuscated ? "a" : "onNeighborBlockChange";
		String targetMethodName2 = obfuscated ? "b" : "updateTick";
		String targetMethodName3 = obfuscated ? "a" : "tickRate";

		String targetMethodDescriptor = obfuscated ? "(Laqu;Ldt;Lbec;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)V";
		String targetMethodDescriptor1 = obfuscated ? "(Laqu;Ldt;Lbec;Latr;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/Block;)V";
		String targetMethodDescriptor2 = obfuscated ? "(Laqu;Ldt;Lbec;Ljava/util/Random;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";
		String targetMethodDescriptor3 = obfuscated ? "(Laqu;)I" : "(Lnet/minecraft/world/World;)I";

		String delegateMethodDescriptor = obfuscated ? "(Laqu;Ldt;Latr;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)V";
		String delegateMethodDescriptor1 = obfuscated ? "(Latr;)I" : "(Lnet/minecraft/block/Block;)I";
		int patchCount = 0;
		for (MethodNode methodNode : classNode.methods) {
			String methodName = methodNode.name;
			String methodDescriptor = methodNode.desc;
			InsnList methodInstructions = methodNode.instructions;
			if (methodName.equals(targetMethodName) && methodDescriptor.equals(targetMethodDescriptor)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
				++patchCount;
			}
			if (methodName.equals(targetMethodName1) && methodDescriptor.equals(targetMethodDescriptor1)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
				++patchCount;
			}
			if (methodName.equals(targetMethodName2) && methodDescriptor.equals(targetMethodDescriptor2)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "onUpdateTick", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
				++patchCount;
			}
			if (methodName.equals(targetMethodName3) && methodDescriptor.equals(targetMethodDescriptor3)) {
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "getTickRate", delegateMethodDescriptor1, false));
				injectionList.add(new InsnNode(IRETURN));
				methodInstructions.insert(injectionList);
				methodInstructions.insert(injectionList);
				++patchCount;
			}
		}
		if (patchCount == 0)
			return bytes;
		System.out.println("Patched " + patchCount + " method" + (patchCount != 1 ? "s" : "") + " in class " + deobfuscatedClassName);
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}
}
