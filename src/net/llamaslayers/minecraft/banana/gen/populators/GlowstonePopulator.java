package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.ArrayList;
import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class GlowstonePopulator extends BananaBlockPopulator {

	@Override
	public void populate(World world, Random rand, Chunk chunk) {
	if(rand.nextInt(8) <= 2)
	{
	ArrayList<Location> glowstonePillar = glowstonePillar(world, chunk);
	for(Location glow : glowstonePillar)
	{
	glow.getBlock().setType(Material.GLOWSTONE);
	}
	}
	}

	public ArrayList<Location> glowstonePillar(World world, Chunk chunk)
	{
		ArrayList<Location> blocks = new ArrayList<Location>();
		for(int y=125; y>0; y++)
		{
		ChunkSnapshot snap = chunk.getChunkSnapshot();
		int id = snap.getBlockTypeId(8, y, 8);
		int id2 = snap.getBlockTypeId(8, y-1, 8);
		if(id==Material.NETHERRACK.getId())
		{
		Location l = new Location(world, chunk.getX()*16+8, y, chunk.getZ()*16+8);
		blocks.add(l);
		if(id2==0)
		{
		Location lu = new Location(world, chunk.getX()*16+8+1, y, chunk.getZ()*16+8);
		Location ld = new Location(world, chunk.getX()*16+8-1, y, chunk.getZ()*16+8);
		Location ll = new Location(world, chunk.getX()*16+8, y, chunk.getZ()*16+8+1);
		Location lr = new Location(world, chunk.getX()*16+8, y, chunk.getZ()*16+8-1);
		Location ldd = new Location(world, chunk.getX()*16+8, y+1, chunk.getZ()*16+8);
		blocks.add(lu);
		blocks.add(ld);
		blocks.add(ll);
		blocks.add(lr);
		blocks.add(ldd);
		}
		}
		else
		{
		break;
		}
		}
		return blocks;
	}
	
	
	
	
}
