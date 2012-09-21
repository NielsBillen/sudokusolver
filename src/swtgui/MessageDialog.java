package swtgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog for chosing a value for a particular field.
 * 
 * @author Niels
 * @version 0.1
 */
public class MessageDialog {
	/**
	 * Creates a new dialog shell with the given shell as parent, using the
	 * given gui and for deciding on the value for the number at the given row
	 * and column.
	 * 
	 * @param parent
	 *            The parent shell to create the dialog on.
	 */
	public MessageDialog(Shell parent, String message) {
		// Create the shell.
		final Shell shell = new Shell(parent,SWT.CLOSE| SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout(1,true));
		
		// Create the label
		Label label =new Label(shell, SWT.NONE);
		label.setText(message);
		GridData d = new GridData();
		d.horizontalAlignment=SWT.CENTER;
		d.grabExcessHorizontalSpace=true;
		d.verticalAlignment=SWT.CENTER;
		d.grabExcessVerticalSpace=true;
		label.setLayoutData(d);
		
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Ok");
		d = new GridData();
		d.widthHint=128;
		d.horizontalAlignment=SWT.CENTER;
		d.verticalAlignment=SWT.CENTER;
		d.grabExcessHorizontalSpace=true;
		d.grabExcessVerticalSpace=true;
		button.setLayoutData(d);
		button.addSelectionListener(new SelectionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		shell.setDefaultButton(button);
		
		// Pack the shell to it's smallest size.
		shell.setMinimumSize(256, 128);
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
