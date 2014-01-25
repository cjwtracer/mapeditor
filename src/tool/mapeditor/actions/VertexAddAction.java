package tool.mapeditor.actions;

public class VertexAddAction extends AbstractAction {

	public VertexAddAction(){
		super(ICommands.CMD_VERTEX_ADD, "添加顶点", "/icons/vector_add.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.setEditModeVertexAdd(isChecked());
	}
}
