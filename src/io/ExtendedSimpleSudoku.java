package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sudoku.Sudoku;

/**
 * Reads the webfriendly txt format.
 * 
 * @author Niels
 * @version 0.1
 */
public class ExtendedSimpleSudoku extends SudokuFormat {
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.SudokuFormat#load(java.lang.String)
	 */
	@Override
	public String load(String filename) throws IOException {
		try {
			FileReader fileReader = new FileReader(new File(filename));
			BufferedReader reader = new BufferedReader(fileReader);

			int currentLine = 0;
			String result = "";
			String line;
			while ((line = reader.readLine()) != null) {
				if (currentLine == 0 || currentLine == 4 || currentLine == 8
						|| currentLine == 12) {
					currentLine++;
					continue;
				}

				line = line.replace('.', '0');

				if (line.length() != 13) {
					reader.close();
					fileReader.close();
					throw new IllegalArgumentException(
							"Invalid length of line!");
				}

				for (int i = 0; i < line.length(); i++)
					if (i == 0 || i == 4 || i == 8 || i == 12)
						continue;
					else if (!isValid(line.charAt(i))) {
						reader.close();
						fileReader.close();
						throw new IllegalArgumentException(
								"Invalid character at position " + i
										+ " with value '" + line.charAt(i)
										+ "' detected!");
					}
					else
						result+=line.charAt(i);
				currentLine++;
			}

			reader.close();
			fileReader.close();
			return result;
		} catch (FileNotFoundException e) {
			throw new IOException("The file could not be found!");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.SudokuFormat#save(java.lang.String, sudoku.Sudoku)
	 */
	@Override
	public void save(String filename, Sudoku sudoku) throws IOException {
		FileWriter fileWriter = new FileWriter(new File(filename));
		BufferedWriter writer = new BufferedWriter(fileWriter);

		writer.write("*-----------*");
		writer.newLine();
		for (int i = 0; i < 9; i++) {
			writer.write("|");
			for (int k = 0; k < 9; k++) {
				if (k == 3 || k == 6)
					writer.write("|");
				int val = sudoku.getValueAt(i, k);
				writer.write("" + (val == 0 ? "." : val));
			}
			writer.write("|");
			
			if (i < 8)
				writer.newLine();
			if (i == 2 || i == 5) {
				writer.write("|---+---+---|");
				writer.newLine();
			}
		}
		writer.newLine();
		writer.write("*-----------*");

		writer.close();
		fileWriter.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.SudokuFormat#getSupportedExtensions()
	 */
	@Override
	public String[] getSupportedExtensions() {
		return new String[] { ".ss",".sss" };
	}
}
