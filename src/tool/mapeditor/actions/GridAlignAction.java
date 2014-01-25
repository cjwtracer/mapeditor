package tool.mapeditor.actions;

public class GridAlignAction extends AbstractAction {

	public GridAlignAction(){
		super(ICommands.CMD_GRID_ALIGN, "对齐到网格", "/icons/slice.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setGridAlign(isChecked());
	}
}
