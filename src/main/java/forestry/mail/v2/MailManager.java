package forestry.mail.v2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import forestry.api.mail.v2.IMailManager;
import forestry.api.mail.v2.carrier.ICarrierType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MailManager implements IMailManager {
    public Codec<ICarrierType<?>> carrierTypeByNameCodec() {
        return ResourceLocation.CODEC.comapFlatMap(
                location -> {
                    ICarrierType<?> type = this.carrierTypes.get(location);
                    if (type == null) {
                        return DataResult.error(() -> "No carrier type was registered with ID: " + location);
                    }
                    return DataResult.success(type);
                },
                ICarrierType::id
        );
    }

    private final ImmutableMap<ResourceLocation, ICarrierType<?>> carrierTypes;

    public MailManager(ImmutableMap<ResourceLocation, ICarrierType<?>> carrierTypes) {
        this.carrierTypes = carrierTypes;
    }

    @Override
    public ICarrierType<?> getCarrierType(ResourceLocation addressTypeId) {
        ICarrierType<?> type = this.carrierTypes.get(addressTypeId);
        if (type == null) {
            throw new IllegalStateException("No carrier type was registered with ID: " + addressTypeId);
        }
        return type;
    }

    @Override
    public List<ICarrierType<?>> getCarrierTypes() {
        return ImmutableList.copyOf(carrierTypes.values());
    }
}
