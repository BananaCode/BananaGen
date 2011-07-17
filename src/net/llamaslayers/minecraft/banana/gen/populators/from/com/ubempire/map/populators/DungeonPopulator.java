package net.llamaslayers.minecraft.banana.gen.populators.from.com.ubempire.map.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.noise.SimplexNoiseGenerator;

/**
 * A BlockPopulator that places dungeons around the map.
 * 
 * @author codename_B
 */
public class DungeonPopulator extends BananaBlockPopulator {

    private SimplexNoiseGenerator simplex;
    private Random random;
    private World world;

    @Override
    public void populate(World w, Random rnd, Chunk chunk) {
        simplex = new SimplexNoiseGenerator(rnd);
        random = rnd;
        world = w;
        
        // Randomly turn exposed stone to treasure
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int cx = (chunk.getX() << 4) + x;
                int cz = (chunk.getZ() << 4) + z;
                int y = world.getHighestBlockYAt(cx, cz);
                Block block = chunk.getBlock(x, y - 1, z);
                
                if (block.getType() == Material.STONE && random.nextInt(1024) == 0) {
                    placeChest(block);
                }
            }
        }

        // Go go dungeons
        double density = simplex.noise(chunk.getX() * 16, chunk.getZ() * 16);
        if (density > 0.8) {
            int roomCount = (int) (density * 10) - 3;

            for (int i = 0; i < roomCount; i++) {
                if (random.nextBoolean()) {
                    int x = (chunk.getX() << 4) + random.nextInt(16);
                    int z = (chunk.getZ() << 4) + random.nextInt(16);
                    int y = 12 + random.nextInt(22);

                    int sizeX = random.nextInt(12) + 5;
                    int sizeY = random.nextInt(6) + 4;
                    int sizeZ = random.nextInt(12) + 5;
                    
                    generateRoom(x, y, z, sizeX, sizeY, sizeZ);
                }
            }
        }
    }

    private void generateRoom(int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ) {
        // Fill with air
        for (int x = posX; x < posX + sizeX; x++) {
            for (int y = posY; y < posY + sizeY; y++) {
                for (int z = posZ; z < posZ + sizeZ; z++) {
                    placeBlock(x, y, z, Material.AIR);
                }
            }
        }
        
        // Spawners
        int numSpawners = 1 + random.nextInt(2);
        for (int i = 0; i < numSpawners; ++i) {
            int x = posX + random.nextInt(sizeX);
            int z = posZ + random.nextInt(sizeZ);
            placeSpawner(world.getBlockAt(x, posY, z));
        }
        
        // Chests
        int numChests = numSpawners + random.nextInt(2);
        for (int i = 0; i < numChests; ++i) {
            int x = posX + random.nextInt(sizeX);
            int z = posZ + random.nextInt(sizeZ);
            placeChest(world.getBlockAt(x, posY, z));
        }

        for (int x = posX - 1; x <= posX + sizeX; x++) {
            for (int z = posZ - 1; z <= posZ + sizeZ; z++) {
                placeBlock(x, posY - 1, z, pickStone());
                placeBlock(x, posY + sizeY, z, pickStone());
            }
        }

        for (int y = posY - 1; y <= posY + sizeX; y++) {
            for (int z = posZ - 1; z <= posZ + sizeZ; z++) {
                placeBlock(posX - 1, y, z, pickStone());
                placeBlock(posX + sizeX, y, z, pickStone());
            }
        }

        for (int x = posX - 1; x <= posX + sizeX; x++) {
            for (int y = posY - 1; y <= posY + sizeY; y++) {
                placeBlock(x, y, posZ - 1, pickStone());
                placeBlock(x, y, posZ + sizeZ, pickStone());
            }
        }
    }
    
    private Material pickStone() {
        if (random.nextInt(6) == 0) {
            return Material.MOSSY_COBBLESTONE;
        }
		return Material.COBBLESTONE;
    }

    private void placeSpawner(Block block) {
        CreatureType[] types = new CreatureType[]{
            CreatureType.SKELETON, CreatureType.ZOMBIE, CreatureType.CREEPER, CreatureType.SPIDER
        };
        
        block.setType(Material.MOB_SPAWNER);
        ((CreatureSpawner) block.getState()).setCreatureType(types[random.nextInt(types.length)]);
    }
    
    private void placeChest(Block block) {
        block.setType(Material.CHEST);
        Inventory chest = ((Chest) block.getState()).getInventory();

        for (int i = 0; i < 5; i++) {
            chest.setItem(random.nextInt(chest.getSize()), getRandomTool(i));
            if (i < 5) chest.setItem(random.nextInt(chest.getSize()), getRandomArmor(i));
        }

        chest.setItem(random.nextInt(chest.getSize()), getRandomOre());
    }

    private ItemStack getRandomOre() {
        int i = random.nextInt(255);
        int count = random.nextInt(63) + 1;

        if (i > 253) {
            return new ItemStack(Material.LAPIS_BLOCK, count);
        } else if (i > 230) {
            return new ItemStack(Material.DIAMOND_ORE, count);
        } else if (i > 190) {
            return new ItemStack(Material.GOLD_ORE, count);
        } else if (i > 150) {
            return new ItemStack(Material.IRON_ORE, count);
        } else {
            return new ItemStack(Material.COAL, count);
        }
    }

    private ItemStack getRandomTool(int index) {
        // 0 = sword, 1 = spade, 2 = pickaxe, 3 = axe
        int i = random.nextInt(255);

        if (i > 245) {
            // Diamond
            return new ItemStack(276 + index, 1);
        } else if (i > 230) {
            // Gold
            return new ItemStack(283 + index, 1);
        } else if (i > 190) {
            if (index == 0) {
                // Iron sword
                return new ItemStack(267, 1);
            }
			// Iron items
			return new ItemStack(255 + index, 1);
        } else if (i > 150) {
            // Stone
            return new ItemStack(272 + index, 1);
        } else {
            // Wood
            return new ItemStack(268 + index, 1);
        }
    }

    private ItemStack getRandomArmor(int index) {
        // 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots
        int i = random.nextInt(255);

        if (i > 245) {
            // Diamond
            return new ItemStack(310 + index, 1);
        } else if (i > 230) {
            // Chainmail
            return new ItemStack(302 + index, 1);
        } else if (i > 190) {
            // Gold
            return new ItemStack(314 + index, 1);
        } else if (i > 150) {
            // Iron
            return new ItemStack(306 + index, 1);
        } else {
            // Leather
            return new ItemStack(298 + index, 1);
        }
    }

    private void placeBlock(int x, int y, int z, Material mat) {
        if (canPlaceBlock(x, y, z)) {
            world.getBlockAt(x, y, z).setType(mat);
        }
    }

    private boolean canPlaceBlock(int x, int y, int z) {
        switch (world.getBlockAt(x, y, z).getType()) {
            case AIR:
            case MOB_SPAWNER:
            case CHEST:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
                return false;
            default:
                return true;
        }
    }

}
