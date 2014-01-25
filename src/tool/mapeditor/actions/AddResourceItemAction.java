package tool.mapeditor.actions;

public class AddResourceItemAction extends AbstractAction {

	public AddResourceItemAction() {
		super(ICommands.CMD_ADD_RESOURCE, "添加单个元素", "/icons/picture_add.png");
	}
	
	public void run(){
		mainApp.addResourceItem();
	}

}
