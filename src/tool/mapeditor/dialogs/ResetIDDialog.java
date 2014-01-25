package tool.mapeditor.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class ResetIDDialog extends Dialog {

	protected Object result;
	protected Shell shlid;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ResetIDDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlid.open();
		shlid.layout();
		Display display = getParent().getDisplay();
		while (!shlid.isDisposed()) {
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
		shlid = new Shell(getParent(), getStyle());
		shlid.setSize(246, 172);
		shlid.setText("重置地图ID");
		
		text = new Text(shlid, SWT.BORDER);
		text.setBounds(10, 39, 73, 23);
		
		Button btnOK = new Button(shlid, SWT.NONE);
		btnOK.setBounds(150, 107, 80, 27);
		btnOK.setText("确定");
		
	}
}
