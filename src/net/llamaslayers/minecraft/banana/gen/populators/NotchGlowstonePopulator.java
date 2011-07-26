// Package Declaration
package net.llamaslayers.minecraft.banana.gen.populators;

// Java Imports
import java.util.Random;

// Bukkit Imports
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

//BananaGen Imports
import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

/**
 * Edited for BananaGen.
 * 
 * @author Markus 'Notch' Persson
 * @author iffa
 */
public class NotchGlowstonePopulator extends BananaBlockPopulator {
	// Variables
	private Random rand = new Random();
	private int x = 0;
	private int y = 0;
	private int z = 0;

	/**
	 * Populates a world with glowstone. Easily configurable (but results in
	 * more rare glowstone) by modifying the suitable()-method.
	 * 
	 * @author Markus 'Notch' Persson
	 * @author iffa
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		x = rand.nextInt(15);
		y = rand.nextInt(127);
		z = rand.nextInt(15);
		while (!suitable(y)) {
			y = rand.nextInt(127);
		}
		// Only populates if the "target" location is air & there is netherrack
		// above it. (and only checked in Y 117-127 for efficiency, might be
		// changed later)
		if (source.getBlock(x, y, z).getType() != Material.AIR) {
			return;
		}
		if (source.getBlock(x, y, z).getRelative(BlockFace.UP).getType() != Material.NETHERRACK) {
			return;
		}
		source.getBlock(x, y, z).setTypeId(89);
		for (int l = 0; l < 1500; l++) {
			int i1 = (x + random.nextInt(8)) - random.nextInt(8);
			int j1 = y - random.nextInt(12);
			int k1 = (z + random.nextInt(8)) - random.nextInt(8);
			if (world.getBlockAt(i1, j1, k1).getTypeId() != 0) {
				continue;
			}
			int l1 = 0;
			for (int i2 = 0; i2 < 6; i2++) {
				int j2 = 0;
				if (i2 == 0) {
					j2 = source.getBlock(i1 - 1, j1, k1).getTypeId();
				}
				if (i2 == 1) {
					j2 = source.getBlock(i1 + 1, j1, k1).getTypeId();
				}
				if (i2 == 2) {
					j2 = source.getBlock(i1, j1 - 1, k1).getTypeId();
				}
				if (i2 == 3) {
					j2 = source.getBlock(i1, j1 + 1, k1).getTypeId();
				}
				if (i2 == 4) {
					j2 = source.getBlock(i1, j1, k1 - 1).getTypeId();
				}
				if (i2 == 5) {
					j2 = source.getBlock(i1, j1, k1 + 1).getTypeId();
				}
				if (j2 == 87) {
					l1++;
				}
			}

			if (l1 == 1) {
				source.getBlock(i1, j1, k1).setTypeId(87);
			}
		}
		return;
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
	public static boolean suitable(int y) {
		if (y > 113 && y < 128) {
			return true;
		}
		if (y > 51 && y < 73) {
			return true;
		}
		return false;
	}

}
