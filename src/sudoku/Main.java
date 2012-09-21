package sudoku;

import swtgui.GUI;
import io.LoadSudoku;

/**
 * This method starts the application by creating a sudoku and a graphical user
 * interface in which the suduku is solved step by step.
 * 
 * @author Niels
 * @version 0.1
 */
public class Main {
	/**
	 * This method starts the application. The optional arguments can contain a
	 * string of 81 characters which represents the sudoku.
	 * 
	 * If no argument is supplied, a standard sudoku is solved instead.
	 * 
	 * @param args
	 *            The arguments for the program.
	 */
	public static void main(String[] args) {
		Sudoku sudoku;

		if (args.length > 0)
			sudoku = new Sudoku(LoadSudoku.getInstance().loadSudoku(args[0]));
		else
			sudoku = new Sudoku(LoadSudoku.getInstance().loadSudoku("sudokus/mostdifficult.txt"));
		new GUI(sudoku);
	}
}
