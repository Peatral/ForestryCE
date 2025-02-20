/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import net.minecraft.nbt.CompoundTag;

public interface INbtWritable {
	CompoundTag write(CompoundTag nbt);
}
