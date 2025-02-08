package forestry.mail.v2.carrier;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import forestry.api.ForestryConstants;
import forestry.api.mail.IPostOffice;
import forestry.api.mail.IPostalState;
import forestry.api.mail.v2.address.IAddress;
import forestry.api.mail.v2.carrier.IPlayerCarrierType;
import forestry.core.utils.PlayerUtil;
import forestry.mail.EnumDeliveryState;
import forestry.mail.POBox;
import forestry.mail.v2.POBoxRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PlayerCarrierType extends AbstractCarrierType<GameProfile> implements IPlayerCarrierType {
    public PlayerCarrierType() {
        super(
                ForestryCarriers.PLAYER,
                ForestryConstants.forestry("mail/carrier.player"),
                "for.gui.addressee.player"
        );
    }

    @Override
    public String getAddresseeName(IAddress<GameProfile> address) {
        return address.addressee().getName();
    }

    @Override
    public Codec<GameProfile> getAddresseeCodec() {
        return ExtraCodecs.GAME_PROFILE;
    }

    @Override
    public IPostalState deliverLetter(ServerLevel world, IPostOffice office, IAddress<GameProfile> recipient, ItemStack letterstack, boolean doDeliver) {
        POBox pobox = POBoxRegistry.getOrCreate(world).getOrCreatePOBox(recipient.addressee());
        if (pobox == null) {
            return EnumDeliveryState.NO_MAILBOX;
        }

        if (!pobox.storeLetter(letterstack.copy())) {
            return EnumDeliveryState.MAILBOX_FULL;
        } else {
            Player player = PlayerUtil.getPlayer(world, recipient.addressee());
            if (player instanceof ServerPlayer) {
                //NetworkUtil.sendToPlayer(new PacketPOBoxInfoResponse(pobox.getPOBoxInfo(), false), (ServerPlayer) player);
            }
        }

        return EnumDeliveryState.OK;
    }
}
