package net.llamaslayers.minecraft.banana.gen.generators.city;

import java.util.Random;
import net.llamaslayers.minecraft.banana.gen.MaterialIds;

/**
 * @author Nightgunner5
 */
public interface CityBlockPopulator extends MaterialIds {
	public void populate(int originX, int originZ, int locX, int locZ, int height, byte[] b, Random random);
}
