package io.github.anon10w1z.craftPP.coremod;

import io.github.anon10w1z.craftPP.main.CppModInfo;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * The class transformer for Craft++
 */
public class CppClassTransformer implements IClassTransformer {
	private static final String DELEGATE_CLASS_NAME = CppModInfo.PACKAGE_LOCATION.replace('.', '/') + "/coremod/CppBlockDelegate";

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
		String targetMethodName = obfuscated ? "func_176213_c" : "onBlockAdded";
		String targetMethodName1 = obfuscated ? "func_176204_a" : "onNeighborBlockChange";
		String targetMethodName2 = obfuscated ? "func_180650_b" : "updateTick";
		String targetMethodName3 = obfuscated ? "func_149738_a" : "tickRate";
		String delegateMethodDescriptor = "(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/Block;)V";
		String delegateMethodDescriptor1 = "(Lnet/minecraft/block/Block;)I";
		for (MethodNode methodNode : classNode.methods) {
			String methodName = methodNode.name;
			InsnList methodInstructions = methodNode.instructions;
			if (methodName.equals(targetMethodName)) {
				System.out.println("Patching onBlockAdded");
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
			} else if (methodName.equals(targetMethodName1)) {
				System.out.println("Patching onNeighborBlockChange");
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "scheduleBlockUpdate", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
			} else if (methodName.equals(targetMethodName2)) {
				System.out.println("Patching updateTick");
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 1));
				injectionList.add(new VarInsnNode(ALOAD, 2));
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "onTick", delegateMethodDescriptor, false));
				methodInstructions.insert(injectionList);
			} else if (methodName.equals(targetMethodName3)) {
				System.out.println("Patching tickRate");
				InsnList injectionList = new InsnList();
				injectionList.add(new VarInsnNode(ALOAD, 0));
				injectionList.add(new MethodInsnNode(INVOKESTATIC, DELEGATE_CLASS_NAME, "getTickRate", delegateMethodDescriptor1, false));
				injectionList.add(new InsnNode(IRETURN));
				methodInstructions.insert(injectionList);
			}
		}
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
