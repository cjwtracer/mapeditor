package tool.mapeditor.actions;

public class DeleteSourceAction extends AbstractAction {
	
	public DeleteSourceAction(){
		super(ICommands.CMD_DELETE_SOURCE, "删除元素集", "/icons/trash.png");
	}
	
	public void run(){
		mainApp.deleteResourceSet();
	}

}
