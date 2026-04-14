import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class Main extends JFrame {
    private static final Color TEXT_PRIMARY = new Color(246, 235, 214);
    private static final Color TEXT_SECONDARY = new Color(196, 182, 156);
    private static final Color PANEL_BG = new Color(34, 28, 24, 210);
    private static final Color BTN_BAJA = new Color(126, 39, 50);
    private static final Color BTN_SUBE = new Color(186, 118, 37);
    private static final Color BTN_IGUAL = new Color(66, 69, 79);
    private static final Color BTN_OTRA_MANO = new Color(38, 118, 81);
    private static final Color TABLE_BG_START = new Color(25, 24, 29);
    private static final Color TABLE_BG_END = new Color(20, 58, 41);
    private static final Color TABLE_GLOW = new Color(255, 214, 133, 32);
    private static final Color CARD_STAGE_START = new Color(53, 95, 72);
    private static final Color CARD_STAGE_END = new Color(24, 49, 37);
    private static final Color CARD_STAGE_BORDER = new Color(181, 153, 92);

    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 42);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.PLAIN, 17);
    private static final Font INFO_FONT = new Font("SansSerif", Font.BOLD, 14);

    private static final int DEFAULT_ROUND_DELAY_MS = 360;
    private static final int DOUBLE_LOSE_DELAY_MS = 520;

    private final Random random = new Random();
    private final List<Carta> mazo = new LinkedList<>();

    private GameButton bBaja;
    private GameButton bSube;
    private GameButton bIgual;
    private GameButton bReiniciar;
    private Carta lastCarta;
    private CardCountBadge cardCount;
    private CardPanel panelCarta;

    private enum GuessAction {
        UP,
        DOWN,
        SAME
    }

    private enum RoundOutcome {
        SAFE,
        LOSE,
        DOUBLE_LOSE,
        DOUBLE_WIN
    }

    public Main() throws IOException {
        super("Carticas");

        initMazo();
        lastCarta = drawRandomCard();

        panelCarta = new CardPanel(CARD_STAGE_START, CARD_STAGE_END, CARD_STAGE_BORDER);
        panelCarta.setCarta(lastCarta);

        cardCount = new CardCountBadge();
        updateCardCount();

        bBaja = createGameButton("BAJA", BTN_BAJA, GuessAction.DOWN);
        bSube = createGameButton("SUBE", BTN_SUBE, GuessAction.UP);
        bIgual = createGameButton("IGUAL", BTN_IGUAL, GuessAction.SAME);
        bReiniciar = createRestartButton();

        GradientPanel root = new GradientPanel(TABLE_BG_START, TABLE_BG_END, TABLE_GLOW);
        root.setLayout(new BorderLayout(22, 22));
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildControls(), BorderLayout.SOUTH);

        setContentPane(root);
        configureFrame();
        showStartDialog();
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 740));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Mesa de Sube y Baja");
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Adivina la que viene... y bebe si te equivocás 😉");
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(TEXT_SECONDARY);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        title.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(subtitle);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleBlock, BorderLayout.WEST);
        header.add(cardCount, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(panelCarta, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildControls() {
        JPanel controls = new JPanel(new GridLayout(1, 4, 12, 0));
        controls.setOpaque(false);
        controls.add(bBaja);
        controls.add(bSube);
        controls.add(bIgual);
        controls.add(bReiniciar);
        return controls;
    }

    private GameButton createGameButton(String text, Color background, GuessAction action) {
        GameButton button = new GameButton(text, background);
        styleActionButton(button);
        button.addActionListener((ActionEvent ae) -> playRound(action));
        return button;
    }

    private GameButton createRestartButton() {
        GameButton button = new GameButton("OTRA MANO", BTN_OTRA_MANO);
        styleActionButton(button);
        button.addActionListener((ActionEvent ae) -> restartGame());
        return button;
    }

    private Carta drawRandomCard() {
        int index = random.nextInt(mazo.size());
        Carta selectedCard = mazo.get(index);
        mazo.remove(index);
        return selectedCard;
    }

    private void setGuessButtonsEnabled(boolean enabled) {
        bBaja.setEnabled(enabled);
        bSube.setEnabled(enabled);
        bIgual.setEnabled(enabled);
    }

    private void setAllControlsEnabled(boolean enabled) {
        setGuessButtonsEnabled(enabled);
        bReiniciar.setEnabled(enabled);
    }

    private void playRound(GuessAction action) {
        Carta newCarta = drawRandomCard();
        RoundOutcome outcome = resolveOutcome(action, newCarta);

        lastCarta = newCarta;
        panelCarta.setCarta(newCarta);
        updateCardCount();
        completeRound(outcome, mazo.isEmpty());
    }

    private RoundOutcome resolveOutcome(GuessAction action, Carta newCarta) {
        switch (action) {
            case UP:
                if (newCarta.number < lastCarta.number) {
                    return RoundOutcome.LOSE;
                }
                return newCarta.number == lastCarta.number ? RoundOutcome.DOUBLE_LOSE : RoundOutcome.SAFE;
            case DOWN:
                if (newCarta.number > lastCarta.number) {
                    return RoundOutcome.LOSE;
                }
                return newCarta.number == lastCarta.number ? RoundOutcome.DOUBLE_LOSE : RoundOutcome.SAFE;
            case SAME:
                return newCarta.number == lastCarta.number ? RoundOutcome.DOUBLE_WIN : RoundOutcome.LOSE;
            default:
                return RoundOutcome.SAFE;
        }
    }

    private void animateOutcome(RoundOutcome outcome) {
        switch (outcome) {
            case LOSE:
                panelCarta.playAlert(new Color(217, 73, 89), false);
                panelCarta.playShake(false);
                break;
            case DOUBLE_LOSE:
                panelCarta.playAlert(new Color(196, 33, 58), true);
                panelCarta.playShake(true);
                break;
            case DOUBLE_WIN:
                panelCarta.playAlert(new Color(31, 160, 116), true);
                break;
            default:
                break;
        }
    }

    private void showOutcomeDialog(RoundOutcome outcome) {
        switch (outcome) {
            case LOSE:
                showWarningDialog("Se te fue la mano. BEBE.", "Te toca");
                break;
            case DOUBLE_LOSE:
                showWarningDialog("Te clavaste duro. BEBE DOBLE.", "Te jodiste");
                break;
            case DOUBLE_WIN:
                showInfoDialog("Manda a Beber a todos DOBLE", "La pegaste");
                break;
            default:
                break;
        }
    }

    private void completeRound(RoundOutcome outcome, boolean gameEnded) {
        setAllControlsEnabled(false);
        animateOutcome(outcome);

        Timer timer = new Timer(getRoundDelay(outcome), e -> {
            ((Timer) e.getSource()).stop();

            if (outcome != RoundOutcome.SAFE) {
                showOutcomeDialog(outcome);
            }

            if (gameEnded) {
                gameOver();
            }

            if (mazo.isEmpty()) {
                setGuessButtonsEnabled(false);
                bReiniciar.setEnabled(true);
            } else {
                setAllControlsEnabled(true);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private int getRoundDelay(RoundOutcome outcome) {
        return outcome == RoundOutcome.DOUBLE_LOSE ? DOUBLE_LOSE_DELAY_MS : DEFAULT_ROUND_DELAY_MS;
    }

    private void gameOver() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Se acabo el mazo. Quieres jugar otra mano?",
                "Se cerro la ronda",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            restartGame();
        }
    }

    private void restartGame() {
        try {
            initMazo();
            lastCarta = drawRandomCard();
            panelCarta.setCarta(lastCarta);
            updateCardCount();
            setAllControlsEnabled(true);
        } catch (IOException e) {
            showErrorDialog("No pude barajear de nuevo.");
        }
    }

    private void initMazo() throws IOException {
        mazo.clear();
        for (int i = 0; i < 40; i++) {
            mazo.add(new Carta(i % 10 + 1, i / 10));
        }
    }

    private void styleActionButton(GameButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 66));
    }

    private void updateCardCount() {
        cardCount.setCount(mazo.size());
    }

    private void showStartDialog() {
        JLabel msg = new JLabel("Bueno, arranco la mano: liga si la proxima carta sube, baja o sale igual.");
        msg.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JOptionPane.showMessageDialog(this, msg, "Vamos a darle", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // If the system look and feel fails, Swing uses the default one.
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Main();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudieron cargar las imagenes de cartas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
