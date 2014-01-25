package tool.mapeditor.actions;

public class MergeNPCAction extends AbstractAction {

	public MergeNPCAction(){
		super(ICommands.CMD_MERGE_NPC, "合并NPC数据");
	}
	
	public void run(){
		mainApp.mergeNPCDatas();
	}

}
