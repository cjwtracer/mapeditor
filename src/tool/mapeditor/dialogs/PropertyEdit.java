package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import tool.model.Model;
import tool.model.Property;

/**
 * A convenience helper for managing a table of a standard model.
 * @author caijw
 *
 */
public class PropertyEdit {
	protected Table table;
	protected Model model;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public PropertyEdit(Shell shell, Table table, Model model) {
		this.shell = shell;
		this.table = table;
		this.model = model;
	}

	public void buildTable() {
		table.removeAll();
		for(Property p : model.getProperties()){
			TableItem i = new TableItem(table, SWT.NONE);
			i.setText(0, p.getName());
			i.setText(1, String.valueOf(p.getValue()));
			i.setData(p);
		}
	}

	public void onEdit() {
		int idx = table.getSelectionIndex();
		if(idx == -1)
			return;
		EditPropertyDialog dlg = new EditPropertyDialog(shell, (Property)table.getItem(idx).getData());
		if(dlg.open() != null)
			buildTable();
	}
	
	public void onAdd(){
		model.newProperty("newProp", "String", "value");
		buildTable();
	}

	public void onRemove() {
		int idx = table.getSelectionIndex();
		if(idx == -1)
			return;
		model.removeProperty(idx);
		buildTable();
	}
}
