package forestry.api.mail.v2.carrier;

import com.mojang.serialization.Codec;
import forestry.api.IForestryApi;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalState;
import forestry.api.mail.v2.address.IAddress;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ICarrierType<T> {
    Codec<ICarrierType<?>> CODEC = ExtraCodecs.lazyInitializedCodec(() -> IForestryApi.INSTANCE.getMailManager().carrierTypeByNameCodec());

    ResourceLocation id();

    Codec<T> getAddresseeCodec();

    String getAddresseeName(IAddress<T> address);

    String getTranslationKey();
    default MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    @OnlyIn(Dist.CLIENT)
    TextureAtlasSprite getIcon();

    IPostalState deliverLetter(ServerLevel world, IPostOffice office, IAddress<T> recipient, ItemStack letterstack, boolean doDeliver);

}
