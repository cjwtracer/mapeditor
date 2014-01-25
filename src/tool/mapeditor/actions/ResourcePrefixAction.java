package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.ResourcePrefixDialog;

public class ResourcePrefixAction extends AbstractAction {

	IWorkbenchWindow window;
	
	public ResourcePrefixAction(IWorkbenchWindow window) {
		super(ICommands.CMD_RES_PROPERTY, "资源属性编辑", "/icons/building.png");
		this.window = window;
	}
	
	public void run(){
		if(mainApp.getCurrentResource() != null){
			ResourcePrefixDialog dlg = new ResourcePrefixDialog(window.getShell());
			dlg.open();
		}
	}

}
