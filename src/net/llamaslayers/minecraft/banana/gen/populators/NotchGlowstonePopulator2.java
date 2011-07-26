// Package Declaration
package net.llamaslayers.minecraft.banana.gen.populators;

// Java Imports
import java.util.Random;

// Bukkit Imports
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

// BananaGen Imports
import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

/**
 * Edited for BananaGen.
 * 
 * @author Markus 'Notch' Persson
 * @author iffa
 */
public class NotchGlowstonePopulator2 extends BananaBlockPopulator {
	// Variables
	private Random rand = new Random();
	private int x = rand.nextInt(15);
	private int y = rand.nextInt(127);
	private int z = rand.nextInt(15);

	/**
	 * Populates a world with glowstone (2). Easily configurable (but results in
	 * more rare glowstone) by modifying NotchGlowstonePopulator's
	 * suitable()-method.
	 * 
	 * @author Markus 'Notch' Persson
	 * @author iffa
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		x = rand.nextInt(15);
		y = rand.nextInt(127);
		z = rand.nextInt(15);
		while (!NotchGlowstonePopulator.suitable(y)) {
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
			if (source.getBlock(i1, j1, k1).getType() != Material.AIR) {
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
				if (j2 == 89) {
					l1++;
				}
			}
			if (l1 == 1) {
				source.getBlock(i1, j1, k1).setTypeId(89);
			}
		}
		return;
	}

}
