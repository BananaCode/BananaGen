package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.MountainGenerator;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Generates boulders for the foot of {@link MountainGenerator}
 * 
 * @author Nightgunner5
 */
public class BoulderPopulator extends BananaBlockPopulator {
	private static final Material BOULDER_MATERIAL = Material.STONE;

	@Override
	public void populate(World world, Random random, Chunk source) {
		int minY = getArgInt(world, "boulder_lowest", 20, 0, 127);
		int maxY = getArgInt(world, "boulder_highest", 50, 0, 127);
		int minSize = getArgInt(world, "boulder_smallest", 2, 1, 50);
		int maxSize = getArgInt(world, "boulder_largest", 5, 1, 50);

		if (minSize > maxSize) {
			// Switch 'em
			minSize ^= maxSize;
			maxSize ^= minSize;
			minSize ^= maxSize;
		}

		if (random.nextInt(100) < getArgInt(world, "boulder_chance", 35, 0, 100)) {
			ChunkSnapshot snapshot = source.getChunkSnapshot();
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			int y = snapshot.getHighestBlockYAt(x, z);
			if (y > maxY || y < minY)
				return;

			int size = random.nextInt(maxSize - minSize + 1);
			int size2 = size * size;

			for (int i = -size; i < size; i++) {
				for (int j = -size; j < size; j++) {
					for (int k = -size; k < size; k++) {
						if (i * i + j * j + k * k < size2
								+ random.nextInt(minSize + 2)) {
							Block block = world.getBlockAt(x + i
									+ source.getX() * 16, y + j, z + k
									+ source.getZ() * 16);
							if (block.getType() == Material.AIR) {
								block.setType(BOULDER_MATERIAL);
							}
						}
					}
				}
			}
		}
	}
}
