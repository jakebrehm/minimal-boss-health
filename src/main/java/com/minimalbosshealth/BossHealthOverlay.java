package com.minimalbosshealth;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;

public abstract class BossHealthOverlay extends Overlay {

    private static final long FADE_MS = 300; // TODO: Make config

    protected final Client client;
    protected final MinimalBossHealthConfig config;

    @Inject
    protected MinimalBossHealthPlugin plugin;

    private NPC trackedBoss = null;
    private long fadeInStart = 0;
    private long fadeOutStart = 0;
    private double lastHealthFraction = 1.0;

    protected BossHealthOverlay(Client client, MinimalBossHealthConfig config) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setMovable(false);
        this.client = client;
        this.config = config;
    }

    protected Dimension renderHealth(Graphics2D graphics, float alpha, double healthFraction) {
        Composite original = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        Dimension result = renderHealth(graphics, healthFraction);
        graphics.setComposite(original);
        return result;
    }

    protected abstract Dimension renderHealth(Graphics2D graphics, double healthFraction);

    @Override
    public final Dimension render(Graphics2D graphics) {
        NPC boss = plugin.currentBoss;
        long now = System.currentTimeMillis();

        float alpha;
        double healthFraction;

        if (config.fadeEnabled()) {
            if (boss != null && trackedBoss == null) {
                // Boss appeared--begin fading in
                trackedBoss = boss;
                fadeInStart = now;
                fadeOutStart = 0;
            } else if (boss == null && trackedBoss != null && fadeOutStart == 0) {
                // Boss disappeared--being fading out
                fadeOutStart = now;
            } else if (boss != null && trackedBoss != null && boss != trackedBoss) {
                // A different boss appeared (e.g., phase change)--reset and fade in
                trackedBoss = boss;
                fadeInStart = now;
                fadeOutStart = 0;
                // TODO: Fade in during these phase changes?
            }

            if (trackedBoss == null) return null;

            // Continue updating the boss's health values
            if (boss != null && boss.getHealthScale() > 0 && boss.getHealthRatio() != -1) {
                lastHealthFraction = (double) boss.getHealthRatio() / boss.getHealthScale();
            }

            // Adjust alpha during fading
            if (fadeOutStart > 0) {
                // Step alpha down from 1 to 0 during fade out
                long elapsed = now - fadeOutStart;
                if (elapsed >= FADE_MS) {
                    // Clean up and stop rendering once fade is complete
                    trackedBoss = null;
                    return null;
                }
                alpha = 1.0f - (float) elapsed / FADE_MS;
            } else {
                // Step alpha up from 0 to 1 during fade in
                alpha = Math.min(1.0f, (float) (now - fadeInStart) / FADE_MS);
            }

            healthFraction = lastHealthFraction;
        } else {
            // Show bar instantly if fading is disabled and boss is present
            if (boss == null || boss.getHealthScale() <= 0) return null;
            alpha = 1.0f;
            healthFraction = (double) boss.getHealthRatio() / boss.getHealthScale();
        }

        // Anchor to the top-left of the viewport
        setPreferredLocation(new Point(
            client.getViewportXOffset(),
            client.getViewportYOffset()
        ));

        return renderHealth(graphics, alpha, healthFraction);
    }

}
