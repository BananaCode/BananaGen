package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.ArrayList;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

/**
 * BlockPopulator that generates lava lakes.
 */
public class LakePopulator extends BananaBlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextInt(10) > 1) {
            return;
        }
        
        int rx = (chunk.getX() << 4) + random.nextInt(16);
        int rz = (chunk.getZ() << 4) + random.nextInt(16);
        int ry = 6 + random.nextInt(world.getHighestBlockYAt(rx, rz) - 3);
        int radius = 2 + random.nextInt(3);
        
        Material liquidMaterial = Material.LAVA;
        Material solidMaterial = Material.OBSIDIAN;
        
        if (random.nextInt(10) < 3) {
            ry = world.getHighestBlockYAt(rx, rz) - 1;
        }
        if (random.nextInt(96) < ry) {
            liquidMaterial = Material.WATER;
            solidMaterial = Material.WATER;
        } else if (world.getBlockAt(rx, ry, rz).getBiome() == Biome.FOREST || world.getBlockAt(rx, ry, rz).getBiome() == Biome.SEASONAL_FOREST) {
            return;
        }
        
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
            if (block.getTypeId() != 0 && (block.getTypeId() < 8 || block.getTypeId() > 11)) {
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
