import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class CardPanel extends JPanel {
    private final Color stageStart;
    private final Color stageEnd;
    private final Color borderColor;

    private Carta carta;
    private java.awt.Image previousImage;
    private float fadeProgress = 1.0f;
    private float alertProgress = 1.0f;
    private float alertMaxAlpha = 0.0f;
    private float shakeProgress = 1.0f;
    private int shakeAmplitude = 0;
    private Color alertColor = new Color(0, 0, 0);

    private final Timer fadeTimer;
    private final Timer alertTimer;
    private final Timer shakeTimer;

    CardPanel(Color stageStart, Color stageEnd, Color borderColor) {
        this.stageStart = stageStart;
        this.stageEnd = stageEnd;
        this.borderColor = borderColor;

        setOpaque(false);
        setPreferredSize(new Dimension(500, 500));
        setMinimumSize(new Dimension(320, 320));

        fadeTimer = new Timer(16, e -> {
            fadeProgress += 0.12f;
            if (fadeProgress >= 1.0f) {
                fadeProgress = 1.0f;
                previousImage = null;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });

        alertTimer = new Timer(16, e -> {
            alertProgress += 0.06f;
            if (alertProgress >= 1.0f) {
                alertProgress = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });

        shakeTimer = new Timer(16, e -> {
            shakeProgress += 0.08f;
            if (shakeProgress >= 1.0f) {
                shakeProgress = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
    }

    void setCarta(Carta carta) {
        if (this.carta != null) {
            previousImage = this.carta.image;
        }
        this.carta = carta;

        if (previousImage == null || carta == null || carta.image == null) {
            fadeProgress = 1.0f;
            repaint();
            return;
        }

        fadeProgress = 0.0f;
        restartTimer(fadeTimer);
    }

    void playAlert(Color color, boolean intense) {
        alertColor = color;
        alertMaxAlpha = intense ? 0.42f : 0.24f;
        alertProgress = 0.0f;
        restartTimer(alertTimer);
    }

    void playShake(boolean intense) {
        shakeAmplitude = intense ? 18 : 10;
        shakeProgress = 0.0f;
        restartTimer(shakeTimer);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int side = Math.min(getWidth(), getHeight()) - 18;
        if (side < 120) {
            g2.dispose();
            return;
        }

        int squareX = ((getWidth() - side) / 2) + computeShakeOffset();
        int squareY = (getHeight() - side) / 2;

        g2.setColor(new Color(0, 0, 0, 54));
        g2.fillRoundRect(squareX + 8, squareY + 10, side, side, 28, 28);

        GradientPaint stage = new GradientPaint(squareX, squareY, stageStart, squareX + side, squareY + side, stageEnd);
        g2.setPaint(stage);
        g2.fillRoundRect(squareX, squareY, side, side, 28, 28);

        paintAlert(g2, squareX, squareY, side);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect(squareX, squareY, side, side, 28, 28);

        paintCard(g2, squareX, squareY, side);
        g2.dispose();
    }

    private void paintAlert(Graphics2D g2, int squareX, int squareY, int side) {
        if (alertProgress >= 1.0f) {
            return;
        }

        float pulse = (float) Math.sin(alertProgress * Math.PI);
        float overlayAlpha = alertMaxAlpha * pulse;
        float borderAlpha = Math.min(0.9f, overlayAlpha + 0.18f);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
        g2.setColor(alertColor);
        g2.fillRoundRect(squareX, squareY, side, side, 28, 28);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, borderAlpha));
        g2.setStroke(new BasicStroke(3.2f));
        g2.drawRoundRect(squareX + 2, squareY + 2, side - 4, side - 4, 26, 26);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void paintCard(Graphics2D g2, int squareX, int squareY, int side) {
        int cardW = (int) (side * 0.62);
        int cardH = (int) (cardW * (Carta.ALTO_CARTA / (double) Carta.ANCHO_CARTA));
        int maxCardH = (int) (side * 0.86);
        if (cardH > maxCardH) {
            cardH = maxCardH;
            cardW = (int) (cardH * (Carta.ANCHO_CARTA / (double) Carta.ALTO_CARTA));
        }

        int cardX = squareX + (side - cardW) / 2;
        int cardY = squareY + (side - cardH) / 2;

        g2.setColor(new Color(0, 0, 0, 32));
        g2.fillRoundRect(cardX + 6, cardY + 8, cardW, cardH, 22, 22);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 22, 22);
        g2.setColor(new Color(215, 223, 234));
        g2.setStroke(new BasicStroke(1.3f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 22, 22);

        if (previousImage != null && fadeProgress < 1.0f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - fadeProgress));
            g2.drawImage(previousImage, cardX + 8, cardY + 8, cardW - 16, cardH - 16, this);
        }

        if (carta != null && carta.image != null) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeProgress));
            g2.drawImage(carta.image, cardX + 8, cardY + 8, cardW - 16, cardH - 16, this);
        }

        g2.setComposite(AlphaComposite.SrcOver);
    }

    private int computeShakeOffset() {
        if (shakeProgress >= 1.0f) {
            return 0;
        }

        double wave = Math.sin(shakeProgress * Math.PI * 10.0);
        double decay = 1.0 - shakeProgress;
        return (int) Math.round(wave * decay * shakeAmplitude);
    }

    private void restartTimer(Timer timer) {
        if (timer.isRunning()) {
            timer.stop();
        }
        timer.start();
    }
}