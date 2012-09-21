package swtgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog for chosing a value for a particular field.
 * 
 * @author Niels
 * @version 0.1
 */
public class ChoiceDialog {
	/**
	 * Creates a new dialog shell with the given shell as parent, using the
	 * given gui and for deciding on the value for the number at the given row
	 * and column.
	 * 
	 * @param parent
	 *            The parent shell to create the dialog on.
	 * @param gui
	 *            The gui to create the dialog on.
	 * @param row
	 *            The row of the element to decidide the value for.
	 * @param column
	 *            The column of the element to decidide the value for.
	 */
	public ChoiceDialog(Shell parent, final GUI gui, final int row,
			final int column) {
		// Create the shell.
		final Shell shell = new Shell(parent,SWT.DIALOG_TRIM| SWT.APPLICATION_MODAL);

		// Layout the cell.
		GridLayout layout = new GridLayout(3, true);
		shell.setLayout(layout);

		// Add nine buttons to decide the value.
		for (int i = 1; i <= 9; i++) {
			// Create the button.
			Button b = new Button(shell, SWT.PUSH);

			// Create the layout data.
			GridData d = new GridData();
			d.widthHint = 36;
			d.heightHint = 36;
			final int index = i;

			// Customize the button.
			b.setLayoutData(d);
			b.setText("" + index);

			if (gui.getSudoku().isPossible(row, column, index)) {
				// Add a listener.
				b.addSelectionListener(new SelectionListener() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * org.eclipse.swt.events.SelectionListener#widgetSelected
					 * (org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						gui.getSudoku().setValue(row, column, index,true);
						shell.close();
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.swt.events.SelectionListener#
					 * widgetDefaultSelected
					 * (org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			} else
				b.setEnabled(false);
		}
		
		// Create the button for an empty number.
		Button b = new Button(shell, SWT.PUSH);
		GridData d = new GridData();
		d.horizontalAlignment = SWT.FILL;
		d.grabExcessHorizontalSpace = false;
		d.heightHint = 36;
		d.horizontalSpan = 3;
		b.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				gui.getSudoku().setValue(row, column, 0,false);
				shell.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		b.setLayoutData(d);
		b.setText(" ");

		// Pack the shell to it's smallest size.
		shell.pack();

		// Get the dimensions
		Rectangle mSize = GUI.shell.getBounds();
		Rectangle sSize = shell.getBounds();

		// Set the location
		shell.setLocation(mSize.x + (mSize.width - sSize.width) / 2, mSize.y
				+ (mSize.height - sSize.height) / 2);
		shell.open();
	}
}
