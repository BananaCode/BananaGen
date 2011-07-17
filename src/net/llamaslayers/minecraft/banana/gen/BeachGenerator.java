/**
 * 
 */
package net.llamaslayers.minecraft.banana.gen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.CavePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.DesertPopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * A generator that makes beach-like worlds, suggested in
 * {@link "http://forums.bukkit.org/threads/req-ocean-island-biome-terrain-generation.23014/"}
 * 
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether" })
public class BeachGenerator extends BananaChunkGenerator {
	private final List<BlockPopulator> populators = Arrays.asList(new CavePopulator().setDefault(this), new DesertPopulator().setDefault(this), new OrePopulator().setDefault(this));

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
		NoiseGenerator noise = new SimplexNoiseGenerator(world);
		NoiseGenerator noise2 = new SimplexNoiseGenerator(
				world.getSeed() + 10163);

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
				double _y = noise.noise((x + chunkX) / 128.0, (z + chunkZ) / 128.0, 16, 0.5, 0.5, true) + 1;
				for (int y = 56 + (int) (_y * _y * 4); y > 0; y--) {
					if (deep < noise2.noise((x + chunkX) / 64.0, (z + chunkZ) / 64.0, 8, 0.5, 0.5, true) * 3 + 5) {
						b[x * 2048 + z * 128 + y] = (byte) (getArg(world, "nether") ? Material.SOUL_SAND
								: Material.SAND).getId();
					} else if (deep < noise2.noise((x + chunkX) / 32.0, (z + chunkZ) / 32.0, 8, 0.5, 0.5, true) * 4 + 7) {
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
