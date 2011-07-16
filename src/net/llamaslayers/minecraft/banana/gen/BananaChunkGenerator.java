package net.llamaslayers.minecraft.banana.gen;

import java.util.*;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public abstract class BananaChunkGenerator extends ChunkGenerator {
	private final Map<String, Map<String, String>> worldArgs = new HashMap<String, Map<String, String>>();

	public final void setWorldArgs(String world, String[] args) {
		Map<String, String> parsedArgs = new HashMap<String, String>();
		worldArgs.put(world, parsedArgs);
		for (String arg : args) {
			if (arg.indexOf('=') > -1) {
				parsedArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=')));
			} else {
				parsedArgs.put(arg, "");
			}
		}
	}

	public final String getArgString(World world, String arg, String def) {
		checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		return worldArgs.get(world.getName()).get(arg);
	}

	public final int getArgInt(World world, String arg, int def) {
		checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		try {
			return Integer.parseInt(worldArgs.get(world.getName()).get(arg));
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	public final int getArgInt(World world, String arg, int def, int min,
		int max) {
		return Math.min(Math.max(getArgInt(world, arg, def), min), max);
	}

	public final double getArgDouble(World world, String arg, double def) {
		checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		try {
			return Double.parseDouble(worldArgs.get(world.getName()).get(arg));
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	public final double getArgDouble(World world, String arg, double def,
		double min,
		double max) {
		return Math.min(Math.max(getArgDouble(world, arg, def), min), max);
	}

	public final boolean getArg(World world, String arg) {
		checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return false;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return false;
		return true;
	}

	private Set<String> args = null;

	private void checkArg(String arg) {
		if (args != null) {
			if (!args.contains(arg)) {
				RuntimeException ex = new RuntimeException("Argument " + arg
						+ " is not declared!");
				ex.fillInStackTrace();
				throw ex;
			}
			return;
		}
		Args allowed = getClass().getAnnotation(Args.class);
		if (allowed == null || allowed.value().length == 0) {
			args = Collections.emptySet();

			RuntimeException ex = new RuntimeException("Argument " + arg
					+ " is not declared!");
			ex.fillInStackTrace();
			throw ex;
		}
		args = new HashSet<String>();
		for (String allowedArg : allowed.value()) {
			args.add(allowedArg);
		}

		if (!args.contains(arg)) {
			RuntimeException ex = new RuntimeException("Argument " + arg
					+ " is not declared!");
			ex.fillInStackTrace();
			throw ex;
		}
	}
}
