// https://www.geeksforgeeks.org/program-sudoku-generator/

/*
1. Fill all the diagonal 3x3 matrices.
2. Fill recursively rest of the non-diagonal matrices.
   For every cell to be filled, we try all numbers until
   we find a safe number to be placed.  
3. Once matrix is fully filled, remove K elements
   randomly to complete game.
 */

package sudoku;

import java.util.Random;

/**
 * The Sudoku number puzzle to be solved
 */
public class Puzzle<Gameboard> {
	// All variables have package access
	int[][] numbers = new int[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// For testing, only 2 cells of "8" is NOT shown
	boolean[][] isShown = new boolean[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// Puzzle Table
	int[][] puzzleTable = new int[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// Nodes that are shown
	boolean[][] puzzleTableIsShown = new boolean[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// Save states
	int[][] saveTable;
	boolean[][] saveIsShown;

	// Constructor
	public Puzzle() {
		super(); // JPanel
	}

	public void puzzleTableClear() {
		for (int row = 0; row < GameBoard.GRID_SIZE; row++) {
			for (int col = 0; col < GameBoard.GRID_SIZE; col++) {
				puzzleTable[row][col] = 0;
			}
		}
	}

	public void puzzleTableIsShownClear() {
		for (int row = 0; row < GameBoard.GRID_SIZE; row++) {
			for (int col = 0; col < GameBoard.GRID_SIZE; col++) {
				puzzleTableIsShown[row][col] = true;
			}
		}
	}

	public void fillGrid() {
		int count = 0;
		for (int i = 0; i < GameBoard.GRID_SIZE; i += GameBoard.SQUAREROOTGRID) {
			int[] randArray = generateRandArr();
			for (int row = 0; row < GameBoard.SQUAREROOTGRID; row++) {
				for (int col = 0; col < GameBoard.SQUAREROOTGRID; col++) {
					puzzleTable[i + row][i + col] = randArray[count];
					count++;
				}
			}
			count = 0;
		}
	}

	public int[] generateRandArr() {
		int[] array = new int[GameBoard.GRID_SIZE];
		for (int i = 0; i < array.length; i++) {
			array[i] = i + 1;
		}
		Random rand = new Random();

		for (int i = 0; i < array.length; i++) {
			int randomIndexToSwap = rand.nextInt(array.length);
			int temp = array[randomIndexToSwap];
			array[randomIndexToSwap] = array[i];
			array[i] = temp;
		}
		return array;
	}

	boolean unUsedInGrid(int rowStart, int colStart, int num) {
		for (int i = 0; i < GameBoard.SQUAREROOTGRID; i++)
			for (int j = 0; j < GameBoard.SQUAREROOTGRID; j++)
				if (puzzleTable[rowStart + i][colStart + j] == num)
					return false;

		return true;
	}

	boolean unUsedInRow(int i, int num) {
		for (int j = 0; j < GameBoard.GRID_SIZE; j++)
			if (puzzleTable[i][j] == num)
				return false;
		return true;
	}

	boolean unUsedInCol(int j, int num) {
		for (int i = 0; i < GameBoard.GRID_SIZE; i++)
			if (puzzleTable[i][j] == num)
				return false;
		return true;
	}

	boolean CheckIfSafe(int i, int j, int num) {
		return (unUsedInRow(i, num) && unUsedInCol(j, num)
				&& unUsedInGrid(i - i % GameBoard.SQUAREROOTGRID, j - j % GameBoard.SQUAREROOTGRID, num));
	}

	boolean fillRemaining(int row, int col) {
		// Loop to next row
		if (col >= GameBoard.GRID_SIZE && row < GameBoard.GRID_SIZE - 1) {
			row += 1;
			col = 0;
		}
		// Fill complete
		if (row >= GameBoard.GRID_SIZE && col >= GameBoard.GRID_SIZE) {
			return true;
		}
		// Prevent modification of diagonal grids
		// Grid 1
		if (row < GameBoard.SQUAREROOTGRID) {
			if (col < GameBoard.SQUAREROOTGRID)
				col = GameBoard.SQUAREROOTGRID;
			// Grid 2
		} else if (row < GameBoard.GRID_SIZE - GameBoard.SQUAREROOTGRID) {
			if (col == (int) (row / GameBoard.SQUAREROOTGRID) * GameBoard.SQUAREROOTGRID)
				col = col + GameBoard.SQUAREROOTGRID;
			// Grid 3
		} else {
			if (col == GameBoard.GRID_SIZE - GameBoard.SQUAREROOTGRID) {
				row += 1;
				col = 0;
				if (row >= GameBoard.GRID_SIZE) {
					return true;
				}
			}
		}

		for (int num = 1; num <= GameBoard.GRID_SIZE; num++) {
			// Enumerate until a valid number is inserted to cell
			if (CheckIfSafe(row, col, num)) {
				puzzleTable[row][col] = num;
				// Fill next cell, if valid cell cannot be found in next cell, 
				// this cell will enumerate to next valid number
				if (fillRemaining(row, col + 1))
					return true;
				// Undo this cell
				puzzleTable[row][col] = 0;
			}
		}
		return false;
	}

	public int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}

	public void removeDigits(int count) {
		while (count != 0) {
			int row = getRandomNumber(0, GameBoard.GRID_SIZE - 1);
			int col = getRandomNumber(0, GameBoard.GRID_SIZE - 1);
			if (puzzleTable[row][col] != 0) {
				puzzleTable[row][col] = 0;
				puzzleTableIsShown[row][col] = false;
				count--;
			}
		}
	}

	public void savePuzzle() {
		saveTable = puzzleTable;
		saveIsShown = puzzleTableIsShown;
	}

	public void loadPuzzle() {
		puzzleTable = saveTable;
		puzzleTableIsShown = saveIsShown;
	}

	public void resetPuzzle() {
		loadPuzzle();
	}

	public void hintPuzzle() {
		boolean search = true;
		for (int row = 0; row < GameBoard.GRID_SIZE; row++) {
			for (int col = 0; col < GameBoard.GRID_SIZE; col++) {
				if (puzzleTableIsShown[row][col] == false && search) {
					puzzleTableIsShown[row][col] = true;
					search = false;
				}
			}

		}
	}

	public void newPuzzle(int numToGuess) {
		// Fill Puzzle Table
		puzzleTableClear();
		fillGrid();
		fillRemaining(0, GameBoard.SQUAREROOTGRID);
		savePuzzle();

		for (int row = 0; row < GameBoard.GRID_SIZE; ++row) {
			for (int col = 0; col < GameBoard.GRID_SIZE; ++col) {
				numbers[row][col] = puzzleTable[row][col];
			}
		}

		puzzleTableIsShownClear();
		removeDigits(numToGuess);

		for (int row = 0; row < GameBoard.GRID_SIZE; ++row) {
			for (int col = 0; col < GameBoard.GRID_SIZE; ++col) {
				isShown[row][col] = puzzleTableIsShown[row][col];
			}
		}
	}

	// (For advanced students) use singleton design pattern for this class
}