package tool.mapeditor.actions;

public class ImportMapAction extends AbstractAction {
	
	public ImportMapAction(){
		super(ICommands.CMD_IMPORT_MAP, "导入地图", "/icons/import.png");
	}
	
	public void run(){
		mainApp.importMap();
	}

}
