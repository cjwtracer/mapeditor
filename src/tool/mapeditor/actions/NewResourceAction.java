package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.application.ResourceDescriptor;
import tool.mapeditor.dialogs.NewResourceDialog;

public class NewResourceAction extends AbstractAction {
	IWorkbenchWindow window;
	
	public NewResourceAction(IWorkbenchWindow window){
		super(ICommands.CMD_NEW_RESOURCE, "新建元素(&R)", "/icons/resource_add.png");
		this.window = window;
	}
	
	public void run(){
		NewResourceDialog dlg = new NewResourceDialog(window.getShell());
		ResourceDescriptor resource = dlg.open();
		if(resource != null){
			mainApp.addResource(resource);
		}
	}

}
