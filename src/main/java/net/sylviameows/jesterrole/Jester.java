package net.sylviameows.jesterrole;

import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.sylviameows.jesterrole.mixin.MPlayerMoodAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Jester implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "jester";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Identifier CHANNEL = id("verify");

    public static @NotNull Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }

    public static boolean ENABLED = true;

    public static final int ROLE_COLOR = 0xF8C8DC;
    public static TMMRoles.Role ROLE = new TMMRoles.Role(Jester.id("role"), ROLE_COLOR, false, false);
    public static RoleAnnouncementTexts.RoleAnnouncementText TEXT = new JesterAnnouncementText();

    private static boolean JESTER_WIN = false;

    public static boolean isJesterWin() {
        return JESTER_WIN;
    }

    public static void setJesterWin(boolean jesterWin, @Nullable List<ServerPlayerEntity> players) {
        if (players != null) {
            players.forEach(player -> ServerPlayNetworking.send(player, new JesterWinPayload(jesterWin)));
        }
        JESTER_WIN = jesterWin;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Attempting role injection.");
        TMMRoles.registerRole(ROLE);
        RoleAnnouncementTexts.registerRoleAnnouncementText(TEXT);

        PayloadTypeRegistry.playS2C().register(JesterWinPayload.ID, JesterWinPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, play) -> {
            ServerPlayNetworking.send(handler.player, new JesterWinPayload(isJesterWin()));
        });


        ServerLoginConnectionEvents.QUERY_START.register(((handler, server, sender, synchronizer) -> {
            ServerLoginNetworking.registerReceiver(handler, CHANNEL, (_server, _handler, understood, buf, _synchronizer, responder) -> {
                if (!understood) {
                    handler.disconnect(Text.literal("")
                            .append(Text.literal("ᴍɪssɪɴɢ ᴍᴏᴅ").withColor(ROLE_COLOR).formatted(Formatting.BOLD))
                            .append("\n\nYou must have the Jester mod installed to play on this server.\n\n")
                            .append(Text.literal("Find it here:\n").formatted(Formatting.GRAY))
                            .append(Text.literal("https://sylviameo.ws/projects/jester/").formatted(Formatting.UNDERLINE))
                            .append(Text.literal("\n\n\n"))
                    );
                    return;
                }

                String version = buf.readString();
                LOGGER.info("client joining with jester version {}", version);

                _synchronizer.waitFor(CompletableFuture.completedFuture(null));
            });

            PacketByteBuf out = PacketByteBufs.create();
            sender.sendPacket(CHANNEL, out.writeBoolean(isJesterWin()));
        }));

        Jester.LOGGER.info("Role successfully injected.");
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(JesterWinPayload.ID, new JesterWinPayload.Receiver());

        ClientLoginConnectionEvents.QUERY_START.register((ClientLoginNetworkHandler handler, MinecraftClient client) -> {
            ClientLoginNetworking.registerReceiver(CHANNEL, (_client, _handler, buf, callbacksConsumer) -> {
                boolean jesterWin = buf.readBoolean();
                setJesterWin(jesterWin, null);

                PacketByteBuf out = PacketByteBufs.create();
                var mod = FabricLoader.getInstance().getModContainer(MOD_ID);
                if (mod.isPresent()) {
                    String version = mod.get().getMetadata().getVersion().getFriendlyString();
                    return CompletableFuture.completedFuture(out.writeString(version));
                }

                return CompletableFuture.completedFuture(null);
            });
        });

    }

    public static void lockpickUse(World world, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        GameWorldComponent game = GameWorldComponent.KEY.get(world);
        if (game.isRole(player, Jester.ROLE)) {
            var moodComponent = PlayerMoodComponent.KEY.get(player);
            float mood = ((MPlayerMoodAccessor) moodComponent).jester$getRealMood();
            if (mood < 0.24f) {
                player.sendMessage(Text.translatable("tip.lockpick.not_sane"), true);
                cir.setReturnValue(ActionResult.PASS);
                cir.cancel();
                return;
            }
            mood -= 0.25f;
            if (mood < 0.15f) mood = 0f;
            moodComponent.setMood(mood);
        }
    }
}
