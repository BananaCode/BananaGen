package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.GenPlugin;

import org.bukkit.Bukkit;
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
	static class FinishSnake implements Runnable {
		private final World world;
		private final Random random;
		private final List<BlockVector> snake;

		public FinishSnake(World world, Random random, List<BlockVector> snake) {
			this.world = world;
			this.random = random;
			this.snake = snake;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			finishSnake(world, random, snake);
		}
	}

	static boolean isGenerating = false;

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(final World world, final Random random, Chunk source) {
		if (isGenerating)
			return;
		if (random.nextInt(16) > 1)
			return;

		final int x = 4 + random.nextInt(8) + source.getX() * 16;
		final int z = 4 + random.nextInt(8) + source.getZ() * 16;
		int maxY = world.getHighestBlockYAt(x, z);
		if (maxY < 16) {
			maxY = 32;
		}

		final int y = random.nextInt(maxY);
		new Thread() {
			@Override
			public void run() {
				isGenerating = true;
				List<BlockVector> snake = startSnake(world, random, x, y, z);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, random, snake));

				if (random.nextInt(16) > 5) {
					if (y > 36) {
						snake = startSnake(world, random, x, y / 2, z);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, random, snake));
					} else if (y < 24) {
						snake = startSnake(world, random, x, y * 2, z);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, random, snake));
					}
				}
				isGenerating = false;
			}
		}.start();
	}

	static List<BlockVector> startSnake(World world, Random random,
		int blockX, int blockY, int blockZ) {
		List<BlockVector> snakeBlocks = new ArrayList<BlockVector>();

		while (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
			if (snakeBlocks.size() > 2000) {
				break;
			}

			if (random.nextInt(20) == 0) {
				blockY++;
			} else if (world.getBlockTypeIdAt(blockX, blockY + 2, blockZ) == 0) {
				blockY += 2;
			} else if (world.getBlockTypeIdAt(blockX + 2, blockY, blockZ) == 0) {
				blockX++;
			} else if (world.getBlockTypeIdAt(blockX - 2, blockY, blockZ) == 0) {
				blockX--;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 2) == 0) {
				blockZ++;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 2) == 0) {
				blockZ--;
			} else if (world.getBlockTypeIdAt(blockX + 1, blockY, blockZ) == 0) {
				blockX++;
			} else if (world.getBlockTypeIdAt(blockX - 1, blockY, blockZ) == 0) {
				blockX--;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 1) == 0) {
				blockZ++;
			} else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 1) == 0) {
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

			if (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
				snakeBlocks.add(new BlockVector(blockX, blockY, blockZ));
			}
		}

		return snakeBlocks;
	}

	static void finishSnake(World world, Random random,
		List<BlockVector> snakeBlocks) {
		for (BlockVector center : snakeBlocks) {
			Block block = world.getBlockAt(center.toLocation(world));
			if (block.getType() != Material.AIR) {
				int radius = 1 + random.nextInt(3);
				int radius2 = radius * radius + 1;
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							Vector position = center.clone().add(new Vector(x, y, z));

							if (center.distanceSquared(position) <= radius2) {
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
