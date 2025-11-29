package net.sylviameows.jesterrole.mixin.client;

import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MultiplayerScreen.class)
public class MMultiplayerScreen {

    @Unique
    private final ServerInfo SERVER_INFO = new ServerInfo("§f§lᴊᴇsᴛᴇʀ§r §8@ §7train.sylviameo.ws", "train.sylviameo.ws", ServerInfo.ServerType.OTHER);

    @Shadow
    private ServerList serverList;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;loadFile()V", shift = At.Shift.AFTER))
    private void addServer$init(CallbackInfo ci) {
        List<ServerInfo> servers = ((ServerListAccessor) serverList).getServers();
        if (!servers.contains(SERVER_INFO)) servers.addFirst(SERVER_INFO);
    }

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void removeServer$init(CallbackInfo ci) {
        List<ServerInfo> servers = ((ServerListAccessor) serverList).getServers();
        servers.removeIf(SERVER_INFO::equals);
    }

}
