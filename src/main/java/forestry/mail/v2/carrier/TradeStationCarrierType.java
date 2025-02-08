package forestry.mail.v2.carrier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.ForestryConstants;
import forestry.api.IForestryApi;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalState;
import forestry.api.mail.v2.address.IAddress;
import forestry.api.mail.v2.carrier.ICarrierType;
import forestry.api.mail.v2.carrier.ITradeStationCarrierType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class TradeStationCarrierType extends AbstractCarrierType<String> implements ITradeStationCarrierType {
    public static final Codec<ITradeStationCarrierType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(ICarrierType::id)
            ).apply(instance, location -> IForestryApi.INSTANCE.getMailManager().getCarrierType(location, TradeStationCarrierType.class)));


    public TradeStationCarrierType() {
        super(
                ForestryCarriers.TRADESTATION,
                ForestryConstants.forestry("mail/carrier.trader"),
                "for.gui.addressee.trader"
        );
    }

    @Override
    public String getAddresseeName(IAddress<String> address) {
        return address.addressee();
    }

    @Override
    public Codec<String> getAddresseeCodec() {
        return Codec.STRING;
    }

    @Override
    public IPostalState deliverLetter(ServerLevel world, IPostOffice office, IAddress<String> recipient, ItemStack letterstack, boolean doDeliver) {
        return null;
    }
}
