package tool.mapeditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ResourceViewResizeAction extends AbstractAction {

	public ResourceViewResizeAction() {
		super(ICommands.CMD_RESOURCEVIEW_RESIZE, "调整图片显示大小", "/icons/zoom_out.png", Action.AS_DROP_DOWN_MENU);
		setMenuCreator(new IMenuCreator(){

			@Override
			public void dispose() {}

			@Override
			public Menu getMenu(Control parent) {
				final Menu menu = new Menu(parent);
				final MenuItem itemSizeReal = new MenuItem(menu, SWT.CHECK);
				itemSizeReal.setText("实际大小");
				itemSizeReal.addListener(SWT.Selection, new Listener(){

					@Override
					public void handleEvent(Event e) {
						for(MenuItem i : menu.getItems()){
							i.setSelection(i == itemSizeReal);
						}
						mainApp.resizeResourceViewImages(false);
					}
					
				});
				final MenuItem itemSizeVisible = new MenuItem(menu, SWT.CHECK);
				itemSizeVisible.setText("适合可见");
				itemSizeVisible.addListener(SWT.Selection, new Listener(){

					@Override
					public void handleEvent(Event arg0) {
						for(MenuItem i : menu.getItems()){
							i.setSelection(i == itemSizeVisible);
						}
						mainApp.resizeResourceViewImages(true);
					}
					
				});
				
				return menu;
			}

			@Override
			public Menu getMenu(Menu arg0) {
				return null;
			}
			
		});
	}

}
