package forestry.mail.v2;

import com.google.common.collect.ImmutableMap;
import forestry.api.mail.v2.carrier.ICarrierType;
import forestry.api.plugin.IMailRegistration;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MailRegistration implements IMailRegistration {
    private final Map<ResourceLocation, Supplier<ICarrierType<?>>> carrierTypes = new HashMap<>();
    @Override
    public void registerCarrier(ResourceLocation location, Supplier<ICarrierType<?>> supplier) {
        carrierTypes.put(location, supplier);
    }

    @Override
    public ImmutableMap<ResourceLocation, ICarrierType<?>> buildCarriers() {
        Map<ResourceLocation, ICarrierType<?>> builtCarrierTypes = new HashMap<>();
        for (Map.Entry<ResourceLocation, Supplier<ICarrierType<?>>> entry : carrierTypes.entrySet()) {
            builtCarrierTypes.put(entry.getKey(), entry.getValue().get());
        }
        return ImmutableMap.copyOf(builtCarrierTypes);
    }
}
