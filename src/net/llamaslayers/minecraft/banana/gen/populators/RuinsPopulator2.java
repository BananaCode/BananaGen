package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Builds ruins. Also great for stalagmite.
 *
 * @author Nightgunner5
 */
public class RuinsPopulator2 extends BananaBlockPopulator {
	private static final int RUINS_CHANCE = 25;
	private static final int MIN_CLUMP_SIZE = 2;
	private static final int MAX_CLUMP_SIZE = 5;

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (getArg(world, "noruins2"))
			return;

		byte material;
		try {
			material = (byte) Material.valueOf(getArgString(world, "ruins2material", "COBBLESTONE")).getId();
		} catch (IllegalArgumentException ex) {
			material = COBBLESTONE;
			// TODO: complain?
		}

		if (random.nextInt(100) < RUINS_CHANCE) {
			int count = random.nextInt(MAX_CLUMP_SIZE - MIN_CLUMP_SIZE + 1) + MIN_CLUMP_SIZE;
			int startX = random.nextInt(16) + source.getX() * 16;
			int startZ = random.nextInt(16) + source.getZ() * 16;

			Block[] seeds = new Block[count];
			for (int i = 0; i < count; i++) {
				int x = startX + random.nextInt(5) - random.nextInt(5);
				int z = startZ + random.nextInt(5) - random.nextInt(5);
				seeds[i] = world.getBlockAt(x, world.getHighestBlockYAt(x, z), z);
			}
			for (Block seed : seeds)
				buildRuin(seed, random, material);
		}
	}

	private static void buildRuin(Block start, Random random, byte material) {
		if (start.isEmpty())
			start = start.getRelative(BlockFace.DOWN);
		int radius = random.nextInt(3) + 1;
		int r2 = radius * radius;
		int height = random.nextInt(5) + 3;
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				for (int y = height * (r2 - (x * x + z * z)) / r2 + random.nextInt(3); y >= 0; y--) {
					start.getRelative(x, y, z).setTypeId(material);
				}
			}
		}
	}
}
