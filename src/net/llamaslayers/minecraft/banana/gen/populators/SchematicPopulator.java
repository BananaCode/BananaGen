package net.llamaslayers.minecraft.banana.gen.populators;

/**
 * Populates using .schematic files. .schematic files can be created easily
 * using WorldEdit.
 *
 * @author Nightgunner5
 */
public abstract class SchematicPopulator extends BuildingPopulator {
	protected SchematicPopulator(String category) {
		super(category, "schematics");
	}
}
