import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class GradientPanel extends JPanel {
    private final Color startColor;
    private final Color endColor;
    private final Color glowColor;

    GradientPanel(Color startColor, Color endColor, Color glowColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.glowColor = glowColor;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint background = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2.setPaint(background);
        g2.fillRect(0, 0, getWidth(), getHeight());

        GradientPaint glow = new GradientPaint(0, 0, glowColor, 0, getHeight() / 2, new Color(255, 214, 133, 0));
        g2.setPaint(glow);
        g2.fillRect(0, 0, getWidth(), getHeight() / 2);
        g2.dispose();
    }
}