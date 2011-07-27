// Package Declaration
package net.llamaslayers.minecraft.banana.gen.populators;

// Java Imports
import java.util.Random;

// Bukkit Imports
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;

// BananaGen Imports
import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

/**
 * Edited for BananaGen.
 *
 * @author Markus 'Notch' Persson
 * @author iffa
 * @author Nightgunner5
 */
public class GlowstonePopulator extends BananaBlockPopulator {
	private static final BlockFace[] faces = {BlockFace.DOWN, BlockFace.EAST,
		BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.WEST};

	/**
	 * Populates a world with glowstone. Easily configurable (but results in
	 * more rare glowstone) by modifying the suitable()-method.
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		for (int i = 0; i < 2; i++) {
			int x = random.nextInt(16);
			int y = random.nextInt(128);
			int z = random.nextInt(16);
			while (!suitable(y)) {
				y = random.nextInt(128);
			}
			Block block = source.getBlock(x, y, z);
			// Only populates if the "target" location is air & there is netherrack
			// above it. (and only checked in Y 114-127 or 52-72 for efficiency,
			// might be changed later)
			if (block.getTypeId() != AIR) {
				return;
			}
			if (block.getRelative(BlockFace.UP).getTypeId() != NETHERRACK) {
				return;
			}
			block.setTypeId(GLOWSTONE);

			for (int j = 0; j < 1500; j++) {
				Block current = block.getRelative(random.nextInt(8) - random.nextInt(8),
												  random.nextInt(12),
												  random.nextInt(8) - random.nextInt(8));
				if (current.getTypeId() != AIR) {
					continue;
				}
				int count = 0;
				for (BlockFace face : faces) {
					if (current.getRelative(face).getTypeId() == GLOWSTONE) {
						count++;
					}
				}

				if (count == 1) {
					current.setTypeId(GLOWSTONE);
				}
			}
		}
	}

	/**
	 * Checks if the given Y-coordinate is "suitable" for glowstone generation. <br />
	 * <br />
	 * If the Y-coordinate is 52-72, it is OK. <br />
	 * If the Y-coordinate is 114-127, it is OK.
	 *
	 * @param y
	 *            Y-coordinate
	 * @return true if the given Y-coordinate is suitable for glowstone
	 *         generation
	 */
	private static boolean suitable(int y) {
		if (y > 113 && y < 128) {
			return true;
		}
		if (y > 51 && y < 73) {
			return true;
		}
		return false;
	}
}
