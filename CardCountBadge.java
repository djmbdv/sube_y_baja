import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class CardCountBadge extends JPanel {
    private static final Color TITLE_COLOR = new Color(190, 168, 123);
    private static final Color VALUE_COLOR = new Color(248, 236, 214);
    private static final Color VALUE_HIGHLIGHT = new Color(255, 247, 227);
    private static final Color GLOW_COLOR = new Color(255, 218, 141);

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final Timer animationTimer;

    private int currentCount = -1;
    private float pulseProgress = 1.0f;
    private float shimmerProgress = 1.0f;

    CardCountBadge() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(148, 84));
        setMinimumSize(new Dimension(148, 84));

        titleLabel = new JLabel("MAZO");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        valueLabel = new JLabel();
        valueLabel.setFont(new Font("Serif", Font.BOLD, 22));
        valueLabel.setForeground(VALUE_COLOR);
        valueLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(javax.swing.Box.createVerticalStrut(14));
        add(titleLabel);
        add(javax.swing.Box.createVerticalStrut(4));
        add(valueLabel);

        animationTimer = new Timer(16, e -> {
            pulseProgress += 0.08f;
            shimmerProgress += 0.11f;

            if (pulseProgress >= 1.0f && shimmerProgress >= 1.0f) {
                pulseProgress = 1.0f;
                shimmerProgress = 1.0f;
                titleLabel.setForeground(TITLE_COLOR);
                valueLabel.setForeground(VALUE_COLOR);
                ((Timer) e.getSource()).stop();
            } else {
                float pulse = (float) Math.sin(Math.min(1.0f, pulseProgress) * Math.PI);
                titleLabel.setForeground(blend(TITLE_COLOR, VALUE_HIGHLIGHT, pulse * 0.45f));
                valueLabel.setForeground(blend(VALUE_COLOR, VALUE_HIGHLIGHT, pulse * 0.75f));
            }

            repaint();
        });
    }

    void setCount(int count) {
        if (currentCount != -1 && currentCount != count) {
            pulseProgress = 0.0f;
            shimmerProgress = 0.0f;
            if (animationTimer.isRunning()) {
                animationTimer.stop();
            }
            animationTimer.start();
        }

        currentCount = count;
        valueLabel.setText(String.valueOf(count));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        float pulse = (float) Math.sin(Math.min(1.0f, pulseProgress) * Math.PI);

        if (pulseProgress < 1.0f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse * 0.28f));
            g2.setColor(GLOW_COLOR);
            g2.fillRoundRect(0, 2, width - 4, height - 4, 28, 28);
            g2.setComposite(AlphaComposite.SrcOver);
        }

        g2.setColor(new Color(0, 0, 0, 42));
        g2.fillRoundRect(6, 8, width - 12, height - 12, 24, 24);

        GradientPaint fill = new GradientPaint(
                0,
                0,
                new Color(47, 37, 29, 235),
                width,
                height,
                new Color(30, 24, 19, 235)
        );
        g2.setPaint(fill);
        g2.fillRoundRect(0, 0, width - 8, height - 8, 24, 24);

        if (shimmerProgress < 1.0f) {
            int shimmerWidth = 28;
            int shimmerX = (int) ((width + shimmerWidth) * shimmerProgress) - shimmerWidth;
            GradientPaint shimmer = new GradientPaint(
                    shimmerX,
                    0,
                    new Color(255, 236, 194, 0),
                    shimmerX + shimmerWidth / 2,
                    0,
                    new Color(255, 236, 194, 110),
                    true
            );
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            g2.setPaint(shimmer);
            g2.fillRoundRect(shimmerX, 0, shimmerWidth, height - 8, 18, 18);
            g2.setComposite(AlphaComposite.SrcOver);
        }

        g2.setColor(new Color(214, 180, 110, 185));
        g2.setStroke(new BasicStroke(1.4f + (pulse * 0.8f)));
        g2.drawRoundRect(0, 0, width - 9, height - 9, 24, 24);

        g2.setColor(new Color(255, 224, 164, 90));
        g2.drawLine(18, 14, width - 28, 14);

        if (pulseProgress < 1.0f) {
            g2.setColor(new Color(255, 230, 179, (int) (120 * pulse)));
            g2.fillOval(width - 28, 12, 8, 8);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private Color blend(Color base, Color target, float amount) {
        float safeAmount = Math.max(0.0f, Math.min(1.0f, amount));
        int red = (int) (base.getRed() + (target.getRed() - base.getRed()) * safeAmount);
        int green = (int) (base.getGreen() + (target.getGreen() - base.getGreen()) * safeAmount);
        int blue = (int) (base.getBlue() + (target.getBlue() - base.getBlue()) * safeAmount);
        return new Color(red, green, blue);
    }
}