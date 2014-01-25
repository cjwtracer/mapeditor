package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.ExportMapsDialog;

public class ExportMapAction extends AbstractAction {
	IWorkbenchWindow window;
	
	public ExportMapAction(IWorkbenchWindow window){
		super(ICommands.CMD_EXPORT_MAP, "导出地图", "/icons/export.png");
		if(window == null)
			throw new NullPointerException();
		this.window = window;
	}
	
	public void run(){
		ExportMapsDialog dlg = new ExportMapsDialog(window.getShell());
		dlg.open();
	}

}
