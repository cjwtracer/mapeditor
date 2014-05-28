package tool.mapeditor.dialogs.mapProperties;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.GridData;

import tool.mapeditor.application.MainApplication;
import tool.model.WorldMap;
import tool.util.StringUtil;
import tool.util.TableItemTextModify;

public class FixedPropsPanel extends Composite {
	MainApplication app = MainApplication.getInstance();
	
	private Table table;
	WorldMap map;
	TableEditor editor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FixedPropsPanel(Composite parent, WorldMap map) {
		super(parent, SWT.NONE);
		this.map = map;
		
		GridLayout layout = new GridLayout(1, false);
		setLayout(layout);
		layout.horizontalSpacing = 10;
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				final int idx = table.getSelectionIndex();
				if(idx == -1)
					return;
				TableItem i = table.getItem(idx);
				new TableItemTextModify(editor, i, i.getText(1), 1){
					public boolean doModify(String newText) {
						return editProps(idx, newText);
					}
				};
			}
		});
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		TableColumn clmKey = new TableColumn(table, SWT.NONE);
		clmKey.setText("属性");
		clmKey.setWidth(200);
		TableColumn clmValue = new TableColumn(table, SWT.NONE);
		clmValue.setText("值");
		clmValue.setWidth(200);
		
		buildTable();

	}
	
	private void buildTable(){
		table.removeAll();
		TableItem itm0 = new TableItem(table, SWT.NONE);
		itm0.setText(0, "地图ID");
		itm0.setText(1, String.valueOf(map.getExpID()));
		TableItem itm1 = new TableItem(table, SWT.NONE);
		itm1.setText(0, "地图名称");
		itm1.setText(1, map.getName());
		TableItem itm2 = new TableItem(table, SWT.NONE);
		itm2.setText(0, "地图背景ID");
		itm2.setText(1, String.valueOf(map.getBackgroundID()));
		TableItem itm3 = new TableItem(table, SWT.NONE);
		itm3.setText("图块宽");
		itm3.setText(1, String.valueOf(map.getCellWidth()));
		TableItem itm4 = new TableItem(table, SWT.NONE);
		itm4.setText("图块高");
		itm4.setText(1, String.valueOf(map.getCellHeight()));
	}
	
	boolean editProps(int index, String newText){
		boolean b = true;
		switch(index){
		case 0:
			if(StringUtil.isNumeric(newText)){
				int id = StringUtil.toNum(newText);
				for(WorldMap m : app.getProject().getMapGroup().values()){
					if(m == map)
						continue;
					if(m.getExpID() == id){
						app.alert("地图<" + m.getName() + ">有相同ID！");
						return false;
					}
				}
				map.setExpID(id);
				app.resetMapExpID(id);
			}else{
				b = false;
			}
			break;
		case 1:
			map.setName(newText);
			MainApplication.getInstance().resetMapName(newText);
			break;
		case 2:
			if(StringUtil.isNumeric(newText)){
				map.setBackgroundID(StringUtil.toNum(newText));
			}else{
				b = false;
			}
			break;
		case 3:
			if(StringUtil.isNumeric(newText)){
				map.setCellWidth(StringUtil.toNum(newText));
			}else{
				b = false;
			}
			break;
		case 4:
			if(StringUtil.isNumeric(newText)){
				map.setCellHeight(StringUtil.toNum(newText));
			}else{
				b = false;
			}
			break;
		}
		return b;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
