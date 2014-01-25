package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import tool.mapeditor.application.MainApplication;
import tool.model.MapLayer;
import tool.model.Property;
import tool.model.Tile;
import tool.model.WorldMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

public class TilePanel extends Composite {
	WorldMap map;
	private Table table;
	int type;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TilePanel(Composite parent, WorldMap map, int type) {
		super(parent, SWT.NONE);
		this.map = map;
		this.type = type;
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		layout.horizontalSpacing = 10;
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableSelected();
			}
		});
		TableColumn clmLayer = new TableColumn(table, SWT.NONE);
		clmLayer.setText("所在层");
		clmLayer.setWidth(50);
		TableColumn clmBounds = new TableColumn(table, SWT.NONE);
		clmBounds.setText("位置信息");
		clmBounds.setWidth(150);
		createColumns();
		buildTable();
		new Label(this, SWT.NONE);
		
		Button btnEdit = new Button(this, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 81;
		btnEdit.setLayoutData(gd_btnNewButton);
		btnEdit.setText("属性");
		btnEdit.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int idx = table.getSelectionIndex();
				if(idx != -1){
					Tile t = (Tile)table.getItem(idx).getData();
					PropertyEditDialog dlg = new PropertyEditDialog(getShell(), t, "砖块属性");
					dlg.open();
					buildTable();
				}
			}
		});
	}
	
	void createColumns(){
		for(String s : Tile.propNames(type)){
			TableColumn clm = new TableColumn(table, SWT.NONE);
			clm.setText(s);
			clm.setWidth(75);
		}
	}
	
	void buildTable(){
		table.removeAll();
		int index = 0;
		int tilesLen = map.getWidth() * map.getHeight();
		for(MapLayer ly : map.getLayers()){
			for(int i = 0; i < tilesLen; ++i){
				Tile t = ly.getTile(i);
				if(t.getType() == type){
					TableItem itm = new TableItem(table, SWT.NONE);
					itm.setText(0, String.valueOf(index));
					itm.setText(1, t.getJ() + " , " + t.getI());
					int k = 2;
					for(Property p : t.getProperties()){
						itm.setText(k++, p.getValue().toString());
					}
					itm.setData(t);
				}
			}
			++index;
		}
	}
	
	void tableSelected(){
		int i = table.getSelectionIndex();
		if(i != -1){
			TableItem itm = table.getItem(i);
			Tile t = (Tile)itm.getData();
			int w = map.getTileWidth(),
				h = map.getTileHeight();
			MainApplication.getInstance().showInView(t.getJ() * w, t.getI() * h, w, h);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
