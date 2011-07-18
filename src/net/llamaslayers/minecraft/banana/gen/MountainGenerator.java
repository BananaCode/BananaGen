package net.llamaslayers.minecraft.banana.gen;

import java.util.*;

import net.llamaslayers.minecraft.banana.gen.populators.BoulderPopulator;
import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.CavePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.FlowerPopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether", "boulder_chance", "boulder_smallest",
		"boulder_largest", "boulder_lowest", "boulder_highest" })
public class MountainGenerator extends BananaChunkGenerator {
	private final List<BlockPopulator> populators = Arrays.asList(
			new BoulderPopulator().setDefault(this),
			new OrePopulator().setDefault(this),
			new CavePopulator().setDefault(this),
			new FlowerPopulator().setDefault(this));

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

			OctaveGenerator gen = new SimplexOctaveGenerator(seed, 1);
			gen.setScale(1 / 128.0);
			octaves.put("terrainHeight", gen);

			gen = new SimplexOctaveGenerator(seed, 2);
			gen.setScale(1 / 128.0);
			octaves.put("terrainType", gen);

			gen = new SimplexOctaveGenerator(seed, 4);
			gen.setScale(1 / 64.0);
			octaves.put("terrainJitter", gen);

			setWorldOctaves(world, octaves);
		}
		OctaveGenerator noiseTerrainHeight = octaves.get("terrainHeight");
		OctaveGenerator noiseTerrainType = octaves.get("terrainType");
		OctaveGenerator noiseTerrainJitter = octaves.get("terrainJitter");

		chunkX <<= 4;
		chunkZ <<= 4;

		byte[] b = new byte[32768];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int deep = 0;
				for (int y = (int) (25 + Math.pow(96, noiseTerrainHeight.noise(
						x + chunkX, z + chunkZ, 0.5, 0.5, true)) + noiseTerrainJitter
						.noise(x + chunkX, z + chunkZ, 0.7, 0.5, true) * 3); y > 0; y--) {
					double terrainType = noiseTerrainType.noise(x + chunkX, z
							+ chunkZ, 0.5, 0.5, true)
							* 5 + y + random.nextDouble() * 10 - 5;
					Material ground = Material.DIRT;
					if (terrainType > 30 && deep < random.nextInt(3) + 2) {
						ground = Material.COBBLESTONE;
					}
					if (terrainType > 50 && deep < random.nextInt(3) + 3) {
						ground = Material.STONE;
					}
					if (terrainType > 85 && deep == 0) {
						ground = Material.SNOW;
					}
					if (ground == Material.DIRT && deep == 0) {
						ground = Material.GRASS;
					}
					b[x * 2048 + z * 128 + y] = (byte) ground.getId();
					deep++;
				}
				b[x * 2048 + z * 128] = (byte) Material.BEDROCK.getId();
			}
		}

		return b;
	}
}
