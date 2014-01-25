package tool.mapeditor.actions;

public class MapPropertyAction extends AbstractAction {

	public MapPropertyAction(){
		super(ICommands.CMD_MAP_PROPERTY, "编辑地图属性", "/icons/ui.png");
	}
	
	public void run(){
		mainApp.showMapPropertyDialog();
	}

}
