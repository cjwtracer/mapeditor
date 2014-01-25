package tool.mapeditor.actions;

public class ExportNPCAction extends AbstractAction {

	public ExportNPCAction(){
		super(ICommands.CMD_EXPORT_NPC, "NPC数据导出");
	}
	
	public void run(){
		mainApp.exportNPCDatas();
	}

}
