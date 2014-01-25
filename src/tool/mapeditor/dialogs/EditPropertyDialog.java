package tool.mapeditor.dialogs;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import tool.model.Model;
import tool.model.Property;
import tool.util.WidgetUtil;

public class EditPropertyDialog extends Dialog {
	protected Property result;
	protected Shell shell;
	private Text textName;
	private Combo comboType;
	private Text textValue;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	EditPropertyDialog(Shell parent, Property property) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		result = property;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Property open() {
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
		shell.setSize(300, 300);
		shell.setText("编辑属性");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(21, 21, 61, 17);
		lblNewLabel.setText("名称：");
		
		textName = new Text(shell, SWT.BORDER);
		textName.setBounds(113, 18, 73, 23);
		textName.setText(result.getName());
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(21, 66, 61, 17);
		lblNewLabel_1.setText("类型：");
		
		comboType = new Combo(shell, SWT.READ_ONLY);
		comboType.setBounds(113, 63, 73, 23);
		for(String s : Property.TYPE_LIST){
			comboType.add(s);
		}
		comboType.setText(result.getType());
		
		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setBounds(21, 110, 61, 17);
		lblNewLabel_2.setText("值：");
		
		textValue = new Text(shell, SWT.BORDER);
		textValue.setBounds(113, 104, 73, 23);
		textValue.setText(result.getValue().toString());
		
		Button btnOK = new Button(shell, SWT.NONE);
		btnOK.setBounds(169, 235, 55, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				onOK();
			}
		});
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(230, 235, 54, 27);
		btnCancel.setText("取消");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				result = null;
				shell.close();
			}
		});
	}

	private void onOK() {
		try{
			result.setValue(comboType.getText(), textValue.getText());
			result.setName(textName.getText());
		}catch(Exception e){
			e.printStackTrace();
		}
		shell.close();
	}
}
