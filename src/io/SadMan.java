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
 * Reads the sadman sdk format.
 * 
 * @author Niels
 * @version 0.1
 */
public class SadMan extends SudokuFormat {
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

			String result = "";
			String line = reader.readLine();
			if (!line.equals("[Puzzle]"))
				System.err
						.println("the header [Puzzle] was not found while reading! Continuing anyway...");
			while ((line = reader.readLine()) != null) {
				line = line.replace('.', '0');

				if (line.length() != 9) {
					reader.close();
					fileReader.close();
					throw new IOException("Invalid length of line!");
				}
				for (int i = 0; i < line.length(); i++)
					if (!isValid(line.charAt(i))) {
						reader.close();
						fileReader.close();
						throw new IOException("Invalid character detected!");
					}
					else
						result+=line.charAt(i);
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

		writer.write("[Puzzle]");
		writer.newLine();
		for (int i = 0; i < 9; i++) {
			for (int k = 0; k < 9; k++) {
				int val = sudoku.getValueAt(i, k);
				writer.write("" + (val == 0 ? "." : val));
			}
			if (i < 8)
				writer.newLine();
		}

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
		return new String[] { ".sdk" };
	}
}
