package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.io.DataCrackException;
import tool.io.ProjectWriter;
import tool.mapeditor.application.MapDescriptor;
import tool.mapeditor.application.MapProject;
import tool.mapeditor.dialogs.NewMapDialog;
import tool.model.Project;
import tool.model.WorldMap;

public class NewMapAction extends AbstractAction {
	IWorkbenchWindow window;
	
	public NewMapAction(IWorkbenchWindow window){
		super(ICommands.CMD_NEW, "新建地图(&N)", "/icons/map_add.png");
		this.window = window;
	}
	
	public void run(){
		NewMapDialog dlg = new NewMapDialog(window.getShell());
		WorldMap map = dlg.open();
		mainApp.showMap(map);
	}

}
