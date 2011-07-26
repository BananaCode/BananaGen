// Package Declaration
package net.llamaslayers.minecraft.banana.gen.populators;

// Java Imports
import java.util.Random;

// Bukkit Imports
import org.bukkit.Chunk;
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
	/**
	 * Populates a world with glowstone. Easily configurable (but results in
	 * more rare glowstone) by modifying the suitable()-method.
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		int x = random.nextInt(16);
		int y = random.nextInt(128);
		int z = random.nextInt(16);
		while (!suitable(y)) {
			y = random.nextInt(128);
		}
		// Only populates if the "target" location is air & there is netherrack
		// above it. (and only checked in Y 117-127 for efficiency, might be
		// changed later)
		if (source.getBlock(x, y, z).getTypeId() != AIR) {
			return;
		}
		if (source.getBlock(x, y, z).getRelative(BlockFace.UP).getTypeId() != NETHERRACK) {
			return;
		}
		source.getBlock(x, y, z).setTypeId(GLOWSTONE);
		x += source.getX() * 16;
		z += source.getZ() * 16;
		for (int l = 0; l < 1500; l++) {
			int currentX = (x + random.nextInt(8)) - random.nextInt(8);
			int currentY = y - random.nextInt(12);
			int currentZ = (z + random.nextInt(8)) - random.nextInt(8);
			if (world.getBlockAt(currentX, currentY, currentZ).getTypeId() != 0) {
				continue;
			}
			int count = 0;
			for (int face = 0; face < 6; face++) {
				int type = 0;
				if (face == 0) {
					type = world.getBlockTypeIdAt(currentX - 1, currentY, currentZ);
				}
				if (face == 1) {
					type = world.getBlockTypeIdAt(currentX + 1, currentY, currentZ);
				}
				if (face == 2) {
					type = world.getBlockTypeIdAt(currentX, currentY - 1, currentZ);
				}
				if (face == 3) {
					type = world.getBlockTypeIdAt(currentX, currentY + 1, currentZ);
				}
				if (face == 4) {
					type = world.getBlockTypeIdAt(currentX, currentY, currentZ - 1);
				}
				if (face == 5) {
					type = world.getBlockTypeIdAt(currentX, currentY, currentZ + 1);
				}
				if (type == GLOWSTONE) {
					count++;
				}
			}

			if (count == 1) {
				world.getBlockAt(currentX, currentY, currentZ).setTypeId(GLOWSTONE);
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
	static boolean suitable(int y) {
		if (y > 113 && y < 128) {
			return true;
		}
		if (y > 51 && y < 73) {
			return true;
		}
		return false;
	}
}
