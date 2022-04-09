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

	// Constructor
	public SudokuMain() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(board, BorderLayout.CENTER);

		// Add a button to the south to re-start the game	
		cp.add(new JButton("RESTART"), BorderLayout.EAST);

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
}