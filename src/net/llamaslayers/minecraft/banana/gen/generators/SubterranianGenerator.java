package net.llamaslayers.minecraft.banana.gen.generators;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import net.llamaslayers.minecraft.banana.gen.Args;
import net.llamaslayers.minecraft.banana.gen.BananaChunkGenerator;
import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.TorchPopulator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether", "torch_max", "torch_chance" })
public class SubterranianGenerator extends BananaChunkGenerator {
	{
		populators = Arrays.asList(
				new OrePopulator().setDefault(this),
				new TorchPopulator().setDefault(this));
	}

	@Override
	protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
		Random seed = new Random(world.getSeed());

		OctaveGenerator gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 64.0);
		octaves.put("floor", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 64.0);
		octaves.put("ceiling", gen);

		gen = new SimplexOctaveGenerator(seed, 5);
		gen.setScale(1 / 32.0);
		octaves.put("jitter1", gen);

		gen = new SimplexOctaveGenerator(seed, 5);
		gen.setScale(1 / 32.0);
		octaves.put("jitter2", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 32.0);
		octaves.put("stalactite", gen);

		gen = new SimplexOctaveGenerator(seed, 10);
		gen.setScale(1 / 48.0);
		octaves.put("stalagmite", gen);
	}

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
		OctaveGenerator noiseFloor = octaves.get("floor");
		OctaveGenerator noiseCeiling = octaves.get("ceiling");
		OctaveGenerator noiseJitter1 = octaves.get("jitter1");
		OctaveGenerator noiseJitter2 = octaves.get("jitter2");
		OctaveGenerator noiseStalactite = octaves.get("stalactite");
		OctaveGenerator noiseStalagmite = octaves.get("stalagmite");

		chunkX <<= 4;
		chunkZ <<= 4;

		byte air = (byte) Material.AIR.getId();
		byte bedrock = (byte) Material.BEDROCK.getId();
		int height = world.getMaxHeight();

		byte[] b = new byte[272 * height];
		Arrays.fill(b, (byte) Material.STONE.getId());

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int min = (int) (Math.abs(noiseFloor.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 3)
						+ 3 + noiseJitter1.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 2
						+ Math.max(noiseStalagmite.noise(x + chunkX, z + chunkZ, 0.5, 0.5)
						* height - height / 2, 0));
				int max = (int) (height - Math.abs(noiseCeiling.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 3)
						- 3 + noiseJitter2.noise(x + chunkX, z + chunkZ, 0.5, 0.5) * 2
						- Math.max(noiseStalactite.noise(x + chunkX, z + chunkZ, 0.5, 0.5)
						* height - height / 2, 0));

				if (min >= max) {
					b[(x * 16 + z) * height] = bedrock;
					b[(x * 16 + z) * height + height - 1] = bedrock;
					continue;
				}

				for (int y = min; y <= max; y++) {
					b[(x * 16 + z) * height + y] = air;
				}

				b[(x * 16 + z) * height] = bedrock;
				b[(x * 16 + z) * height + height - 1] = bedrock;
			}
		}

		return b;
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		while (true) {
			int x = random.nextInt(128) - 64;
			int y = world.getMaxHeight() / 2;
			int z = random.nextInt(128) - 64;

			if (world.getBlockAt(x, y, z).isEmpty()) {
				while (world.getBlockAt(x, y - 1, z).isEmpty() && y > 0) {
					y--;
				}
				return new Location(world, x, y, z);
			}
		}
	}
}
