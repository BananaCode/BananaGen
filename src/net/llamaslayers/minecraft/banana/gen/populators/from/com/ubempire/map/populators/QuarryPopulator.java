package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * BlockPopulator that generates stone quarries.
 * 
 * @author codename_B
 */
public class QuarryPopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (random.nextInt(100) < 95)
			return;

		Block block = source.getBlock(8, world.getHighestBlockYAt(source.getX() * 16 + 8, source.getZ() * 16 + 8), 8);
		int sizeX = 5 + random.nextInt(6);
		int sizeY = 5 + random.nextInt(6);
		int sizeZ = 3 + random.nextInt(7);

		for (int y = 0; y <= sizeZ + 1; y++) {
			sizeX = sizeX - y;
			sizeY = sizeY - y;
			for (int x = -sizeX; x <= sizeX; x++) {
				for (int z = -sizeY; z <= sizeY; z++) {
					Block block2 = block.getRelative(x, -y - 1, z);
					if (block2.getTypeId() != 0
							&& (block2.getTypeId() < 8 || block2.getTypeId() > 11)) {
						if (random.nextBoolean()) {
							block2.setType(getArg(world, "nether") ? Material.NETHERRACK
									: Material.COBBLESTONE);
						} else {
							block2.setType(Material.GRAVEL);
						}
					}

					if (random.nextBoolean()) {
						if (!block.getRelative(x, -y, z).isLiquid()) {
							block.getRelative(x, -y, z).setType(Material.AIR);
						}
					}
				}
			}
		}
	}

}
