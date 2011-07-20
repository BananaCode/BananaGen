package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.ArrayList;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * BlockPopulator that generates lava lakes.
 * 
 * @author codename_B
 */
public class LakePopulator extends BananaBlockPopulator {
	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (random.nextInt(10) > 1)
			return;

		ChunkSnapshot snapshot = source.getChunkSnapshot();

		int rx16 = random.nextInt(16);
		int rx = (source.getX() << 4) + rx16;
		int rz16 = random.nextInt(16);
		int rz = (source.getZ() << 4) + rz16;
		if (snapshot.getHighestBlockYAt(rx16, rz16) < 4)
			return;
		int ry = 6 + random.nextInt(snapshot.getHighestBlockYAt(rx16, rz16) - 3);
		int radius = 2 + random.nextInt(3);

		Material liquidMaterial = Material.LAVA;
		Material solidMaterial = Material.OBSIDIAN;

		if (random.nextInt(10) < 3) {
			ry = snapshot.getHighestBlockYAt(rx16, rz16) - 1;
		}
		if (random.nextInt(96) < ry && !getArg(world, "nether")) {
			liquidMaterial = Material.WATER;
			solidMaterial = Material.WATER;
		} else if (world.getBlockAt(rx, ry, rz).getBiome() == Biome.FOREST
				|| world.getBlockAt(rx, ry, rz).getBiome() == Biome.SEASONAL_FOREST)
			return;

		ArrayList<Block> lakeBlocks = new ArrayList<Block>();
		for (int i = -1; i < 4; i++) {
			Vector center = new BlockVector(rx, ry - i, rz);
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					Vector position = center.clone().add(new Vector(x, 0, z));
					if (center.distance(position) <= radius + 0.5 - i) {
						lakeBlocks.add(world.getBlockAt(position.toLocation(world)));
					}
				}
			}
		}

		for (Block block : lakeBlocks) {
			// Ensure it's not air or liquid already
			if (block.getTypeId() != 0 && block.isLiquid()) {
				if (block.getY() == ry + 1) {
					if (random.nextBoolean()) {
						block.setType(Material.AIR);
					}
				} else if (block.getY() == ry) {
					block.setType(Material.AIR);
				} else if (random.nextInt(10) > 1) {
					block.setType(liquidMaterial);
				} else {
					block.setType(solidMaterial);
				}
			}
		}
	}
}
