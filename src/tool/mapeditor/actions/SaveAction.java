package tool.mapeditor.actions;

public class SaveAction extends AbstractAction {
	
	public SaveAction(){
		super(ICommands.CMD_SAVE, "保存(&S)", "/icons/save.png");
	}
	
	public void run(){
		mainApp.saveProject();
		mainApp.inform("项目已保存");
	}

}
