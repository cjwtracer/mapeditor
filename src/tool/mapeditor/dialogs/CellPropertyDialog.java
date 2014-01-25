package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import tool.mapeditor.application.MainApplication;
import tool.util.WidgetUtil;

public class CellPropertyDialog extends Dialog {
	MainApplication mainApp = MainApplication.getInstance();

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CellPropertyDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		setText("编辑属性");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetUtil.layoutRightTop(shell);
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
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("旋转");
		final Scale scaleRotate = new Scale(composite, SWT.NONE);
		scaleRotate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		scaleRotate.setMaximum(360);
		scaleRotate.setIncrement(1);
		scaleRotate.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scaleRotate.getSelection();
				mainApp.setCurrentRotation(v);
			}
		});
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("透明度");
		final Scale scaleTransparent = new Scale(composite, SWT.NONE);
		scaleTransparent.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		scaleTransparent.setMaximum(256);
		scaleTransparent.setIncrement(1);
		scaleTransparent.setSelection(255);
		scaleTransparent.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scaleTransparent.getSelection();
				mainApp.setCurrentTransparency(v);
			}
		});
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setText("缩放");
		final Scale scaleScale = new Scale(composite, SWT.NONE);
		scaleScale.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		scaleScale.setMaximum(200);
		scaleScale.setIncrement(2);
		scaleScale.setSelection(10);
		scaleScale.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				float v = scaleScale.getSelection();
				mainApp.setCurrentScale(v / 10);
			}
		});
	}
}
