package tool.mapeditor.dialogs;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tool.util.ImageUtil;
import tool.util.WidgetUtil;

import tool.io.DataCrackException;
import tool.io.ProjectWriter;
import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.MapProject;
import tool.mapeditor.application.ResourceDescriptor;
import tool.model.ResourceSet;
import tool.resourcemanager.Resources;

public class NewResourceDialog extends Dialog {

	protected ResourceDescriptor resource;
	protected Shell shell;
	Label labelHead;
	Text textGroupName;
	Table tableFiles;
	
	Map<String, File> importedFiles = new HashMap<String, File>();
	String alert = "";
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NewResourceDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("添加元素集");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public ResourceDescriptor open() {
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
		return resource;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(390, 405);
		shell.setText(getText());

		{
			labelHead = new Label(shell, SWT.NONE);
			labelHead.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(Resources.RED);
					e.gc.drawString(alert, 10, 20, true);
				}
			});
			labelHead.setBackground(Resources.WHITE);
			labelHead.setAlignment(SWT.RIGHT);
			labelHead.setImage(Resources.getImage("/icons/dialog_import_resources.png"));
			labelHead.setBounds(0, 0, 384, 57);
		}
		{
			Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(0, 57, 384, 2);
		}
		Label labelGroupName = new Label(shell, SWT.NONE);
		labelGroupName.setBounds(20, 75, 60, 17);
		labelGroupName.setText("元素集名称");
		textGroupName = new Text(shell, SWT.BORDER);
		textGroupName.setBounds(91, 72, 279, 23);
		
		tableFiles = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableFiles.setBounds(10, 135, 294, 187);
		tableFiles.setHeaderVisible(true);
		tableFiles.setLinesVisible(true);
		TableColumn tableColumnID = new TableColumn(tableFiles, SWT.NONE);
		tableColumnID.setWidth(45);
		tableColumnID.setText("编号");
		TableColumn tableColumnName = new TableColumn(tableFiles, SWT.NONE);
		tableColumnName.setWidth(100);
		tableColumnName.setText("文件名");
		TableColumn tableColumnSize = new TableColumn(tableFiles, SWT.NONE);
		tableColumnSize.setWidth(60);
		tableColumnSize.setText("尺寸");
		TableColumn tableColumnPath = new TableColumn(tableFiles, SWT.NONE);
		tableColumnPath.setWidth(80);
		tableColumnPath.setText("位置");
		
		Button btnAddDir = new Button(shell, SWT.NONE);
		btnAddDir.addSelectionListener(new SelectionAdapter() {	
			public void widgetSelected(SelectionEvent e) {
				onAddDirectory();
			}
		});
		btnAddDir.setBounds(310, 135, 60, 25);
		btnAddDir.setText("添加目录");
		
		Button btnAddFile = new Button(shell, SWT.NONE);
		btnAddFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAddFile();
			}
		});
		btnAddFile.setBounds(310, 166, 60, 25);
		btnAddFile.setText("添加文件");
		
		Button btnRemove = new Button(shell, SWT.NONE);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onRemoveFile();
			}
		});
		btnRemove.setBounds(310, 197, 60, 25);
		btnRemove.setText("删除选中");
		
		Button btnOK = new Button(shell, SWT.NONE);
		btnOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOK();
			}
		});
		btnOK.setBounds(244, 341, 60, 25);
		btnOK.setText("确定");
		shell.setDefaultButton(btnOK);
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onCancel();
			}
		});
		btnCancel.setBounds(310, 341, 60, 25);
		btnCancel.setText("取消");
	}
	
	void onAddDirectory(){
		DirectoryDialog dlg = new DirectoryDialog(shell, SWT.OPEN);
		String dirPath = dlg.open();
		if(dirPath == null) return;
		File dir = new File(dirPath);
		if(!dir.exists()) return;
		
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				return filename.contains(".png");			
			}
		});
		
		for(File file : files) {
			if(file.isDirectory())
				continue;
			String filePath = file.getAbsolutePath();
			if(importedFiles.containsKey(filePath))
				continue;
			createTableItem(file);
		}
	}
	
	void onAddFile(){
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setFilterNames(new String[]{"PNG图片"});
		dlg.setFilterExtensions(new String[]{"*.png"});
		String filePath = dlg.open();
		if (filePath == null)
			return;
		File file = new File(filePath);
		if (!file.exists() || importedFiles.containsKey(filePath))
			return;
		createTableItem(file);
	}
	
	void onRemoveFile(){
		TableItem[] items = tableFiles.getSelection();
		for (TableItem item : items) {
			importedFiles.remove(item.getText(3));
			item.dispose();
		}
	}
	
	void onOK(){
		if(textGroupName.getText().equals("")){
			alert = "请填写元素集名称";
			return;
		}else if(importedFiles.size() == 0){
			alert = "请添加图片元素";
			return;
		}else{
			alert = "";
		}
		labelHead.redraw();
		MainApplication app = MainApplication.getInstance();
		String setName = textGroupName.getText().trim();
		if(app.checkResourceSet(setName) != null){
			alert = "同名资源组已存在";
			return;
		}
		resource = app.newResourceSet(setName, importedFiles);
		
		shell.close();
	}
	
	void onCancel(){
		resource = null;
		shell.close();
	}
	
	private TableItem createTableItem(File file) {
		String filePath = file.getAbsolutePath();
		Point size = ImageUtil.getImageSize(filePath);
		importedFiles.put(filePath, file);
		TableItem item = new TableItem(tableFiles, SWT.NONE);
		item.setText(new String[] { Integer.toString(tableFiles.getItemCount()), file.getName(), size.x + "x" + size.y, filePath });
		item.setData(size);
		return item;
	}

}
