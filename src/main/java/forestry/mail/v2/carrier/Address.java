package forestry.mail.v2.carrier;

import com.mojang.serialization.Codec;
import forestry.api.mail.v2.address.IAddress;
import forestry.api.mail.v2.carrier.ICarrierType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

public class Address<T> implements IAddress<T> {

    private final ICarrierType<T> carrierType;
    private final T addressee;

    public Address(ICarrierType<T> carrierType, T addressee) {
        this.carrierType = carrierType;
        this.addressee = addressee;
    }

    @Override
    public T addressee() {
        return this.addressee;
    }

    @Override
    public ICarrierType<T> carrier() {
        return this.carrierType;
    }

    public static ICarrierType<?> readCarrierType(CompoundTag tag) {;
        return ICarrierType.CODEC.decode(NbtOps.INSTANCE, tag)
                .result().orElseThrow().getFirst();
    }

    public static <R> R readAddressee(ICarrierType<R> carrier, CompoundTag tag) {
        Codec<R> codec = carrier.getAddresseeCodec();
        return codec.decode(NbtOps.INSTANCE, tag)
                .result().orElseThrow().getFirst();
    }

    public static IAddress<?> readAddress(CompoundTag tag) {
        ICarrierType<?> carrierType = readCarrierType(tag.getCompound("carrier"));
        Object addressee = readAddressee(carrierType, tag.getCompound("addressee"));
        return new Address(carrierType, addressee);
    }
}
