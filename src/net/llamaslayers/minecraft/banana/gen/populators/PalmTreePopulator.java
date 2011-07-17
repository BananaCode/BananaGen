package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Generate palm trees, like those one would find on a beach.
 * 
 * @author codename_B
 */
public class PalmTreePopulator extends BananaBlockPopulator {

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World w, Random r, Chunk c) {
		int cx = c.getX() * 16 + r.nextInt(16), cz = c.getZ() * 16
				+ r.nextInt(16);
		int cy = w.getHighestBlockYAt(cx, cz);
		if (r.nextInt(200) > 183) {
			createTree(w, r, cx, cy, cz);
		}
	}

	private static void createTree(World w, Random r, int cx, int cy, int cz) {
		if (w.getBlockTypeIdAt(cx, cy - 1, cz) != Material.SAND.getId())
			return;
		int cp = cy + r.nextInt(3) + 4;
		for (int i = cy; i <= cp; i++) {
			Location l = new Location(w, cx, i, cz);
			setBlock(w, Material.LOG, l);
		}
		for (int i = 0; i < 4; i++) {
			w.getBlockAt(cx - 1 - i, cp, cz).setType(Material.LEAVES);
			w.getBlockAt(cx + 1 + i, cp, cz).setType(Material.LEAVES);
			w.getBlockAt(cx, cp, cz + 1 + i).setType(Material.LEAVES);
			w.getBlockAt(cx, cp, cz - 1 - i).setType(Material.LEAVES);
			if (i == 3) {
				w.getBlockAt(cx - 1 - i, cp - 1, cz).setType(Material.LEAVES);
				w.getBlockAt(cx + 1 + i, cp - 1, cz).setType(Material.LEAVES);
				w.getBlockAt(cx, cp - 1, cz + 1 + i).setType(Material.LEAVES);
				w.getBlockAt(cx, cp - 1, cz - 1 - i).setType(Material.LEAVES);
			}
		}
	}
}