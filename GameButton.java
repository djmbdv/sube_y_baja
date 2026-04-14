import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class GameButton extends JButton {
    private final Color baseColor;
    private final Color hoverColor;
    private final Color pressedColor;

    GameButton(String text, Color baseColor) {
        super(text);
        this.baseColor = baseColor;
        this.hoverColor = baseColor.brighter();
        this.pressedColor = baseColor.darker();

        setContentAreaFilled(false);
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = baseColor;
        if (!isEnabled()) {
            fill = new Color(92, 80, 66);
        } else if (getModel().isPressed()) {
            fill = pressedColor;
        } else if (getModel().isRollover()) {
            fill = hoverColor;
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g2.setColor(new Color(241, 210, 155, isEnabled() ? 180 : 70));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();

        setForeground(isEnabled() ? new Color(251, 244, 233) : new Color(207, 191, 167));
        super.paintComponent(g);
    }
}