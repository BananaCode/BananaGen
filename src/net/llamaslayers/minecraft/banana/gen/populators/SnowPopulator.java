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
 * BlockPopulator that coats {@link Biome#TUNDRA tundra} and {@link Biome#TAIGA
 * taiga} with snow.
 * 
 * @author codename_B
 */
public class SnowPopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (getArg(world, "nether"))
			return;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Block block = source.getBlock(x, 64, z);
				if (block.getBiome() != Biome.TAIGA
						&& block.getBiome() != Biome.TUNDRA) {
					continue;
				}

				for (int y = 126; y >= 4; --y) {
					block = source.getBlock(x, y, z);
					if (block.getType() == Material.WATER
							|| block.getType() == Material.STATIONARY_WATER) {
						if (block.getData() == 0) {
							block.setType(Material.ICE);
						}
						break;
					} else if (block.getType() == Material.LAVA
							|| block.getType() == Material.STATIONARY_LAVA) {
						break;
					} else if (block.getType() != Material.AIR) {
						block.getRelative(BlockFace.UP).setType(Material.SNOW);
						if (block.getType() == Material.DIRT) {
							block.setType(Material.GRASS);
						}
						break;
					}
				}
			}
		}
	}

}
