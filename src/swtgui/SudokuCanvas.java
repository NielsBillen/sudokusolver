package swtgui;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import sudoku.Sudoku;

/**
 * 
 * @author Niels
 * @version 0.1
 */
public class SudokuCanvas extends Canvas {
	private Color[][] colors = new Color[9][9];
	private boolean[][] dirty = new boolean[9][9];

	/**
	 * An extension of a canvas for drawing sudokus.
	 * 
	 * @param composite
	 *            The parent composite.
	 * @param style
	 *            The style of the composite.
	 */
	public SudokuCanvas(Composite composite, final GUI gui, int style) {
		super(composite, style | SWT.NO_BACKGROUND);

		// Fill with default values.
		for (int i = 0; i < 9; i++) {
			Arrays.fill(colors[i], GUI.BLACK);
			Arrays.fill(dirty[i], true);
		}

		// Add a paint listener
		addPaintListener(new PaintListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse
			 * .swt.events.PaintEvent)
			 */
			@Override
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;

				for (int i = 0; i < 9; i++)
					for (int k = 0; k < 9; k++)
						if (dirty[i][k]) {
							drawSquare(gc, getClientArea(), i, k,
									gui.getSudoku(), colors[i][k]);
							dirty[i][k] = false;
						}
			}
		});

		// Add the mouse listener
		addMouseListener(new MouseListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.
			 * events.MouseEvent)
			 */
			@Override
			public void mouseUp(MouseEvent e) {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt
			 * .events.MouseEvent)
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				if (gui.isSolving())
					return;
				Rectangle rectangle = getClientArea();
				int width = rectangle.width / 9;
				int height = rectangle.height / 9;
				int row = e.x / width;
				int column = e.y / height;

				new ChoiceDialog(GUI.shell, gui, row, column);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse
			 * .swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});

	}

	/**
	 * Draws a complete sudoku on the given gc with the given rectangle as
	 * drawing area.
	 */
	public static void drawSudoku(GC gc, Rectangle rectangle, Sudoku sudoku) {
		for (int i = 0; i < 9; i++)
			for (int k = 0; k < 9; k++)
				drawSquare(gc, rectangle, i, k, sudoku,
						sudoku.isAssignment(i, k) ? GUI.BLUE : GUI.BLACK);
	}

	/**
	 * Draws a square with a number on the given graphics context, with the
	 * given rectangle as client area.
	 * 
	 * @param gc
	 *            The graphics context to draw upon.
	 * @param rectangle
	 *            The bounds of the drawing region.
	 * @param row
	 *            The row of the square.
	 * @param column
	 *            The column of the square.
	 */
	public static void drawSquare(GC gc, Rectangle rectangle, int row,
			int column, Sudoku sudoku, Color color) {
		int width = rectangle.width / 9;
		int height = rectangle.height / 9;
		int thick = Math.max(1, width / 30);
		gc.setForeground(GUI.BLACK);
		gc.setBackground(GUI.WHITE);

		// Draw the background.
		gc.fillRectangle(rectangle.x + row * width, rectangle.y + column
				* height, width - 1, height - 1);

		// Adapt the font to the correct size.
		FontData data = GUI.FONT.getFontData()[0];
		data.setHeight(height / 2);
		Font font = new Font(GUI.display, data);
		gc.setFont(font);

		// Set the text to draw.
		String string = sudoku.getValueAt(row, column) == 0 ? " " : ""
				+ sudoku.getValueAt(row, column);
		Point extent = gc.stringExtent(string);

		// Draw the text
		gc.setForeground(color);
		gc.drawText(string, rectangle.x + row * width + (width - extent.x) / 2,
				rectangle.y + column * height + (height - extent.y) / 2);
		// Cleanu
		font.dispose();

		gc.setBackground(GUI.BLACK);

		// Draw the horizontal lines.
		if (column == 0 || column == 3 || column == 6)
			gc.fillRectangle(rectangle.x + row * width, rectangle.y + column
					* height, width - 1, thick * 2);
		else if (column == 8)
			gc.fillRectangle(rectangle.x + row * width, rectangle.y + 9
					* height - thick * 2 - 1, width - 1, thick * 2);
		gc.fillRectangle(rectangle.x + row * width, rectangle.y + (column + 1)
				* height - thick / 2, width - 1, thick / 2);
		gc.fillRectangle(rectangle.x + row * width, rectangle.y + column
				* height, width - 1, thick / 2 + 1);

		// Draw the vertical lines
		if (row == 0 || row == 3 || row == 6)
			gc.fillRectangle(rectangle.x + row * width, rectangle.y + column
					* height, thick * 2, height - 1);
		else if (row == 8)
			gc.fillRectangle(rectangle.x + 9 * width - thick * 2 - 1,
					rectangle.y + column * height, thick * 2, height - 1);
		gc.fillRectangle(rectangle.x + row * width - thick / 2, rectangle.y
				+ column * height, thick / 2, height);
		gc.fillRectangle(rectangle.x + row * width, rectangle.y + column
				* height, thick / 2 + 1, height);
	}

	/**
	 * Sets the value of the given row and column.
	 * 
	 * @param row
	 *            The row of the value to set.
	 * @param column
	 *            The column of the value to set.
	 * @param value
	 *            The value to set.
	 */
	public void setDirty(int row, int column) {
		if (isDisposed())
			return;
		Rectangle r = getBounds();
		int width = r.width / 9;
		int height = r.height / 9;
		dirty[row][column] = true;

		redraw(row * width, column * width, width, height, true);
	}

	/**
	 * Sets the color of the given row and column.
	 * 
	 * @param row
	 *            The row of the value to set.
	 * @param column
	 *            The column of the value to set.
	 * @param color
	 *            The color to set.
	 */
	public void setColor(int row, int column, Color color) {
		if (isDisposed())
			return;
		Rectangle r = getBounds();
		int width = r.width / 9;
		int height = r.height / 9;
		colors[row][column] = color;
		dirty[row][column] = true;
		redraw(row * width, column * width, width, height, true);
	}

	public void redraw() {
		for (int i = 0; i < 9; i++)
			Arrays.fill(dirty[i], true);
		super.redraw();
	}
}
