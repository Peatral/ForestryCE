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
package forestry.mail.inventory;

import com.google.common.collect.ImmutableSet;

import forestry.api.mail.ILetter;
import forestry.mail.v2.LetterUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import forestry.api.core.IErrorSource;
import forestry.api.core.IError;
import forestry.api.core.ForestryError;
import forestry.core.inventory.ItemInventory;
import forestry.core.items.ItemWithGui;
import forestry.core.utils.SlotUtil;
import forestry.mail.items.ItemStamp;

public class ItemInventoryLetter extends ItemInventory implements IErrorSource {
	private final ILetter letter;

	public ItemInventoryLetter(Player player, ItemStack itemstack) {
		super(player, 0, itemstack);
		letter = LetterUtils.getLetter(itemstack).orElseThrow();
	}

	public ILetter getLetter() {
		return letter;
	}

	public void onLetterClosed() {
		ItemStack parent = getParent();
		//setParent(LetterProperties.closeLetter(parent, letter));
	}

	public void onLetterOpened() {
		ItemStack parent = getParent();
		//setParent(LetterProperties.openLetter(parent));
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack result = letter.removeItem(index, count);
		LetterUtils.setLetter(getParent(), letter);
		return result;
	}

	@Override
	public void setItem(int index, ItemStack itemstack) {
		letter.setItem(index, itemstack);
		LetterUtils.setLetter(getParent(), letter);
	}

	@Override
	public ItemStack getItem(int i) {
		return letter.getItem(i);
	}

	@Override
	public int getContainerSize() {
		return letter.getContainerSize();
	}

	@Override
	public int getMaxStackSize() {
		return letter.getMaxStackSize();
	}

	@Override
	public boolean stillValid(Player player) {
		return letter.stillValid(player);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return letter.removeItemNoUpdate(slot);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		if (letter.isProcessed()) {
			return false;
		} else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_POSTAGE_1, Letter.SLOT_POSTAGE_COUNT)) {
			Item item = stack.getItem();
			return item instanceof ItemStamp;
		} else if (SlotUtil.isSlotInRange(slotIndex, Letter.SLOT_ATTACHMENT_1, Letter.SLOT_ATTACHMENT_COUNT)) {
			return !(stack.getItem() instanceof ItemWithGui);
		}
		return false;
	}

	/* IErrorSource */
	@Override
	public ImmutableSet<IError> getErrors() {

		ImmutableSet.Builder<IError> errorStates = ImmutableSet.builder();

		if (!letter.hasRecipient()) {
			errorStates.add(ForestryError.NO_RECIPIENT);
		}

		if (!letter.isProcessed() && !letter.isPostPaid()) {
			errorStates.add(ForestryError.NOT_POST_PAID);
		}

		return errorStates.build();
	}
}
