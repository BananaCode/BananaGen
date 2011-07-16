package net.llamaslayers.minecraft.banana.gen;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class MountainGenerator extends BananaChunkGenerator {
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return !world.getHighestBlockAt(x, z).isLiquid();
	}

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		NoiseGenerator noise = new SimplexNoiseGenerator(world);
		NoiseGenerator noise2 = new SimplexNoiseGenerator(
				world.getSeed() + 10163);
		NoiseGenerator noise3 = new SimplexNoiseGenerator(world.getSeed() + 2);

		chunkX <<= 4;
		chunkZ <<= 4;

		byte[] b = new byte[32768];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int level = 0;
				for (int y = (int) (25 + Math.pow(96, noise.noise(
						(x + chunkX) / 128.0, (z + chunkZ) / 128.0, 3)) + noise3
						.noise((x + chunkX) / 64.0, (z + chunkZ) / 64.0, 4,
								0.7, 0.5, true) * 3); y > 0; y--) {
					double terrainType = noise2.noise((x + chunkX) / 128.0,
							(z + chunkZ) / 128.0, 2, 0.5, 0.5, true)
							* 5
							+ y
							+ random.nextDouble() * 10 - 5;
					Material ground = Material.DIRT;
					if (terrainType > 30 && level < random.nextInt(3) + 2) {
						ground = Material.COBBLESTONE;
					}
					if (terrainType > 50 && level < random.nextInt(3) + 3) {
						ground = Material.STONE;
					}
					if (terrainType > 85 && level == 0) {
						ground = Material.SNOW;
					}
					if (ground == Material.DIRT && level == 0) {
						ground = Material.GRASS;
					}
					b[x * 2048 + z * 128 + y] = (byte) ground.getId();
					level++;
				}
				b[x * 2048 + z * 128] = (byte) Material.BEDROCK.getId();
			}
		}

		return b;
	}
}
