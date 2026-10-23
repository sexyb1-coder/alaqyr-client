package com.example.mixin;

import com.example.NursuiteClient;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import org.joml.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClientMixins {

    @Mixin(Keyboard.class)
    public static class MixinKeyboard {
        @Inject(method = "onKey", at = @At("HEAD"))
        private void onKey(long w, int key, int sc, int action, int mods, CallbackInfo ci) {
            NursuiteClient.onKeyPress(key, action);
        }
    }

    @Mixin(ClientPlayerEntity.class)
    public static class MixinPlayer {
        @Inject(method = "tick", at = @At("HEAD"))
        private void onTick(CallbackInfo ci) {
            NursuiteClient.onTick();
        }
    }

    @Mixin(InGameHud.class)
    public static class MixinHud {
        @Inject(method = "render", at = @At("TAIL"))
        private void onRender(DrawContext c, float td, CallbackInfo ci) {
            NursuiteClient.renderHud(c);
        }
    }

    @Mixin(HeldItemRenderer.class)
    public static class MixinHand {
        @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
        private void onBlockHit(MatrixStack m, Hand h, float td, float p, float sp, float ep, CallbackInfo ci) {
            if (NursuiteClient.target != null && h == Hand.MAIN_HAND) {
                m.translate(0.1f, -0.1f, -0.2f);
                m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0f));
                m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-35.0f));
                m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(15.0f));
                float sw = (float) Math.sin(sp * Math.PI);
                m.translate(-sw * 0.2f, sw * 0.1f, 0.0f);
            }
        }
    }
}
