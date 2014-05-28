package tool.mapeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import tool.mapeditor.actions.AddAnimGroupAction;
import tool.mapeditor.actions.MapBackgroundAction;
import tool.mapeditor.actions.MapPropertyAction;
import tool.mapeditor.actions.MappingRegionAction;
import tool.mapeditor.actions.MovableAction;
import tool.mapeditor.actions.PlayAnimationAction;
import tool.mapeditor.actions.RectRegionAction;
import tool.mapeditor.actions.ShapeRegionAction;
import tool.mapeditor.actions.VertexAddAction;
import tool.mapeditor.actions.VertexDelAction;
import tool.resourcemanager.Resources;
import tool.util.Constants;
import tool.util.WidgetUtil;

/**
 * 地图面板（主面板）
 * @author caijw
 *
 */
public class MapView extends ViewPart {
	public static final String ID = "mapeditor.MapView";
	
	MovableAction movableAction;
	RectRegionAction rectRegionAction;
	VertexAddAction vertexAddAction;
	VertexDelAction vertexDelAction;
	ShapeRegionAction shapeRegionAction;
	MappingRegionAction mapCollisionAction;
	MapBackgroundAction mapBackgroundAction;
	PlayAnimationAction playAnimationAction;
	MapPropertyAction mapPropertyAction;
	AddAnimGroupAction addAnimGroupAction;
	
	IStatusLineManager statusLine;
	
	List<Action> exclusiveActions;
	
	WindowsHarbor harbor;
	MapCanvas canvas;
	Menu popupMenu;
	WorldMapEdit mapEdit;
	WorldMapPainter mapPainter;
	AnimationPlayer animationPlayer;

	public MapView() {
		super();
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		canvas = MapCanvas.getInstance(container);
		canvas.container = this;
		popupMenu = new Menu(getViewSite().getShell(), SWT.POP_UP);
		popupMenu.setVisible(false);
		canvas.getShell().setMenu(popupMenu);
		statusLine = getViewSite().getActionBars().getStatusLineManager();
		animationPlayer = new AnimationPlayer(canvas){

			@Override
			protected void beforeWait() {
				if(mapEdit != null)
					mapEdit.resetAnimations();
			}

			@Override
			protected void doUpdate() {
				if(mapEdit != null)
					mapEdit.updateAnimations();
			}

			@Override
			protected boolean isPlayStatus() {
				if(mapPainter != null){
					return mapPainter.isPlay();
				}
				return false;
			}

			@Override
			protected void beforeNotify() {
				if(mapEdit != null)
					mapEdit.initAnimations();
			}
			
		};

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		exclusiveActions = new ArrayList<Action>();
		movableAction = new MovableAction();
		exclusiveActions.add(movableAction);
		rectRegionAction = new RectRegionAction();
		exclusiveActions.add(rectRegionAction);
		vertexAddAction = new VertexAddAction();
		exclusiveActions.add(vertexAddAction);
		vertexDelAction = new VertexDelAction();
		exclusiveActions.add(vertexDelAction);
		shapeRegionAction = new ShapeRegionAction();
		exclusiveActions.add(shapeRegionAction);
		mapCollisionAction = new MappingRegionAction();
		exclusiveActions.add(mapCollisionAction);
		mapBackgroundAction = new MapBackgroundAction(window);
		playAnimationAction = new PlayAnimationAction();
		mapPropertyAction = new MapPropertyAction();
		addAnimGroupAction = new AddAnimGroupAction(window);
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(movableAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(rectRegionAction);
		toolbarManager.add(vertexAddAction);
		toolbarManager.add(vertexDelAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(shapeRegionAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(mapCollisionAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(mapBackgroundAction);
		toolbarManager.add(mapPropertyAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(addAnimGroupAction);
		toolbarManager.add(playAnimationAction);
		toolbarManager.add(new Separator());
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
	}
	
	/**
	 * 将面板标题设置为指定的名称
	 * @param title
	 */
	public void setMapTitle(String title){
		setPartName(title);
	}

	public void setWorldMap(WorldMapEdit edit, WorldMapPainter painter) {
		canvas.mapEdit = mapEdit = edit;
		mapPainter = canvas.mapPainter = painter;
		setTitleImage(Resources.getImage("/icons/map.png"));
		setPartName(edit != null ? edit.getMapName() + " [" + edit.getMapID() + "]" : "");
		if(edit != null && painter != null){
			canvas.setContent(painter.getPixelWidth(), painter.getPixelHeight());
		}
		canvas.redraw();
	}

	void uncheckExcept(byte mode) {
		Action a = null;
		switch(mode){
		case WorldMapEdit.MODE_REGION_RECT:
			a = rectRegionAction;
			break;
		case WorldMapEdit.MODE_SHAPE_REGION:
			a = shapeRegionAction;
			break;
		case WorldMapEdit.MODE_VERTEX_ADD:
			a = vertexAddAction;
			break;
		case WorldMapEdit.MODE_VERTEX_DEL:
			a = vertexDelAction;
			break;
		case WorldMapEdit.MODE_MAPPING:
			a = mapCollisionAction;
			break;
		case WorldMapEdit.MODE_MOVE:
			a = movableAction;
			break;
		}
		for(Action action : exclusiveActions){
			if(action == a)
				continue;
			action.setChecked(false);
		}
	}

	void showPopupMenu(final Drawable d) {
		if(d != null){
			Enum<?>[] list = d.getOperationList();
			if(list != null){
				for(MenuItem item : popupMenu.getItems()){
					item.dispose();
				}
				int idx = 0;
				for(final Enum<?> oper : list){
					if(oper.ordinal() == 0){
						new MenuItem(popupMenu, SWT.SEPARATOR);
					}else{
						MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
						item.setText(oper.name());
						item.setEnabled(d.getEditabilities(oper));
						item.addSelectionListener(new SelectionAdapter(){
							public void widgetSelected(SelectionEvent e){
								if(d.operate(oper)){
									canvas.redraw();
								}
							}
						});
					}
					idx++;
				}
				popupMenu.setVisible(true);
			}
		}
	}
	
	public Point getMapCanvasOffset(){
		return new Point(canvas.origin.x, canvas.origin.y);
	}

}