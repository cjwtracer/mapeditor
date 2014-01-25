package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import tool.mapeditor.dialogs.PropertyEdit;
import tool.model.Property;
import tool.model.RegionPolygon;
import tool.util.WidgetUtil;

public class RegionPropertyDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Table table;

	PropertyEdit propEdit;
	RegionPolygon region;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RegionPropertyDialog(Shell parent, RegionPolygon region) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		if(region == null)
			throw new IllegalArgumentException("region can't be null!");
		this.region = region;
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
		shell.setText("区域属性");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 40, 356, 158);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		propEdit = new PropertyEdit(shell, table, region);
		
		TableColumn colKey = new TableColumn(table, SWT.NONE);
		colKey.setText("属性");
		colKey.setWidth(200);
		TableColumn colValue = new TableColumn(table, SWT.NONE);
		colValue.setText("值");
		colValue.setWidth(200);
		propEdit.buildTable();
		
		Button btnAdd = new Button(shell, SWT.NONE);
		btnAdd.setBounds(390, 40, 80, 27);
		btnAdd.setText("添加");
		btnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onAdd();
			}
		});
		
		Button btnEdit = new Button(shell, SWT.NONE);
		btnEdit.setBounds(390, 82, 80, 27);
		btnEdit.setText("编辑");
		btnEdit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onEdit();
			}
		});
		
		Button btnRemove = new Button(shell, SWT.NONE);
		btnRemove.setBounds(390, 126, 80, 27);
		btnRemove.setText("删除");
		btnRemove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onRemove();
			}
		});
		
		Button btnShut = new Button(shell, SWT.NONE);
		btnShut.setBounds(390, 222, 80, 27);
		btnShut.setText("关闭");
		btnShut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
	}
}
