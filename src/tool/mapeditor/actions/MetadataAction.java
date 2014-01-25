package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.MetadataDialog;

public class MetadataAction extends AbstractAction{
	
	IWorkbenchWindow window;

	public MetadataAction(IWorkbenchWindow window) {
		super(ICommands.CMD_PREFERANCE, "编辑元类型", "/icons/collect.png");
		if(window == null)
			throw new IllegalArgumentException();
		this.window = window;
	}
	
	public void run(){
//		MetadataDialog dlg = new MetadataDialog(window.getShell());
//		dlg.open();
	}

}
