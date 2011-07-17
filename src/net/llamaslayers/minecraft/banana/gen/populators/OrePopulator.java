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
				internal(world, random, source.getX() * 16 + random.nextInt(16), random.nextInt(maxHeight[i]), source.getZ()
						* 16 + random.nextInt(16), amount[i], type[i]);
			}
		}
	}

	private static void internal(World world, Random random, int originX,
		int originY, int originZ, int amount, Material type) {
		double angle = random.nextDouble() * Math.PI;
		double x1 = ((originX + 8) + Math.sin(angle) * amount / 8);
		double x2 = ((originX + 8) - Math.sin(angle) * amount / 8);
		double z1 = ((originZ + 8) + Math.cos(angle) * amount / 8);
		double z2 = ((originZ + 8) - Math.cos(angle) * amount / 8);
		double y1 = (originY + random.nextInt(3) + 2);
		double y2 = (originY + random.nextInt(3) + 2);

		for (int i = 0; i <= amount; i++) {
			double seedX = x1 + (x2 - x1) * i / amount;
			double seedY = y1 + (y2 - y1) * i / amount;
			double seedZ = z1 + (z2 - z1) * i / amount;
			double size = ((Math.sin(i * Math.PI / amount) + 1)
					* random.nextDouble() * amount / 16 + 1) / 2;

			int startX = (int) (seedX - size);
			int startY = (int) (seedY - size);
			int startZ = (int) (seedZ - size);
			int endX = (int) (seedX + size);
			int endY = (int) (seedY + size);
			int endZ = (int) (seedZ + size);

			for (int x = startX; x <= endX; x++) {
				double sizeX = (x + 0.5 - seedX) / size;
				sizeX *= sizeX;

				if (sizeX < 1) {
					for (int y = startY; y <= endY; y++) {
						double sizeY = (y + 0.5 - seedY) / size;
						sizeY *= sizeY;

						if (sizeX + sizeY < 1) {
							for (int z = startZ; z <= endZ; z++) {
								double sizeZ = (z + 0.5 - seedZ) / size;
								sizeZ *= sizeZ;

								Block block = world.getBlockAt(x, y, z);
								if (sizeX + sizeY + sizeZ < 1
										&& block.getType() == Material.STONE) {
									block.setType(type);
								}
							}
						}
					}
				}
			}
		}
	}
}
