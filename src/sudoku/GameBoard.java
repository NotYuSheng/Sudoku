package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class GameBoard extends JPanel {
	// Name-constants for the game board properties
	public static final int GRID_SIZE = 9; // Size of the board
	public static final int SUBGRID_SIZE = 3; // Size of the sub-grid
	public static final int SQUAREROOTGRID = (int) Math.sqrt(GRID_SIZE);

	// Name-constants for UI sizes
	public static final int CELL_SIZE = 60; // Cell width/height in pixels
	public static final int BOARD_WIDTH = CELL_SIZE * GRID_SIZE;
	public static final int BOARD_HEIGHT = CELL_SIZE * GRID_SIZE;
	public static final int DEFAULT_DIFFICULTY = 50;
	// Board width/height in pixels

	public int incomplete_cell;
	public JTextField status = new JTextField("Status: " + incomplete_cell + " unsolved");
	
	// The game board composes of 9x9 "Customized" JTextFields,
	private Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];
	// It also contains a Puzzle
	private Puzzle puzzle = new Puzzle();
	
	boolean isGamePaused = false;
	boolean isSoundDisabled = false;
	
	static File correctsoundFile = new File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\correct.wav");
	static File wrongsoundFile = new File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\wrong.wav");
	static AudioInputStream audioIn;
	// Get a sound clip resource.
	static Clip clip;

	// Constructor
	public GameBoard() {
		super.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // JPanel

		// Allocate the 2D array of Cell, and added into JPanel.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col] = new Cell(row, col);
				super.add(cells[row][col]); // JPanel
			}
		}

		// [TODO 3] Allocate a common listener as the ActionEvent listener for all the

		CellInputListener listener = new CellInputListener();
		// [TODO 4] Every editable cell adds this common listener

		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				if (cells[row][col].isEditable()) {
					// For all editable rows and cols
					cells[row][col].addActionListener(listener);
				}
			}
		}
		super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
	}

	/**
	 * Initialize the puzzle number, status, background/foreground color, of all the
	 * cells from puzzle[][] and isRevealed[][]. Call to start a new game.
	 */
	public void init(int numGuess) {
		// Get a new puzzle
		incomplete_cell = numGuess;
		puzzle.newPuzzle(numGuess);
		isGamePaused = false;
		//status.setText("Status: " + GameBoard.DEFAULT_DIFFICULTY + " unsolved");
		
		// Based on the puzzle, initialize all the cells.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col].init(puzzle.numbers[row][col], puzzle.puzzleTableIsShown[row][col]);
			}
		}
	}
	
	public void loadPuzzle() {
		// Get a new puzzle
		puzzle.loadPuzzle();

		// Based on the puzzle, initialize all the cells.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col].init(puzzle.numbers[row][col], puzzle.puzzleTableIsShown[row][col]);
			}
		}
		
	}
	
	public void hintPuzzle() {
		// Get a new puzzle
		puzzle.hintPuzzle();

		// Based on the puzzle, initialize all the cells.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col].init(puzzle.numbers[row][col], puzzle.puzzleTableIsShown[row][col]);
			}
		}
		incomplete_cell -= 1;
		if (isSolved()) {
			JOptionPane.showMessageDialog(null, "Congratulation!");
			init(DEFAULT_DIFFICULTY);
		}
	}

	/**
	 * Return true if the puzzle is solved i.e., none of the cell have status of
	 * NO_GUESS or WRONG_GUESS
	 */
	public boolean isSolved() {
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				if (cells[row][col].status == CellStatus.NO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void pause() {
		isGamePaused = true;
	}
	
	public void resume() {
		isGamePaused = false;
	}
	
	public void enableSound() {
		isSoundDisabled = false;
	}
	
	public void disableSound() {
		isSoundDisabled = true;
	}
	
	// [TODO 2] Define a Listener Inner Class
	private class CellInputListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!isGamePaused) {
				// Get the JTextField that triggers this event
				Cell sourceCell = (Cell) e.getSource();
				
				// Retrieve the int entered
				int numberIn = Integer.parseInt(sourceCell.getText());
				// For debugging
				System.out.println("You entered " + sourceCell.getText());
	
				/*
				 * [TODO 5] Check the numberIn against sourceCell.number. Update the cell status
				 * sourceCell.status, and re-paint the cell via sourceCell.paint().
				 */
				if (numberIn == sourceCell.number) {
					sourceCell.status = CellStatus.CORRECT_GUESS;
					try {
						// Open an audio input stream.
						// from a wave File
						audioIn = AudioSystem.getAudioInputStream(correctsoundFile);
						// Get a sound clip resource.
						clip = AudioSystem.getClip();
						// Open audio clip and load samples from the audio input stream.
						clip.open(audioIn);
						if (!isSoundDisabled) {
							clip.start();
						}
					} catch (UnsupportedAudioFileException error) {
						error.printStackTrace();
					} catch (IOException error) {
						error.printStackTrace();
					} catch (LineUnavailableException error) {
						error.printStackTrace();
					}
				} else {
					sourceCell.status = CellStatus.WRONG_GUESS;
					try {
						// Open an audio input stream.
						// from a wave File
						audioIn = AudioSystem.getAudioInputStream(wrongsoundFile);
						// Get a sound clip resource.
						clip = AudioSystem.getClip();
						// Open audio clip and load samples from the audio input stream.
						clip.open(audioIn);
						if (!isSoundDisabled) {
							clip.start();
						}
					} catch (UnsupportedAudioFileException error) {
						error.printStackTrace();
					} catch (IOException error) {
						error.printStackTrace();
					} catch (LineUnavailableException error) {
						error.printStackTrace();
					}
				}
				sourceCell.paint();
				/*
				 * [TODO 6][Later] Check if the player has solved the puzzle after this move, by
				 * call isSolved(). Put up a congratulation JOptionPane, if so.
				 */
				if (isSolved()) {
					JOptionPane.showMessageDialog(null, "Congratulation!");
					init(DEFAULT_DIFFICULTY);
				}
			}
		}
	}
}