package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import tool.mapeditor.dialogs.PropertyEdit;
import tool.model.Property;
import tool.model.WorldMap;

public class DynamicPropsPanel extends Composite {
	private Table table;
	PropertyEdit propEdit;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DynamicPropsPanel(Composite parent, WorldMap map) {
		super(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 8));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn clmKey = new TableColumn(table, SWT.NONE);
		clmKey.setText("属性");
		clmKey.setWidth(200);
		TableColumn clmValue = new TableColumn(table, SWT.NONE);
		clmValue.setText("值");
		clmValue.setWidth(200);
		propEdit = new PropertyEdit(getShell(), table, map);
		propEdit.buildTable();
		
		Button btnAdd = new Button(this, SWT.NONE);
		GridData gd_btnAdd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnAdd.widthHint = 80;
		btnAdd.setLayoutData(gd_btnAdd);
		btnAdd.setText("添加");
		btnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onAdd();
			}
		});
		
		Button btnEdit = new Button(this, SWT.NONE);
		btnEdit.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnEdit.setText("编辑");
		btnEdit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onEdit();
			}
		});
		
		Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		btnRemove.setText("删除");
		btnRemove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				propEdit.onRemove();
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
