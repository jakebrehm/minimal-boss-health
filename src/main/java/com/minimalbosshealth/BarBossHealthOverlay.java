package com.minimalbosshealth;

import net.runelite.api.Client;
import net.runelite.api.Point;

import javax.inject.Inject;
import java.awt.*;
import lombok.extern.slf4j.Slf4j;


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
                if (!hovered) { break; }
            case ALWAYS:
                String text = String.format("%.1f%%", healthFraction * 100);
                graphics.setFont(graphics.getFont().deriveFont((float) config.barTextFontSize()));
                FontMetrics fm = graphics.getFontMetrics();
                int textX = (width - fm.stringWidth(text)) / 2 + config.barTextXOffset();
                int textY = height + fm.getAscent() + 2 + config.barTextYOffset();

                graphics.setColor(Color.BLACK);
                graphics.drawString(text, textX + 1, textY + 1);

                graphics.setColor(Color.WHITE);
                graphics.drawString(text, textX, textY);

                totalHeight = height + fm.getHeight() + 2;
        }

        return new Dimension(width, totalHeight);
    }

}
