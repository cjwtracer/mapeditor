package tool.mapeditor.actions;

public class ShapeRegionAction extends AbstractAction {

	public ShapeRegionAction(){
		super(ICommands.CMD_SHAPE_REGION, "选择区域", "/icons/shape_handles.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setEditModeRegionShape(isChecked());
	}
}
