package tool.mapeditor.actions;

public class PlayAnimationAction extends AbstractAction {
	
	public PlayAnimationAction(){
		super(ICommands.CMD_PLAY_ANIMATION, "演示", "/icons/control_play.png", AS_CHECK_BOX);
	}
	
	public void run(){
		mainApp.playAnimation(isChecked());
	}

}
