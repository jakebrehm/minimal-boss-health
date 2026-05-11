package com.minimalbosshealth;

import net.runelite.api.Client;
import net.runelite.api.Point;

import javax.inject.Inject;
import java.awt.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import java.util.Optional;

@Slf4j
public class BarBossHealthOverlay extends BossHealthOverlay {

    @Inject
    private BarBossHealthOverlay(Client client, MinimalBossHealthConfig config) {
        super(client, config);
    }

    @Override
    protected Dimension renderHealth(Graphics2D graphics, double healthFraction) {
        int height = config.barHeight();
        int width = client.getViewportWidth();
        int remainingWidth = (int) (width * healthFraction);

        // Draw bars
        graphics.setColor(config.missingHpColor());
        graphics.fillRect(0, 0, width, height);

        graphics.setColor(config.remainingHpColor());
        graphics.fillRect(0, 0, remainingWidth, height);

        // Add text
        int totalHeight = height;
        switch (config.displayBarText()) {
            case OFF:
                break;
            case HOVER:
                // Determine if hovering, then fall into ALWAYS case logic if so
                Point mouse = client.getMouseCanvasPosition();
                int tolerance = config.barHoverTolerance();
                int mouseX = mouse.getX();
                int mouseY = mouse.getY();
                int viewportX = client.getViewportXOffset();
                int viewportY = client.getViewportYOffset();
                boolean hovered = (mouseX >= viewportX)
                               && (mouseX <= viewportX + width)
                               && (mouseY >= viewportY - tolerance)
                               && (mouseY <= viewportY + height + tolerance);
                if (!hovered) break;
            case ALWAYS:
                String text = constructText(healthFraction).orElse(null);
                if (text == null) break;

                graphics.setFont(graphics.getFont().deriveFont((float) config.barTextFontSize()));
                FontMetrics fm = graphics.getFontMetrics();
                int textX = (width - fm.stringWidth(text)) / 2 + config.barTextXOffset();
                int textY = height + fm.getAscent() + 2 + config.barTextYOffset();

                graphics.setColor(Color.BLACK);
                graphics.drawString(text, textX + 1, textY + 1);

                graphics.setColor(Color.WHITE);
                graphics.drawString(text, textX, textY);

                totalHeight = height + fm.getHeight() + 2;
                break;
        }

        return new Dimension(width, totalHeight);
    }

    protected Optional<String> constructText(double healthFraction) {

        Widget widget = client.getWidget(InterfaceID.HpbarHud.HP_BAR_TEXT);
        boolean canUsePreciseValues = widget != null;

        String text = getPercentText(healthFraction);
        if (canUsePreciseValues) {
            final int currentHp = client.getVarbitValue(VarbitID.HPBAR_HUD_HP);
            final int maximumHp = client.getVarbitValue(VarbitID.HPBAR_HUD_BASEHP);
            if (maximumHp <= 0) { return Optional.empty(); }
            switch (config.hitpointsDisplayStyle()) {
                case HITPOINTS:
                    text = currentHp + "/" + maximumHp;
                    break;
                case PERCENTAGE:
                    text = getPercentText((double) currentHp / maximumHp);
                    break;
                case BOTH:
                    String hitpointsText = currentHp + "/" + maximumHp;
                    String percentText = getPercentText((double) currentHp / maximumHp);
                    text = hitpointsText + " (" + percentText + ")";
                    break;
            }
        }
        return Optional.of(text);

    }

    protected static String getPercentText(double healthFraction) {
        return String.format("%.1f%%", healthFraction * 100);
    }

}
