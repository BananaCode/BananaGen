package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;
import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * @author Nightgunner5
 */
public class LavaLightFixPopulator extends BananaBlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		for (int x = 0; x < 4; x++) {
			for (int z = 0; z < 4; z++) {
				Block block = source.getBlock(x * 4, 3, z * 4);
				if (block.isLiquid()) {
					block.setType(block.getType());
				}
			}
		}
	}
}
