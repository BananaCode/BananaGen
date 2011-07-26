package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Generate palm trees, like those one would find on a
 * {@link net.llamaslayers.minecraft.banana.gen.generators.BeachGenerator beach}
 * .
 *
 * @author codename_B
 * @author Nightgunner5
 */
public class PalmTreePopulator extends BuildingPopulator {
	@SuppressWarnings("javadoc")
	public PalmTreePopulator() {
		super("palmtrees");
	}

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (getArg(world, "nopalmtrees"))
			return;
		int x = random.nextInt(16);
		int z = random.nextInt(16);

		Block block = source.getBlock(x, 127, z);
		while (block.isEmpty() || block.isLiquid()) {
			block = block.getRelative(BlockFace.DOWN);
		}
		block = block.getRelative(BlockFace.UP);

		Building building = getRandomBuilding(block, random);
		if (building != null) {
			building.maybePlace(block, random);
		}
	}
}
