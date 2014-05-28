package tool.mapeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import tool.mapeditor.actions.ExitAction;
import tool.mapeditor.actions.ExportAnimationAction;
import tool.mapeditor.actions.ExportDataAction;
import tool.mapeditor.actions.ExportMapAction;
import tool.mapeditor.actions.ExportNPCAction;
import tool.mapeditor.actions.GridAlignAction;
import tool.mapeditor.actions.ImageSpliteAction;
import tool.mapeditor.actions.ImportAnimationAction;
import tool.mapeditor.actions.ImportMapAction;
import tool.mapeditor.actions.ImportNPCModelAction;
import tool.mapeditor.actions.MergeNPCAction;
import tool.mapeditor.actions.MetadataAction;
import tool.mapeditor.actions.NewMapAction;
import tool.mapeditor.actions.NewResourceAction;
import tool.mapeditor.actions.OpenAction;
import tool.mapeditor.actions.OpenExportFileFolderAction;
import tool.mapeditor.actions.PropertyAction;
import tool.mapeditor.actions.RefreshMapAction;
import tool.mapeditor.actions.SaveAction;
import tool.mapeditor.actions.MovableAction;
import tool.resourcemanager.ResourceManager;

import org.eclipse.jface.action.Separator;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	Action newMapAction;
	Action openAction;
	Action saveAction;
	Action exitAction;
	Action gridAlignAction;
//	Action newResourceAction;
//	Action propertyAction;
	Action imageSpliteAction;
	Action openExportFolderAction;
	Action exportDataAction;
	Action exportNPCAction;
	Action mergeNPCAction;
	Action importNPCModelAction;
	Action exportMapAction;
	Action importMapAction;
	Action metadataAction;
	Action refreshMapDataAction;
	Action importAnimationAction;
	Action exportAnimationAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	protected void makeActions(final IWorkbenchWindow window){
		newMapAction = new NewMapAction(window);
		register(newMapAction);
		openAction = new OpenAction(window);
		register(openAction);
		saveAction = new SaveAction();
		register(saveAction);
		exitAction = new ExitAction();
		register(exitAction);
		gridAlignAction = new GridAlignAction();
		register(gridAlignAction);
//		propertyAction = new PropertyAction(window);
//		register(propertyAction);
		imageSpliteAction = new ImageSpliteAction(window);
		register(imageSpliteAction);
		openExportFolderAction = new OpenExportFileFolderAction();
		register(openExportFolderAction);
		exportDataAction = new ExportDataAction();
		register(exportDataAction);
		exportNPCAction = new ExportNPCAction();
		register(exportNPCAction);
		mergeNPCAction = new MergeNPCAction();
		register(mergeNPCAction);
		importNPCModelAction = new ImportNPCModelAction();
		register(importNPCModelAction);
		importMapAction = new ImportMapAction();
		register(importMapAction);
		exportMapAction = new ExportMapAction(window);
		register(exportMapAction);
		metadataAction = new MetadataAction(window);
		register(metadataAction);
		refreshMapDataAction = new RefreshMapAction();
		importAnimationAction = new ImportAnimationAction(window);
		register(importAnimationAction);
		exportAnimationAction = new ExportAnimationAction();
		register(exportAnimationAction);
//		newResourceAction = new NewResourceAction(window);
//		register(newResourceAction);
	}
	
	protected void fillMenuBar(IMenuManager menuBar) {
		menuBar.setVisible(true);
		MenuManager fileMenu = new MenuManager("文件(&F)", "file");
		menuBar.add(fileMenu);
		fileMenu.add(newMapAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(exportDataAction);
		fileMenu.add(exportNPCAction);
		fileMenu.add(mergeNPCAction);
		fileMenu.add(exportAnimationAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		MenuManager editMenu = new MenuManager("编辑(&E)", "edit");
		menuBar.add(editMenu);
		editMenu.add(metadataAction);
		MenuManager viewMenu = new MenuManager("视图(&V)", "view");
		menuBar.add(viewMenu);
		viewMenu.add(new Action("view"){});
		MenuManager mapMenu = new MenuManager("地图(&M)", "map");
		menuBar.add(mapMenu);
		mapMenu.add(importAnimationAction);
		MenuManager layerMenu = new MenuManager("图层(&L)", "layer");
		menuBar.add(layerMenu);
		layerMenu.add(new Action("layer"){});
		MenuManager toolMenu = new MenuManager("工具(&T)", "tool");
		menuBar.add(toolMenu);
		toolMenu.add(imageSpliteAction);
		toolMenu.add(new Separator());
		toolMenu.add(openExportFolderAction);
	}
	
	protected void fillCoolBar(ICoolBarManager coolBar){
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		ToolBarContributionItem toolBarContributionItem = new ToolBarContributionItem(toolbar, "main");
		coolBar.add(toolBarContributionItem);
		toolbar.add(saveAction);
		toolbar.add(openAction);
		toolbar.add(newMapAction);
		toolbar.add(new Separator());
		toolbar.add(gridAlignAction);
//		toolbar.add(propertyAction);
		toolbar.add(new Separator());
		toolbar.add(importMapAction);
		toolbar.add(exportMapAction);
		
		toolbar.add(new Separator());
		toolbar.add(refreshMapDataAction);
	}

}
