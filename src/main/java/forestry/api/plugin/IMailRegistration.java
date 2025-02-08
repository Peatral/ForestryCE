package forestry.api.plugin;

import com.google.common.collect.ImmutableMap;
import forestry.api.mail.v2.carrier.ICarrierType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface IMailRegistration {
    void registerCarrier(ResourceLocation location, Supplier<ICarrierType<?>> supplier);

    ImmutableMap<ResourceLocation, ICarrierType<?>> buildCarriers();
}
