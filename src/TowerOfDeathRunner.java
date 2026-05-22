import javax.swing.SwingUtilities;

public class TowerOfDeathRunner {

	public static void main(String[] args) {
		// 1. Safe initialization of the Swing UI on the Event Dispatch Thread (EDT)
		SwingUtilities.invokeLater(() -> {

			GameWindow window = new GameWindow();

			// 2. Spawn the Game Engine on a SEPARATE Background Worker Thread
			Thread gameLogicThread = new Thread(() -> {
				GameEngine game = new GameEngine(window);
				game.main_Menu();
			});

			// 3. Start the engine thread so it can "wait" safely without freezing the window
			gameLogicThread.start();
		});
	}
}