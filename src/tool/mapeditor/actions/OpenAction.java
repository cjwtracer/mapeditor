package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.application.MapDescriptor;
import tool.mapeditor.dialogs.OpenMapDialog;

public class OpenAction extends AbstractAction {
	IWorkbenchWindow window;
	
	public OpenAction(IWorkbenchWindow window){
		super(ICommands.CMD_OPEN, "打开(&O)", "/icons/tool_open.png");
		this.window = window;
	}
	
	public void run(){
		OpenMapDialog dlg = new OpenMapDialog(window.getShell());
		dlg.open();
	}

}
