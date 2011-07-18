package net.llamaslayers.minecraft.banana.gen;

import java.util.*;

import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.MetaPopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * Basic generator with lots of hills
 * 
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether", "groundcover", "baseheight", "tscale",
		"terrainheight", "nodirt", "waterlevel", "tree_scarcity", "torch_max",
		"torch_chance" })
public class HillyGenerator extends BananaChunkGenerator {
	private final List<BlockPopulator> populators = Collections.singletonList((BlockPopulator) new MetaPopulator(this));

	/**
	 * @see org.bukkit.generator.ChunkGenerator#getDefaultPopulators(org.bukkit.World)
	 */
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		if (world != null && getArg(world, "nopopulate"))
			return Collections.emptyList();
		return populators;
	}

	/**
	 * @see org.bukkit.generator.ChunkGenerator#generate(org.bukkit.World,
	 *      java.util.Random, int, int)
	 */
	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
		if (octaves == null) {
			octaves = new HashMap<String, OctaveGenerator>();
			Random seed = new Random(world.getSeed());

			OctaveGenerator gen = new SimplexOctaveGenerator(seed, 5);
			gen.setScale(1 / getArgDouble(world, "tscale", 64.0));
			octaves.put("terrainHeight", gen);

			gen = new SimplexOctaveGenerator(seed, 2);
			gen.setScale(1 / 128.0);
			octaves.put("terrainType", gen);

			setWorldOctaves(world, octaves);
		}
		OctaveGenerator noiseTerrainHeight = octaves.get("terrainHeight");
		OctaveGenerator noiseTerrainType = octaves.get("terrainType");

		chunkX <<= 4;
		chunkZ <<= 4;

		Material matMain = getArg(world, "nether") ?
				Material.NETHERRACK : Material.DIRT;
		Material matShore = getArg(world, "nether") ?
				Material.SOUL_SAND : Material.SAND;
		Material matShore2 = Material.GRAVEL;
		Material matTop = getArg(world, "nether") ?
				Material.NETHERRACK : Material.GRASS;
		try {
			matTop = Material.valueOf(getArgString(world, "groundcover", ""));
		} catch (IllegalArgumentException ex) {
			// TODO: complain?
		}
		Material matUnder = getArg(world, "nether") ?
				Material.NETHERRACK : Material.STONE;
		Material matLiquid = getArg(world, "nether") ?
				Material.STATIONARY_LAVA : Material.STATIONARY_WATER;

		byte[] b = new byte[32768];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int deep = 0;
				for (int y = (int) Math.min(getArgInt(world, "baseheight", 70, 0, 127)
						+ noiseTerrainHeight.noise(x + chunkX, z + chunkZ, 0.7, 0.6, true)
						* getArgDouble(world, "terrainheight", 16.0), 127); y > 0; y--) {
					double terrainType = noiseTerrainType.noise(x + chunkX, y, z
							+ chunkZ, 0.5, 0.5, true);
					Material ground = matTop;
					if (Math.abs(terrainType) < random.nextDouble() / 3
							&& !getArg(world, "nodirt")) {
						ground = matMain;
					} else if (deep != 0
							|| y < getArgInt(world, "waterlevel", 64, 0, 127)) {
						ground = matMain;
					}

					if (Math.abs(y - getArgInt(world, "waterlevel", 64, 0, 127)) < 5) {
						if (terrainType < random.nextDouble() / 2) {
							if (terrainType < random.nextDouble() / 4) {
								ground = matShore;
							} else {
								ground = matShore2;
							}
						}
					}

					if (deep > random.nextInt(3) + 6) {
						ground = matUnder;
					}

					b[x * 2048 + z * 128 + y] = (byte) ground.getId();
					deep++;
				}
				b[x * 2048 + z * 128] = (byte) Material.BEDROCK.getId();
			}
		}

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < getArgInt(world, "waterlevel", 64, 0, 127); y++) {
					if (b[x * 2048 + z * 128 + y] == 0) {
						b[x * 2048 + z * 128 + y] = (byte) matLiquid.getId();
					}
				}
			}
		}

		return b;
	}
}
