/**
 * 
 */
package net.llamaslayers.minecraft.banana.gen.populators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.OperationNotSupportedException;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Populates using BO2 files. An editor is available at <a
 * href="http://faskerstudio.com/minecraft/BBOB/"
 * >http://faskerstudio.com/minecraft/BBOB/</a>
 * 
 * @author Nightgunner5
 */
public abstract class BuildingPopulator extends BananaBlockPopulator {
	final Map<String, Building> buildings;
	private final Map<String, List<Building>> groups;

	/**
	 * @param category
	 *            The folder to look for building .bo2 files in
	 */
	protected BuildingPopulator(String category) {
		buildings = new HashMap<String, Building>();
		groups = new HashMap<String, List<Building>>();
		Enumeration<JarEntry> schematics;
		JarFile jar;
		try {
			URLClassLoader classLoader = (URLClassLoader) getClass().getClassLoader();
			URL jarUrl = classLoader.getURLs()[0];
			jar = new JarFile(jarUrl.toString().replace("file:/", "").replace("%20", " "));
			schematics = jar.entries();
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		while (schematics.hasMoreElements()) {
			JarEntry schematic = schematics.nextElement();
			if (!schematic.getName().startsWith("buildings/" + category + "/")) {
				continue;
			}

			String buildingName = schematic.getName();
			try {
				buildingName = buildingName.substring(buildingName.lastIndexOf('/') + 1);
				buildingName = buildingName.substring(0, buildingName.lastIndexOf('.'));
			} catch (StringIndexOutOfBoundsException ex) {
				continue;
			}
			Building building = new Building(buildingName);
			buildings.put(buildingName, building);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						jar.getInputStream(schematic)));
				while (building.parseLine(in.readLine())) {
					;
				}
				in.close();
				building.produceRotatedVersions();
			} catch (IOException ex) {
				ex.printStackTrace();
				buildings.remove(buildingName);
			}
		}
	}

	/**
	 * Returns this populator's building list
	 * 
	 * @return All buildings
	 */
	protected List<Building> getAllBuildings() {
		return new ArrayList<Building>(buildings.values());
	}

	/**
	 * Returns a random building
	 * 
	 * @param random
	 *            A random number generator to be used in calculations
	 * @return One random building or null if there are none to choose from
	 */
	protected Building getAnyBuilding(Random random) {
		List<Building> acceptable = getAllBuildings();

		if (acceptable.size() == 0)
			return null;

		return acceptable.get(random.nextInt(acceptable.size()));
	}

	/**
	 * Returns a list of buildings that could be placed at the given block
	 * 
	 * @param seed
	 *            The block where location 0, 0, 0 would be
	 * @return All buildings that could be placed at the given block
	 */
	protected List<Building> getPossibleBuildings(Block seed) {
		List<Building> acceptable = new ArrayList<Building>();
		for (Building building : buildings.values()) {
			if (building.canPlaceOn(seed)) {
				acceptable.add(building);
			}
		}

		return acceptable;
	}

	/**
	 * Returns a random building that could be placed at the given block
	 * 
	 * @param seed
	 *            The block where location 0, 0, 0 would be
	 * @param random
	 *            A random number generator to be used in calculations
	 * @return One random building or null if there are none to choose from
	 */
	protected Building getRandomBuilding(Block seed, Random random) {
		List<Building> acceptable = getPossibleBuildings(seed);

		if (acceptable.size() == 0)
			return null;

		return acceptable.get(random.nextInt(acceptable.size()));
	}

	void addToGroup(String group, Building building) {
		if (!groups.containsKey(group)) {
			groups.put(group, new ArrayList<Building>());
		}
		groups.get(group).add(building);
	}

	/**
	 * A parsed .bo2 file
	 * 
	 * @author Nightgunner5
	 */
	protected class Building {
		/**
		 * The file name of this building
		 */
		public final String name;
		private final List<BuildingBlock> blocks;
		private Biome[] biomes;
		private boolean randomRotation;
		private boolean underfill;
		private boolean spawnLava;
		private boolean spawnWater;
		private boolean spawnDark;
		private int[] spawnBlockType;
		private boolean spawnLight;
		private boolean needsFoundation;
		private boolean dig;
		private int rarity;
		private int collisionPercent;
		private int heightMin = 0;
		private int heightMax = 127;
		private int maxX;
		private int minX;
		private int maxY;
		private int minY;
		private int maxZ;
		private int minZ;
		private int section = 0;
		private static final int META = 1;
		private static final int DATA = 2;
		private String group;

		Building(String name) {
			this.name = name;
			blocks = new ArrayList<BuildingBlock>();
		}

		private Building(Building parent, int rot) {
			name = parent.name + "/" + rot;
			blocks = new ArrayList<BuildingBlock>();
			underfill = parent.underfill;
			spawnLight = parent.spawnLight;
			spawnDark = parent.spawnDark;
			spawnWater = parent.spawnWater;
			spawnLava = parent.spawnLava;
			spawnBlockType = parent.spawnBlockType;
			biomes = parent.biomes;
			needsFoundation = parent.needsFoundation;
			dig = parent.dig;
			rarity = parent.rarity;
			collisionPercent = parent.collisionPercent;
			heightMin = parent.heightMin;
			heightMax = parent.heightMax;
			if (parent.group != null) {
				group = parent.group;
				addToGroup(group, this);
			}
			for (BuildingBlock block : parent.blocks) {
				BuildingBlock newBlock = block.rotate(rot);
				maxX = Math.max(maxX, newBlock.x);
				minX = Math.min(minX, newBlock.x);
				maxY = Math.max(maxY, newBlock.y);
				minY = Math.min(minY, newBlock.y);
				maxZ = Math.max(maxZ, newBlock.z);
				minZ = Math.min(minZ, newBlock.z);
				blocks.add(newBlock);
			}
		}

		boolean parseLine(String line) {
			if (line == null)
				return false;
			if (line.trim().length() == 0)
				return true;

			if (line.equals("[META]")) {
				section = META;
				return true;
			}
			if (line.equals("[DATA]")) {
				section = DATA;
				return true;
			}
			if (section == META) {
				String[] parts = line.split("=", 2);
				String key = parts[0].toLowerCase();
				String value = parts[1].toLowerCase();

				if (key.equals("version")) {
					if (!value.equals("2.0"))
						throw new RuntimeException("In " + name
								+ ".bo2: Expecting version 2.0; got version "
								+ value);
					return true;
				}
				if (key.equals("spawninbiome")) {
					if (biomes != null)
						throw new RuntimeException("In " + name
								+ ".bo2: multiple spawninbiome declarations");
					if (value.equals("all")) {
						biomes = Biome.values();
					} else {
						String[] biomeNames = value.toUpperCase().split(",");
						biomes = new Biome[biomeNames.length];
						for (int i = 0; i < biomeNames.length; i++) {
							biomes[i] = Biome.valueOf(biomeNames[i].replace(' ', '_').replace("RAIN_FOREST", "RAINFOREST"));
						}
					}
					Arrays.sort(biomes);
					return true;
				}
				if (key.equals("randomrotation")) {
					randomRotation = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("spawnonblocktype")) {
					String[] blockTypes = value.split(",");
					spawnBlockType = new int[blockTypes.length];
					for (int i = 0; i < blockTypes.length; i++) {
						spawnBlockType[i] = Integer.parseInt(blockTypes[i]);
					}
					Arrays.sort(spawnBlockType);
					return true;
				}
				if (key.equals("spawnsunlight")) {
					spawnLight = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("spawndarkness")) {
					spawnDark = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("spawnwater")) {
					spawnWater = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("spawnlava")) {
					spawnLava = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("underfill")) {
					underfill = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("dig")) {
					dig = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("tree"))
					return true; // TODO, currently unsupported
				if (key.equals("branch"))
					return true; // TODO, currently unsupported
				if (key.equals("diggingbranch"))
					return true; // TODO, currently unsupported
				if (key.equals("needsfoundation")) {
					needsFoundation = Boolean.parseBoolean(value);
					return true;
				}
				if (key.equals("rarity")) {
					rarity = Integer.parseInt(value);
					return true;
				}
				if (key.equals("collisionpercentage")) {
					collisionPercent = Integer.parseInt(value);
					return true;
				}
				if (key.equals("spawnelevationmin")) {
					heightMin = Integer.parseInt(value);
					return true;
				}
				if (key.equals("spawnelevationmax")) {
					heightMax = Integer.parseInt(value);
					return true;
				}
				if (key.equals("groupid")) {
					group = value.toLowerCase();
					addToGroup(group, this);
					return true;
				}
				if (key.equals("groupfrequencymin")
						|| key.equals("groupfrequencymax")
						|| key.equals("groupseperationmin")
						|| key.equals("groupseperationmax"))
					return true; // TODO, currently unsupported
				if (key.equals("branchlimit"))
					return true; // TODO, currently unsupported
			}
			if (section == DATA) {
				try {
					BuildingBlock block = new BuildingBlock(line);
					maxX = Math.max(maxX, block.x);
					minX = Math.min(minX, block.x);
					maxY = Math.max(maxY, block.y);
					minY = Math.min(minY, block.y);
					maxZ = Math.max(maxZ, block.z);
					minZ = Math.min(minZ, block.z);
					blocks.add(block);
					return true;
				} catch (OperationNotSupportedException ex) {
					throw new RuntimeException("In " + name + ".bo2: "
							+ ex.getMessage());
				}
			}

			throw new RuntimeException("Unparsed line in " + name + ".bo2: "
					+ line);
		}

		void produceRotatedVersions() {
			if (randomRotation) {
				for (int i = 1; i < 4; i++) {
					Building rotated = new Building(this, i);
					buildings.put(rotated.name, rotated);
				}
			}
		}

		/**
		 * Determine if this building can be placed on a given block
		 * 
		 * @param block
		 *            The block where location 0, 0, 0 would be
		 * @return true if the building can be placed, false if it cannot
		 */
		public boolean canPlaceOn(Block block) {
			if (block.getY() > heightMax || block.getY() < heightMin)
				//debug("Start Y out of range");
				return false;
			if (block.getY() + maxY > 127 || block.getY() + minY < 0)
				//debug("End Y out of range");
				return false;

			if (spawnBlockType != null
					&& Arrays.binarySearch(spawnBlockType, block.getRelative(BlockFace.DOWN).getTypeId()) < 0)
				//debug("Trying to spawn on ", Integer.toString(block.getTypeId()),
				//		", allowed are ", Arrays.toString(spawnBlockType));
				return false;
			if (biomes != null
					&& Arrays.binarySearch(biomes, block.getBiome()) < 0)
				//debug("Trying to spawn in ", block.getBiome().name(), ", allowed are ", Arrays.toString(biomes));
				return false;

			byte light = block.getLightLevel();
			if (block.getWorld().getHighestBlockYAt(block.getX(), block.getZ()) - 1 == block.getY()) {
				light = 15;
			}
			if (light < 4 && !spawnDark)
				//debug("Trying to spawn in the dark, but not allowed to");
				return false;
			if (light >= 4 && !spawnLight)
				//debug("Trying to spawn in the light, but not allowed to");
				return false;

			if (!spawnWater
					&& (checkFor(Material.WATER, block) || checkFor(Material.STATIONARY_WATER, block)))
				//debug("Water in the way");
				return false;
			if (!spawnLava
					&& (checkFor(Material.LAVA, block) || checkFor(Material.STATIONARY_LAVA, block)))
				//debug("Lava in the way");
				return false;

			if (getCollisionPercent(block) > collisionPercent)
				//debug("Collision: ", Integer.toString(getCollisionPercent(block)), " > ", Integer.toString(collisionPercent));
				return false;

			if (needsFoundation && !underfill) {
				if (!isSupported(block))
					//debug("Unsupported, but needs foundation");
					return false;
			}

			if (checkFor(Material.BEDROCK, block))
				//debug("Trying to spawn on bedrock");
				return false;
			//debug("Success!");
			return true;
		}

		/*private void debug(String... strings) {
			StringBuilder sb = new StringBuilder(name).append(": ");
			for (String string : strings) {
				sb.append(string);
			}
			System.out.println(sb.toString());
		}*/

		private boolean isSupported(Block block) {
			for (BuildingBlock b : blocks) {
				Block relative = block.getRelative(b.x, b.y, b.z);

				if (b.y == minY) {
					relative = relative.getRelative(BlockFace.DOWN);
					if (!relative.isLiquid() && !relative.isEmpty())
						return false;
				}
			}
			return true;
		}

		private int getCollisionPercent(Block block) {
			int count = 0;
			for (BuildingBlock b : blocks) {
				Block relative = block.getRelative(b.x, b.y, b.z);
				if (!relative.isLiquid() && !relative.isEmpty()) {
					count++;
				}
			}
			return count * 100 / blocks.size();
		}

		private boolean checkFor(Material material, Block block) {
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						if (block.getRelative(x, y, z).getType() == material)
							return true;
					}
				}
			}
			return false;
		}

		/**
		 * Place a building
		 * 
		 * @param seed
		 *            The block where location 0, 0, 0 would be
		 * @param random
		 *            A random number generator to be used in calculations
		 */
		public void place(Block seed, Random random) {
			if (!canPlaceOn(seed))
				return;

			if (underfill) {
				for (BuildingBlock block : blocks) {
					if (block.y != minY) {
						continue;
					}
					Block replace = seed.getRelative(block.x, block.y, block.z);
					while (replace.isEmpty()) {
						replace.setType(seed.getType());
						replace = replace.getRelative(BlockFace.DOWN);
					}
				}
			}

			for (BuildingBlock block : blocks) {
				Block replace = seed.getRelative(block.x, block.y, block.z);
				if (replace.isEmpty() || replace.isLiquid()) {
					replace.setTypeIdAndData(block.id, block.data, false);
				} else if (dig) {
					replace.setTypeIdAndData(block.id, block.data, false);
				}
			}
		}

		/**
		 * Place a building, respecting rarity
		 * 
		 * @param seed
		 *            The block where location 0, 0, 0 would be
		 * @param random
		 *            A random number generator to be used in calculations
		 */
		public void maybePlace(Block seed, Random random) {
			if (random.nextInt(100) < rarity)
				return;
			place(seed, random);
		}

		/**
		 * Place a building with random blocks skipped
		 * 
		 * @param seed
		 *            The block where location 0, 0, 0 would be
		 * @param random
		 *            A random number generator to be used in calculations
		 * @param min
		 *            The minimum percent of blocks to skip
		 * @param max
		 *            The maximum percent of blocks to skip
		 */
		public void placeDestroyed(Block seed, Random random, int min, int max) {
			if (!canPlaceOn(seed))
				return;

			if (min > max)
				return;
			if (min >= 100)
				return;

			if (underfill) {
				for (BuildingBlock block : blocks) {
					if (block.y != minY) {
						continue;
					}
					Block replace = seed.getRelative(block.x, block.y, block.z);
					while (replace.isEmpty()) {
						replace.setType(seed.getType());
						replace = replace.getRelative(BlockFace.DOWN);
					}
				}
			}

			for (BuildingBlock block : blocks) {
				if (random.nextInt(100 - min) < max - min) {
					continue;
				}
				Block replace = seed.getRelative(block.x, block.y, block.z);
				if (replace.isEmpty() || replace.isLiquid()) {
					replace.setTypeIdAndData(block.id, block.data, false);
				} else if (dig) {
					replace.setTypeIdAndData(block.id, block.data, false);
				}
			}
		}

		/**
		 * Place a building with random blocks skipped, respecting rarity
		 * 
		 * @param seed
		 *            The block where location 0, 0, 0 would be
		 * @param random
		 *            A random number generator to be used in calculations
		 * @param min
		 *            The minimum percent of blocks to skip
		 * @param max
		 *            The maximum percent of blocks to skip
		 */
		public void maybePlaceDestroyed(Block seed, Random random, int min,
			int max) {
			if (random.nextInt(100) < rarity)
				return;
			placeDestroyed(seed, random, min, max);
		}
	}

	static class BuildingBlock {
		public final int x;
		public final int y;
		public final int z;
		public final byte id;
		public final byte data;
		private static final Pattern pattern =
				Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\#(\\d+))?(?:\\@(\\d+))?$");

		BuildingBlock(String line) throws OperationNotSupportedException {
			String[] halves = line.split(":");
			String[] coords = halves[0].split(",");
			x = Integer.parseInt(coords[0]);
			y = Integer.parseInt(coords[2]);
			z = Integer.parseInt(coords[1]);

			Matcher remaining = pattern.matcher(halves[1]);
			remaining.find();
			id = (byte) Integer.parseInt(remaining.group(1));
			data = remaining.group(2) == null ? 0
					: Byte.parseByte(remaining.group(2));
			if (remaining.group(3) != null || remaining.group(4) != null)
				throw new OperationNotSupportedException("Branches are not yet supported");
		}

		private BuildingBlock(int x, int y, int z, byte id, byte data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.id = id;
			this.data = data;
		}

		BuildingBlock rotate(int rot) {
			int newX, newZ;
			switch (rot) {
			case 0: // north
				newX = x;
				newZ = z;
				break;
			case 1: // east
				newX = z;
				newZ = -x;
				break;
			case 2: // south
				newX = -x;
				newZ = -z;
				break;
			case 3: // west
				newX = -z;
				newZ = x;
				break;
			default:
				throw new IllegalArgumentException("rot = " + rot
						+ "; must be 0 <= rot < 4");
			}
			return new BuildingBlock(newX, y, newZ, id, data);
		}
	}
}
