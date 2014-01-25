package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import tool.mapeditor.application.MainApplication;
import tool.resourcemanager.SWTResourceManager;
import tool.util.WidgetUtil;

public class ResourcePrefixDialog extends Dialog {

	MainApplication app = MainApplication.getInstance();
	protected Object result;
	protected Shell shell;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResourcePrefixDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		setText("当前资源组前缀");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
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
		shell.setSize(375, 168);
		shell.setText(getText());
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 48, 106, 23);
		lblNewLabel.setText("资源名称前缀：");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(122, 49, 222, 23);
		text.setText(app.getCurrentResource().getPrefix());
		
		Button btnOK = new Button(shell, SWT.NONE);
		btnOK.setBounds(218, 103, 58, 27);
		btnOK.setText("确定");
		btnOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				app.getCurrentResource().setPrefix(text.getText());
				shell.close();
			}
		});
		btnOK.forceFocus();
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(293, 103, 51, 27);
		btnCancel.setText("取消");
		btnCancel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				shell.close();
			}
		});

	}

}
