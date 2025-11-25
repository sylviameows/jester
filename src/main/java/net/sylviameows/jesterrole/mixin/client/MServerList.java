package net.sylviameows.jesterrole.mixin.client;

import com.google.common.collect.Lists;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerList.class)
public class MServerList {

    @Shadow
    private final List<ServerInfo> servers = Lists.newArrayList();

    @Inject(method = "loadFile", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 0, shift = At.Shift.AFTER))
    private void load(CallbackInfo ci) {
        servers.addFirst(new ServerInfo("§f§lᴊᴇsᴛᴇʀ§r §8@ §7train.sylviameo.ws", "train.sylviameo.ws", ServerInfo.ServerType.OTHER));
    }

    @Inject(method = "saveFile", at = @At("HEAD"))
    private void save(CallbackInfo ci) {
        servers.removeFirst();
    }
}
