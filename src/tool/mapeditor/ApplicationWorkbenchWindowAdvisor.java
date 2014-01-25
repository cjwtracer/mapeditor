package tool.mapeditor;

import java.io.File;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import tool.mapeditor.application.MainApplication;
import tool.mapeditor.dialogs.CreateWorkspaceDialog;
import tool.resourcemanager.SWTResourceManager;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1280, 800));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle("地图编辑器");
		MainApplication mainApp = MainApplication.getInstance();
		mainApp.verify();
		mainApp.prepareProject();
	}
	
	public void postWindowCreate(){
		MainApplication mainApp = MainApplication.getInstance();
		WindowsHarbor wh = new WindowsHarbor(getWindowConfigurer().getWindow());
		mainApp.setWidgetsHarbor(wh);
		mainApp.launchWorkbench();
		if(!mainApp.isCreateWorkspace())
			mainApp.launchProject();
	}
	
	public void postWindowOpen(){
		MainApplication mainApp = MainApplication.getInstance();
		if(mainApp.isCreateWorkspace()){
			CreateWorkspaceDialog dlg = 
				new CreateWorkspaceDialog(getWindowConfigurer().getWindow().getShell());
			String workspace = dlg.open();
			if(!workspace.equals("")){
				mainApp.createWorkspace(workspace + File.separator + MainApplication.ROOT);
			}else{
				Application.getInstance().stop();
			}
		}
	}
	
	public boolean preWindowShellClose(){
		return MainApplication.getInstance().beforeExit();
	}
	
	public void postWindowClose(){
		MainApplication.getInstance().release();
	}
}
