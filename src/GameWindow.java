import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameWindow extends JFrame {
    private JTextArea headerPane;
    private JTextArea logPane;
    private JPanel buttonPanel;

    private String lastInput = "";
    private boolean inputReady = false;

    // Fixed default dimensions matching the ideal wide aspect ratio layout
    private static final int DEFAULT_WIDTH = 900;
    private static final int DEFAULT_HEIGHT = 520;

    // Bounds to prevent the layout from shrinking into an unreadable size
    private static final int MIN_WIDTH = 700;
    private static final int MIN_HEIGHT = 480;

    public GameWindow() {
        setTitle("Tower Of Death - Retro Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Allow the user to maximize or stretch, but lock the floor boundary size
        setResizable(true);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.BLACK);

        Font monoFont = new Font("Courier New", Font.PLAIN, 13);

        // Top Viewport (Status / Enemy Encounter View)
        headerPane = createStaticPane(4, 60, monoFont, "CURRENT STATUS");
        add(headerPane, BorderLayout.NORTH);

        // Center Viewport (The Scrolling Action/Combat Log)
        logPane = createStaticPane(12, 60, monoFont, "ACTION LOG");

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // South Pane (Dynamic Button Control Menu layout)
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN), "COMMANDS",
                TitledBorder.CENTER, TitledBorder.TOP, monoFont, Color.GREEN));
        add(buttonPanel, BorderLayout.SOUTH);

        // Enforce the ideal dimensional scale instead of using pack()
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JTextArea createStaticPane(int rows, int cols, Font font, String title) {
        JTextArea area = new JTextArea(rows, cols);
        area.setFont(font);
        area.setEditable(false);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.GREEN);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN), title,
                TitledBorder.LEFT, TitledBorder.TOP, font, Color.GREEN));
        return area;
    }

    // --- Thread Control Methods ---

    public synchronized void updateHeader(String text) {
        SwingUtilities.invokeLater(() -> headerPane.setText(text));
    }

    public synchronized void appendLog(String text) {
        SwingUtilities.invokeLater(() -> logPane.append(text + "\n"));
    }

    public synchronized void clearLog() {
        SwingUtilities.invokeLater(() -> logPane.setText(""));
    }

    public void clearButtons() {
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
    }

    public void addButton(String label, String outputValue) {
        SwingUtilities.invokeLater(() -> {
            JButton btn = new JButton(label);
            btn.setFont(headerPane.getFont());
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.GREEN);

            // Outer bounding box frame matching terminal line aesthetics
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GREEN, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                // Fixed outer instance synchronization context reference lock
                synchronized(GameWindow.this) {
                    lastInput = outputValue;
                    inputReady = true;
                    GameWindow.this.notifyAll();
                }
            });
            buttonPanel.add(btn);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
    }

    public String getButtonInput() {
        synchronized(this) {
            inputReady = false;
            while (!inputReady) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return lastInput;
        }
    }
}