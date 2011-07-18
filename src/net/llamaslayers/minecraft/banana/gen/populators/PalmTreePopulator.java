package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Generate palm trees, like those one would find on a
 * {@link net.llamaslayers.minecraft.banana.gen.generators.BeachGenerator beach}
 * .
 * 
 * @author codename_B
 */
public class PalmTreePopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (random.nextInt(100) < 9) {
			int x = source.getX() * 16 + random.nextInt(16);
			int z = source.getZ() * 16 + random.nextInt(16);
			int y = world.getHighestBlockYAt(x, z);
			createTree(world, random, x, y, z);
		}
	}

	private static void createTree(World world, Random random, int x, int y,
		int z) {
		if (world.getBlockTypeIdAt(x, y - 1, z) != Material.SAND.getId())
			return;

		int height = y + random.nextInt(3) + 4;
		for (int i = y; i <= height; i++) {
			setBlock(world, x, i, z, Material.LOG);
		}
		for (int i = 1; i < 5; i++) {
			setBlock(world, x - i, height, z, Material.LEAVES);
			setBlock(world, x + i, height, z, Material.LEAVES);
			setBlock(world, x, height, z + i, Material.LEAVES);
			setBlock(world, x, height, z - i, Material.LEAVES);
			if (i == 4) {
				setBlock(world, x - i, height - 1, z, Material.LEAVES);
				setBlock(world, x + i, height - 1, z, Material.LEAVES);
				setBlock(world, x, height - 1, z + i, Material.LEAVES);
				setBlock(world, x, height - 1, z - i, Material.LEAVES);
			}
		}
	}
}
