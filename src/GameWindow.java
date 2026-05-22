import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameWindow extends JFrame {
    private JTextArea headerPane;
    private JTextArea logPane;
    private JPanel buttonPanel;

    private String lastInput = "";
    private boolean inputReady = false;

    private static final int DEFAULT_WIDTH = 1050;
    private static final int DEFAULT_HEIGHT = 560;

    private static final int MIN_WIDTH = 700;
    private static final int MIN_HEIGHT = 520;

    public GameWindow() {
        setTitle("Tower Of Death - Retro Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        // --- UPDATED COMMANDS SECTION (Side-Scroll / 3-Row Layout) ---

        // 1. A Grid locked to exactly 3 rows. It expands columns infinitely to the right.
        buttonPanel = new JPanel(new GridLayout(3, 0, 8, 8));
        buttonPanel.setBackground(Color.BLACK);

        // 2. A wrapper to align the grid to the left. This stops the buttons from
        // stretching weirdly across the whole screen when there are only 2 or 3 of them.
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.BLACK);
        wrapperPanel.add(buttonPanel, BorderLayout.WEST);

        // 3. The ScrollPane to handle the horizontal side-scrolling
        JScrollPane commandScroller = new JScrollPane(wrapperPanel);
        commandScroller.setBackground(Color.BLACK);
        commandScroller.getViewport().setBackground(Color.BLACK);

        // Turn ON horizontal scrolling, turn OFF vertical scrolling
        commandScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        commandScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Move the retro border to frame the scroll pane instead of the inner panel
        commandScroller.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GREEN), "COMMANDS",
                TitledBorder.CENTER, TitledBorder.TOP, monoFont, Color.GREEN));

        // Lock the height to perfectly fit 3 rows plus the horizontal scrollbar track below it
        commandScroller.setPreferredSize(new Dimension(DEFAULT_WIDTH, 175));
        add(commandScroller, BorderLayout.SOUTH);

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
            // Force the wrapper and scrollpane to recalculate their widths immediately
            buttonPanel.getParent().revalidate();
        });
    }

    public void addButton(String label, String outputValue) {
        SwingUtilities.invokeLater(() -> {
            JButton btn = new JButton(label);
            btn.setFont(headerPane.getFont());
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.GREEN);

            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GREEN, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
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