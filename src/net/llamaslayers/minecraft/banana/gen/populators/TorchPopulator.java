package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Populator from BananaMaze to place torches on random surfaces.
 *
 * @author Nightgunner5
 */
public class TorchPopulator extends BananaBlockPopulator {
	private static final int ATTEMPTS = 30;
	private static final BlockFace[] directions = new BlockFace[] {
			BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
			BlockFace.WEST };

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (getArg(world, "notorches"))
			return;

		ChunkSnapshot snapshot = source.getChunkSnapshot();

		for (int i = 0; i < getArgInt(world, "torch_max", 3); i++) {
			if (random.nextInt(100) < getArgInt(world, "torch_chance", 70, 0, 100)) {
				attemptloop: for (int j = 0; j < ATTEMPTS; j++) {
					int x = random.nextInt(16);
					int z = random.nextInt(16);
					int y = snapshot.getHighestBlockYAt(x, z);
					if (y < 19) {
						continue;
					}
					y = random.nextInt(y - 18) + 18;

					Block base = source.getBlock(x, y, z);
					for (BlockFace direction : directions) {
						if (direction.getModX() + x > 15
								|| direction.getModX() + x < 0) {
							continue;
						}
						if (direction.getModZ() + z > 15
								|| direction.getModZ() + z < 0) {
							continue;
						}
						if (base.getRelative(direction).getType() == Material.AIR) {
							base.getRelative(direction).setType(Material.TORCH);
							break attemptloop;
						}
					}
				}
			}
		}
	}
}
