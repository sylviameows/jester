package net.sylviameows.jesterrole.mixin;

import com.mojang.brigadier.CommandDispatcher;
import dev.doctor4t.trainmurdermystery.command.ForceRoleCommand;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.sylviameows.jesterrole.Jester;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ForceRoleCommand.class)
public class MForceRoleCommand {

    @Inject(method = "register", at = @At(value = "TAIL"))
    private static void jester$force(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        dispatcher.register(CommandManager.literal("tmm:forceRole")
                .then(CommandManager.literal("jester").then(CommandManager.argument("players", EntityArgumentType.players())
                        .executes(context -> forceJester(context.getSource(), EntityArgumentType.getPlayers(context, "players")))
                ))
        );
    }

    @Unique
    private static int forceJester(ServerCommandSource source, Collection<ServerPlayerEntity> players) {
        for (var player : players) Jester.forcedJesters.add(player.getUuid());
        return 1;
    }


}
