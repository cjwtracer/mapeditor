package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tool.model.Model;
import tool.model.Property;
import tool.util.WidgetUtil;
import tool.util.TableItemTextModify;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;

public class PropertyEditDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	Model model;
	private Table table;
	String title;
	TableEditor editor;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PropertyEditDialog(Shell parent, Model model, String title) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.model = model;
		this.title = title;
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
		shell.setText(title);
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 332, 252);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int idx = table.getSelectionIndex();
				if(idx == -1)
					return;
				TableItem i = table.getItem(idx);
				final Property p = (Property)i.getData();
				if(p.getDefaultValues() == null){
					new TableItemTextModify(editor, i, i.getText(1), 1){
						public boolean doModify(String newText) {
							return p.setValue(newText);
						}
					};
				}
			}
		});
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		TableColumn colKey = new TableColumn(table, SWT.NONE);
		colKey.setText("属性");
		colKey.setWidth(200);
		TableColumn colValue = new TableColumn(table, SWT.NONE);
		colValue.setText("值");
		colValue.setWidth(200);
		buildTable();
		
		Button btnShut = new Button(shell, SWT.NONE);
		btnShut.setBounds(354, 235, 80, 27);
		btnShut.setText("关闭");
		btnShut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});

	}

	public void buildTable() {
		table.removeAll();
		for(final Property p : model.getProperties()){
			TableItem i = new TableItem(table, SWT.NONE);
			i.setText(0, p.getName());
			String value = String.valueOf(p.getValue());
			i.setText(1, value);
			i.setData(p);
			final String[] vs = p.getDefaultValues();
			if(vs != null){
				final CCombo c = WidgetUtil.getTableCombo(table, i, 1, vs);
				c.addListener(SWT.Selection, new Listener(){
					public void handleEvent(Event e){
						p.setValue(p.getType(), vs[c.getSelectionIndex()]);
					}
				});
				for(int k = 0; k < vs.length; k++){
					if(vs[k].equals(value)){
						c.select(k);
						break;
					}
				}
			}
		}
	}
}
