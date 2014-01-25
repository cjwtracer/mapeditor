package tool.mapeditor.actions;

public class ExportDataAction extends AbstractAction {

	public ExportDataAction(){
		super(ICommands.CMD_EXPORT_DATA, "地图数据导出");
	}
	
	public void run(){
		mainApp.exportMapDatas();
	}
}
