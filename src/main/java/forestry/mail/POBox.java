package forestry.mail;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.mail.ILetter;
import forestry.api.mail.v2.carrier.ICarrierType;
import forestry.mail.v2.IWatchable;
import forestry.mail.v2.LetterUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;

import java.util.*;

public class POBox implements Container, IWatchable {
    public static final short SLOT_SIZE = 84;

    public static final Codec<POBox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ItemStack.CODEC).fieldOf("letters").forGetter(pobox -> InventoryUtil.getStacks(pobox.letters))
    ).apply(instance, POBox::new));

    private final InventoryAdapter letters = new InventoryAdapter(SLOT_SIZE, "Letters").disableAutomation();

    private final Set<Watcher> updateWatchers = new HashSet<>();

    public POBox() {
    }

    private POBox(List<ItemStack> letterItems) {
        for (int i = 0; i < letterItems.size(); i++) {
            ItemStack stack = letterItems.get(i);
            if (!stack.isEmpty()) {
                letters.setItem(i, stack);
            }
        }
    }

    public boolean storeLetter(ItemStack letterstack) {
        Optional<ILetter> letterOptional = LetterUtils.getLetter(letterstack);

        if (letterOptional.isEmpty()) {
            return false;
        }

        ILetter letter = letterOptional.get();

        // Mark letter as processed
        letter.setProcessed(true);
        letter.invalidatePostage();

        LetterUtils.setLetter(letterstack, letter);

        this.setDirty();

        return InventoryUtil.tryAddStack(letters, letterstack, true);
    }

    public POBoxInfo getPOBoxInfo() {
        Map<ICarrierType<?>, Integer> letterCounts = new HashMap<>();
        for (int i = 0; i < letters.getContainerSize(); i++) {
            if (letters.getItem(i).isEmpty()) {
                continue;
            }
            CompoundTag tagCompound = letters.getItem(i).getTag();
            if (tagCompound != null) {
                CompoundTag tag = tagCompound.getCompound("letter");
                ILetter letter = Letter.CODEC.decode(NbtOps.INSTANCE, tag).result().orElseThrow().getFirst();
                ICarrierType<?> carrier = letter.getSender().carrier();
                int amount = letterCounts.getOrDefault(carrier, 0) + 1;
                letterCounts.put(carrier, amount);
            }
        }

        return new POBoxInfo(ImmutableMap.copyOf(letterCounts));
    }

    /* IINVENTORY */

    @Override
    public boolean isEmpty() {
        return letters.isEmpty();
    }

    @Override
    public void setDirty() {
        updateWatchers.forEach(Watcher::onWatchableUpdate);
        letters.setChanged();
    }

    @Override
    public boolean registerUpdateWatcher(Watcher updateWatcher) {
        return updateWatchers.add(updateWatcher);
    }

    @Override
    public boolean unregisterUpdateWatcher(Watcher updateWatcher) {
        return updateWatchers.remove(updateWatcher);
    }

    @Override
    public void setItem(int var1, ItemStack var2) {
        this.setDirty();
        letters.setItem(var1, var2);
    }

    @Override
    public int getContainerSize() {
        return letters.getContainerSize();
    }

    @Override
    public ItemStack getItem(int var1) {
        return letters.getItem(var1);
    }

    @Override
    public ItemStack removeItem(int var1, int var2) {
        return letters.removeItem(var1, var2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return letters.removeItemNoUpdate(index);
    }

    @Override
    public int getMaxStackSize() {
        return letters.getMaxStackSize();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player var1) {
        return letters.stillValid(var1);
    }

    @Override
    public void startOpen(Player var1) {
    }

    @Override
    public void stopOpen(Player var1) {
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return letters.canPlaceItem(i, itemstack);
    }

    @Override
    public void clearContent() {
    }

}
