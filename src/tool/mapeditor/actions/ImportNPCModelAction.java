package tool.mapeditor.actions;

public class ImportNPCModelAction extends AbstractAction{
	
	public ImportNPCModelAction(){
		super(ICommands.CMD_IMPORT_NPC_MODEL, "导入NPC模板");
	}
	
	public void run(){
		mainApp.importNPCModel();
	}

}
