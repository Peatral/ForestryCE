package forestry.core.network;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.ICustomPacket;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import forestry.Forestry;

@OnlyIn(Dist.CLIENT)
public class PacketHandlerClient {
	public void onPacket(NetworkEvent.ServerCustomPayloadEvent event) {
		PacketBufferForestry data = new PacketBufferForestry(event.getPayload());
		byte idOrdinal = data.readByte();
		PacketIdClient id = PacketIdClient.VALUES[idOrdinal];

		IForestryPacketHandlerClient packetHandler = id.getPacketHandler();
		NetworkEvent.Context ctx = event.getSource().get();
		ctx.enqueueWork(() -> {
			Player player = Minecraft.getInstance().player;

			if (player != null) {
				try {
					packetHandler.onPacketData(data, player);
				} catch (IOException e) {
					Forestry.LOGGER.error("exception handling packet", e);
				}
			} else {
				Forestry.LOGGER.warn("the player was null, event: {}", event);
			}
		});
		ctx.setPacketHandled(true);
	}

	public static void sendPacket(IForestryPacketServer packet) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener netHandler = minecraft.getConnection();
		if (netHandler != null) {
			Pair<FriendlyByteBuf, Integer> packetData = IForestryPacket.getPacketData(packet);
			ICustomPacket<Packet<?>> payload = NetworkDirection.PLAY_TO_SERVER.buildPacket(packetData, PacketHandlerServer.CHANNEL_ID);
			netHandler.send(payload.getThis());
		}
	}

}
