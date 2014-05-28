package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.ImportAnimationDialog;

public class ImportAnimationAction extends AbstractAction {
	IWorkbenchWindow window;

	public ImportAnimationAction(IWorkbenchWindow window) {
		super(ICommands.CMD_IMPORT_ANIM, "导入动画", "/icons/file_anim.png");
		this.window = window;
	}
	
	public void run(){
		ImportAnimationDialog dlg = new ImportAnimationDialog(window.getShell());
		dlg.open();
	}

}
