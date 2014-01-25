package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import tool.io.DataCrackException;
import tool.mapeditor.SearchBox;
import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.MapDescriptor;
import tool.model.WorldMap;

public class OpenMapDialog extends Dialog {
	MainApplication mainApp = MainApplication.getInstance();

	protected MapDescriptor result;
	protected Shell shell;
	Tree tree;
	Canvas canvas;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public OpenMapDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public MapDescriptor open() {
		createContents();
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(850, 500);
		shell.setText("选择地图");
		shell.setLayout(new GridLayout(2, false));
		SearchBox searchBox = new SearchBox(shell);
		GridData gd_searchBox = new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1);
		gd_searchBox.widthHint = 150;
		gd_searchBox.heightHint = 33;
		searchBox.setLayoutData(gd_searchBox);
		canvas = new Canvas(shell, SWT.NONE);
		canvas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		tree = new Tree(shell, SWT.BORDER);
		tree.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 2));
		searchBox.setSearchTree(tree);
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setLayout(null);
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite.heightHint = 43;
		composite.setLayoutData(gd_composite);
		
		Button btnOK = new Button(composite, SWT.NONE);
		btnOK.setBounds(445, 10, 76, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				onOK();
			}
		});
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.setBounds(540, 10, 76, 27);
		btnCancel.setText("取消");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				result = null;
				shell.close();
			}
		});
		tree.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event event) {
			}
		});
		final Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem itemDel = new MenuItem(menu, SWT.PUSH);
		itemDel.setText("删除");
		itemDel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				TreeItem item = tree.getSelection()[0];
				mainApp.deleteMap((Integer)item.getData());
				buildTree();
			}
		});
		tree.addMenuDetectListener(new MenuDetectListener(){
			public void menuDetected(MenuDetectEvent e){
				TreeItem[] items = tree.getSelection();
				if(items.length > 0){
					menu.setLocation(e.x, e.y);
					menu.setVisible(true);
				}
			}
		});
		buildTree();
	}
	
	private void buildTree(){
		tree.removeAll();
		for(WorldMap m : mainApp.getProject().getMapGroup().values()){
			TreeItem item = new TreeItem(tree, SWT.NONE);
			StringBuffer buf = new StringBuffer(m.getName()).append("[").append(m.getExpID()).append("]");
			item.setText(buf.append("<").append(m.getFileID()).append(">").toString());
			item.setData(Integer.valueOf(m.getFileID()));
		}
	}
	
	private void onOK(){
		TreeItem[] is = tree.getSelection();
		if(is.length == 1){
			TreeItem i = is[0];
			int id = (Integer)i.getData();
			mainApp.openMap(id);
			shell.close();
		}
	}
}
