package sudoku;

import java.util.ArrayList;
import java.util.Arrays;

import swtgui.ChangeListener;

/**
 * Represents a regular sudoku.
 * 
 * It also provides a method of solving the sudoku.
 * 
 * @author Niels
 * @version 0.1
 */
public class Sudoku {
	// Array indicating a value is from the original problem
	private final boolean[][] original = new boolean[9][9];
	// The array with numbers of the sudoku
	private final int[][] numbers = new int[9][9];
	// Array which indicates whether a value is possible.
	private final boolean[][][] possible = new boolean[9][9][9];
	// The number of set numbers
	private int set = 0;
	// List with listeners
	public ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
	// Index on the stack.
	private int index = 0;
	// Whether backtracking is required.
	private boolean backtrack = false;
	// The stack which contains the indices of the numbers
	private final int[] stackIndex = new int[81];
	// The stack which contains the index of possible values.
	private final int[] stackPossible = new int[81];

	/**
	 * Creates a new sudoku from the given string.
	 * 
	 * @param sudokustring
	 *            The string to create the sudoku from.
	 * @throws NullPointerException
	 *             When the sudokustring is null.
	 * @throws IllegalArgumentException
	 *             When the string contains a illegal sudoku.
	 */
	public Sudoku(String sudokustring) throws NullPointerException,
			IllegalArgumentException {
		// Perform input validation.
		if (sudokustring == null)
			throw new NullPointerException("the given sudokustring is null!");
		if (sudokustring.length() <= 81)
			throw new IllegalArgumentException(
					"the sudokustring must be at least 81 characters long!");
		// Set every value to be possible.
		for (int i = 0; i < 9; i++)
			for (int k = 0; k < 9; k++)
				Arrays.fill(possible[i][k], true);
		// Read the sudoku from a given string.
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				// Read the correct character
				String c = sudokustring.substring(i * 9 + k, i * 9 + k + 1);

				// Parse the integer from the string
				int value = Integer.parseInt(c);

				// Set the value
				setValue(i, k, value, value > 0);
			}
		}
		// Initialize the variables to solve the sudoku.
		Arrays.fill(stackPossible, 0);
		Arrays.fill(stackIndex, -1);
	}

	/**
	 * Creates a new empty sudoku.
	 */
	public Sudoku() {
		clear();
	}

	/**
	 * Clears the sudoku and execution process.
	 */
	public void clear() {
		// Set every value to be possible.
		for (int i = 0; i < 9; i++) {
			Arrays.fill(numbers[i], 0);
			for (int k = 0; k < 9; k++)
				Arrays.fill(possible[i][k], true);
		}
		// Initialize the variables to solve the sudoku.
		Arrays.fill(stackPossible, 0);
		Arrays.fill(stackIndex, -1);
		index = 0;
	}

	/**
	 * Resets the sudoku to it's start before execution.
	 */
	public void reset() {
		for (int i = 0; i < stackIndex.length; i++) {
			if (stackIndex[i] == -1)
				break;

			int x = stackIndex[i] / 9;
			int y = stackIndex[i] % 9;
			setValue(x, y, 0, false);
		}
		// Initialize the variables to solve the sudoku.
		Arrays.fill(stackPossible, 0);
		Arrays.fill(stackIndex, -1);
		index = 0;
		backtrack = false;
	}

	/**
	 * Sets the value of the number at that given position.
	 * 
	 * @param row
	 *            The row of the value to change (between 0 and 8)
	 * @param column
	 *            The column of the value to change (between 0 and 8)
	 * @param value
	 *            The value for the number at the position.
	 * @param isOriginal
	 *            Boolean indicating whether the value at the given position is
	 *            an original value.
	 * @throws IllegalStateException
	 *             When that number is not possible at the given position.
	 */
	public void setValue(int row, int column, int value, boolean isOriginal)
			throws IllegalStateException {
		if (value > 0 && !possible[row][column][value - 1])
			throw new IllegalStateException(
					"That value is inconsistent with the sudoku!");
		if (numbers[row][column] == value)
			return;
		if (value == 0)
			set--;
		else if (numbers[row][column] == 0)
			set++;
		notifyChange(row, column, numbers[row][column], value);
		numbers[row][column] = value;
		original[row][column] = isOriginal;

		if (value == 0)
			update();
		else {
			numbers[row][column] = value;
			int rr = (row / 3) * 3;
			int cc = (column / 3) * 3;
			for (int i = 0; i < 3; i++)
				for (int k = 0; k < 3; k++)
					possible[rr + i][cc + k][value - 1] = false;
			for (int i = 0; i < 9; i++) {
				// possible[row][column][i] = false;
				possible[row][i][value - 1] = false;
				possible[i][column][value - 1] = false;
			}
		}
	}

	/**
	 * Solves the sudoku completely by trying any number combination and pruning
	 * invalid results.
	 */
	public void solveCompletely() {
		while (!doStep())
			;
	}

	/**
	 * Does one iteration in solving the sudoku.
	 */
	public boolean doStep() throws IllegalStateException {
		if (backtrack || !canBeSolved()) {
			if (--index < 0) // go to previous stack element
				throw new IllegalStateException("No solution exists!");

			int element = stackIndex[index]; // find the element
			int x = element / 9; // find the x position
			int y = element % 9; // find the y position
			setValue(x, y, 0, false);
			while (stackPossible[index] < 9
					&& !possible[x][y][stackPossible[index]])
				stackPossible[index]++;
			if (stackPossible[index] == 9) {
				stackIndex[index] = -1;
				stackPossible[index] = 0;
				setValue(x, y, 0, false);
				backtrack = true;
				return false; // backtrack further
			}

			setValue(x, y, stackPossible[index] + 1, false);
			stackPossible[index]++;
			index++;
			backtrack = false;
		} else if (index >= 0) {
			int element = findFirstInvalid();

			if (element == -1) {
				backtrack = true;
				return false;
			}

			int x = element / 9; // find the x position
			int y = element % 9; // find the y position
			while (stackPossible[index] < 9
					&& !possible[x][y][stackPossible[index]])
				stackPossible[index]++;
			if (stackPossible[index] == 9) {
				backtrack = true;
				return false;
			}
			stackIndex[index] = element; // put it up the stack.
			setValue(x, y, stackPossible[index] + 1, false);
			stackPossible[index]++;
			index++;
		} else
			throw new IllegalStateException("No solution exists!");
		return isValidSolution();
	}

	/**
	 * Returns the index of the first modifiable number.
	 * 
	 * @return the index of the first modifiable number.
	 */
	private int findFirstInvalid() {
		int minimum = 10;
		int index = -1;
		for (int i = 0; i < 9; i++)
			for (int k = 0; k < 9; k++)
				if (numbers[i][k] == 0) {
					int count = 0;
					for (int u = 0; u < 9; u++)
						if (possible[i][k][u])
							count++;
					if (count == 1)
						return i * 9 + k;
					if (count > 0 && count < minimum) {
						minimum = count;
						index = i * 9 + k;
					}
				}
		return index;
	}

	/**
	 * Returns whether the sudoku can still be solved.
	 * 
	 * @return whether the sudoku can still be solved.
	 */
	private boolean canBeSolved() {
		if (set == 81)
			return true;
		for (int i = 0; i < 9; i++)
			for (int k = 0, count = 0; k < 9; k++) {
				if (numbers[i][k] > 0)
					continue;
				for (int u = 0; u < 9; u++)
					if (possible[i][k][u])
						count++;
				if (count == 0)
					return false;
			}
		return true;
	}

	/**
	 * Checks whether the current state of the sudoku is a valid solution. <br>
	 * <br>
	 * The solution is valid when each 3x3 subgrid only contains each number
	 * between 1-9 once and each row and column only contains each number once.
	 * 
	 * @return whether the current state is a valid solution.
	 */
	public boolean isValidSolution() {
		if (set < 81)
			return false;
		// Check each 3x3 grid whether it contains each number once.
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 3; k++) {
				boolean[] used = new boolean[9];
				Arrays.fill(used, false);

				for (int x = 0; x < 3; x++)
					for (int y = 0; y < 3; y++)
						used[numbers[i * 3 + x][k * 3 + y] - 1] = true;
				for (int u = 0; u < 9; u++)
					if (!used[u])
						return false;
			}

		// Check each row and column whether it contains each element once.
		for (int i = 0; i < 9; i++) {
			boolean[] usedRow = new boolean[9];
			boolean[] usedColumn = new boolean[9];

			for (int k = 0; k < 9; k++) {
				usedRow[numbers[i][k] - 1] = true;
				usedColumn[numbers[k][i] - 1] = true;
			}

			for (int u = 0; u < 9; u++)
				if (!usedRow[u] || !usedColumn[u])
					return false;
		}
		notifySolved();
		return true;
	}

	/**
	 * This method updates the possible values of the sudoku from scratch.
	 */
	private void update() {
		// Clear the possible fields.
		for (int i = 0; i < 9; ++i)
			for (int k = 0; k < 9; ++k)
				for (int u = 0; u < 9; u++)
					possible[i][k][u] = numbers[i][k] == 0 ? true : false;
		// Update each 3x3 subgrid
		for (int i = 0; i < 3; i++)
			for (int k = 0; k < 3; k++)
				updateSquare(i * 3, k * 3);
		for (int i = 0; i < 9; ++i)
			updateRow(i);
		for (int i = 0; i < 9; ++i)
			updateColumn(i);
	}

	/**
	 * This method updates the possible values for a single 3x3 grid.
	 * 
	 * @param xoffset
	 *            The xoffset in the grid.
	 * @param yoffset
	 *            The yoffset in the grid.
	 */
	private void updateSquare(int xoffset, int yoffset) {
		int[] usedValues = new int[10];
		int index = 0;

		for (int x = 0; x < 3; ++x)
			for (int y = 0; y < 3; ++y)
				if (numbers[xoffset + x][yoffset + y] > 0)
					usedValues[index++] = numbers[xoffset + x][yoffset + y];
		for (int x = 0; x < 3; ++x)
			for (int y = 0; y < 3; ++y)
				if (numbers[xoffset + x][yoffset + y] == 0)
					for (int u = 0; u < index; u++)
						possible[xoffset + x][yoffset + y][usedValues[u] - 1] = false;
	}

	/**
	 * Updates the possible values per row.
	 * 
	 * @param row
	 *            The row to update.
	 */
	private void updateRow(int row) {
		int[] usedValues = new int[10];
		int index = 0;

		for (int column = 0; column < 9; ++column)
			if (numbers[row][column] > 0)
				usedValues[index++] = numbers[row][column];
		for (int column = 0; column < 9; ++column)
			if (numbers[row][column] == 0)
				for (int u = 0; u < index; u++)
					possible[row][column][usedValues[u] - 1] = false;

	}

	/**
	 * Updates the possible values per column.
	 * 
	 * @param column
	 *            The column to update.
	 */
	public void updateColumn(int column) {
		int[] usedValues = new int[10];
		int index = 0;

		for (int row = 0; row < 9; ++row)
			if (numbers[row][column] > 0)
				usedValues[index++] = numbers[row][column];
		for (int row = 0; row < 9; ++row)
			if (numbers[row][column] == 0)
				for (int u = 0; u < index; u++)
					possible[row][column][usedValues[u] - 1] = false;
	}

	/**
	 * Returns the number at the given position.
	 * 
	 * @param row
	 *            The row to get the number of.
	 * @param column
	 *            The column to get then number of.
	 * @return the value of the sudoku at the given position.
	 */
	public int getValueAt(int row, int column) {
		return numbers[row][column];
	}

	/**
	 * Returns whether the given value is still possible at the given position.
	 * 
	 * @param row
	 *            The row to check.
	 * @param y
	 *            The column to check.
	 * @param value
	 *            The value to check.
	 * @return blank is always possible. Else the possible list is consulted.
	 */
	public boolean isPossible(int row, int y, int value) {
		return value == 0 || possible[row][y][value - 1];
	}

	/**
	 * Returns whether the given value belongs to the original problem.
	 * 
	 * @return whether the given value belongs to the original problem.
	 */
	public boolean isAssignment(int row, int column) {
		return original[row][column];
	}

	/**
	 * Add's the given listener to the set of listeners.<br>
	 * <br>
	 * 
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(ChangeListener listener) {
		if (listener == null)
			return;
		listeners.add(listener);
	}

	/**
	 * Removes the given listener from the set of listeners.<br>
	 * <br>
	 * 
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies the listeners that the value at the given position has changed
	 * from value.
	 * 
	 * @param x
	 *            The row of the changed element.
	 * @param y
	 *            The column of the changed element.
	 * @param previous
	 *            The previous value of the number.
	 * @param next
	 *            The new value of the number.
	 */
	private void notifyChange(int x, int y, int previous, int next) {
		for (ChangeListener listener : listeners)
			listener.changed(x, y, previous, next);
	}

	/**
	 * Notifies the listeners that the value at the given position has changed
	 * from value.
	 * 
	 * @param x
	 *            The row of the changed element.
	 * @param y
	 *            The column of the changed element.
	 * @param previous
	 *            The previous value of the number.
	 * @param next
	 *            The new value of the number.
	 */
	private void notifySolved() {
		for (ChangeListener listener : listeners)
			listener.solved();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < 9; ++i) {
			for (int k = 0; k < 9; ++k)
				result += (numbers[i][k] == 0 ? "-" : numbers[i][k]) + " ";
			result += "\n";
		}

		return result;
	}
}
