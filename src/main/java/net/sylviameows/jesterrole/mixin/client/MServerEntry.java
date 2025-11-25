package net.sylviameows.jesterrole.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Identifier;
import net.sylviameows.jesterrole.Jester;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerServerListWidget.ServerEntry.class)
abstract public class MServerEntry {

    @Unique
    private static final Identifier JESTER = Jester.id("hud/mood_jester");

    @Final
    @Shadow
    private ServerInfo server;


    @Shadow
    protected abstract void draw(DrawContext context, int x, int y, Identifier textureId);

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget$ServerEntry;draw(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/util/Identifier;)V", shift = At.Shift.AFTER)
    )
    private void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (server.address.equals("train.sylviameo.ws")) {
            context.drawGuiTexture(JESTER, x + 2, y - 2, 14, 17);
        }
    }
}
