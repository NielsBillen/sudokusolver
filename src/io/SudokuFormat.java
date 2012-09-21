package io;

import java.io.IOException;

import sudoku.Sudoku;

/**
 * An interface that all sudoku formats should implement.
 * 
 * @author Niels
 * @version 0.1
 */
public abstract class SudokuFormat {
	/**
	 * Returns the extensions for this file.
	 * 
	 * @return a list with extensions for this file.
	 */
	public abstract String[] getSupportedExtensions();

	/**
	 * Whether the format supports the given extension.
	 * 
	 * @param extension
	 *            The extension of the file.
	 * @return whether it supports the extension.
	 */
	public boolean supports(String extension) {
		for (String string : getSupportedExtensions())
			if (string.equals(extension))
				return true;
		return false;
	}

	/**
	 * Returns whether a given character is a valid character.
	 * 
	 * @return whether the given character is a valid character.
	 */
	public boolean isValid(char c) {
		return c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
				|| c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
	}

	/**
	 * Loads the sudoku from the file.
	 * 
	 * @param filename
	 *            Loads the sudoku from the filename.
	 * @return the string containing the sudoku.
	 */
	public abstract String load(String filename) throws IOException;

	/**
	 * Saves the sudoku in this format with the given filename.
	 * 
	 * @param filename
	 *            The filename to save it in.
	 * @param sudoku
	 *            The sudoku to save.
	 */
	public abstract void save(String filename, Sudoku sudoku) throws IOException;
}
