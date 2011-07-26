// Package Declaration
package net.llamaslayers.minecraft.banana.gen.populators;

// Java Imports
import java.util.Random;

// Bukkit Imports
import org.bukkit.Chunk;
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
		int x = random.nextInt(15);
		int y = random.nextInt(127);
		int z = random.nextInt(15);
		while (!NotchGlowstonePopulator.suitable(y)) {
			y = random.nextInt(127);
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
			if (source.getBlock(currentX, currentY, currentZ).getTypeId() != AIR) {
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

}
