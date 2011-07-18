/**
 * 
 */
package net.llamaslayers.minecraft.banana.gen;

import java.util.*;

import net.llamaslayers.minecraft.banana.gen.populators.BoatPopulator;
import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.PalmTreePopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * A generator that makes beach-like worlds, as suggested on
 * <a href="http://forums.bukkit.org/threads/23014/">the Bukkit forums</a>
 * 
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether" })
public class BeachGenerator extends BananaChunkGenerator {
	private final List<BlockPopulator> populators = Arrays.asList(
			new OrePopulator().setDefault(this),
			new PalmTreePopulator().setDefault(this),
			new BoatPopulator().setDefault(this));

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

			OctaveGenerator gen = new SimplexOctaveGenerator(seed, 16);
			gen.setScale(1 / 128.0);
			octaves.put("terrainHeight", gen);

			gen = new SimplexOctaveGenerator(seed, 8);
			gen.setScale(1 / 64.0);
			octaves.put("terrainType", gen);

			gen = new SimplexOctaveGenerator(seed, 8);
			gen.setScale(1 / 32.0);
			octaves.put("terrainType2", gen);

			setWorldOctaves(world, octaves);
		}
		OctaveGenerator noiseTerrainHeight = octaves.get("terrainHeight");
		OctaveGenerator noiseTerrainType = octaves.get("terrainType");
		OctaveGenerator noiseTerrainType2 = octaves.get("terrainType2");

		chunkX <<= 4;
		chunkZ <<= 4;

		byte[] b = new byte[32768];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 1; y < 64; y++) {
					b[x * 2048 + z * 128 + y] = (byte) (getArg(world, "nether") ? Material.STATIONARY_LAVA
							: Material.STATIONARY_WATER).getId();
				}
				int deep = 0;

				double _y = noiseTerrainHeight.noise(x + chunkX, z + chunkZ, 0.5, 0.5, true) + 1;
				_y = _y * _y * 4;
				if (_y < 8) {
					_y = _y * _y / 4 - 8;
				}

				for (int y = 56 + (int) _y; y > 0; y--) {
					if (deep < noiseTerrainType.noise(x + chunkX, z + chunkZ, 0.5, 0.5, true) * 3 + 5) {
						b[x * 2048 + z * 128 + y] = (byte) (getArg(world, "nether") ? Material.SOUL_SAND
								: Material.SAND).getId();
					} else if (deep < noiseTerrainType2.noise(x + chunkX, z
							+ chunkZ, 0.5, 0.5, true) * 4 + 7) {
						b[x * 2048 + z * 128 + y] = (byte) (getArg(world, "nether") ? Material.NETHERRACK
								: Material.SANDSTONE).getId();
					} else {
						b[x * 2048 + z * 128 + y] = (byte) (getArg(world, "nether") ? Material.NETHERRACK
								: Material.STONE).getId();
					}
					deep++;
				}
				b[x * 2048 + z * 128] = (byte) Material.BEDROCK.getId();
			}
		}

		return b;
	}
}
