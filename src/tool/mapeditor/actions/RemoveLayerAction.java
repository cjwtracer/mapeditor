package tool.mapeditor.actions;

public class RemoveLayerAction extends AbstractAction {
	
	public RemoveLayerAction(){
		super(ICommands.CMD_REMOVE_LAYER, "删除层(&D)", "/icons/trash.png");
	}

	public void run(){
		mainApp.removeLayer();
	}
}
