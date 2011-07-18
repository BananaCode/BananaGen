package net.llamaslayers.minecraft.banana.gen;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.llamaslayers.minecraft.banana.gen.from.com.dinnerbone.bukkit.smooth.WorldRenderer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * @author Nightgunner5
 */
public class GenPlugin extends JavaPlugin implements Runnable {
	/**
	 * A reference to this plugin object for easy access
	 */
	public static GenPlugin instance;

	/**
	 * Internal array of generators
	 */
	public static final Map<String, BananaChunkGenerator> generators;
	static {
		generators = new HashMap<String, BananaChunkGenerator>();
		generators.put("hilly", new HillyGenerator());
		generators.put("mountains", new MountainGenerator());
		generators.put("beach", new BeachGenerator());
	}

	public void onDisable() {
		instance = null;
	}

	public void onEnable() {
		instance = this;
		getServer().getScheduler().scheduleSyncDelayedTask(this, this);
		getCommand("bananaworld").setExecutor(this);
		getCommand("bananagen").setExecutor(this);
		getCommand("bananaregen").setExecutor(this);
	}

	public void run() {
		for (String conf : new String[] {/* CraftBukkit */"bukkit.yml", /* Glowstone */
				"config/glowstone.yml" }) {
			File confFile = new File(conf);
			if (!confFile.exists()) {
				continue;
			}

			Configuration bukkitYML = new Configuration(
					confFile);
			bukkitYML.load();

			List<String> worlds = bukkitYML.getKeys("worlds");
			if (worlds != null) {
				for (String world : worlds) {
					if (bukkitYML.getString(
							"worlds." + world + ".generator",
							".").split(":")[0]
							.equals(getDescription().getName())
							&& getServer().getWorld(world) == null) {
						String generator = bukkitYML.getString(
								"worlds." + world + ".generator");
						getServer().createWorld(world,
								Environment.NORMAL, getDefaultWorldGenerator(world,
										generator.substring(generator.indexOf(':') + 1)));
					}
				}
			}
			return;
		}
		getServer().getLogger().severe("[BananaGen] Could not find server configuration file. What Bukkit implementation are you running?");
	}

	private boolean commandWorld(CommandSender sender, String[] args) {
		if (args.length < 1)
			return false;

		World world = getServer().getWorld(args[0]);
		if (world == null)
			return false;

		Player player;
		if (args.length > 1) {
			player = getServer().getPlayer(args[1]);
			if (player == null)
				return false;
		} else {
			if (sender instanceof Player) {
				player = (Player) sender;
			} else
				return false;
		}
		if (player == sender) {
			if (!sender.hasPermission("bananagen.world.self")) {
				sender.sendMessage("You do not have permission to do that.");
				return true;
			}
		} else {
			if (!sender.hasPermission("bananagen.world.other")) {
				sender.sendMessage("You do not have permission to do that.");
				return true;
			}
		}
		Permission worldPerm = new Permission("bananagen.world.to."
				+ world.getName(), PermissionDefault.OP);
		if (!sender.hasPermission(worldPerm)) {
			sender.sendMessage("You do not have permission to do that.");
			return true;
		}
		player.teleport(world.getSpawnLocation());
		return true;
	}

	private boolean commandGenerate(CommandSender sender, String[] args) {
		if (!sender.hasPermission("bananagen.generate")) {
			sender.sendMessage("You do not have permission to do that.");
			return true;
		}

		if (args.length < 1)
			return false;

		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			return false;
		}

		if (radius < 1)
			return false;

		int x, z;
		if (args.length > 2) {
			try {
				x = Integer.parseInt(args[1]);
				z = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				return false;
			}
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			x = player.getLocation().getBlockX() / 16;
			z = player.getLocation().getBlockZ() / 16;
		} else
			return false;

		World world;
		if (args.length > 3) {
			if (sender instanceof Player
					&& !((Player) sender).getWorld().getName().equals(args[3])) {
				sender.sendMessage("You must be on the world you wish to generate.");
				return true;
			}
			world = getServer().getWorld(args[3]);
		} else if (sender instanceof Player) {
			world = ((Player) sender).getWorld();
		} else
			return false;
		if (world == null)
			return false;

		new WorldRenderer(this, world, sender, x - radius, z - radius, x
				+ radius, z + radius, 10, 10, 10).start();
		return true;
	}

	private boolean commandRegenerate(final CommandSender sender, String[] args) {
		if (!sender.hasPermission("bananagen.regenerate")) {
			sender.sendMessage("You do not have permission to do that.");
			return true;
		}

		if (args.length < 1)
			return false;

		final int radius;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			return false;
		}

		if (radius < 1)
			return false;

		sender.sendMessage("Starting regeneration...");
		final Location start = sender instanceof Player ? ((Player) sender).getLocation()
				: new Location(getServer().getWorlds().get(0), 0, 0, 0);
		new Runnable() {
			private int i = 0;
			private int[] coords;
			private int taskID = -1;
			private final int startX = start.getBlockX() / 16;
			private final int startZ = start.getBlockZ() / 16;

			public void run() {
				if (i >= coords.length / 2) {
					getServer().getScheduler().cancelTask(taskID);
					sender.sendMessage("Regeneration finished.");
					return;
				}
				start.getWorld().regenerateChunk(startX + coords[i * 2], startZ
						+ coords[i * 2 + 1]);
				i++;
			}

			public void schedule() {
				coords = new int[8 * radius * radius + 8 * radius + 2];
				int _i = 0;
				for (int r = 0; r <= radius; r++) {
					for (int x = -r; x <= r; x++) {
						for (int z = -r; z <= r; z++) {
							if (Math.abs(x) == r || Math.abs(z) == r) {
								coords[_i++] = x;
								coords[_i++] = z;
							}
						}
					}
				}

				taskID = getServer().getScheduler().scheduleSyncRepeatingTask(GenPlugin.this, this, 1, 10);
			}
		}.schedule();
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
		String label, String[] args) {
		if (label.equals("bananaworld"))
			return commandWorld(sender, args);
		else if (label.equals("bananagen"))
			return commandGenerate(sender, args);
		else if (label.equals("bananaregen"))
			return commandRegenerate(sender, args);
		throw new IllegalStateException("Unknown command " + label);
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String _id) {
		String[] args = _id.split(",");
		String id = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);
		if (!generators.containsKey(id)) {
			getServer().getLogger().severe(
					"[BananaGen] Unknown generator ID: " + id);
			return null;
		}
		generators.get(id).setWorldArgs(worldName, args);
		return generators.get(id);
	}
}
