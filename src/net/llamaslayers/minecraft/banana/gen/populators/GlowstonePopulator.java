package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.XYZ;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * @author codename_B
 */
public class GlowstonePopulator extends BananaBlockPopulator {
	@Override
	public void populate(World world, Random random, Chunk source) {
		if (random.nextInt(100) < 37) {
			for (XYZ glow : glowstonePillar(world, source)) {
				source.getBlock(glow.x, glow.y, glow.z).setType(Material.GLOWSTONE);
			}
		}
	}

	public List<XYZ> glowstonePillar(World world, Chunk source) {
		List<XYZ> blocks = new ArrayList<XYZ>();
		ChunkSnapshot snapshot = source.getChunkSnapshot();
		int stone = getArg(world, "nether") ? Material.NETHERRACK.getId() : Material.STONE.getId();
		boolean found = false;
		for (int y = 125; y > 0; y++) {
			int id = snapshot.getBlockTypeId(8, y, 8);
			if ((id == stone && !found) || (id == 0 && found)) {
				found = true;
				blocks.add(new XYZ(8, y, 8));
				int id2 = snapshot.getBlockTypeId(8, y - 1, 8);
				if (id2 == 0) {
					blocks.add(new XYZ(9, y, 8));
					blocks.add(new XYZ(7, y, 8));
					blocks.add(new XYZ(8, y, 9));
					blocks.add(new XYZ(8, y, 7));
					blocks.add(new XYZ(8, y - 1, 8));
				}
			} else {
				break;
			}
		}
		return blocks;
	}
}
