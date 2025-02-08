package forestry.mail.v2;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import forestry.mail.POBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class POBoxRegistry extends SavedData implements IWatchable.Watcher {
    private static final String SAVE_NAME = "forestry_poboxes";

    public static final Codec<POBox> POBOX_CODEC = null;
    public static final Codec<Map<GameProfile, POBox>> CODEC = Codec.unboundedMap(ExtraCodecs.GAME_PROFILE, POBOX_CODEC);

    public final Map<GameProfile, POBox> cachedPOBoxes = new HashMap<>();

    public POBoxRegistry() {
    }

    public POBoxRegistry(Map<GameProfile, POBox> poboxes) {
        poboxes.forEach(this::registerPOBOx);
    }

    private void registerPOBOx(GameProfile address, POBox box) {
        cachedPOBoxes.put(address, box);
        //box.registerUpdateWatcher(this);
        setDirty();
    }

    public POBox getPOBox(GameProfile address) {
        return cachedPOBoxes.get(address);
    }

    public POBox getOrCreatePOBox(GameProfile address) {
        POBox pobox = getPOBox(address);

        if (pobox == null) {
            pobox = new POBox();
            registerPOBOx(address, pobox);
            pobox.setDirty();
        }

        return pobox;
    }

    @Override
    public void onWatchableUpdate() {
        setDirty();
    }

    private static POBoxRegistry create() {
        return new POBoxRegistry();
    }

    private static POBoxRegistry load(CompoundTag compoundTag) {
        return new POBoxRegistry(CODEC.decode(NbtOps.INSTANCE, compoundTag).result().orElseThrow().getFirst());
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, cachedPOBoxes).result().orElse(compoundTag);
    }

    public static POBoxRegistry getOrCreate(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(POBoxRegistry::load, POBoxRegistry::create, SAVE_NAME);
    }
}

