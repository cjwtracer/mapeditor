package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

import tool.mapeditor.dialogs.ImageSliceDialog;

public class ImageSpliteAction extends AbstractAction {
	IWorkbenchWindow window;

	public ImageSpliteAction(IWorkbenchWindow window){
		super(ICommands.CMD_IMG_SPLITE, "大图切割");
		this.window = window;
	}
	
	public void run(){
		ImageSliceDialog dlg = new ImageSliceDialog(window.getShell());
		dlg.open();
	}
}
