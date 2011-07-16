package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * BlockPopulator that turns deserts into sand and places cacti.
 */
public class DesertPopulator extends BananaBlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int tx = (chunk.getX() << 4) + x;
                int tz = (chunk.getZ() << 4) + z;
                int y = world.getHighestBlockYAt(tx, tz);
                
                Block block = chunk.getBlock(x, y, z).getFace(BlockFace.DOWN);
                if (block.getBiome() != Biome.DESERT) continue;
                
                // Set top few layers of grass/dirt to sand
                for (int i = 0; i < 5; ++i) {
                    Block b2 = block.getFace(BlockFace.DOWN, i);
                    if (b2.getType() == Material.GRASS || b2.getType() == Material.DIRT) {
                        b2.setType(Material.SAND);
                    }
                }
                
                // Generate cactus
                if (block.getType() == Material.SAND) {
                    if (random.nextInt(20) == 0) {
                        // Make sure it's surrounded by air
                        Block base = block.getFace(BlockFace.UP);
                        if (base.getTypeId() == 0 && base.getFace(BlockFace.NORTH).getTypeId() == 0 && base.getFace(BlockFace.EAST).getTypeId() == 0 && base.getFace(BlockFace.SOUTH).getTypeId() == 0 & base.getFace(BlockFace.WEST).getTypeId() == 0) {
                            generateCactus(base, random.nextInt(4));
                        }
                    }
                }
            }
        }
    }

    private void generateCactus(Block block, int height) {
        for (int i = 0; i < height; ++i) {
            block.getFace(BlockFace.UP, i).setType(Material.CACTUS);
        }
    }
    
}
