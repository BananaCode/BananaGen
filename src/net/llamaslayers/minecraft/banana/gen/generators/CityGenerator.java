package net.llamaslayers.minecraft.banana.gen.generators;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.llamaslayers.minecraft.banana.gen.Args;
import net.llamaslayers.minecraft.banana.gen.BananaChunkGenerator;
import net.llamaslayers.minecraft.banana.gen.generators.city.CityBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.generators.city.Fountain;
import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.StreetlightPopulator;
import org.bukkit.World;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * @author Nightgunner5
 */
@Args({"nostreetlights", "nether", "noores"})
public class CityGenerator extends BananaChunkGenerator {
	{
		populators = Arrays.asList(
				new StreetlightPopulator().setDefault(this),
				new OrePopulator().setDefault(this));
	}

	@Override
	protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
		Random seed = new Random(world.getSeed());

		OctaveGenerator gen = new SimplexOctaveGenerator(seed, 2);
		octaves.put("roads", gen);
	}

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		int height = 128;
		Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
		OctaveGenerator noiseRoads = octaves.get("roads");

		boolean road1 = noiseRoads.noise(chunkX, chunkZ, 0.5, 0.5) > 0;
		boolean road2 = noiseRoads.noise(chunkX + 1, chunkZ, 0.5, 0.5) > 0;
		boolean road3 = noiseRoads.noise(chunkX, chunkZ + 1, 0.5, 0.5) > 0;
		boolean road4 = noiseRoads.noise(chunkX + 1, chunkZ + 1, 0.5, 0.5) > 0;

		byte[] b = getDefaultChunk(height);

		if (road1) {
			b[(0 * 16 + 0) * height + height / 2] = GRAVEL;
			b[(1 * 16 + 1) * height + height / 2] = STONE;

			if (road2) {
				for (int x = 1; x < 15; x++) {
					b[(x * 16 + 0) * height + height / 2] = GRAVEL;
					b[(x * 16 + 1) * height + height / 2] = STONE;
				}
			} else {
				b[(1 * 16 + 0) * height + height / 2] = STONE;
			}

			if (!road3) {
				b[(0 * 16 + 1) * height + height / 2] = STONE;
			}
		}

		if (road2) {
			b[(15 * 16 + 0) * height + height / 2] = GRAVEL;
			b[(14 * 16 + 1) * height + height / 2] = STONE;

			if (road4) {
				for (int z = 1; z < 15; z++) {
					b[(15 * 16 + z) * height + height / 2] = GRAVEL;
					b[(14 * 16 + z) * height + height / 2] = STONE;
				}
			} else {
				b[(15 * 16 + 1) * height + height / 2] = STONE;
			}

			if (!road1) {
				b[(14 * 16 + 0) * height + height / 2] = STONE;
			}
		}

		if (road3) {
			b[(0 * 16 + 15) * height + height / 2] = GRAVEL;
			b[(1 * 16 + 14) * height + height / 2] = STONE;

			if (road1) {
				for (int z = 1; z < 15; z++) {
					b[(0 * 16 + z) * height + height / 2] = GRAVEL;
					b[(1 * 16 + z) * height + height / 2] = STONE;
				}
			} else {
				b[(0 * 16 + 14) * height + height / 2] = STONE;
			}

			if (!road4) {
				b[(1 * 16 + 15) * height + height / 2] = STONE;
			}
		}

		if (road4) {
			b[(15 * 16 + 15) * height + height / 2] = GRAVEL;
			b[(14 * 16 + 14) * height + height / 2] = STONE;

			if (road3) {
				for (int x = 1; x < 15; x++) {
					b[(x * 16 + 15) * height + height / 2] = GRAVEL;
					b[(x * 16 + 14) * height + height / 2] = STONE;
				}
			} else {
				b[(14 * 16 + 15) * height + height / 2] = STONE;
			}

			if (!road2) {
				b[(15 * 16 + 14) * height + height / 2] = STONE;
			}
		}

		populateCityBlock(chunkX, chunkZ, b, height, octaves, random);

		return b;
	}

	private void populateCityBlock(int chunkX, int chunkZ, byte[] b, int height, Map<String, OctaveGenerator> octaves, Random random) {
		int sizeX, sizeZ, locX, locZ;
		OctaveGenerator noiseRoads = octaves.get("roads");
		boolean[][] blockOpen = new boolean[6][6];

		for (int x = -2; x <= 3; x++) {
			for (int z = -2; z <= 3; z++) {
				blockOpen[x + 2][z + 2] = noiseRoads.noise(chunkX + x, chunkZ +z, 0.5, 0.5) <= 0;
			}
		}

		int[] position = getCityBlockPosition(blockOpen);
		sizeX = position[0];
		sizeZ = position[1];
		locX = position[2];
		locZ = position[3];

		getCityPopulator(sizeX, sizeZ, random).populate(chunkX - locX, chunkZ - locZ, locX, locZ, height, b, random);
	}

	private int[] getCityBlockPosition(boolean[][] blockOpen) {
		int[] position = new int[] {1, 1, 0, 0};

		// TODO: Make a tracing function for city blocks and find the optimal position

		return position;
	}

	private final Map<Integer, CityBlockPopulator[]> cityPopulators = new HashMap<Integer, CityBlockPopulator[]>();

	{
		cityPopulators.put(1 << 8 | 1, new CityBlockPopulator[] {
			new Fountain()
		});
	}

	private CityBlockPopulator getCityPopulator(int sizeX, int sizeZ, Random random) {
		return cityPopulators.get(sizeX << 8 | sizeZ)[random.nextInt(cityPopulators.get(sizeX << 8 | sizeZ).length)];
	}

	private final Map<Integer, SoftReference<byte[]>> defaultCache = new HashMap<Integer, SoftReference<byte[]>>();

	private byte[] getDefaultChunk(int height) {
		SoftReference<byte[]> cached = defaultCache.get(height);
		if (cached != null && cached.get() != null) {
			return cached.get().clone();
		}

		byte[] b = new byte[272 * height];

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				b[(x * 16 + z) * height] = BEDROCK;
				for (int y = 1; y < height / 3; y++) {
					b[(x * 16 + z) * height + y] = STONE;
				}
				for (int y = height / 3; y < height / 2; y++) {
					b[(x * 16 + z) * height + y] = DIRT;
				}
				b[(x * 16 + z) * height + height / 2] = GRASS;
			}
		}
		defaultCache.put(height, new SoftReference<byte[]>(b));
		return b.clone();
	}
}
