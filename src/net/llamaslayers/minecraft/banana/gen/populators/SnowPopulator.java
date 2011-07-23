package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
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

		int chunkX = source.getX() * 16;
		int chunkZ = source.getZ() * 16;
		ChunkSnapshot snapshot = source.getChunkSnapshot();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				Biome biome = world.getBiome(x + chunkX, z + chunkZ);
				if (biome != Biome.TAIGA && biome != Biome.TUNDRA) {
					continue;
				}

				int y = snapshot.getHighestBlockYAt(x, z);
				if (source.getBlock(x, y, z).getType() == Material.AIR) {
					y--;
				}

				Block block = source.getBlock(x, y, z);
				switch (block.getType()) {
				case WATER:
				case STATIONARY_WATER:
					if (block.getData() == 0) {
						block.setType(Material.ICE);
					}
					break;
				case LAVA:
				case STATIONARY_LAVA:
					break;
				case DIRT:
					block.setType(Material.GRASS);
				default:
					block.getRelative(BlockFace.UP).setType(Material.SNOW);
					break;
				}
			}
		}
	}
}
