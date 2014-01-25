package tool.mapeditor.actions;

public class MovableAction extends AbstractAction {
	
	public MovableAction(){
		super(ICommands.CMD_SELECT, "移动元素", "/icons/cursor.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setEditModeSelect(isChecked());
	}

}
