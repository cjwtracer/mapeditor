package tool.mapeditor.actions;

public class MappingRegionAction extends AbstractAction {

	public MappingRegionAction(){
		super(ICommands.CMD_MAP_COLLISION, "映射/取消区域到砖块", "/icons/collide_5.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.mappingRegion(isChecked());
	}
}
