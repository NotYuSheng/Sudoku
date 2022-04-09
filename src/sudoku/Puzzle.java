// https://www.geeksforgeeks.org/program-sudoku-generator/

package sudoku;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The Sudoku number puzzle to be solved
 */
public class Puzzle {
	// All variables have package access
	int[][] numbers = new int[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// For testing, only 2 cells of "8" is NOT shown
	boolean[][] isShown = new boolean[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// Puzzle Table
	int[][] puzzleTable = new int[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];

	// Nodes that are shown
	boolean[][] puzzleTableIsShown = new boolean[GameBoard.GRID_SIZE][GameBoard.GRID_SIZE];
	
	// Constructor
	public Puzzle() {
		super(); // JPanel
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
		for (int i = 0; i<GameBoard.GRID_SIZE; i += GameBoard.SQUAREROOTGRID) {
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
		if (col >= GameBoard.GRID_SIZE && row < GameBoard.GRID_SIZE - 1) {
			row += 1;
			col = 0;
		}
		if (row >= GameBoard.GRID_SIZE && col >= GameBoard.GRID_SIZE) {
			return true;
		}

		if (row < GameBoard.SQUAREROOTGRID) {
			if (col < GameBoard.SQUAREROOTGRID)
				col = GameBoard.SQUAREROOTGRID;
		} else if (row < GameBoard.GRID_SIZE - GameBoard.SQUAREROOTGRID) {
			if (col == (int) (row / GameBoard.SQUAREROOTGRID) * GameBoard.SQUAREROOTGRID)
				col = col + GameBoard.SQUAREROOTGRID;
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
			if (CheckIfSafe(row, col, num)) {
				puzzleTable[row][col] = num;
				if (fillRemaining(row, col + 1))
					return true;

				puzzleTable[row][col] = 0;
			}
		}
		return false;
	}

	public void removeDigits(int remove) {
		while (remove != 0) {
			int cellId = (int) Math.floor((Math.random() * GameBoard.GRID_SIZE * GameBoard.GRID_SIZE + 1)) - 1;

			int row = (cellId / GameBoard.GRID_SIZE);
			int col = cellId % 9;
			if (col != 0)
				col -= 1;

			if (puzzleTable[row][col] != 0) {
				remove--;
				puzzleTable[row][col] = 0;
				puzzleTableIsShown[row][col] = false;
			}
		}
	}

	public void fillValues(int numToGuess) {
		fillGrid();
		fillRemaining(0, GameBoard.SQUAREROOTGRID);
		puzzleTableIsShownClear();
		removeDigits(numToGuess);
	}

	public void newPuzzle(int numToGuess) {
		// Fill Puzzle Table
		fillValues(numToGuess);

		System.out.println(Arrays.deepToString(puzzleTable));
		
		

		for (int row = 0; row < GameBoard.GRID_SIZE; ++row) {
			for (int col = 0; col < GameBoard.GRID_SIZE; ++col) {
				numbers[row][col] = puzzleTable[row][col];
			}
		}

		/*
		// Hardcoded here for simplicity.
				int[][] hardcodedNumbers = { { 5, 3, 4, 6, 7, 8, 9, 1, 2 }, { 6, 7, 2, 1, 9, 5, 3, 4, 8 },
						{ 1, 9, 8, 3, 4, 2, 5, 6, 7 }, { 8, 5, 9, 7, 6, 1, 4, 2, 3 }, { 4, 2, 6, 8, 5, 3, 7, 9, 1 },
						{ 7, 1, 3, 9, 2, 4, 8, 5, 6 }, { 9, 6, 1, 5, 3, 7, 2, 8, 4 }, { 2, 8, 7, 4, 1, 9, 6, 3, 5 },
						{ 3, 4, 5, 2, 8, 6, 1, 7, 9 } };
		
		// Need to use numToGuess!
		// For testing, only 2 cells of "8" is NOT shown
		
		boolean[][] hardcodedIsShown = { { true, true, true, true, true, false, true, true, true },
				{ true, true, true, true, true, true, true, true, false },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true },
				{ true, true, true, true, true, true, true, true, true } };
		*/

		for (int row = 0; row < GameBoard.GRID_SIZE; ++row) {
			for (int col = 0; col < GameBoard.GRID_SIZE; ++col) {
				isShown[row][col] = puzzleTableIsShown[row][col];
			}
		}
	}

	// (For advanced students) use singleton design pattern for this class
}