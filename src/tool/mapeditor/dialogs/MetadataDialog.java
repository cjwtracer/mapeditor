package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Tree;

import tool.util.WidgetUtil;

public class MetadataDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table tableMain;
	private Table tableType;
	Tree tree;
	TreeItem itemTile;
	TreeItem itemUnit;
	TreeItem itemRegion;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public MetadataDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("元类型");
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
		shell.setSize(700, 500);
		shell.setText(getText());
		shell.setLayout(new GridLayout(3, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.FILL, false, true, 1, 2);
		gd_composite.widthHint = 111;
		composite.setLayoutData(gd_composite);
		
		tree = new Tree(composite, SWT.BORDER);
		itemTile = new TreeItem(tree, SWT.NONE);
		itemTile.setText("地表砖块");
		itemUnit = new TreeItem(tree, SWT.NONE);
		itemUnit.setText("地图元素");
		itemRegion = new TreeItem(tree, SWT.NONE);
		itemRegion.setText("地图区域");
		tree.setSelection(itemTile);
		tree.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				int i = tree.getSelectionCount();
				if(i > 0)
					selectTree(tree.getSelection()[0]);
			}
		});
		
		tableType = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableType.setHeaderVisible(true);
		tableType.setLinesVisible(true);
		TableColumn colName = new TableColumn(tableType, SWT.NONE);
		colName.setText("名称");
		colName.setWidth(50);
		TableColumn colType = new TableColumn(tableType, SWT.NONE);
		colType.setText("类型");
		colType.setWidth(60);
		selectTree(itemTile);
		tableType.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				if(tableType.getSelectionCount() > 0)
					selectTableType(tableType.getSelectionIndex());
			}
		});
		
		tableMain = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableMain.setHeaderVisible(true);
		tableMain.setLinesVisible(true);
		selectTableType(0);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		GridData gd_composite_1 = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_composite_1.widthHint = 83;
		composite_1.setLayoutData(gd_composite_1);
		
		Button btnAdd = new Button(composite_1, SWT.NONE);
		btnAdd.setBounds(10, 28, 63, 27);
		btnAdd.setText("添加");
		
		Button btnEdit = new Button(composite_1, SWT.NONE);
		btnEdit.setBounds(10, 76, 63, 27);
		btnEdit.setText("编辑");
		
		Button btnDelete = new Button(composite_1, SWT.NONE);
		btnDelete.setBounds(10, 130, 63, 27);
		btnDelete.setText("删除");
		
		Composite composite_2 = new Composite(shell, SWT.BORDER);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_composite_2.heightHint = 43;
		composite_2.setLayoutData(gd_composite_2);
		
		Button btnOK = new Button(composite_2, SWT.NONE);
		btnOK.setBounds(350, 11, 80, 27);
		btnOK.setText("保存");
		btnOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				saveConfigs();
				shell.close();
			}
		});
		
		Button btnCancel = new Button(composite_2, SWT.NONE);
		btnCancel.setBounds(474, 11, 80, 27);
		btnCancel.setText("取消");
		btnCancel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				shell.close();
			}
		});

	}

	private void selectTableType(int i) {
		if(i < tableType.getItemCount()){
			TableItem item = tableType.getItem(i);
			
		}
	}

	private void selectTree(TreeItem item) {
		tableType.removeAll();
		if(item == itemTile){
//			for()
		}
	}
	
	private void saveConfigs(){
		
	}
}
