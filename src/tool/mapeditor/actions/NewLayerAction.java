package tool.mapeditor.actions;

public class NewLayerAction extends AbstractAction {
	
	public NewLayerAction(){
		super(ICommands.CMD_NEW_LAYER, "添加层(&L)", "/icons/picture_add.png");
	}
	
	public void run(){
		mainApp.createMapLayer();
	}
}
