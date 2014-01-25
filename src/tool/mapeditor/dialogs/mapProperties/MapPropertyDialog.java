package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import tool.mapeditor.application.MainApplication;
import tool.model.MapLayer;
import tool.model.WorldMap;
import tool.util.WidgetUtil;

public class MapPropertyDialog extends Dialog{
	final static String SEP = "|";
	final static String PROP_FIX = "固有属性";
	final static String PROP_DEF = "自定义属性";
//	final static String[] PROP_LIST = {, "", "NPC", "传送区域"};
	
	static PropHelper propTile;
	static PropHelper propUnit;
	static PropHelper propRegion;
	
	static void loadProps(){
		String dir = MainApplication.getInstance().getProject().getConfigDir();
		propTile = new PropHelper(dir + "tile.config");
		propUnit = new PropHelper(dir + "unit.config");
		propRegion = new PropHelper(dir + "region.config");
	}
	
	static boolean propLoaded;

	protected Object result;
	protected Shell shell;
	Composite rightPanel;
	Composite lastPanel;
	TreeItem lastItem;
	Tree tree;
	WorldMap map;
	int layerIdx;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public MapPropertyDialog(Shell parent, WorldMap map, int layerIdx) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		if(map == null)
			throw new IllegalArgumentException("map can't be null!");
		this.map = map;
		this.layerIdx = layerIdx;
		if(!propLoaded){
			loadProps();
		}
		//update tile
		int tilesLen = map.getWidth() * map.getHeight();
		for(MapLayer ly : map.getLayers()){
			for(int i = 0; i < tilesLen; ++i){
				ly.getTile(i).updateProps();
			}
		}
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetUtil.layoutRightTop(shell);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	/**
	 * Create contents of the dialog.
	 */
	protected void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(812, 484);
		shell.setText("地图属性");
		shell.setLayout(new GridLayout(2, false));
		
		tree = new Tree(shell, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		tree.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				treeSelected();
			}
		});
		buildTree();
		
		rightPanel = new Composite(shell, SWT.NONE);
		rightPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		rightPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
	}
	
	void setContent(Composite panel) {
		if(WidgetUtil.valid(lastPanel))
			lastPanel.dispose();
		lastPanel = panel;
	}

	void buildTree(){
		tree.removeAll();
		TreeItem i = new TreeItem(tree, SWT.NONE);
		i.setText(PROP_FIX);
		i = new TreeItem(tree, SWT.NONE);
		i.setText(PROP_DEF);
		createPropItems(propTile);
		createPropItems(propUnit);
		createPropItems(propRegion);
	}
	
	void createPropItems(PropHelper prop){
		int k = 0;
		for(String s : prop.nameList){
			TreeItem i = new TreeItem(tree, SWT.NONE);
			i.setText(s);
			i.setData(new ItemDataHelper(prop, prop.typeList[k]));
			++k;
		}
	}

	void treeSelected() {
		TreeItem[] items = tree.getSelection();
		if(items.length == 0 || lastItem == items[0])
			return;
		lastItem = items[0];
		Object data = items[0].getData();
		if(data == null){
			String s = lastItem.getText();
			if(s.equals(PROP_FIX)){
				setContent(new FixedPropsPanel(rightPanel, map));
			}else if(s.equals(PROP_DEF)){
				setContent(new DynamicPropsPanel(rightPanel, map));
			}
		}else if(data instanceof ItemDataHelper){
			ItemDataHelper v = (ItemDataHelper)data;
			if(v.prop == propTile){
				setContent(new TilePanel(rightPanel, map, v.type));
			}else if(v.prop == propUnit){
				setContent(new UnitPanel(rightPanel, map, v.type));
			}else if(v.prop == propRegion){
				setContent(new RegionPanel(rightPanel, map, v.type));
			}
		}
		rightPanel.layout();
	}
}
