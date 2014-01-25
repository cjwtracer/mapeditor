package tool.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public abstract class TableItemTextModify {
	
	public TableItemTextModify(TableEditor editor, final TableItem item, String initText, final int editColumn){
		Control oldEditor = editor.getEditor();
		if(oldEditor != null)
			oldEditor.dispose();
		if(item != null){
			Table table = item.getParent();
			final Text newEditor = new Text(table, SWT.NONE);
			newEditor.setText(initText);
			editor.setEditor(newEditor, item, editColumn);
			Listener tableListener = new Listener(){
				public void handleEvent(Event e){
					switch(e.type){
					case SWT.FocusOut:
						newEditor.dispose();
						e.doit = false;
						item.setText(editColumn, item.getText(editColumn));
						break;
					case SWT.Traverse:
						switch(e.detail){
						case SWT.TRAVERSE_RETURN:
							String name = newEditor.getText();
							if(doModify(name))
								item.setText(editColumn, name);
							break;
						case SWT.TRAVERSE_ESCAPE:
							e.doit = false;
							item.setText(editColumn, item.getText(editColumn));
							break;
						}
						newEditor.dispose();
						break;
					}
				}
			};
			newEditor.addListener(SWT.FocusOut, tableListener);
			newEditor.addListener(SWT.Traverse, tableListener);
			newEditor.setFocus();
			newEditor.selectAll();
		}
	}
	
	/**
	 * 
	 * @param newText
	 * @return Flag indicating whether the modification is to be applied. 
	 */
	public abstract boolean doModify(String newText);

}
