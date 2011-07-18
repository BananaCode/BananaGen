package net.llamaslayers.minecraft.banana.gen.from.com.dinnerbone.bukkit.smooth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("javadoc")
public class WorldRenderer implements Runnable {
	private final int total;
	private int count;
	private final Plugin plugin;
	private final World world;
	private final int endz;
	private final int stepx;
	private final int stepz;
	private final CommandSender sender;
	private final int startx;
	private final int startz;
	private final int endx;
	private final int speed;
	private int curx;
	private int curz;
	private int lastPct = -1;
	private int jobid = -1;

	public WorldRenderer(Plugin plugin, World world, CommandSender sender,
			int startx, int startz, int endx, int endz, int stepx, int stepz,
			int speed) {
		this.sender = sender;
		this.plugin = plugin;
		this.world = world;
		this.endz = endz;
		this.stepx = stepx;
		this.stepz = stepz;
		this.startx = startx;
		this.startz = startz;
		this.endx = endx;
		this.speed = speed;

		curx = startx;
		curz = startz;
		total = (endx - startx) * (endz - startz);
	}

	public void start() {
		if (jobid < 0) {
			jobid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 1, speed);
			sender.sendMessage(ChatColor.BLUE + "Starting generation from "
					+ startx + "," + startz + " to " + endx + "," + endz
					+ " - " + total + " total chunks. Estimated time: "
					+ ((endx - startx) / stepx * (endz - startz) / stepz
							* speed / 20) + " seconds.");
		}
	}

	public void stop() {
		if (jobid >= 0) {
			Bukkit.getServer().getScheduler().cancelTask(jobid);
		}
	}

	@Override
	public void run() {
		loadChunks(world, curx, curz, Math.min(curx + stepx, endx), Math.min(curz
				+ stepz, endz));

		curx += stepx;

		if (curx >= endx) {
			curz += stepz;
			curx = startx;

			if (curz >= endz) {
				sender.sendMessage(ChatColor.BLUE + "Generation complete. "
						+ count + "/" + total + " chunks rendered.");
				stop();
				return;
			}
		}

		int pct = Math.round(((float) count / total) * 100);
		if (pct != lastPct) {
			lastPct = pct;
			sender.sendMessage(ChatColor.GREEN.toString() + count + "/" + total
					+ " chunks generated. " + pct + "% done.");
		}
	}

	private void loadChunks(World world, int sx, int sz, int ex, int ez) {
		for (int x = sx; x < ex; x++) {
			for (int z = sz; z < ez; z++) {
				loadChunk(world, x, z);
				count++;
			}
		}

		for (int x = sx; x < ex; x++) {
			for (int z = sz; z < ez; z++) {
				unloadChunk(world, x, z);
			}
		}
	}

	private static void loadChunk(World world, int x, int z) {
		world.loadChunk(x + 1, z);
		world.loadChunk(x - 1, z);
		world.loadChunk(x, z);
		world.loadChunk(x, z + 1);
		world.loadChunk(x, z - 1);
	}

	private static void unloadChunk(World world, int x, int z) {
		world.unloadChunkRequest(x + 1, z);
		world.unloadChunkRequest(x - 1, z);
		world.unloadChunkRequest(x, z);
		world.unloadChunkRequest(x, z + 1);
		world.unloadChunkRequest(x, z - 1);
	}
}