package tool.mapeditor.actions;

public class RectRegionAction extends AbstractAction {
	
	public RectRegionAction(){
		super(ICommands.CMD_RECT_REGION, "添加矩形区域", "/icons/shape_square_add.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setEditModeRegionRect(isChecked());
	}

}
