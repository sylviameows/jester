package net.sylviameows.jesterrole;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record JesterWinPayload(boolean isJesterWin) implements CustomPayload {
    public static final Id<JesterWinPayload> ID = new Id<>(Jester.id("jester"));
    public static final PacketCodec<PacketByteBuf, JesterWinPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, JesterWinPayload::isJesterWin, JesterWinPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Receiver implements ClientPlayNetworking.PlayPayloadHandler<JesterWinPayload> {
        @Override
        public void receive(@NotNull JesterWinPayload payload, ClientPlayNetworking.@NotNull Context context) {
            Jester.setJesterWin(payload.isJesterWin, null);
        }
    }
}
