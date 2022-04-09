package sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The main Sudoku program
 */
public class SudokuMain extends JFrame {
	// private variables
	GameBoard board = new GameBoard();
	JButton btnNewGame = new JButton("New Game");
	public static final int WINDOW_WIDTH = 300;
	public static final int WINDOW_HEIGHT = 150;
	private Button newGameBtn;
	
	// Constructor
	public SudokuMain() {
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(board, BorderLayout.CENTER);

		// Add a button to the south to re-start the game
		newGameBtn = new Button("New Game");
		cp.add(newGameBtn, BorderLayout.EAST);
		newGameBtnListener listener = new newGameBtnListener();
		newGameBtn.addActionListener(listener);
		
		board.init();

		pack(); // Pack the UI components, instead of setSize()
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle window closing
		setTitle("Sudoku");
		setVisible(true);
	}

	/** The entry main() entry method */
	public static void main(String[] args) {
		// [TODO 1] Check Swing program template on how to run the constructor
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SudokuMain(); // Let the constructor do the job
			}
		});
	}
	
	private class newGameBtnListener implements ActionListener {
		// ActionEvent handler ‐ Called back upon button‐click.
		@Override
		public void actionPerformed(ActionEvent evt) {
			board.init();
		}
	}
}