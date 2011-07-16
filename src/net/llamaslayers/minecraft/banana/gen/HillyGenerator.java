package net.llamaslayers.minecraft.banana.gen;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators.MetaPopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class HillyGenerator extends BananaChunkGenerator {
	private final List<BlockPopulator> populators = Collections.singletonList((BlockPopulator) new MetaPopulator(this));

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return populators;
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return !world.getHighestBlockAt(x, z).isLiquid();
	}

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		NoiseGenerator noise = new SimplexNoiseGenerator(world);
		NoiseGenerator noise2 = new SimplexNoiseGenerator(
				world.getSeed() + 10163);

		chunkX <<= 4;
		chunkZ <<= 4;

		Material matMain = getArg(world, "nether") ?
				Material.NETHERRACK : Material.DIRT;
		Material matShore = getArg(world, "nether") ?
				Material.SOUL_SAND : Material.SAND;
		Material matShore2 = Material.GRAVEL;
		Material matTop = getArg(world, "nether") ?
				Material.NETHERRACK : Material.GRASS;
		Material matUnder = getArg(world, "nether") ?
				Material.NETHERRACK : Material.STONE;
		Material matLiquid = getArg(world, "nether") ?
				Material.STATIONARY_LAVA : Material.STATIONARY_WATER;

		byte[] b = new byte[32768];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int deep = 0;
				for (int y = (int) Math.min(getArgInt(world, "baseheight", 70, 0, 127)
						+ noise.noise((x + chunkX)
								/ getArgDouble(world, "tscale", 64.0),
								(z + chunkZ)
										/ getArgDouble(world, "tscale", 64.0), 4, 0.7, 0.6, true)
						* getArgDouble(world, "terrainheight", 16), 127); y > 0; y--) {
					double terrainType = noise2.noise((x + chunkX) / 128.0,
							y / 128.0, (z + chunkZ) / 128.0, 2, 0.5, 0.5, true);
					Material ground = matTop;
					if (Math.abs(terrainType) < random.nextDouble() / 3) {
						ground = matMain;
					} else if (deep != 0) {
						ground = matMain;
					}

					if (Math.abs(y - getArgInt(world, "waterlevel", 64, 0, 127)) < 5) {
						if (terrainType < random.nextDouble() / 2) {
							if (terrainType < -random.nextDouble() / 6) {
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
