package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import java.awt.Color;

public class NursuiteClient implements ModInitializer {
    public static boolean killauraEnabled = false;
    public static boolean flightEnabled = false;
    public static PlayerEntity target = null;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitialize() {
        System.out.println("=== ALAQYR CLIENT УСПІШНО ЗАПУЩЕНО ===");
    }

    public static void onKeyPress(int key, int action) {
        if (action != 1) return;

        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            mc.execute(() -> mc.setScreen(new ClickGui()));
        }
        if (key == GLFW.GLFW_KEY_R) {
            killauraEnabled = !killauraEnabled;
            if (!killauraEnabled) target = null;
        }
        if (key == GLFW.GLFW_KEY_G) {
            flightEnabled = !flightEnabled;
        }
    }

    public static void onTick() {
        if (mc.world == null || mc.player == null) return;

        if (flightEnabled) {
            mc.player.getAbilities().flying = true;
        } else if (!mc.player.isCreative()) {
            mc.player.getAbilities().flying = false;
        }

        if (killauraEnabled) {
            target = null;
            double reach = 3.8;
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player || !p.isAlive() || p.isInvisible()) continue;
                if (mc.player.distanceTo(p) < reach) {
                    target = p;
                    break;
                }
            }
            if (target != null) {
                float[] rots = getRotations(target);
                mc.player.setYaw(rots[0]);
                mc.player.setPitch(rots[1]);

                if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    public static void renderHud(DrawContext context) {
        if (mc.options.hudHidden || mc.player == null) return;

        String info = "§cALAQYR §7| §f" + mc.getCurrentFps() + "fps §7| §f" + (int)mc.player.getX() + " " + (int)mc.player.getY() + " " + (int)mc.player.getZ();
        context.fill(10, 10, 220, 24, new Color(10, 10, 12, 200).getRGB());
        context.drawText(mc.textRenderer, info, 16, 14, -1, false);

        if (target != null) {
            int hX = context.getScaledWindowWidth() / 2 - 75;
            int hY = context.getScaledWindowHeight() / 2 + 45;
            
            context.fill(hX, hY, hX + 150, hY + 40, new Color(15, 15, 17, 230).getRGB());
            context.fill(hX + 6, hY + 6, hX + 34, hY + 34, new Color(40, 30, 25).getRGB());
            
            context.drawText(mc.textRenderer, target.getGameProfile().getName(), hX + 42, hY + 8, -1, false);
            float hp = target.getHealth();
            context.drawText(mc.textRenderer, String.format("§aHP: %.1f", hp), hX + 42, hY + 18, -1, false);

            context.fill(hX + 42, hY + 29, hX + 140, hY + 33, new Color(40, 40, 45).getRGB());
            float pct = Math.min(1.0f, hp / target.getMaxHealth());
            context.fill(hX + 42, hY + 29, hX + 42 + (int)(98 * pct), hY + 33, new Color(0, 255, 160).getRGB());
        }
    }

    private static float[] getRotations(PlayerEntity e) {
        double x = e.getX() - mc.player.getX();
        double z = e.getZ() - mc.player.getZ();
        double y = (e.getY() + e.getStandingEyeHeight() / 2.0) - (mc.player.getY() + mc.player.getStandingEyeHeight());
        double d = Math.sqrt(x * x + z * z);
        return new float[]{(float)(Math.toDegrees(Math.atan2(z, x))) - 90.0F, (float)(-Math.toDegrees(Math.atan2(y, d)))};
    }

    public static class ClickGui extends Screen {
        public ClickGui() { super(Text.of("ClickGUI")); }
        @Override
        public void render(DrawContext c, int mX, int mY, float d) {
            this.renderBackground(c, mX, mY, d);
            int x = (this.width - 400) / 2, y = (this.height - 250) / 2;
            
            c.fill(x, y, x + 400, y + 250, new Color(20, 20, 23, 240).getRGB()); 
            c.drawText(this.textRenderer, "⭐ ALAQYR PREMIUM", x + 15, y + 15, new Color(255, 10, 70).getRGB(), false);
            
            c.fill(x + 50, y + 60, x + 350, y + 85, killauraEnabled ? new Color(255, 10, 70, 200).getRGB() : new Color(40, 40, 45).getRGB());
            c.drawText(this.textRenderer, "Killaura [R] - " + (killauraEnabled ? "ON" : "OFF"), x + 65, y + 68, -1, false);

            c.fill(x + 50, y + 100, x + 350, y + 125, flightEnabled ? new Color(255, 10, 70, 200).getRGB() : new Color(40, 40, 45).getRGB());
            c.drawText(this.textRenderer, "Flight [G] - " + (flightEnabled ? "ON" : "OFF"), x + 65, y + 108, -1, false);
            super.render(c, mX, mY, d);
        }
        @Override
        public boolean mouseClicked(double mX, double mY, int b) {
            int x = (this.width - 400) / 2, y = (this.height - 250) / 2;
            if (mX >= x + 50 && mX <= x + 350 && mY >= y + 60 && mY <= y + 85) killauraEnabled = !killauraEnabled;
            if (mX >= x + 50 && mX <= x + 350 && mY >= y + 100 && mY <= y + 125) flightEnabled = !flightEnabled;
            return super.mouseClicked(mX, mY, b);
        }
        @Override
        public boolean shouldPause() { return false; }
    }
}
