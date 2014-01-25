package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.MapDescriptor;
import tool.model.MapLayer;
import tool.model.Property;
import tool.model.RegionPolygon;
import tool.model.WorldMap;

public class RegionPanel extends Composite {
	private Table table;
	WorldMap map;
	int type;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RegionPanel(Composite parent, WorldMap map, int type) {
		super(parent, SWT.NONE);
		this.map = map;
		this.type = type;
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableSelected();
			}
		});
		TableColumn clmLayer = new TableColumn(table, SWT.NONE);
		clmLayer.setText("所在层");
		clmLayer.setWidth(100);
		TableColumn clmBounds = new TableColumn(table, SWT.NONE);
		clmBounds.setText("传送区域");
		clmBounds.setWidth(150);
		createColumns();
		buildTable();

		new Label(this, SWT.NONE);
		Button btnProp = new Button(this, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 84;
		btnProp.setLayoutData(gd_btnNewButton);
		btnProp.setText("属性");
		btnProp.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int idx = table.getSelectionIndex();
				if(idx >= 0){
					RegionPolygon r = (RegionPolygon)table.getItem(idx).getData();
					PropertyEditDialog dlg = new PropertyEditDialog(getShell(), r, "区域属性");
					dlg.open();
					buildTable();
				}
			}
		});

	}
	
	void createColumns(){
		for(String s : RegionPolygon.propNames(type)){
			TableColumn clm = new TableColumn(table, SWT.NONE);
			clm.setText(s);
			clm.setWidth(75);
		}
	}
	
	void buildTable(){
		table.removeAll();
		int index = 0;
		for(MapLayer layer : map.getLayers()){
			for(RegionPolygon r : layer.getRegions()){
				if(r.isType(type)){
					TableItem i = new TableItem(table, SWT.NONE);
					i.setText(0, String.valueOf(index));
					i.setText(1, r.getBounds().toString());
					int k = 2;
					for(Property p : r.getProperties()){
						i.setText(k++, p.getValue().toString());
					}
					i.setData(r);
				}
			}
			++index;
		}
	}
	
	void tableSelected(){
		int i = table.getSelectionIndex();
		if(i != -1){
			TableItem item = table.getItem(i);
			RegionPolygon r = (RegionPolygon)item.getData();
			Rectangle bounds = r.getBounds();
			int tw = map.getTileWidth(), th = map.getTileHeight();
			bounds.x *= tw;
			bounds.y *= th;
			bounds.width *= tw;
			bounds.height *= th;
			MainApplication.getInstance().showInView(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
