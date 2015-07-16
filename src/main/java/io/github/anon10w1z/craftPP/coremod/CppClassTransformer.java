package io.github.anon10w1z.craftPP.coremod;

import net.minecraft.launchwrapper.IClassTransformer;

public class CppClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String className, String string, byte[] bytes) {
		if (className.equals("atr"))
			return transformBlock(bytes, true);
		if (className.equals("net.minecraft.block.Block"))
			return transformBlock(bytes, false);
		if (className.equals("z"))
			return transformCommandBase(bytes, true);
		if (className.equals("net.minecraft.command.CommandBase"))
			return transformCommandBase(bytes, false);
		return new byte[0];
	}

	private byte[] transformBlock(byte[] bytes, boolean obfuscated) {
		return bytes;
	}

	private byte[] transformCommandBase(byte[] bytes, boolean obfuscated) {
		return bytes;
	}
}
