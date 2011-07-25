package net.llamaslayers.minecraft.banana.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

/**
 * @author Nightgunner5
 */
public abstract class BananaBlockPopulator extends BlockPopulator implements MaterialIds {
	private final Map<World, BananaChunkGenerator> generators = new HashMap<World, BananaChunkGenerator>();
	private BananaChunkGenerator defgen = null;

	/**
	 * @param generator
	 *            The generator to use if
	 *            {@link BananaBlockPopulator#populate(BananaChunkGenerator, World, Random, Chunk)}
	 *            is not used
	 * @return this object
	 */
	public BlockPopulator setDefault(BananaChunkGenerator generator) {
		defgen = generator;
		return this;
	}

	/**
	 * @see BlockPopulator#populate(World, Random, Chunk)
	 * @param generator
	 *            The generator to use to get args
	 * @param world
	 *            The world to generate in
	 * @param random
	 *            The random generator to use
	 * @param source
	 *            The chunk to generate for
	 */
	public void populate(BananaChunkGenerator generator, World world,
		Random random, Chunk source) {
		generators.put(world, generator);
		populate(world, random, source);
	}

	private BananaChunkGenerator getGen(World world) {
		if (generators.containsKey(world))
			return generators.get(world);
		return defgen;
	}

	/**
	 * @see BananaChunkGenerator#getArgString(World, String, String)
	 */
	@SuppressWarnings("javadoc")
	public final String getArgString(World world, String arg, String def) {
		return getGen(world).getArgString(world, arg, def);
	}

	/**
	 * @see BananaChunkGenerator#getArgInt(World, String, int)
	 */
	@SuppressWarnings("javadoc")
	public final int getArgInt(World world, String arg, int def) {
		return getGen(world).getArgInt(world, arg, def);
	}

	/**
	 * @see BananaChunkGenerator#getArgInt(World, String, int, int, int)
	 */
	@SuppressWarnings("javadoc")
	public final int getArgInt(World world, String arg, int def, int min,
		int max) {
		return getGen(world).getArgInt(world, arg, def, min, max);
	}

	/**
	 * @see BananaChunkGenerator#getArgDouble(World, String, double)
	 */
	@SuppressWarnings("javadoc")
	public final double getArgDouble(World world, String arg, double def) {
		return getGen(world).getArgDouble(world, arg, def);
	}

	/**
	 * @see BananaChunkGenerator#getArgDouble(World, String, double, double,
	 *      double)
	 */
	@SuppressWarnings("javadoc")
	public final double getArgDouble(World world, String arg, double def,
		double min, double max) {
		return getGen(world).getArgDouble(world, arg, def, min, max);
	}

	/**
	 * @see BananaChunkGenerator#getArg(World, String)
	 */
	@SuppressWarnings("javadoc")
	public final boolean getArg(World world, String arg) {
		return getGen(world).getArg(world, arg);
	}

	/**
	 * Sets a block to a specified material, but only if the block was
	 * previously air.
	 *
	 * @param world
	 *            The world in which to set the block
	 * @param x
	 *            The x coordinate of the block
	 * @param y
	 *            The y coordinate of the block
	 * @param z
	 *            The z coordinate of the block
	 * @param type
	 *            The type to set the block to
	 * @return true if the block was set, false if a precondition failed
	 */
	protected static boolean setBlock(World world, int x, int y, int z,
		Material type) {
		Block block = world.getBlockAt(x, y, z);
		if (block.getType() != Material.AIR)
			return false;
		block.setType(type);
		return true;
	}

	/**
	 * Sets a block to a specified material, but only if the block was
	 * previously air.
	 *
	 * @param world
	 *            The world in which to set the block
	 * @param x
	 *            The x coordinate of the block
	 * @param y
	 *            The y coordinate of the block
	 * @param z
	 *            The z coordinate of the block
	 * @param type
	 *            The type to set the block to
	 * @param data
	 *            The data value to set on the block
	 * @return true if the block was set, false if a precondition failed
	 */
	protected static boolean setBlock(World world, int x, int y, int z,
		Material type, byte data) {
		Block block = world.getBlockAt(x, y, z);
		if (block.getType() != Material.AIR)
			return false;
		block.setTypeIdAndData(type.getId(), data, true);
		return true;
	}
}
