/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.farming;

import java.util.Collection;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * The IFarmLogic is used by farm blocks and multi-blocks to cultivate and harvest crops and plants.
 * <p>
 * Every farm block has only ony one logic a multi-block farm has four, one for every direction.
 */
//TODO: Add cleaning function that removes all crops and cultivation blocks
//TODO: Clean Up
public interface IFarmLogic {

	/**
	 * Collects all items that are laying on the ground and are in the {@link IFarmHousing#getArea()} of the farm.
	 *
	 * @param world       The world of the farm.
	 * @param farmHousing The farm that uses this logic.
	 * @return A collection that contains all items that were collected.
	 */
	default List<ItemStack> collect(Level world, IFarmHousing farmHousing) {
		return NonNullList.create();
	}

	/**
	 * Tries to cultivate one or more blocks at the given position and with the given extent.
	 *
	 * @param world       The world of the farm.
	 * @param farmHousing The farm that uses this logic.
	 * @param pos         The position at that the logic should start to cultivate.
	 * @param direction   The direction of the extension.
	 * @param extent      How many blocks this logic has to cultivate after it cultivated the block at the given position.
	 *                    The positions of the next blocks are having a offset in the given direction.
	 * @return True if the logic has cultivated any block.
	 */
	default boolean cultivate(Level world, IFarmHousing farmHousing, BlockPos pos, Direction direction, int extent) {
		return false;
	}

	/**
	 * Tries to harvest one or more blocks at the given position and with the given extent.
	 *
	 * @param world     The world of the farm.
	 * @param housing   The farm that uses this logic.
	 * @param direction The direction of the extension.
	 * @param extent    How many blocks this logic tries to harvest after it has tried to harvested the block at the given position.
	 *                  The positions of the next blocks are having a offset in the given direction.
	 * @param pos       The position at that the logic should start to harvest.
	 * @return True if the logic has cultivated any block.
	 */
	Collection<ICrop> harvest(Level world, IFarmHousing housing, Direction direction, int extent, BlockPos pos);

	/**
	 * Returns the {@link IFarmProperties} that created this logic.
	 *
	 * @return Returns the {@link IFarmProperties} that created this logic.
	 */
	IFarmProperties getProperties();

	boolean isManual();
}
