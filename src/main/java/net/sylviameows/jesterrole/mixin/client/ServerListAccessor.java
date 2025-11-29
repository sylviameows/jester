package net.sylviameows.jesterrole.mixin.client;

import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.option.ServerList.class)
public interface ServerListAccessor {
    @Accessor
    List<ServerInfo> getServers();
}
