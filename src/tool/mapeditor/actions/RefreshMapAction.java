package tool.mapeditor.actions;

public class RefreshMapAction extends AbstractAction {

	public RefreshMapAction() {
		super(ICommands.CMD_REFRESH_MAP, "刷新所有地图", "/icons/refresh_collision.png");
	}
	
	public void run(){
		mainApp.refreshDatas();
	}

}
