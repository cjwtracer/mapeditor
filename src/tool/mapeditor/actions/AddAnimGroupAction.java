package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.AddAnimGroupDialog;

public class AddAnimGroupAction extends AbstractAction {
	IWorkbenchWindow window;

	public AddAnimGroupAction(IWorkbenchWindow window) {
		super(ICommands.CMD_ADD_ANIM_GROUP, "添加地图动画", "/icons/anim_edit.png");
		this.window = window;
	}
	
	public void run(){
		AddAnimGroupDialog dlg = new AddAnimGroupDialog(window.getShell());
		dlg.open();
	}

}
