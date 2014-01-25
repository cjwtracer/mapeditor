package tool.mapeditor.actions;

public class VertexDelAction extends AbstractAction {

	public VertexDelAction(){
		super(ICommands.CMD_VERTEX_DEL, "删除顶点", "/icons/vector_delete.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setEditModeVertexDel(isChecked());
	}
}
