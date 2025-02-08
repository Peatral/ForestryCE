package forestry.api.mail;

import forestry.api.mail.v2.address.IAddress;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface ILetter extends Container {
    IAddress<?> getSender();
    @Nullable
    IAddress<?> getRecipient();

    NonNullList<ItemStack> getPostage();

    void setProcessed(boolean flag);

    boolean isProcessed();

    boolean isMailable();

    void setSender(IAddress<?> address);

    boolean hasRecipient();

    void setRecipient(@Nullable IAddress<?> address);

    void setText(String text);

    String getText();

    void addTooltip(List<Component> list);

    boolean isPostPaid();

    int requiredPostage();

    void invalidatePostage();

    NonNullList<ItemStack> getAttachments();

    void addAttachment(ItemStack itemstack);

    void addAttachments(NonNullList<ItemStack> itemstacks);

    int countAttachments();

    void addStamps(ItemStack stamps);
}
