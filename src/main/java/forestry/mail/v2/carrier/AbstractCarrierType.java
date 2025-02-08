package forestry.mail.v2.carrier;

import forestry.api.client.IForestryClientApi;
import forestry.api.mail.v2.carrier.ICarrierType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCarrierType<T> implements ICarrierType<T> {
    private final ResourceLocation id;
    private final ResourceLocation iconId;
    private final String translationKey;

    public AbstractCarrierType(ResourceLocation id, ResourceLocation iconId, String translationKey) {
        this.id = id;
        this.iconId = iconId;
        this.translationKey = translationKey;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return IForestryClientApi.INSTANCE.getTextureManager().getSprite(this.iconId);
    }
}
