package net.llamaslayers.minecraft.banana.gen.generators;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.Args;
import net.llamaslayers.minecraft.banana.gen.BananaChunkGenerator;
import net.llamaslayers.minecraft.banana.gen.populators.BoulderPopulator;
import net.llamaslayers.minecraft.banana.gen.populators.CavePopulator;
import net.llamaslayers.minecraft.banana.gen.populators.FlowerPopulator;
import net.llamaslayers.minecraft.banana.gen.populators.OrePopulator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

/**
 * @author Nightgunner5
 */
@Args({ "nopopulate", "nether", "boulder_chance", "boulder_smallest",
		"boulder_largest", "boulder_lowest", "boulder_highest" })
public class MountainGenerator extends BananaChunkGenerator {
	{
		populators = Arrays.asList(
				new BoulderPopulator().setDefault(this),
				new OrePopulator().setDefault(this),
				new CavePopulator().setDefault(this),
				new FlowerPopulator().setDefault(this)
				);
	}

	/**
	 * @see org.bukkit.generator.ChunkGenerator#generate(org.bukkit.World,
	 *      java.util.Random, int, int)
	 */
	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
		OctaveGenerator noiseTerrainHeight = octaves.get("terrainHeight");
		OctaveGenerator noiseTerrainType = octaves.get("terrainType");
		OctaveGenerator noiseTerrainJitter = octaves.get("terrainJitter");

		chunkX <<= 4;
		chunkZ <<= 4;

		byte[] b = new byte[272 * 128];

		byte dirt = (byte) Material.DIRT.getId();
		byte cobblestone = (byte) Material.COBBLESTONE.getId();
		byte stone = (byte) Material.STONE.getId();
		byte snow = (byte) Material.SNOW.getId();
		byte grass = (byte) Material.GRASS.getId();
		byte bedrock = (byte) Material.BEDROCK.getId();

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int deep = 0;
				for (int y = (int) (25 + Math.pow(96, noiseTerrainHeight.noise(
						x + chunkX, z + chunkZ, 0.5, 0.5, true)) + noiseTerrainJitter
						.noise(x + chunkX, z + chunkZ, 0.7, 0.5, true) * 3); y > 0; y--) {
					double terrainType = noiseTerrainType.noise(x + chunkX, z
							+ chunkZ, 0.5, 0.5, true)
							* 5 + y + random.nextDouble() * 10 - 5;
					byte ground = dirt;
					if (terrainType > 30 && deep < random.nextInt(3) + 2) {
						ground = cobblestone;
					}
					if (terrainType > 50 && deep < random.nextInt(3) + 3) {
						ground = stone;
					}
					if (terrainType > 85 && deep == 0) {
						ground = snow;
					}
					if (ground == dirt && deep == 0) {
						ground = grass;
					}
					b[x * 2048 + z * 128 + y] = ground;
					deep++;
				}
				b[x * 2048 + z * 128] = bedrock;
			}
		}

		return b;
	}

	/**
	 * @see net.llamaslayers.minecraft.banana.gen.BananaChunkGenerator#createWorldOctaves(org.bukkit.World,
	 *      java.util.Map)
	 */
	@Override
	protected void createWorldOctaves(World world,
		Map<String, OctaveGenerator> octaves) {
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
	}
}
