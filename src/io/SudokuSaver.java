package io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import sudoku.Sudoku;

/**
 * Class for loading sudokus.
 * 
 * @author Niels
 * @version 0.1
 */
public class SudokuSaver {
	// The singleton instance of the file loader.
	private static SudokuSaver instance;

	// The list with supported formats.
	private ArrayList<SudokuFormat> formats = new ArrayList<SudokuFormat>();

	/**
	 * Singleton constuctor.
	 */
	private SudokuSaver() {
		formats.add(new VBForums());
		formats.add(new SimpleSudoku());
		formats.add(new ExtendedSimpleSudoku());
		formats.add(new SadMan());
		formats.add(new SuDokuSolver());
		formats.add(new WebFriendly());
	}

	/**
	 * A singleton instance for the fileloader.
	 * 
	 * @return a singleton instance for the fileloader.
	 */
	public static SudokuSaver getInstance() {
		if (instance == null)
			instance = new SudokuSaver();
		return instance;
	}

	/**
	 * Returns all the supported extensions.
	 * 
	 * @return all the supported extensions.
	 */
	public String[] getSupportedExtensions() {
		HashSet<String> extensions = new HashSet<String>();
		for (SudokuFormat format : formats)
			for (String string : format.getSupportedExtensions())
				extensions.add(string);
		String[] result = new String[extensions.size()];
		int i = 0;
		for (String string : extensions)
			result[i++] = "*"+string;
		return result;

	}

	/**
	 * Loads a sudoku from the given filename.
	 * 
	 * @param filename
	 *            The name of the file.
	 * @return the sudoku string.
	 */
	public void saveSudoku(String filename, Sudoku sudoku) throws IllegalArgumentException {
		int pos = filename.lastIndexOf('.');
		String extension = filename.substring(pos);
		for (SudokuFormat format : formats)
			if (format.supports(extension))
				try {
					format.save(filename, sudoku);
					return;
				} catch (IOException e) {
					System.err.println(e.getMessage());
				} catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				}
		throw new IllegalArgumentException("The given file cannot be read!");
	}
}
