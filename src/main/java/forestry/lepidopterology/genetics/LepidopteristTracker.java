/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import forestry.api.genetics.ForestrySpeciesTypes;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.lepidopterology.ILepidopteristTracker;
import forestry.api.lepidopterology.genetics.IButterfly;
import forestry.core.genetics.BreedingTracker;
import forestry.core.utils.SpeciesUtil;

import genetics.api.individual.IIndividual;

public class LepidopteristTracker extends BreedingTracker implements ILepidopteristTracker {

	/**
	 * Required for creation from map storage
	 */
	public LepidopteristTracker() {
		super();
	}

	public LepidopteristTracker(CompoundTag tag) {
		super(tag);
	}

	@Override
	protected IBreedingTracker getBreedingTracker(Player player) {
		//TODO world cast
		return SpeciesUtil.BUTTERFLY_TYPE.get().getBreedingTracker(player.level, player.getGameProfile());
	}

	@Override
	protected ResourceLocation getSpeciesId() {
		return ForestrySpeciesTypes.BUTTERFLY;
	}

	@Override
	public void registerPickup(IIndividual individual) {
	}

	@Override
	public void registerCatch(IButterfly butterfly) {
		registerSpecies(butterfly.getGenome().getPrimarySpecies());
		registerSpecies(butterfly.getGenome().getSecondarySpecies());
	}

}
