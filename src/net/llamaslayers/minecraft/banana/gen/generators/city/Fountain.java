package net.llamaslayers.minecraft.banana.gen.generators.city;

import java.util.Random;

/**
 * @author Nightgunner5
 */
public class Fountain implements CityBlockPopulator {
	@Override
	public void populate(int originX, int originZ, int locX, int locZ, int height, byte[] b, Random random) {
		int halfheight = height / 2;
		for (int x = 5; x < 10; x++) {
			for (int z = 5; z < 10; z++) {
				b[(x * 16 + z) * height + halfheight] = STONE;
				if (x == 5 || x == 9 || z == 5 || z == 9) {
					b[(x * 16 + z) * height + halfheight + 1] = STONE;
				}
				if (x == 7 && z == 7) {
					b[(x * 16 + z) * height + halfheight + 1] = STONE;
					b[(x * 16 + z) * height + halfheight + 2] = STONE;
					b[(x * 16 + z) * height + halfheight + 3] = WATER;
				}
			}
		}
	}
}
