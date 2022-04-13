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

import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors

import java.io.BufferedWriter;
import java.io.File; // Import the File class
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class GameBoard extends JPanel {
	// Name-constants for the game board properties
	public static final int GRID_SIZE = 9; // Size of the board
	public static final int SUBGRID_SIZE = 3; // Size of the sub-grid
	public static final int SQUAREROOTGRID = (int) Math.sqrt(GRID_SIZE);

	// Name-constants for UI sizes
	public static final int CELL_SIZE = 60; // Cell width/height in pixels
	public static final int BOARD_WIDTH = CELL_SIZE * GRID_SIZE;
	public static final int BOARD_HEIGHT = CELL_SIZE * GRID_SIZE;
	public static final int DEFAULT_DIFFICULTY = 5;
	// Board width/height in pixels

	public int original_incomplete_cell;
	public int incomplete_cell;

	// The game board composes of 9x9 "Customized" JTextFields,
	private Cell[][] cells = new Cell[GRID_SIZE][GRID_SIZE];
	// It also contains a Puzzle
	private Puzzle puzzle = new Puzzle(this);

	boolean isGamePaused = false;
	boolean isSoundDisabled = false;

	static File correctsoundFile = new File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\correct.wav");
	static File wrongsoundFile = new File("C:\\Users\\Yu Sheng\\Desktop\\Java\\JavaTutorial\\src\\wrong.wav");
	static AudioInputStream audioIn;
	// Get a sound clip resource.
	static Clip clip;

	SudokuMain sudokumain;

	public int hint_used = 0;

	String nameRegex = "\\d\\w+\\d";
	String hourRegex = "[h]\\d\\d";
	String minRegex = "[m]\\d\\d";
	String secRegex = "[s]\\d\\d";

	// Constructor
	public GameBoard(SudokuMain sudokumain) {
		this.sudokumain = sudokumain;
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

	public void setHintsUsed(int hint_used) {
		this.hint_used = hint_used;
	}

	/**
	 * Initialize the puzzle number, status, background/foreground color, of all the
	 * cells from puzzle[][] and isRevealed[][]. Call to start a new game.
	 */
	public void init(int numGuess) {
		// Get a new puzzle
		original_incomplete_cell = numGuess;
		incomplete_cell = numGuess;
		puzzle.newPuzzle(numGuess);
		isGamePaused = false;
		sudokumain.elaspedTime = 0;
		sudokumain.updateStatus();
		// status.setText("Status: " + GameBoard.DEFAULT_DIFFICULTY + " unsolved");

		// Based on the puzzle, initialize all the cells.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col].init(puzzle.numbers[row][col], puzzle.puzzleTableIsShown[row][col]);
			}
		}

	}

	public void loadPuzzle() {
		// Get a new puzzle
		hint_used = 0;
		incomplete_cell = original_incomplete_cell;
		puzzle.loadPuzzle();
		sudokumain.updateStatus();

		// Based on the puzzle, initialize all the cells.
		for (int row = 0; row < GRID_SIZE; ++row) {
			for (int col = 0; col < GRID_SIZE; ++col) {
				cells[row][col].init(puzzle.numbers[row][col], puzzle.isShown[row][col]);
			}
		}

	}

	public void hintPuzzle() {
		// Get a new puzzle
		int[] rowCol = puzzle.hintPuzzle();
		int row = rowCol[0];
		int col = rowCol[1];
		hint_used += 1;
		cells[row][col].init(puzzle.numbers[row][col], puzzle.puzzleTableIsShown[row][col]);

		incomplete_cell -= 1;
		sudokumain.updateStatus();
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
	public class CellInputListener implements ActionListener {
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
					incomplete_cell -= 1;
					int row = sourceCell.getRow();
					int col = sourceCell.getCol();
					puzzle.puzzleTableIsShown[row][col] = true;
					sudokumain.updateStatus();
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
					// puzzle.CheckIfSafe(sourceCell.getRow(), sourceCell.getCol(),
					// sourceCell.getNumber());

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
					String easyName = "null";
					String mediumName = "null";
					String hardName = "null";
					int easyHour = 0;
					int easyMin = 0;
					int easySec = 0;
					int mediumHour = 0;
					int mediumMin = 0;
					int mediumSec = 0;
					int hardHour = 0;
					int hardMin = 0;
					int hardSec = 0;

					// Player's score
					int playerHour = Integer.parseInt(sudokumain.hours_string);
					int playerMin = Integer.parseInt(sudokumain.minutes_string);
					int playerSec = Integer.parseInt(sudokumain.seconds_string);
					
					JOptionPane.showMessageDialog(null,
							"Congratulation! You solved the puzzle on " + sudokumain.difficulty + " difficulty in: "
									+ sudokumain.hours_string + ":" + sudokumain.minutes_string + ":"
									+ sudokumain.seconds_string,
							"Congratulations!", JOptionPane.INFORMATION_MESSAGE);
					
					// Read highscore.txt
					String lineone = "";
					String linetwo = "";
					String linethree = "";
					try {
						File myObj = new File("highscore.txt");
						Scanner myReader = new Scanner(myObj);
						lineone = myReader.nextLine();
						linetwo = myReader.nextLine();
						linethree = myReader.nextLine();

						myReader.close();
					} catch (FileNotFoundException ex) {
						System.out.println("An error occurred.");
						ex.printStackTrace();
					}

					// Query player name
					String name = JOptionPane.showInputDialog("Enter your name to be included in the highscore");

					// Extract three categories and update if player gets a new record
					// Regex
					Pattern namePattern = Pattern.compile(nameRegex);
					Pattern hourPattern = Pattern.compile(hourRegex);
					Pattern minPattern = Pattern.compile(minRegex);
					Pattern secPattern = Pattern.compile(secRegex);

					Matcher matchereasyname = namePattern.matcher(lineone);
					Matcher matchereasyhour = hourPattern.matcher(lineone);
					Matcher matchereasymin = minPattern.matcher(lineone);
					Matcher matchereasysec = secPattern.matcher(lineone);

					Matcher matchermediumname = namePattern.matcher(linetwo);
					Matcher matchermediumhour = hourPattern.matcher(linetwo);
					Matcher matchermediummin = minPattern.matcher(linetwo);
					Matcher matchermediumsec = secPattern.matcher(linetwo);

					Matcher matcherhardname = namePattern.matcher(linethree);
					Matcher matcherhardhour = hourPattern.matcher(linethree);
					Matcher matcherhardmin = minPattern.matcher(linethree);
					Matcher matcherhardsec = secPattern.matcher(linethree);

					matchereasyname.find();
					matchereasyhour.find();
					matchereasymin.find();
					matchereasysec.find();

					// Remove first and last character from name
					easyName = matchereasyname.group().substring(1);
					StringBuilder builderEasy = new StringBuilder();
					int nameLength = easyName.length();
					builderEasy.append(easyName);
					builderEasy.delete(nameLength - 1, nameLength);
					easyName = builderEasy.toString();

					easyHour = Integer.parseInt(matchereasyhour.group().substring(1));
					easyMin = Integer.parseInt(matchereasymin.group().substring(1));
					easySec = Integer.parseInt(matchereasysec.group().substring(1));

					matchermediumname.find();
					matchermediumhour.find();
					matchermediummin.find();
					matchermediumsec.find();

					// Remove first and last character from name
					mediumName = matchermediumname.group().substring(1);
					StringBuilder builderMedium = new StringBuilder();
					nameLength = mediumName.length();
					builderMedium.append(mediumName);
					builderMedium.delete(nameLength - 1, nameLength);
					mediumName = builderMedium.toString();

					mediumHour = Integer.parseInt(matchermediumhour.group().substring(1));
					mediumMin = Integer.parseInt(matchermediummin.group().substring(1));
					mediumSec = Integer.parseInt(matchermediumsec.group().substring(1));

					matcherhardname.find();
					matcherhardhour.find();
					matcherhardmin.find();
					matcherhardsec.find();

					// Remove first and last character from name
					hardName = matcherhardname.group().substring(1);
					StringBuilder builderHard = new StringBuilder();
					nameLength = hardName.length();
					builderHard.append(hardName);
					builderHard.delete(nameLength - 1, nameLength);
					hardName = builderHard.toString();

					hardHour = Integer.parseInt(matcherhardhour.group().substring(1));
					hardMin = Integer.parseInt(matcherhardmin.group().substring(1));
					hardSec = Integer.parseInt(matcherhardsec.group().substring(1));

					String easyHourStr = String.valueOf(easyHour);
					String easyMinStr = String.valueOf(easyMin);
					String easySecStr = String.valueOf(easySec);
					String mediumHourStr = String.valueOf(mediumHour);
					String mediumMinStr = String.valueOf(mediumMin);
					String mediumSecStr = String.valueOf(mediumSec);
					String hardHourStr = String.valueOf(hardHour);
					String hardMinStr = String.valueOf(hardMin);
					String hardSecStr = String.valueOf(hardSec);
					
					if (sudokumain.difficulty == "easy") {
						if (playerHour < easyHour) {
							easyName = name;
							easyHour = playerHour;
							easyMin = playerMin;
							easySec = playerSec;
						} else if (playerMin < easyMin) {
							easyName = name;
							easyHour = playerHour;
							easyMin = playerMin;
							easySec = playerSec;
						} else if (playerSec < easySec) {
							easyName = name;
							easyHour = playerHour;
							easyMin = playerMin;
							easySec = playerSec;
						}
					} else if (sudokumain.difficulty == "medium") {
						if (playerHour < mediumHour) {
							mediumName = name;
							mediumHour = playerHour;
							mediumMin = playerMin;
							mediumSec = playerSec;
						} else if (playerMin < mediumMin) {
							mediumName = name;
							mediumHour = playerHour;
							mediumMin = playerMin;
							mediumSec = playerSec;
						} else if (playerSec < mediumSec) {
							mediumName = name;
							mediumHour = playerHour;
							mediumMin = playerMin;
							mediumSec = playerSec;
						}
					} else if (sudokumain.difficulty == "hard") {
						if (playerHour < hardHour) {
							hardName = name;
							hardHour = playerHour;
							hardMin = playerMin;
							hardSec = playerSec;
						} else if (playerMin < hardMin) {
							hardName = name;
							hardHour = playerHour;
							hardMin = playerMin;
							hardSec = playerSec;
						} else if (playerSec < hardSec) {
							hardName = name;
							hardHour = playerHour;
							hardMin = playerMin;
							hardSec = playerSec;
						}
					}

					// Padding string values
					if (easyHour < 10) {
						easyHourStr = "0" + easyHour;
					}
					if (easyMin < 10) {
						easyMinStr = "0" + easyMin;
					}
					if (easySec < 10) {
						easySecStr = "0" + easySec;
					}
					if (mediumHour < 10) {
						mediumHourStr = "0" + mediumHour;
					}
					if (mediumMin < 10) {
						mediumMinStr = "0" + mediumMin;
					}
					if (mediumSec < 10) {
						mediumSecStr = "0" + mediumSec;
					}
					if (hardHour < 10) {
						hardHourStr = "0" + hardHour;
					}
					if (hardMin < 10) {
						hardMinStr = "0" + hardMin;
					}
					if (hardSec < 10) {
						hardSecStr = "0" + hardSec;
					}

					// Write score into text file
					try {
						// Create new file
						File myObj = new File("highscore.txt");
						myObj.createNewFile();

						// Write into file
						FileWriter myWriter = new FileWriter("highscore.txt");
						myWriter.write("Name: 1" + easyName + "1, Easy" + ", Time: h" + easyHourStr + "m" + easyMinStr
								+ "s" + easySecStr + "\n");
						myWriter.write("Name: 1" + mediumName + "1, Medium" + ", Time: h" + mediumHourStr + "m"
								+ mediumMinStr + "s" + mediumSecStr + "\n");
						myWriter.write("Name: 1" + hardName + "1, Hard" + ", Time: h" + hardHourStr + "m" + hardMinStr
								+ "s" + hardSecStr + "\n");
						myWriter.close();
					} catch (IOException i) {
						i.printStackTrace();
					}

					// Highscore message dialog
					String lineOne = "Name: " + easyName + ", Difficulty: Easy, " + "Time: " + easyHourStr + ":"
							+ easyMinStr + ":" + easySecStr;
					String lineTwo = "Name: " + mediumName + ", Difficulty: Medium, " + "Time: " + mediumHourStr + ":"
							+ mediumMinStr + ":" + mediumSecStr;
					String lineThree = "Name: " + hardName + ", Difficulty: Hard, " + "Time: " + hardHourStr + ":"
							+ hardMinStr + ":" + hardSecStr;

					// String html = "<html>   <head>     <link       href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css'       rel='stylesheet'       integrity='sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3'       crossorigin='anonymous'     />   </head>   <!-- Confirmation Page -->   <section     class='bg-dark text-light p-5 p-lg-0 pt-lg-5 text-center text-sm-start'   >     <div class='container'>       <div class='row g-4'>         <div class='d-sm-flex align-items-center justify-content-between'>           <div>             <h1>Highscore</h1>           </div>         </div>       </div>       <div class='p-3'></div>     </div>   </section>    <!-- Table section -->   <section id='about' class='p-5'>     <table class='table'>       <thead>         <tr>           <th scope='col'>Name</th>           <th scope='col'>Difficulty</th>           <th scope='col'>Time</th>         </tr>       </thead>       <tbody>         <tr>           <th scope='row'>Lucas</th>           <td>Easy</td>           <td>00:03:34</td>         </tr>         <tr>           <th scope='row'>Felix</th>           <td>Medium</td>           <td>00:05:59</td>         </tr>         <tr>           <th scope='row'>PengHwee</th>           <td>Hard</td>           <td>00:12:34</td>         </tr>       </tbody>     </table>   </section> </html>";
					
					String html = "<html><h1>The top score for each difficulty are:</h1>" + "<p>" + lineOne + "<p>"
							+ "<p>" + lineTwo + "<p>" + "<p>" + lineThree + "<p></html>";
					// change to alter the width
					int w = 175;

					JOptionPane.showMessageDialog(null, String.format(html, w, w), "Highscore",
							JOptionPane.INFORMATION_MESSAGE);

					init(DEFAULT_DIFFICULTY);
				}
			}
		}
	}
}