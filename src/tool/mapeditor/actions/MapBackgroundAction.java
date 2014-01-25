package tool.mapeditor.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class MapBackgroundAction extends AbstractAction {
	IWorkbenchWindow window;

	public MapBackgroundAction(IWorkbenchWindow window){
		super(ICommands.CMD_MAP_BACKGROUND, "地图背景", "/icons/background.png");
		this.window = window;
	}
	
	public void run(){
		FileDialog dlg = new FileDialog(window.getShell(), SWT.OPEN);
		dlg.setFilterExtensions(new String[]{"*.jpg", "*.png"});
		dlg.setFilterNames(new String[]{"JPG图片", "PNG图片"});
		String img = dlg.open();
		if(img != null){
			mainApp.createMapBackground(img, dlg.getFileName(), mainApp.getCurrentMap());
		}
	}
}
