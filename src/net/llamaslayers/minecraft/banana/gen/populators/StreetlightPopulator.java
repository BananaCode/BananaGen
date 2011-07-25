package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;
import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * @author Nightgunner5
 */
public class StreetlightPopulator extends BananaBlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		for (int i = 0; i < 10; i++) {
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			if (x != 1 && x != 14 && z != 1 && z != 14)
				continue;
			int x2 = x;
			int z2 = z;
			if (x == 1)
				x2 = 0;
			else if (x == 14)
				x2 = 15;
			else if (z == 1)
				z2 = 0;
			else if (z == 14)
				z2 = 15;
			if (source.getBlock(x, 128 / 2, z).getTypeId() == STONE && source.getBlock(x, 128 / 2 + 1, z).getTypeId() == AIR) {
				for (int y = 128 / 2 + 1; y < 128 / 2 + 6; y++) {
					source.getBlock(x, y, z).setTypeId(FENCE, false);
				}
				source.getBlock(x2, 128 / 2 + 5, z2).setTypeId(FENCE, false);
				source.getBlock(x2, 128 / 2 + 4, z2).setTypeId(GLOWSTONE);
			}
		}
	}
}
