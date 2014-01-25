package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tool.util.WidgetUtil;

public class CreateWorkspaceDialog extends Dialog {

	protected String result = "";
	protected Shell shell;
	private Text text;
	private Button btnOK;
	private Button btnCancel;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateWorkspaceDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("选择工作空间");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
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
		shell.setSize(500, 220);
		shell.setText(getText());
		Label label = new Label(shell, SWT.NONE);
		label.setBounds(34, 92, 56, 26);
		label.setText("工作空间");
		text = new Text(shell, SWT.BORDER);
		text.setBounds(96, 89, 243, 23);
		text.setEditable(false);
		
		Button btn = new Button(shell, SWT.NONE);
		btn.setBounds(369, 88, 89, 27);
		btn.setText("选择路径");
		btn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dlg = new DirectoryDialog(shell);
				String dir = dlg.open();
				if(dir != null){
					text.setText(dir);
				}
			}
		});
		
		btnOK = new Button(shell, SWT.NONE);
		btnOK.setBounds(259, 155, 80, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String p = text.getText();
				if(!p.equals(""))
					result = p;
				shell.close();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(378, 155, 80, 27);
		btnCancel.setText("取消");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
	}
}
