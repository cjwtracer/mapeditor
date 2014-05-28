package tool.mapeditor.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import tool.util.WidgetUtil;

public class ConfirmDialog extends Dialog {

	protected boolean result;
	protected Shell shell;
	
	String msg = "";

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConfirmDialog(Shell parent, String msg) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("确认对话框");
		if(msg != null)
			this.msg = msg;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public boolean open() {
		createContents();
		WidgetUtil.layoutCenter(shell);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 166);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		label.setAlignment(SWT.CENTER);
		label.setText(msg);
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 56;
		composite.setLayoutData(gd_composite);
		
		Button btnOK = new Button(composite, SWT.NONE);
		btnOK.setBounds(228, 13, 80, 27);
		btnOK.setText("是");
		btnOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				result = true;
				shell.close();
			}
		});
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(344, 13, 80, 27);
		btnCancel.setText("否");
		btnCancel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				result = false;
				shell.close();
			}
		});

	}
}
