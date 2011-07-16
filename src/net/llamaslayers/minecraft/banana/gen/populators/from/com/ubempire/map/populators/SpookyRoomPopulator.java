package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

/**
 * BlockPopulator that generates stone quarries.
 */
public class SpookyRoomPopulator extends BananaBlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        if (random.nextInt(200) >= 1) {
            return;
        }

        int sizeX = 16;
        int sizeY = 12;
        int sizeZ = 16;
        int spawnerChance = 400; // Spawners are generated at a chance of 12/400
        Material matWalls = Material.NETHERRACK;
        Material matFloor = Material.SOUL_SAND;
        Material matDecor = Material.WEB;

        //Editing is over
        int lengthH = sizeX / 2;
        int heightH = sizeY / 2;
        int widthH = sizeZ / 2;
        int centerX = chunk.getX() * 16 + 8;
        int centerY = world.getHighestBlockYAt(chunk.getX() * 16 + 8, chunk.getZ() * 16 + 8) / 2;
        int centerZ = chunk.getZ() * 16 + 8;
        int minX = centerX - lengthH;
        int maxX = centerX + lengthH;
        int minY = centerY - heightH;
        int maxY = centerY + heightH;
        int minZ = centerZ - widthH;
        int maxZ = centerZ + widthH;

        // Step 1: Cuboid generation around the entire area
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                world.getBlockAt(x, y, minZ).setType(matWalls);
                world.getBlockAt(x, y, maxZ).setType(matWalls);
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(minX, y, z).setType(matWalls);
                world.getBlockAt(maxX, y, z).setType(matWalls);
            }
        }

        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                world.getBlockAt(x, minY, z).setType(matWalls);
                world.getBlockAt(x, maxY, z).setType(matWalls);
            }
        }
        
        // Step 2: Add netherrack and web noise to walls (XY, YZ)
        minX++; maxX--;
        minY++; maxY--;
        minZ++; maxZ--;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                world.getBlockAt(x, y, minZ).setType(pickDecor(random, matDecor, matWalls));
                world.getBlockAt(x, y, maxZ).setType(pickDecor(random, matDecor, matWalls));
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(minX, y, z).setType(pickDecor(random, matDecor, matWalls));
                world.getBlockAt(maxX, y, z).setType(pickDecor(random, matDecor, matWalls));
            }
        }

        // Step 3: Generate a floor of soul sand/mobs and a ceiling of netherrack
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                int floor = random.nextInt(spawnerChance); // spawner rate
                Block block = world.getBlockAt(x, minY, z);
                if (floor < 12) {
                    block.setType(Material.MOB_SPAWNER);
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    if (floor <= 2) {
                        spawner.setCreatureType(CreatureType.ZOMBIE);
                    } else if (floor >= 3 && floor <= 7) {
                        spawner.setCreatureType(CreatureType.SPIDER);
                    } else if (floor >= 8 && floor <= 10) {
                        spawner.setCreatureType(CreatureType.SKELETON);
                    } else {
                        spawner.setCreatureType(CreatureType.GHAST);
                    }
                } else {
                    block.setType(matFloor);
                }
                world.getBlockAt(x, maxY, z).setType(matWalls);
            }
        }

        // Step 4: Stalagmites
        minX++; maxX--;
        minY++; maxY--;
        minZ++; maxZ--;
        
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getFace(BlockFace.DOWN).getType() != Material.AIR && block.getFace(BlockFace.DOWN).getType() != matDecor) {
                        int rand = random.nextInt(10);
                        if (rand <= 6) {
                            world.getBlockAt(x, y, z).setType(matFloor);
                        } else {
                            world.getBlockAt(x, y, z).setType(pickDecor(random, matDecor, Material.AIR));
                        }
                    } else {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }
    }

    private Material pickDecor(Random random, Material decor, Material wall) {
        return (random.nextInt(5) == 0) ? decor : wall;
    }

}
