package tool.mapeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import tool.mapeditor.actions.NewLayerAction;
import tool.mapeditor.actions.RemoveLayerAction;
import tool.resourcemanager.Resources;
import tool.util.TableItemTextModify;

public class LayerView extends ViewPart {

	public static final String ID = "mapeditor.LayerView"; //$NON-NLS-1$
	
	WorldMapEdit mapEdit;
	WorldMapPainter mapPainter;
	NewLayerAction newLayerAction;
	RemoveLayerAction removeLayerAction;
	WindowsHarbor harbor;
	private Composite container;
	private Table table;
	private TableItem modifiedItem;

	public LayerView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK){
					TableItem item = (TableItem) event.item;
					mapEdit.setLayerVisible(item.getChecked(), table.getItemCount() - 1 - table.indexOf(item));
					harbor.redrawMap();
				}else{
					mapEdit.setCurrentLayerIndex(table.getItemCount() - table.getSelectionIndex() - 1);
				}
			}
		});
		final Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem rename = new MenuItem(menu, SWT.PUSH);
		rename.setText("重命名");
		rename.setImage(Resources.getImage("/icons/edit.png"));
		rename.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				new TableItemTextModify(editor, modifiedItem, mapEdit.getLayerName(mapEdit.getCurrentLayerIndex()), 0){
					public boolean doModify(String newText){
						mapEdit.setLayerName(newText);
						return true;
					}
				};
			}
		});
		table.addMenuDetectListener(new MenuDetectListener(){
			public void menuDetected(MenuDetectEvent e){
				TableItem[] is = table.getSelection();
				if(is.length > 0){
					modifiedItem = is[0];
					menu.setLocation(e.x, e.y);
					menu.setVisible(true);
				}
			}
		});

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		newLayerAction = new NewLayerAction();
		removeLayerAction = new RemoveLayerAction();
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(newLayerAction);
		toolbarManager.add(removeLayerAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
	}

	public void setWorldMap(WorldMapEdit edit, WorldMapPainter painter) {
		mapEdit = edit;
		mapPainter = painter;
		table.removeAll();
		if(mapEdit != null){
			int s = mapEdit.getLayerCount();
			for(int i = 0; i < s; ++i)
				addLayer(i);
			mapEdit.setCurrentLayerIndex(s - 1);
			table.setSelection(0);
		}
	}
	
	void createLayer(){
		addLayer(mapEdit.getCurrentLayerIndex() + 1);
	}
	
	private void addLayer(int pos){
		if(pos < 0 || pos > table.getItemCount())
			throw new IllegalArgumentException("Invalid insertion!");
		int index = table.getItemCount() - pos;
		TableItem item = new TableItem(table, SWT.NONE, index);
		item.setText(mapEdit.getLayerName(pos));
		item.setChecked(mapEdit.isLayerVisible(pos));
	}

	public void removeLayer() {
		table.remove(table.getSelectionIndex());
	}

}
