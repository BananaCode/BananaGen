package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.GenPlugin;
import net.llamaslayers.minecraft.banana.gen.XYZ;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * BlockPopulator for snake-based caves.
 *
 * @author Pandarr
 */
public class CavePopulator extends BananaBlockPopulator {
	static class FinishSnake implements Runnable {
		private final World world;
		private final XYZ[] snake;

		public FinishSnake(World world, Set<XYZ> snake) {
			this.world = world;
			this.snake = snake.toArray(new XYZ[0]);
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			finishSnake(world, snake);
			for (XYZ block : snake) {
				world.unloadChunkRequest(block.x / 16, block.z / 16);
			}
		}
	}

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(final World world, final Random random, Chunk source) {
		if (getArg(world, "nocaves"))
			return;
		if (random.nextInt(100) < 10) {
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
					Set<XYZ> snake = startSnake(world, random, x, y, z);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, snake));

					if (random.nextInt(16) > 5) {
						if (y > 36) {
							snake = startSnake(world, random, x, y / 2, z);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, snake));
						} else if (y < 24) {
							snake = startSnake(world, random, x, y * 2, z);
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GenPlugin.instance, new FinishSnake(world, snake));
						}
					}
				}
			}.start();
		}
	}

	static Set<XYZ> startSnake(World world, Random random, int blockX,
		int blockY, int blockZ) {
		Set<XYZ> snakeBlocks = new HashSet<XYZ>();

		int airHits = 0;
		XYZ block = new XYZ();
		while (true) {
			if (airHits > 2000) {
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
				int radius = 1 + random.nextInt(3);
				int radius2 = radius * radius + 1;
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							if (x * x + y * y + z * z <= radius2 && y >= 0
									&& y < 128) {
								if (world.getBlockTypeIdAt(blockX + x, blockY
										+ y, blockZ + z) == 0) {
									airHits++;
								} else {
									block.x = blockX + x;
									block.y = blockY + y;
									block.z = blockZ + z;
									if (snakeBlocks.add(block)) {
										block = new XYZ();
									}
								}
							}
						}
					}
				}
			} else {
				airHits++;
			}
		}

		return snakeBlocks;
	}

	static void finishSnake(World world, XYZ[] snakeBlocks) {
		for (XYZ loc : snakeBlocks) {
			Block block = world.getBlockAt(loc.x, loc.y, loc.z);
			if (!block.isEmpty() && !block.isLiquid()
					&& block.getType() != Material.BEDROCK) {
				block.setType(Material.AIR);
			}
		}
	}
}
