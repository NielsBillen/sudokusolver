package swtgui;

import io.LoadSudoku;
import io.SudokuSaver;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import sudoku.Sudoku;

/**
 * A graphical user interface which allows to input the sudoku and solve it.
 * 
 * @author Niels
 * @version 0.1
 */
public class GUI {
	// The display on which the gui will be displayed.
	public static final Display display = new Display();
	// The shell in which the gui shall be displayed.
	public static Shell shell = new Shell(display);
	// The size for the labels with the numbers.
	public static final int LABELSIZE = 72;
	// The sudoku to be solved.
	private Sudoku sudoku;
	// The black color
	public final static Color BLACK = display.getSystemColor(SWT.COLOR_BLACK);
	// The white color
	public final static Color WHITE = display.getSystemColor(SWT.COLOR_WHITE);
	// The red color
	public final static Color RED = display.getSystemColor(SWT.COLOR_RED);
	// The blue color
	public final static Color BLUE = display.getSystemColor(SWT.COLOR_BLUE);
	// The font for the numbers
	public final static Font FONT = new Font(display, "Helvetica", 24, SWT.BOLD);
	// The previous changed text
	private Point previous;
	// Whether we are solving the sudoku
	private boolean solving = false;
	// Flag for pauzing the solving process.
	private boolean pauze = false;
	// Flag whether all solutions have been found.
	private boolean allfound = false;
	// Execution speed in number of execution steps per second.
	private int stepsPerSecond = 1;
	// The listener of the sudoku.
	private ChangeListener listener;
	// The start button
	private Button startButton;
	// The pauze button
	private Button pauzeButton;
	// The reset button
	private Button clearButton;
	// The canvas
	private SudokuCanvas canvas;

	/**
	 * Creates a new gui for the given sudoku.
	 * 
	 * @param sudoku
	 *            The sudoku which will be solved.
	 */
	public GUI(Sudoku sudoku) {
		// Get the main monitor
		Monitor monitor = display.getPrimaryMonitor();

		ImageLoader loader = new ImageLoader();
		ImageData data = loader.load("images/Logo.png")[0];
		Image image = new Image(display, data);
		shell.setImage(image);

		// Customize the shell
		GridLayout layout = noMarginGridLayout(1, true, 16);
		shell.setLayout(layout);

		// Add the sudoku composite.
		getMenu(shell);
		getSudokuComposite(shell);
		getControlComposite(shell);

		// Set the sudoku
		setSudoku(sudoku);

		// Make the shell as small as possible.
		shell.pack();
		shell.setMinimumSize(shell.getSize());

		// Monitor dimension
		Rectangle mSize = monitor.getBounds();
		Rectangle sSize = shell.getBounds();

		// Set the location
		shell.setLocation(mSize.x + (mSize.width - sSize.width) / 2, mSize.y
				+ (mSize.height - sSize.height) / 2);

		// Open the shell and execute it's events.
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
		display.dispose();
	}

	/**
	 * Sets the sudoku of this gui to the given sudoku.
	 * 
	 * @param sudoku
	 */
	public void setSudoku(Sudoku sudoku) {
		if (this.sudoku != null)
			this.sudoku.removeListener(listener);
		this.sudoku = sudoku;

		// Set the buttons
		startButton.setText("Start");
		startButton.setEnabled(true);
		startButton.setSelection(false);
		pauzeButton.setEnabled(false);
		pauzeButton.setSelection(false);
		solving = false;
		pauze = false;
		previous = null;

		// Add a change listener
		listener = new ChangeListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see gui.ChangeListener#changed(int, int, int, int)
			 */
			@Override
			public void changed(int x, int y, int prev, int next) {
				setText(x, y, next);
				/*
				 * If we are not solving, we set the color to blue to indicate
				 * that the given value is constant.
				 * 
				 * If we are solving we set it to red and the color of the
				 * previous assignment, if it exists to black.
				 */
				if (solving) {
					if (previous == null)
						previous = new Point(x, y);
					else
						setColor(previous.x, previous.y, BLACK);
					setColor(x, y, RED);
				} else
					setColor(x, y, BLUE);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see gui.ChangeListener#solved()
			 */
			@Override
			public void solved() {
				copySudokuContent();
				startButton.setText("Find next solution?");
				startButton.setEnabled(true);
				pauzeButton.setEnabled(false);
				pauzeButton.setSelection(false);
			}
		};
		this.sudoku.addListener(listener);

		// Copies the content of the sudoku.
		copySudokuContent();
	}

	/**
	 * Copies the content from the sudoku.
	 */
	private void copySudokuContent() {
		// Read the content of the sudoku
		for (int i = 0; i < 9; i++)
			for (int k = 0; k < 9; k++) {
				/*
				 * If we are not solving, we set the color to blue to indicate
				 * that the given value is constant.
				 * 
				 * If we are solving we set it to red and the color of the
				 * previous assignment, if it exists to black.
				 */
				setColor(i, k, sudoku.isAssignment(i, k) ? BLUE : BLACK);
				setText(i, k, this.sudoku.getValueAt(i, k));
			}
	}

	/**
	 * Returns the reference to the sudoku.
	 * 
	 * @return the referene to the sudoku.
	 */
	public Sudoku getSudoku() {
		return sudoku;
	}

	/**
	 * Returns whether execution is pauzed.
	 * 
	 * @return whether execution is pauzed.
	 */
	public boolean isPauzed() {
		return pauze;
	}

	/**
	 * Returns whether execution is started.
	 * 
	 * @return whether execution is started.
	 */
	public boolean isSolving() {
		return solving;
	}

	/**
	 * Sets the text for the square with the given row and column.
	 * 
	 * @param row
	 *            The row of the square to change (between 0-8)
	 * @param column
	 *            The column of the square to change (between 0-8)
	 * @param value
	 *            The value to put in the square.
	 */
	private void setText(int row, int column, int value) {
		canvas.setDirty(row, column);
	}

	/**
	 * Sets the color to the given color.
	 * 
	 * @param x
	 *            The x coordinate of the square to change (between 0-8)
	 * @param y
	 *            The y coordinate of the square to change (between 0-8)
	 * @param color
	 *            The color to change to.
	 */
	private void setColor(int x, int y, Color color) {
		canvas.setColor(x, y, color);
		if (solving && previous != null) {
			previous.x = x;
			previous.y = y;
		}
	}

	/**
	 * Creates the composite which displays the sudoku.
	 * 
	 * @param parent
	 *            The parent to put the sudoku window in.
	 * @return the composite which displays the sudoku.
	 */
	private Composite getSudokuComposite(Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(noMarginGridLayout(1, true, 0));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		Rectangle bounds = c.getBounds();
		int min = Math.min(bounds.width, bounds.height);

		canvas = new SudokuCanvas(c, this, SWT.NONE);

		GridData d = new GridData();
		d.horizontalAlignment = SWT.CENTER;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.CENTER;
		d.grabExcessVerticalSpace = true;
		d.minimumWidth = 576;
		d.minimumHeight = 576;
		d.widthHint = (min / 9) * 9;
		d.heightHint = (min / 9) * 9;
		canvas.setLayoutData(d);

		c.addControlListener(new ControlListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.ControlListener#controlResized(org.eclipse
			 * .swt.events.ControlEvent)
			 */
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle bounds = c.getBounds();
				int min = Math.min(bounds.width, bounds.height);
				GridData d = new GridData();
				d.horizontalAlignment = SWT.CENTER;
				d.grabExcessHorizontalSpace = true;
				d.verticalAlignment = SWT.CENTER;
				d.grabExcessVerticalSpace = true;
				d.minimumWidth = 576;
				d.minimumHeight = 576;
				d.widthHint = (min / 9) * 9;
				d.heightHint = (min / 9) * 9;
				canvas.setLayoutData(d);
				canvas.redraw();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
			 */
			@Override
			public void controlMoved(ControlEvent arg0) {
			}
		});

		return canvas;
	}

	/**
	 * Creates the composite which displays the sudoku.
	 * 
	 * @param parent
	 *            The parent to put the sudoku window in.
	 * @return the composite which displays the sudoku.
	 */
	private Composite getControlComposite(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);

		// Customize the composite
		GridLayout layout = noMarginGridLayout(3, true, 8);
		c.setLayout(layout);
		GridData d = new GridData();
		d.horizontalAlignment = SWT.CENTER;
		c.setLayoutData(d);

		Group controlGroup = new Group(c, SWT.SHADOW_ETCHED_OUT);
		controlGroup.setText("Control");
		controlGroup.setLayout(noMarginGridLayout(3, true, 8));
		d = new GridData(GridData.FILL_BOTH);
		// d.grabExcessHorizontalSpace = true;
		// d.grabExcessVerticalSpace = true;
		// d.horizontalAlignment = SWT.FILL;
		// d.verticalAlignment = SWT.FILL;
		d.horizontalSpan = 2;
		controlGroup.setLayoutData(d);

		// Create the start button.
		startButton = new Button(controlGroup, SWT.PUSH);
		d = new GridData(GridData.FILL_BOTH);
		 d.widthHint = 144;
		 d.heightHint = 32;
		// d.horizontalAlignment = SWT.CENTER;
		// d.grabExcessHorizontalSpace = true;
		// d.verticalAlignment = SWT.CENTER;
		// d.grabExcessVerticalSpace = true;
		startButton.setText("Start solving");
		startButton.setLayoutData(d);

		// Create the pauze button
		clearButton = new Button(controlGroup, SWT.PUSH);
		d = new GridData(GridData.FILL_BOTH);
		// d.widthHint = 128;
		// d.heightHint = 32;
		// d.horizontalAlignment = SWT.CENTER;
		// d.grabExcessHorizontalSpace = true;
		// d.verticalAlignment = SWT.CENTER;
		// d.grabExcessVerticalSpace = true;
		clearButton.setText("Clear");
		clearButton.setSelection(false);
		clearButton.setLayoutData(d);

		// Create the pauze button
		pauzeButton = new Button(controlGroup, SWT.TOGGLE);
		d = new GridData(GridData.FILL_BOTH);
		// d.widthHint = 128;
		// d.heightHint = 32;
		// d.horizontalAlignment = SWT.CENTER;
		// d.grabExcessHorizontalSpace = true;
		// d.verticalAlignment = SWT.CENTER;
		// d.grabExcessVerticalSpace = true;
		pauzeButton.setText("Pauze solving");
		pauzeButton.setEnabled(false);
		pauzeButton.setSelection(false);
		pauzeButton.setLayoutData(d);

		// Create the group
		Group g = new Group(c, SWT.SHADOW_ETCHED_OUT);
		g.setText("Execution speed:");
		g.setLayout(noMarginGridLayout(1, true, 8));
		// Place the group.
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.FILL;
		d.grabExcessVerticalSpace = true;
		g.setLayoutData(d);

		// Create the slider
		final Scale slider = new Scale(g, SWT.HORIZONTAL);
		d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = true;
		d.verticalAlignment = SWT.CENTER;
		d.grabExcessVerticalSpace = true;
		slider.setLayoutData(d);
		slider.setMinimum(1);
		slider.setMaximum(1000);
		slider.setIncrement(100);
		slider.setPageIncrement(100);
		slider.setEnabled(true);

		startButton.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (allfound)
					sudoku.reset();
				allfound = false;
				startButton.setEnabled(false);
				pauzeButton.setEnabled(true);
				start();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		clearButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				startButton.setText("Start");
				startButton.setEnabled(true);
				pauzeButton.setSelection(false);
				pauzeButton.setEnabled(false);
				solving = false;
				pauze = false;
				previous = null;
				allfound = false;
				sudoku.reset();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		pauzeButton.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				pauze();

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		slider.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				stepsPerSecond = slider.getSelection();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		return c;
	}

	/**
	 * Start the solving process.
	 */
	private void start() {
		solving = true;
		pauzeButton.setEnabled(true);
		pauzeButton.setSelection(false);
		Runnable runnable = new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				if (solving == false)
					return;
				try {
					if (stepsPerSecond <= 100) {
						if (!sudoku.doStep() && !pauze)
							display.timerExec(1000 / stepsPerSecond, this);
					} else if (stepsPerSecond < 1000) {
						int i = 0;
						boolean finished = false;
						while (!finished && !pauze
								&& (i++ < stepsPerSecond / 10))
							finished = sudoku.doStep();
						if (!finished && !pauze)
							display.timerExec(10, this);
					} else {
						try {
							sudoku.removeListener(listener);
							long startTime = System.currentTimeMillis();
							while (!sudoku.doStep() && !pauze)
								;
							System.out.println("Finished solving in: "
									+ (System.currentTimeMillis() - startTime)
									+ "ms");
							listener.solved();
							copySudokuContent();
							sudoku.addListener(listener);
						} catch (IllegalStateException e) {
							new MessageDialog(shell,
									"No solution could be found!");
							copySudokuContent();
							solving = false;
							allfound = true;
							startButton.setEnabled(true);
							startButton.setText("Restart?");
							pauzeButton.setEnabled(false);
							pauzeButton.setSelection(false);
							sudoku.addListener(listener);
						}
					}
				} catch (IllegalStateException e) {
					new MessageDialog(shell, "No solution could be found!");
					copySudokuContent();
					solving = false;
					allfound = true;
					startButton.setEnabled(true);
					startButton.setText("Restart?");
					pauzeButton.setEnabled(false);
					pauzeButton.setSelection(false);
				}
			}
		};
		display.timerExec(1, runnable);
	}

	/**
	 * Pauze the execution.
	 */
	private void pauze() {
		if (!pauze)
			pauze = true;
		else {
			pauze = false;
			start();
		}
	}

	public Menu getMenu(final Shell shell) {
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem fileItem = new MenuItem(menu, SWT.CASCADE);
		fileItem.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		fileItem.setMenu(fileMenu);

		MenuItem load = new MenuItem(fileMenu, SWT.PUSH);
		load.setText("Load");

		MenuItem save = new MenuItem(fileMenu, SWT.PUSH);
		save.setText("Save");

		MenuItem print = new MenuItem(fileMenu, SWT.PUSH);
		print.setText("Print");

		load.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				File file = new File("");
				String directory = file.getAbsolutePath() + "/sudokus";

				dialog.setFilterExtensions(LoadSudoku.getInstance()
						.getSupportedExtensions());
				dialog.setFilterPath(directory);

				String filename = dialog.open();
				if (filename != null) {
					String sudokustring = LoadSudoku.getInstance().loadSudoku(
							filename);
					Sudoku sudoku = new Sudoku(sudokustring);
					setSudoku(sudoku);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		save.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				File file = new File("");
				String directory = file.getAbsolutePath() + "/sudokus";

				dialog.setFilterPath(directory);
				dialog.setFilterExtensions(SudokuSaver.getInstance()
						.getSupportedExtensions());

				String filename = dialog.open();
				System.out.println(filename);
				if (filename != null)
					SudokuSaver.getInstance().saveSudoku(filename, sudoku);

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		print.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				PrintDialog dialog = new PrintDialog(shell);
				PrinterData data = dialog.open();
				if (data == null)
					return;
				Printer printer = new Printer(data);
				GC gc = new GC(printer);

				if (!printer.startJob("Sudoku")) {
					System.err.println("Starting printing task failed!");
					return;
				}

				if (!printer.startPage())
					System.err.println("Starting of page 1 failed!");

				double border = 0.7;

				Rectangle clipping = gc.getClipping();
				int size = (int) (border
						* Math.min(clipping.width, clipping.height) / 9) * 9;
				int x = clipping.x + (clipping.width - size) / 2;
				int y = clipping.y + (clipping.height - size) / 2;

				Rectangle drawRectangle = new Rectangle(x, y, size, size);
				SudokuCanvas.drawSudoku(gc, drawRectangle, sudoku);

				printer.endPage();
				printer.endJob();

				gc.dispose();
				printer.dispose();

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
			 * (org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		return menu;
	}

	/**
	 * Creates a grid layout that has uniform margins.
	 * 
	 * @param numcolumns
	 *            The number of columns.
	 * @param equalwith
	 *            Whether each column should have an equal width.
	 * @param margins
	 *            The uniform margin.
	 * @return a layout without margins.
	 */
	public static GridLayout noMarginGridLayout(int numcolumns,
			boolean equalwith, int margins) {
		GridLayout layout = new GridLayout(numcolumns, equalwith);
		layout.marginBottom = margins;
		layout.marginTop = margins;
		layout.marginLeft = margins;
		layout.marginRight = margins;
		layout.horizontalSpacing = margins;
		layout.verticalSpacing = margins;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}
}