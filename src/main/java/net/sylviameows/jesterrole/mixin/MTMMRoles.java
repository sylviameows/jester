package net.sylviameows.jesterrole.mixin;

import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TMMRoles.class)
public class MTMMRoles {

    @Inject(method = "registerRole", at = @At("HEAD"), cancellable = true)
    private static void registerRole(Role role, CallbackInfoReturnable<Role> cir) {

        // probably a better way to do this but i don't want to mess with mod loading order.
        if (role.identifier().equals(Identifier.of("noellesroles", "jester"))) {
            cir.cancel();
        }

    }

}
