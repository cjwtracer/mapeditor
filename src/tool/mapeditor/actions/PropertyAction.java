package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.Drawable;
import tool.mapeditor.dialogs.CellPropertyDialog;

public class PropertyAction extends AbstractAction {
	IWorkbenchWindow window;

	public PropertyAction(IWorkbenchWindow window){
		super(ICommands.CMD_CELL_PROP, "编辑选中单元属性(&P)", "/icons/ui.png");
		this.window = window;
	}
	
	public void run(){
		CellPropertyDialog dlg = new CellPropertyDialog(window.getShell());
		dlg.open();
	}
}
