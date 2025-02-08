package forestry.api.mail.v2;

import com.mojang.serialization.Codec;
import forestry.api.mail.v2.carrier.ICarrierType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface IMailManager {
    Codec<ICarrierType<?>> carrierTypeByNameCodec();

    ICarrierType<?> getCarrierType(ResourceLocation addressTypeId);
    default <T extends ICarrierType<?>> T getCarrierType(ResourceLocation addressTypeId, Class<T> typeClass) {
        return typeClass.cast(getCarrierType(addressTypeId));
    }

    List<ICarrierType<?>> getCarrierTypes();
}
