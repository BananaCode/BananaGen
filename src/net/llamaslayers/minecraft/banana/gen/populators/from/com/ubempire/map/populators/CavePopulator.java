package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.ArrayList;
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
 */
public class CavePopulator extends BananaBlockPopulator {

	private static boolean isGenerating = false;

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		if (isGenerating)
			return;
		if (random.nextInt(16) > 1)
			return;

		int rx = 4 + random.nextInt(8);
		int rz = 4 + random.nextInt(8);
		int maxY = world.getHighestBlockYAt(rx, rz);
		if (maxY < 16) {
			maxY = 32;
		}

		isGenerating = true;
		int ry = random.nextInt(maxY);
		ArrayList<Block> snake = startSnake(world, random, chunk.getBlock(rx, ry, rz));
		finishSnake(world, random, snake);

		if (random.nextInt(16) > 5) {
			if (ry > 36) {
				snake = startSnake(world, random, chunk.getBlock(rx, ry / 2, rz));
				finishSnake(world, random, snake);
			} else if (ry < 24) {
				snake = startSnake(world, random, chunk.getBlock(rx, ry * 2, rz));
				finishSnake(world, random, snake);
			}
		}
		isGenerating = false;
	}

	private ArrayList<Block> startSnake(World world, Random random, Block block) {
		ArrayList<Block> snakeBlocks = new ArrayList<Block>();

		int blockX = block.getX();
		int blockY = block.getY();
		int blockZ = block.getZ();
		while (world.getBlockAt(blockX, blockY, blockZ).getTypeId() != 0) {
			if (snakeBlocks.size() > 2000) {
				break;
			}

			if (random.nextInt(20) == 0) {
				blockY = blockY + 1;
			} else if (world.getBlockAt(blockX, blockY + 2, blockZ).getTypeId() == 0) {
				blockY = blockY + 2;
			} else if (world.getBlockAt(blockX + 2, blockY, blockZ).getTypeId() == 0) {
				blockX = blockX + 1;
			} else if (world.getBlockAt(blockX - 2, blockY, blockZ).getTypeId() == 0) {
				blockX = blockX - 1;
			} else if (world.getBlockAt(blockX, blockY, blockZ + 2).getTypeId() == 0) {
				blockZ = blockZ + 1;
			} else if (world.getBlockAt(blockX, blockY, blockZ - 2).getTypeId() == 0) {
				blockZ = blockZ - 1;
			} else if (world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId() == 0) {
				blockX = blockX + 1;
			} else if (world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId() == 0) {
				blockX = blockX - 1;
			} else if (world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId() == 0) {
				blockZ = blockZ + 1;
			} else if (world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId() == 0) {
				blockZ = blockZ - 1;
			} else if (random.nextBoolean()) {
				if (random.nextBoolean()) {
					blockX = blockX + 1;
				} else {
					blockZ = blockZ + 1;
				}
			} else {
				if (random.nextBoolean()) {
					blockX = blockX - 1;
				} else {
					blockZ = blockZ - 1;
				}
			}

			if (world.getBlockAt(blockX, blockY, blockZ).getTypeId() != 0) {
				snakeBlocks.add(world.getBlockAt(blockX, blockY, blockZ));
			}
		}

		return snakeBlocks;
	}

	private void finishSnake(World world, Random random,
		ArrayList<Block> snakeBlocks) {
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

	private boolean canPlaceBlock(World world, int x, int y, int z) {
		switch (world.getBlockAt(x, y, z).getType()) {
		case AIR:
		case WATER:
		case STATIONARY_WATER:
		case LAVA:
		case STATIONARY_LAVA:
			return false;
		default:
			return true;
		}
	}

}
