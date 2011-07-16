package net.llamaslayers.minecraft.banana.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public abstract class BananaBlockPopulator extends BlockPopulator {
	private final Map<World, BananaChunkGenerator> generators = new HashMap<World, BananaChunkGenerator>();

	public void populate(BananaChunkGenerator generator, World world,
		Random random, Chunk source) {
		generators.put(world, generator);
		populate(world, random, source);
		generators.remove(world);
	}

	public final String getArgString(World world, String arg, String def) {
		return generators.get(world).getArgString(world, arg, def);
	}

	public final int getArgInt(World world, String arg, int def) {
		return generators.get(world).getArgInt(world, arg, def);
	}

	public final int getArgInt(World world, String arg, int def, int min,
		int max) {
		return generators.get(world).getArgInt(world, arg, def, min, max);
	}

	public final double getArgDouble(World world, String arg, double def) {
		return generators.get(world).getArgDouble(world, arg, def);
	}

	public final boolean getArg(World world, String arg) {
		return generators.get(world).getArg(world, arg);
	}
}
