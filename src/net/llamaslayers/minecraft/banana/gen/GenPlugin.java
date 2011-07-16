package net.llamaslayers.minecraft.banana.gen;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.llamaslayers.minecraft.banana.gen.from.com.dinnerbone.bukkit.smooth.WorldRenderer;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class GenPlugin extends JavaPlugin implements Runnable, CommandExecutor {
	public static final Map<String, BananaChunkGenerator> generators;
	static {
		generators = new HashMap<String, BananaChunkGenerator>();
		generators.put("hilly", new HillyGenerator());
		generators.put("mountains", new MountainGenerator());
	}

	public void onDisable() {
	}

	public void onEnable() {
		getServer().getScheduler().scheduleSyncDelayedTask(this, this);
		getCommand("bananaworld").setExecutor(this);
		getCommand("bananagen").setExecutor(this);
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
						getServer().createWorld(world,
								Environment.NORMAL, getDefaultWorldGenerator(world, bukkitYML.getString(
										"worlds." + world + ".generator")));
					}
				}
			}
			return;
		}
		getServer().getLogger().severe("[BananaGen] Could not find server configuration file. What Bukkit implementation are you running?");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
		String label, String[] args) {
		if (sender.isOp()) {
			if (label.equals("bananaworld")) {
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
				player.teleport(world.getSpawnLocation());
				return true;
			} else if (label.equals("bananagen")) {
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
			return false;
		}
		return true;
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
