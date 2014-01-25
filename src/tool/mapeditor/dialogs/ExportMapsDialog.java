package tool.mapeditor.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import tool.mapeditor.SearchBox;
import tool.mapeditor.application.MainApplication;
import tool.model.WorldMap;
import tool.util.WidgetUtil;

public class ExportMapsDialog extends Dialog {
	MainApplication app = MainApplication.getInstance();

	protected Object result;
	protected Shell shell;
	private Text text;
	private Tree tree;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExportMapsDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
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
		shell.setSize(630, 363);
		shell.setText("选择地图");
		shell.setLayout(new GridLayout(2, false));
		
		SearchBox searchBox = new SearchBox(shell);
		GridData gd_searchBox = new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1);
		gd_searchBox.widthHint = 150;
		gd_searchBox.heightHint = 25;
		searchBox.setLayoutData(gd_searchBox);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		Canvas canvas = new Canvas(composite, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 51;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("另存为：");
		
		text = new Text(composite, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 175;
		text.setLayoutData(gd_text);
		
		Button btnPath = new Button(composite, SWT.NONE);
		btnPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnPath.setText("...");
		btnPath.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dlg = new DirectoryDialog(shell, SWT.SAVE);
				String pth = dlg.open();
				if(pth != null){
					text.setText(pth);
				}
			}
		});
		
		Composite composite_1 = new Composite(composite, SWT.BORDER);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 2);
		gd_composite_1.heightHint = 50;
		composite_1.setLayoutData(gd_composite_1);
		
		Button btnOK = new Button(composite_1, SWT.NONE);
		btnOK.setBounds(228, 10, 69, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String pth = text.getText();
				if(!new File(pth).exists()){
					app.alert("无法解析目标路径");
					return;
				}
				List<WorldMap> maps = new ArrayList<WorldMap>();
				for(TreeItem item : tree.getItems()){
					if(item.getChecked()){
						maps.add((WorldMap)item.getData());
					}
				}
				app.exportMaps(maps, pth);
				shell.close();
			}
		});
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.setBounds(322, 10, 69, 27);
		btnCancel.setText("取消");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
		
		tree = new Tree(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		buildTree();
		searchBox.setSearchTree(tree);
		
		final Button btnAll = new Button(shell, SWT.CHECK);
		btnAll.setText("全选");
		btnAll.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				for(TreeItem i : tree.getItems()){
					i.setChecked(btnAll.getSelection());
				}
			}
		});
	}
	
	void buildTree(){
		for(WorldMap m : app.getProject().getMapGroup().values()){
			TreeItem i = new TreeItem(tree, SWT.NONE);
			StringBuffer buf = new StringBuffer(m.getName()).append("[").append(m.getExpID()).append("]");
			i.setText(buf.append("<").append(m.getFileID()).append(">").toString());
			i.setData(m);
		}
	}
}
