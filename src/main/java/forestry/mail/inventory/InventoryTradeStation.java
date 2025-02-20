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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;

import forestry.api.mail.IStamps;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.ItemStackUtil;
import forestry.core.utils.SlotUtil;
import forestry.mail.carriers.trading.TradeStation;

public class InventoryTradeStation extends InventoryAdapter {

	public InventoryTradeStation() {
		super(TradeStation.SLOT_SIZE, "INV");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		List<Integer> slots = new ArrayList<>();

		for (int i = TradeStation.SLOT_LETTERS_1; i < TradeStation.SLOT_LETTERS_1 + TradeStation.SLOT_LETTERS_COUNT; i++) {
			slots.add(i);
		}
		for (int i = TradeStation.SLOT_STAMPS_1; i < TradeStation.SLOT_STAMPS_1 + TradeStation.SLOT_STAMPS_COUNT; i++) {
			slots.add(i);
		}
		for (int i = TradeStation.SLOT_RECEIVE_BUFFER; i < TradeStation.SLOT_RECEIVE_BUFFER + TradeStation.SLOT_RECEIVE_BUFFER_COUNT; i++) {
			slots.add(i);
		}
		for (int i = TradeStation.SLOT_SEND_BUFFER; i < TradeStation.SLOT_SEND_BUFFER + TradeStation.SLOT_SEND_BUFFER_COUNT; i++) {
			slots.add(i);
		}

		int[] slotsInt = new int[slots.size()];
		for (int i = 0; i < slots.size(); i++) {
			slotsInt[i] = slots.get(i);
		}

		return slotsInt;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction side) {
		return SlotUtil.isSlotInRange(slot, TradeStation.SLOT_RECEIVE_BUFFER, TradeStation.SLOT_RECEIVE_BUFFER_COUNT);
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		if (SlotUtil.isSlotInRange(slotIndex, TradeStation.SLOT_SEND_BUFFER, TradeStation.SLOT_SEND_BUFFER_COUNT)) {
			for (int i = 0; i < TradeStation.SLOT_TRADEGOOD_COUNT; i++) {
				ItemStack tradeGood = getItem(TradeStation.SLOT_TRADEGOOD + i);
				if (ItemStackUtil.isIdenticalItem(tradeGood, stack)) {
					return true;
				}
			}
			return false;
		} else if (SlotUtil.isSlotInRange(slotIndex, TradeStation.SLOT_LETTERS_1, TradeStation.SLOT_LETTERS_COUNT)) {
			Item item = stack.getItem();
			return item == Items.PAPER;
		} else if (SlotUtil.isSlotInRange(slotIndex, TradeStation.SLOT_STAMPS_1, TradeStation.SLOT_STAMPS_COUNT)) {
			Item item = stack.getItem();
			return item instanceof IStamps;
		}

		return false;
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		return canSlotAccept(i, itemstack);
	}
}
