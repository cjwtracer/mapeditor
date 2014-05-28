package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import tool.mapeditor.SearchBox;
import tool.mapeditor.application.MainApplication;
import tool.model.UnitGroup;
import tool.util.WidgetUtil;
import org.eclipse.swt.widgets.Tree;

public class AddAnimGroupDialog extends Dialog {
	MainApplication app = MainApplication.getInstance();

	protected Object result;
	protected Shell shell;
	Tree tree;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AddAnimGroupDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("添加地图动画");
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
		shell.setSize(233, 348);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		SearchBox searchBox = new SearchBox(composite);
		searchBox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));
		
		tree = new Tree(composite, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		searchBox.setSearchTree(tree);
		for(UnitGroup ug : app.getProject().getUnitsGroup().values()){
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(ug.getName());
			item.setData(ug);
		}
		
		Composite composite_1 = new Composite(shell, SWT.BORDER);
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_1.heightHint = 44;
		composite_1.setLayoutData(gd_composite_1);
		
		Button btnOK = new Button(composite_1, SWT.NONE);
		btnOK.setBounds(69, 10, 80, 27);
		btnOK.setText("确定");
		btnOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				TreeItem[] selections = tree.getSelection();
				if(selections.length > 0){
					app.addAnimGroupToMap((UnitGroup)selections[0].getData());
					shell.close();
				}
			}
		});

	}
}
