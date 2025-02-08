package forestry.mail;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.mail.IStamps;
import forestry.api.mail.ILetter;
import forestry.api.mail.v2.address.IAddress;
import forestry.core.inventory.InventoryAdapter;
import forestry.core.utils.InventoryUtil;
import forestry.mail.v2.carrier.Address;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

public class Letter implements ILetter {
    public static final Codec<ILetter> CODEC = RecordCodecBuilder.create(
    instance -> instance.group(
                Address.CODEC.fieldOf("sender").forGetter(ILetter::getSender),
                Address.CODEC.fieldOf("recipient").forGetter(ILetter::getSender)
            ).apply(instance, Letter::new));

    private IAddress<?> sender;
    @Nullable
    private IAddress<?> recipient;

    public static final short SLOT_ATTACHMENT_1 = 0;
    public static final short SLOT_ATTACHMENT_COUNT = 18;
    public static final short SLOT_POSTAGE_1 = 18;
    public static final short SLOT_POSTAGE_COUNT = 4;

    private boolean isProcessed = false;

    private String text = "";
    private final InventoryAdapter inventory = new InventoryAdapter(22, "attachments");

    public Letter(IAddress<?> sender, IAddress<?> recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    @Override
    public IAddress<?> getSender() {
        return this.sender;
    }

    @Override
    public @org.jetbrains.annotations.Nullable IAddress<?> getRecipient() {
        return this.recipient;
    }

    @Override
    public NonNullList<ItemStack> getPostage() {
        return InventoryUtil.getStacks(inventory, SLOT_POSTAGE_1, SLOT_POSTAGE_COUNT);
    }

    @Override
    public NonNullList<ItemStack> getAttachments() {
        return InventoryUtil.getStacks(inventory, SLOT_ATTACHMENT_1, SLOT_ATTACHMENT_COUNT);
    }

    @Override
    public int countAttachments() {
        int count = 0;
        for (ItemStack stack : getAttachments()) {
            if (!stack.isEmpty()) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void addAttachment(ItemStack itemstack) {
        InventoryUtil.tryAddStack(inventory, itemstack, false);
    }

    @Override
    public void addAttachments(NonNullList<ItemStack> itemstacks) {
        for (ItemStack stack : itemstacks) {
            addAttachment(stack);
        }
    }

    @Override
    public void invalidatePostage() {
        for (int i = SLOT_POSTAGE_1; i < SLOT_POSTAGE_1 + SLOT_POSTAGE_COUNT; i++) {
            inventory.setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void setProcessed(boolean flag) {
        this.isProcessed = flag;
    }

    @Override
    public boolean isProcessed() {
        return this.isProcessed;
    }

    @Override
    public boolean isMailable() {
        // Can't resend an already sent letter
        // Requires at least one recipient
        return !isProcessed && recipient != null;
    }

    @Override
    public boolean isPostPaid() {

        int posted = 0;

        for (ItemStack stamp : getPostage()) {
            if (stamp.isEmpty()) {
                continue;
            }
            if (!(stamp.getItem() instanceof IStamps)) {
                continue;
            }

            posted += ((IStamps) stamp.getItem()).getPostage(stamp).getValue() * stamp.getCount();
        }

        return posted >= requiredPostage();
    }

    @Override
    public int requiredPostage() {

        int required = 1;
        for (ItemStack attach : getAttachments()) {
            if (!attach.isEmpty()) {
                required++;
            }
        }

        return required;
    }

    @Override
    public void addStamps(ItemStack stamps) {
        InventoryUtil.tryAddStack(inventory, stamps, SLOT_POSTAGE_1, 4, false);
    }

    @Override
    public boolean hasRecipient() {
        return recipient != null && !StringUtils.isBlank(recipient.getAddresseeName());
    }

    @Override
    public void setSender(IAddress<?> address) {
        this.sender = address;
    }

    @Override
    public void setRecipient(@Nullable IAddress<?> address) {
        this.recipient = address;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void addTooltip(List<Component> list) {
        if (StringUtils.isNotBlank(this.sender.getAddresseeName())) {
            list.add(Component.translatable("for.gui.mail.from")
                    .append(": " + this.sender.getAddresseeName())
                    .withStyle(ChatFormatting.GRAY));
        }
        if (this.recipient != null) {
            list.add(Component.translatable("for.gui.mail.to")
                    .append(": " + this.recipient.getAddresseeName())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // / IINVENTORY
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public ItemStack getItem(int var1) {
        return inventory.getItem(var1);
    }

    @Override
    public ItemStack removeItem(int var1, int var2) {
        return inventory.removeItem(var1, var2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return inventory.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int var1, ItemStack var2) {
        inventory.setItem(var1, var2);
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        inventory.setChanged();
    }

    @Override
    public boolean stillValid(Player var1) {
        return true;
    }

    @Override
    public void startOpen(Player var1) {
        inventory.startOpen(var1);
    }

    @Override
    public void stopOpen(Player var1) {
        inventory.stopOpen(var1);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemstack) {
        return inventory.canPlaceItem(i, itemstack);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }
}
