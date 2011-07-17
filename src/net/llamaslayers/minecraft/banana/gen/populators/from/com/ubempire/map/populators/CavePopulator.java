package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * BlockPopulator for snake-based caves.
 * 
 * @author Pandarr
 */
public class CavePopulator extends BananaBlockPopulator {
	private static boolean isGenerating = false;

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (isGenerating)
			return;
		if (random.nextInt(16) > 1)
			return;

		int x = 4 + random.nextInt(8) + source.getX() * 16;
		int z = 4 + random.nextInt(8) + source.getZ() * 16;
		int maxY = world.getHighestBlockYAt(x, z);
		if (maxY < 16) {
			maxY = 32;
		}

		isGenerating = true;
		int y = random.nextInt(maxY);
		List<Block> snake = startSnake(world, random, x, y, z);
		finishSnake(world, random, snake);

		if (random.nextInt(16) > 5) {
			if (y > 36) {
				snake = startSnake(world, random, x, y / 2, z);
				finishSnake(world, random, snake);
			} else if (y < 24) {
				snake = startSnake(world, random, x, y * 2, z);
				finishSnake(world, random, snake);
			}
		}
		isGenerating = false;
	}

	private static List<Block> startSnake(World world, Random random,
		int blockX, int blockY, int blockZ) {
		List<Block> snakeBlocks = new ArrayList<Block>();

		while (world.getBlockAt(blockX, blockY, blockZ).getTypeId() != 0) {
			if (snakeBlocks.size() > 2000) {
				break;
			}

			if (random.nextInt(20) == 0) {
				blockY++;
			} else if (world.getBlockAt(blockX, blockY + 2, blockZ).getTypeId() == 0) {
				blockY += 2;
			} else if (world.getBlockAt(blockX + 2, blockY, blockZ).getTypeId() == 0) {
				blockX++;
			} else if (world.getBlockAt(blockX - 2, blockY, blockZ).getTypeId() == 0) {
				blockX--;
			} else if (world.getBlockAt(blockX, blockY, blockZ + 2).getTypeId() == 0) {
				blockZ++;
			} else if (world.getBlockAt(blockX, blockY, blockZ - 2).getTypeId() == 0) {
				blockZ--;
			} else if (world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId() == 0) {
				blockX++;
			} else if (world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId() == 0) {
				blockX--;
			} else if (world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId() == 0) {
				blockZ++;
			} else if (world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId() == 0) {
				blockZ--;
			} else if (random.nextBoolean()) {
				if (random.nextBoolean()) {
					blockX++;
				} else {
					blockZ++;
				}
			} else {
				if (random.nextBoolean()) {
					blockX--;
				} else {
					blockZ--;
				}
			}

			if (world.getBlockAt(blockX, blockY, blockZ).getTypeId() != 0) {
				snakeBlocks.add(world.getBlockAt(blockX, blockY, blockZ));
			}
		}

		return snakeBlocks;
	}

	private static void finishSnake(World world, Random random,
		List<Block> snakeBlocks) {
		for (Block block : snakeBlocks) {
			Vector center = new BlockVector(block.getX(), block.getY(), block.getZ());
			if (block.getType() != Material.AIR) {
				int radius = 1 + random.nextInt(3);
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							Vector position = center.clone().add(new Vector(x, y, z));

							if (center.distance(position) <= radius + 0.5) {
								if (canPlaceBlock(world, position.getBlockX(), position.getBlockY(), position.getBlockZ())) {
									world.getBlockAt(position.toLocation(world)).setType(Material.AIR);
								}
							}
						}
					}
				}
			}
		}
	}

	private static boolean canPlaceBlock(World world, int x, int y, int z) {
		Block block = world.getBlockAt(x, y, z);
		return !block.isLiquid() && !block.isEmpty();
	}
}
