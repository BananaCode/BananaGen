package net.llamaslayers.minecraft.banana.gen.populators;

import java.util.Random;

import net.llamaslayers.minecraft.banana.gen.BananaBlockPopulator;
import net.llamaslayers.minecraft.banana.gen.BananaChunkGenerator;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * BlockPopulator that calls all other populators in a specific sequence.
 * 
 * @author codename_B
 */
public class MetaPopulator extends BlockPopulator {
	private final BananaChunkGenerator generator;
	/**
	 * Internal array of populators
	 */
	public final BananaBlockPopulator[] list;

	/**
	 * @param generator
	 *            The generator creating this populator. It will be used in
	 *            {@link BananaBlockPopulator#populate(BananaChunkGenerator, World, Random, Chunk)}
	 */
	public MetaPopulator(BananaChunkGenerator generator) {
		this.generator = generator;

		list = new BananaBlockPopulator[] {
				// In-ground
				new QuarryPopulator(),
				new LakePopulator(),
				// On-ground
				// Desert is before tree and mushroom but snow is after so trees have snow on top
				new DesertPopulator(), new RuinsPopulator(),
				new TreePopulator(), new MushroomPopulator(),
				new SnowPopulator(), new FlowerPopulator(),
				// Below-ground
				new SpookyRoomPopulator(), new DungeonPopulator(),
				new CavePopulator(), new TorchPopulator(), new OrePopulator()
		};
	}

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 *      java.util.Random, org.bukkit.Chunk)
	 * @see BananaBlockPopulator#populate(BananaChunkGenerator, World, Random,
	 *      Chunk)
	 */
	@Override
	public void populate(World world, Random random, Chunk source) {
		for (BananaBlockPopulator pop : list) {
			pop.populate(generator, world, random, source);
		}
	}

}
