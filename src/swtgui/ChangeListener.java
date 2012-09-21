package swtgui;

/**
 * A listener which listens to changes in the sudoku.
 * 
 * @author Niels
 * @version 0.1
 */
public interface ChangeListener {
	/**
	 * Is called when a number takes a new value.
	 * 
	 * @param row
	 *            The row of the changed element.
	 * @param column
	 *            The column of the changed element.
	 * @param previous
	 *            The previous value of the element.
	 * @param next
	 *            The new value of the element.
	 */
	public void changed(int row, int column, int previous, int next);
	
	/**
	 * Called when a solution is found
	 */
	public void solved();
}
