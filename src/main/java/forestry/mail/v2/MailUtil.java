package forestry.mail.v2;

import com.mojang.serialization.Codec;
import forestry.Forestry;
import forestry.api.IForestryApi;
import forestry.api.mail.v2.address.IAddress;
import forestry.api.mail.v2.carrier.ICarrierType;
import forestry.api.mail.v2.carrier.IPlayerCarrierType;
import forestry.api.mail.v2.carrier.ITradeStationCarrierType;
import forestry.mail.v2.carrier.ForestryCarriers;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.Lazy;

public class MailUtil {

    public static final Lazy<ITradeStationCarrierType> TRADE_STATION_CARRIER = Lazy.of(() -> IForestryApi.INSTANCE.getMailManager().getCarrierType(ForestryCarriers.TRADESTATION, ITradeStationCarrierType.class));
    public static final Lazy<IPlayerCarrierType> PLAYER_CARRIER = Lazy.of(() -> IForestryApi.INSTANCE.getMailManager().getCarrierType(ForestryCarriers.PLAYER, IPlayerCarrierType.class));


    public static <R, A extends IAddress<R>> Tag serializeAddress(A address) {
        @SuppressWarnings("unchecked")
        Codec<A> codec = (Codec<A>) address.carrier().getAddresseeCodec();
        return codec.encodeStart(NbtOps.INSTANCE, address).result().orElse(null);
    }


    public static <A extends IAddress<R>, R> A deserializeAddress(ICarrierType<A, R> type, Tag nbt) {
        return type.getAddresseeCodec().decode(NbtOps.INSTANCE, nbt).resultOrPartial(Forestry.LOGGER::error).orElseThrow().getFirst();
    }
}
