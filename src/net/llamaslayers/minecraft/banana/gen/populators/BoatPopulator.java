package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;

/**
 * Boats. You heard me the first time.
 * 
 * @author codename_B
 */
public class BoatPopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (random.nextInt(100) < 5) {
			int x = source.getX() * 16 + random.nextInt(16);
			int z = source.getZ() * 16 + random.nextInt(16);
			int y = world.getHighestBlockYAt(x, z);
			createBoat(world, x, y, z);
		}
	}

	private static void createBoat(World w, int x, int y, int z) {
		if (w.getBlockAt(x, y - 1, z).isLiquid()) {
			w.spawn(new Location(w, x, y, z), Boat.class);
		}
	}
}