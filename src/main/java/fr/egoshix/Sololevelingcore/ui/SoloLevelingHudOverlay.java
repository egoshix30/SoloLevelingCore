package fr.egoshix.Sololevelingcore.ui;

import fr.egoshix.Sololevelingcore.leveling.LevelCapability;
import fr.egoshix.Sololevelingcore.leveling.LevelProvider;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

class HudAnimationState {
    public static float animHealth = 1f;
    public static float targetHealth = 1f;
    public static float animXp = 1f;
    public static float targetXp = 1f;

    public static int lastLevel = -1;
    public static int levelFlashTicks = 0;

    // Particules
    public static class Particle {
        public float px, py, vx, vy, life, alpha;
        public int color;
        public Particle(float px, float py, float vx, float vy, float life, int color) {
            this.px = px; this.py = py; this.vx = vx; this.vy = vy; this.life = life; this.alpha = 1f; this.color = color;
        }
    }
    public static java.util.List<Particle> particles = new java.util.ArrayList<>();
    public static Random rand = new Random();
}

@Mod.EventBusSubscriber(modid = "sololevelingcore", value = Dist.CLIENT)
public class SoloLevelingHudOverlay {

    // Génère des particules de couleur sur la barre
    private static void spawnParticles(int barX, int barY, int width, int color, int n) {
        for (int i = 0; i < n; i++) {
            float px = barX + HudAnimationState.rand.nextFloat() * width;
            float py = barY + 7 + HudAnimationState.rand.nextFloat() * 2 - 1;
            float vx = (HudAnimationState.rand.nextFloat() - 0.5f) * 0.6f;
            float vy = -HudAnimationState.rand.nextFloat() * 0.6f - 0.1f;
            float life = HudAnimationState.rand.nextFloat() * 12 + 8;
            int c = color;
            HudAnimationState.particles.add(new HudAnimationState.Particle(px, py, vx, vy, life, c));
        }
    }

    @SubscribeEvent
    public static void onPreRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().toString().contains("health")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPostRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        LevelProvider prov = LevelCapability.get(mc.player);
        if (prov == null) return;
        var data = prov.getData();

        float health = mc.player.getHealth();
        float maxHealth = mc.player.getMaxHealth();
        int level = data.getLevel();
        int xp = data.getXp();
        int xpNext = data.xpForNextLevel();

        int sw = mc.getWindow().getGuiScaledWidth();
        int barWidth = 180, barHeight = 14;
        int x = (sw - barWidth) / 2;
        int sh = mc.getWindow().getGuiScaledHeight();
        int y = sh - 49;
        int xpBarY = y - 18;

        // --- Animation et FLASH ---
        float targetHealth = (maxHealth > 0) ? (health / maxHealth) : 0;
        float targetXp = (xpNext > 0) ? ((float) xp / xpNext) : 1f;

        float animSpeed = 0.20f;
        if (Math.abs(HudAnimationState.animHealth - targetHealth) > 0.01f)
            HudAnimationState.animHealth += (targetHealth - HudAnimationState.animHealth) * animSpeed;
        else
            HudAnimationState.animHealth = targetHealth;

        if (Math.abs(HudAnimationState.animXp - targetXp) > 0.01f)
            HudAnimationState.animXp += (targetXp - HudAnimationState.animXp) * animSpeed;
        else
            HudAnimationState.animXp = targetXp;

        // Effet flash niveau up (HUD)
        if (HudAnimationState.lastLevel != level) {
            HudAnimationState.levelFlashTicks = 18; // 18 ticks (~0.9s)
            HudAnimationState.lastLevel = level;
            // Spawn plus de particules flashy sur la barre d'xp
            spawnParticles(x, xpBarY, barWidth, 0xFF8C00FF, 20);
        }

        if (HudAnimationState.levelFlashTicks > 0) HudAnimationState.levelFlashTicks--;

        // --- DESSIN POLISH & EFFETS ---
        // Ombre et fond
        int shadow = 0x44000000;
        int fondLife = 0xD00D0B2E;
        int fondXp   = 0xD01C335A;

        // FLASH de niveau
        if (HudAnimationState.levelFlashTicks > 0 && (HudAnimationState.levelFlashTicks % 4 < 2)) {
            fondXp = 0xF0706DF7; // flash violet
        }

        // BARRE DE VIE (smooth)
        int lifeAnimWidth = (int) (barWidth * HudAnimationState.animHealth);
        event.getGuiGraphics().fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, shadow);
        event.getGuiGraphics().fill(x, y, x + barWidth, y + barHeight, fondLife);
        event.getGuiGraphics().fill(x, y, x + lifeAnimWidth, y + barHeight, 0xFFA43AFF); // violet animé
        // Glow
        if (lifeAnimWidth > 0)
            event.getGuiGraphics().fill(x, y + barHeight - 3, x + lifeAnimWidth, y + barHeight, 0x99B295FF);

        event.getGuiGraphics().drawString(mc.font, "PV: " + (int) health + "/" + (int) maxHealth,
                x + 10, y + 2, 0xFFF3CFFF);

        // BARRE XP
        int xpAnimWidth = (int) (barWidth * HudAnimationState.animXp);
        event.getGuiGraphics().fill(x - 2, xpBarY - 2, x + barWidth + 2, xpBarY + barHeight + 2, shadow);
        event.getGuiGraphics().fill(x, xpBarY, x + barWidth, xpBarY + barHeight, fondXp);
        event.getGuiGraphics().fill(x, xpBarY, x + xpAnimWidth, xpBarY + barHeight, 0xFF00E3FF); // cyan flash animé

        if (xpAnimWidth > 0)
            event.getGuiGraphics().fill(x, xpBarY + barHeight - 3, x + xpAnimWidth, xpBarY + barHeight, 0x55C7F8FF);

        event.getGuiGraphics().drawString(mc.font,
                "XP: " + xp + "/" + xpNext + " | Niveau " + level,
                x + 10, xpBarY + 2, 0xFFCAEDFF);

        // --- PARTICULES/GLINTS animées sur la barre XP ---
        // Génère des particules en continue sur la barre d'xp pour le "magique"
        if (HudAnimationState.rand.nextFloat() < 0.24f) {
            spawnParticles(x, xpBarY, xpAnimWidth, 0xFF00AFFF, 1 + HudAnimationState.rand.nextInt(2));
        }
        // Et un peu sur la barre de vie
        if (HudAnimationState.rand.nextFloat() < 0.13f) {
            spawnParticles(x, y, lifeAnimWidth, 0xFFD37BFF, 1);
        }

        // Update et draw particules
        java.util.Iterator<HudAnimationState.Particle> it = HudAnimationState.particles.iterator();
        while (it.hasNext()) {
            HudAnimationState.Particle p = it.next();
            // Déplacement simple
            p.px += p.vx;
            p.py += p.vy;
            p.vy -= 0.03f; // "chute"
            p.life -= 1;
            if (p.life <= 0) { it.remove(); continue; }
            p.alpha = Math.max(0.08f, Math.min(1f, p.life / 16f));
            int a = ((int)(p.alpha * 255)) << 24;
            event.getGuiGraphics().fill(
                    (int)p.px, (int)p.py, (int)p.px+2, (int)p.py+2, a | (p.color & 0xFFFFFF)
            );
        }

        // Effet d'éclair/glow flash sur barre d'xp si tu montes de niveau
        if (HudAnimationState.levelFlashTicks > 0) {
            int alpha = (int) (120 + Math.sin(HudAnimationState.levelFlashTicks * 0.5f) * 80);
            int flashColor = (alpha << 24) | 0xC085FA;
            event.getGuiGraphics().fill(x, xpBarY, x + barWidth, xpBarY + barHeight, flashColor);
        }
    }
}
