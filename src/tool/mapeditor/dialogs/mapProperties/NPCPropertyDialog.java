package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import tool.mapeditor.dialogs.PropertyEdit;
import tool.model.Unit;
import tool.util.WidgetUtil;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;

public class NPCPropertyDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	Unit unit;
	private Table table;
	PropertyEdit propEdit;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NPCPropertyDialog(Shell parent, Unit unit) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.unit = unit;
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
		shell.setSize(450, 300);
		shell.setText("NPC属性");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 332, 252);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn colKey = new TableColumn(table, SWT.NONE);
		colKey.setText("属性");
		colKey.setWidth(200);
		TableColumn colValue = new TableColumn(table, SWT.NONE);
		colValue.setText("值");
		colValue.setWidth(200);
		propEdit = new PropertyEdit(shell, table, unit);
		propEdit.buildTable();
		
		Button btnEdit = new Button(shell, SWT.NONE);
		btnEdit.setBounds(354, 27, 80, 27);
		btnEdit.setText("编辑");
		btnEdit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onEdit();
			}
		});
		
		Button btnShut = new Button(shell, SWT.NONE);
		btnShut.setBounds(354, 235, 80, 27);
		btnShut.setText("关闭");
		btnShut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});

	}
}
