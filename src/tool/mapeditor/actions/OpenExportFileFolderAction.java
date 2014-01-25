package tool.mapeditor.actions;

import java.io.IOException;

public class OpenExportFileFolderAction extends AbstractAction {

	public OpenExportFileFolderAction() {
		super(ICommands.CMD_OPEN_EXPORT_FOLDER, "打开数据文件文件夹");
	}
	
	public void run(){
		try {
			Runtime.getRuntime().exec("explorer " + mainApp.getProject().getExportDir());
		} catch (IOException e) {
			mainApp.alert("fail to open export folder");
			e.printStackTrace();
		}
	}

}
