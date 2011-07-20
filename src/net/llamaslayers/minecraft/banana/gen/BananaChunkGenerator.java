package net.llamaslayers.minecraft.banana.gen;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;

import com.google.common.collect.MapMaker;

/**
 * @author Nightgunner5
 */
public abstract class BananaChunkGenerator extends ChunkGenerator {
	/**
	 * Populators to be returned by {@link #getDefaultPopulators(World)}
	 */
	protected List<BlockPopulator> populators;

	/**
	 * @see org.bukkit.generator.ChunkGenerator#getDefaultPopulators(org.bukkit.World)
	 */
	@Override
	public final List<BlockPopulator> getDefaultPopulators(World world) {
		if (world != null && getArg(world, "nopopulate"))
			return Collections.emptyList();
		return populators;
	}

	private final Map<World, Map<String, OctaveGenerator>> octaveCache = new MapMaker().weakKeys().makeMap();

	/**
	 * @param world
	 *            The world to create OctaveGenerators for
	 * @param octaves
	 *            The map to put the OctaveGenerators into
	 */
	protected abstract void createWorldOctaves(World world,
		Map<String, OctaveGenerator> octaves);

	/**
	 * @param world
	 *            The world to look for in the cache
	 * @return A map of {@link OctaveGenerator}s created by
	 *         {@link #createWorldOctaves(World, Map)}
	 */
	protected final Map<String, OctaveGenerator> getWorldOctaves(World world) {
		if (octaveCache.get(world) == null) {
			Map<String, OctaveGenerator> octaves = new HashMap<String, OctaveGenerator>();
			createWorldOctaves(world, octaves);
			octaveCache.put(world, octaves);
			return octaves;
		}
		return octaveCache.get(world);
	}

	private final Map<String, Map<String, String>> worldArgs = new HashMap<String, Map<String, String>>();

	/**
	 * @param world
	 *            args are specified for
	 * @param args
	 *            that are specified for the world
	 */
	public final void setWorldArgs(String world, String[] args) {
		Map<String, String> parsedArgs = new HashMap<String, String>();
		worldArgs.put(world, parsedArgs);
		for (String arg : args) {
			if (arg.indexOf('=') > -1) {
				parsedArgs.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
			} else {
				parsedArgs.put(arg, "");
			}
		}
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @param def
	 *            default value
	 * @return the value of the arg, or def if the arg is not defined
	 */
	public final String getArgString(World world, String arg, String def) {
		//checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		return worldArgs.get(world.getName()).get(arg);
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @param def
	 *            default value
	 * @return the value of the arg, or def if the arg is not defined or is not
	 *         an integer
	 */
	public final int getArgInt(World world, String arg, int def) {
		//checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		try {
			return Integer.parseInt(worldArgs.get(world.getName()).get(arg));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return def;
		}
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @param def
	 *            default value
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @return the value of the arg, or def if the arg is not defined or is not
	 *         an integer
	 */
	public final int getArgInt(World world, String arg, int def, int min,
		int max) {
		return Math.min(Math.max(getArgInt(world, arg, def), min), max);
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @param def
	 *            default value
	 * @return the value of the arg, or def if the arg is not defined or is not
	 *         a double
	 */
	public final double getArgDouble(World world, String arg, double def) {
		//checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return def;
		if (!worldArgs.get(world.getName()).containsKey(arg))
			return def;
		try {
			return Double.parseDouble(worldArgs.get(world.getName()).get(arg));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return def;
		}
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @param def
	 *            default value
	 * @param min
	 *            minimum value
	 * @param max
	 *            maximum value
	 * @return the value of the arg, or def if the arg is not defined or is not
	 *         a double
	 */
	public final double getArgDouble(World world, String arg, double def,
		double min,
		double max) {
		return Math.min(Math.max(getArgDouble(world, arg, def), min), max);
	}

	/**
	 * @param world
	 *            to look for arg in
	 * @param arg
	 *            identifier
	 * @return true if the arg was specified for the world, false if it was not
	 */
	public final boolean getArg(World world, String arg) {
		//checkArg(arg);
		if (!worldArgs.containsKey(world.getName()))
			return false;
		return worldArgs.get(world.getName()).containsKey(arg);
	}

	/*private Set<String> args = null;

	private void checkArg(String arg) throws RuntimeException {
		if (args == null) {
			Args allowed = getClass().getAnnotation(Args.class);
			if (allowed == null || allowed.value().length == 0) {
				RuntimeException ex = new RuntimeException("Argument " + arg
						+ " is not declared in class " + getClass().getName()
						+ "!");
				ex.fillInStackTrace();
				throw ex;
			}
			args = new HashSet<String>();
			for (String allowedArg : allowed.value()) {
				args.add(allowedArg);
			}
		}

		if (!args.contains(arg)) {
			RuntimeException ex = new RuntimeException("Argument " + arg
					+ " is not declared in class " + getClass().getName() + "!");
			ex.fillInStackTrace();
			throw ex;
		}
	}*/

	private static final Set<Material> FORBIDDEN_SPAWN_FLOORS = new HashSet<Material>();
	static {
		// Air and liquids are already accounted for.
		FORBIDDEN_SPAWN_FLOORS.add(Material.FIRE); // That would hurt.
		FORBIDDEN_SPAWN_FLOORS.add(Material.CACTUS); // Ouch!
		FORBIDDEN_SPAWN_FLOORS.add(Material.LEAVES); // Spawning on top of a tree is so 2010s.
	}

	/**
	 * @see org.bukkit.generator.ChunkGenerator#canSpawn(org.bukkit.World, int,
	 *      int)
	 */
	@Override
	public boolean canSpawn(World world, int x, int z) {
		Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
		return !block.isLiquid() && !block.isEmpty()
				&& !FORBIDDEN_SPAWN_FLOORS.contains(block.getType());
	}

	/**
	 * Stop spawning over water or 100 feet from the ground.
	 * 
	 * @see org.bukkit.generator.ChunkGenerator#getFixedSpawnLocation(org.bukkit.World,
	 *      java.util.Random)
	 */
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		int x = -16;
		do {
			x += 16;
			world.loadChunk(x / 16, 0);
		} while (!canSpawn(world, x, 0));

		return new Location(world, x, world.getHighestBlockYAt(x, 0), 0);
	}
}
