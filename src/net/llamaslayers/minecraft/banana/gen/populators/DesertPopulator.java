package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * BlockPopulator that turns deserts into sand and places cacti.
 * 
 * @author codename_B
 */
public class DesertPopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		Material matSand = getArg(world, "nether") ? Material.SOUL_SAND
				: Material.SAND;
		Material matDirt = getArg(world, "nether") ? Material.NETHERRACK
				: Material.DIRT;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int tx = (chunk.getX() << 4) + x;
				int tz = (chunk.getZ() << 4) + z;
				int y = world.getHighestBlockYAt(tx, tz);

				Block block = chunk.getBlock(x, y, z).getRelative(BlockFace.DOWN);
				if (block.getBiome() != Biome.DESERT) {
					continue;
				}

				// Set top few layers of grass/dirt to sand
				for (int i = 0; i < 5; ++i) {
					Block b2 = block.getRelative(0, -i, 0);
					if (b2.getType() == Material.GRASS
							|| b2.getType() == matDirt) {
						b2.setType(matSand);
					}
				}

				// Generate cactus
				if (block.getType() == matSand) {
					if (random.nextInt(20) == 0) {
						// Make sure it's surrounded by air
						Block base = block.getRelative(BlockFace.UP);
						if (base.getTypeId() == 0
								&& base.getRelative(BlockFace.NORTH).getTypeId() == 0
								&& base.getRelative(BlockFace.EAST).getTypeId() == 0
								&& base.getRelative(BlockFace.SOUTH).getTypeId() == 0
								&& base.getRelative(BlockFace.WEST).getTypeId() == 0) {
							generateCactus(base, random.nextInt(4));
						}
					}
				}
			}
		}
	}

	private void generateCactus(Block block, int height) {
		if (getArg(block.getWorld(), "nether")) {
			block.setType(Material.FIRE);
		} else {
			for (int i = 0; i < height; ++i) {
				block.getRelative(0, i, 0).setType(Material.CACTUS);
			}
		}
	}
}
