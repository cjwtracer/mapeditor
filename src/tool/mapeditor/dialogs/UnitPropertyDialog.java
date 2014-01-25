package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tool.model.Property;
import tool.model.Unit;
import tool.util.WidgetUtil;
import org.eclipse.swt.widgets.Table;

public class UnitPropertyDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Table table;

	PropertyEdit propEdit;
	Unit unit;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UnitPropertyDialog(Shell parent, Unit unit) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		if(unit == null)
			throw new IllegalArgumentException("unit can't be null!");
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
	protected void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(500, 300);
		shell.setText("元素属性");
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 97, 384, 116);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		propEdit = new PropertyEdit(shell, table, unit);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(404, 224, 80, 27);
		btnCancel.setText("关闭");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
		
		final Button checkNormal = new Button(shell, SWT.CHECK);
		checkNormal.setBounds(10, 55, 98, 17);
		checkNormal.setText("怪物");
		checkNormal.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				unit.setType(checkNormal.getSelection(), Unit.MONSTER);
				propEdit.buildTable();
			}
		});
		checkNormal.setSelection(unit.isType(Unit.MONSTER));
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 89, 474, 2);
		
		Label label_1 = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label_1.setBounds(10, 38, 474, 2);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 61, 17);
		lblNewLabel.setText("类型：");
		
		TableColumn colKey = new TableColumn(table, SWT.NONE);
		colKey.setText("属性");
		colKey.setWidth(200);
		TableColumn colValue = new TableColumn(table, SWT.NONE);
		colValue.setText("值");
		colValue.setWidth(200);
		propEdit.buildTable();
		
		Button btnAdd = new Button(shell, SWT.NONE);
		btnAdd.setBounds(404, 97, 80, 27);
		btnAdd.setText("添加");
		btnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onAdd();
			}
		});
		
		Button btnEdit = new Button(shell, SWT.NONE);
		btnEdit.setBounds(404, 141, 80, 27);
		btnEdit.setText("编辑");
		btnEdit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onEdit();
			}
		});
	}
}
