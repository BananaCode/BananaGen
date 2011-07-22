package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Populates the world with ores.
 * 
 * @author Nightgunner5
 * @author Markus Persson
 */
public class OrePopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (getArg(world, "nether"))
			return;

		int[] iterations = new int[] { 10, 20, 20, 2, 8, 1, 1, 1 };
		int[] amount = new int[] { 32, 16, 8, 8, 7, 7, 6 };
		Material[] type = new Material[] { Material.GRAVEL, Material.COAL_ORE,
				Material.IRON_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE,
				Material.DIAMOND_ORE, Material.LAPIS_ORE };
		int[] maxHeight = new int[] { 128, 128, 128, 128, 128, 64, 32, 16, 16,
				32 };

		for (int i = 0; i < type.length; i++) {
			for (int j = 0; j < iterations[i]; j++) {
				internal(source, random, random.nextInt(16), random.nextInt(maxHeight[i]), random.nextInt(16), amount[i], type[i]);
			}
		}
	}

	private static void internal(Chunk source, Random random, int originX,
		int originY, int originZ, int amount, Material type) {
		for (int i = 0; i < amount; i++) {
			int x = originX + random.nextInt(amount / 2) - amount / 4;
			int y = originY + random.nextInt(amount / 4) - amount / 8;
			int z = originZ + random.nextInt(amount / 2) - amount / 4;
			x &= 0xf;
			z &= 0xf;
			if (y > 127 || y < 0) {
				continue;
			}
			Block block = source.getBlock(x, y, z);
			if (block.getType() == Material.STONE) {
				block.setType(type);
			}
		}
	}
}
