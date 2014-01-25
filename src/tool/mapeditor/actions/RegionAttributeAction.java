package tool.mapeditor.actions;

import org.eclipse.ui.IWorkbenchWindow;

public class RegionAttributeAction extends AbstractAction {
	IWorkbenchWindow window;

	public RegionAttributeAction(IWorkbenchWindow window){
		super(ICommands.CMD_REGION_ATTRIBUTE, "设置区域属性", "/icons/clip_edit.png");
		this.window = window;
	}
	
	public void run(){
		
	}
}
