package tool.mapeditor.actions;

public class ExitAction extends AbstractAction {
	
	public ExitAction(){
		super(ICommands.CMD_EXIT, "退出(&E)");
	}
	
	public void run(){
		mainApp.getShell().close();
	}

}
