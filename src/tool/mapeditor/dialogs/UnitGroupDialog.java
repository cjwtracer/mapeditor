package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tool.mapeditor.application.MainApplication;
import tool.model.Animation;
import tool.model.Unit;
import tool.model.UnitGroup;
import tool.util.WidgetUtil;

import org.eclipse.swt.layout.FillLayout;

public class UnitGroupDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	UnitGroup unitGroup;
	private Table tableAction;
	MainApplication app = MainApplication.getInstance();

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public UnitGroupDialog(Shell parent, UnitGroup unitGroup) {
		super(parent, SWT.APPLICATION_MODAL);
		if(unitGroup == null)
			throw new IllegalArgumentException("unit group is null");
		setText("原型属性设置（" + unitGroup.getName() + "）");
		this.unitGroup = unitGroup;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetUtil.layoutCenter(shell);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.CLOSE | SWT.APPLICATION_MODAL);
		shell.setSize(823, 300);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		tableAction = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		tableAction.setHeaderVisible(true);
		tableAction.setLinesVisible(true);
		final TableEditor editorAction = new TableEditor(tableAction);
		editorAction.horizontalAlignment = SWT.LEFT;
		editorAction.grabHorizontal = true;
		tableAction.addListener(SWT.MouseDoubleClick, new Listener(){
			public void handleEvent(Event event){
				Rectangle clientArea = tableAction.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = tableAction.getTopIndex ();
				while (index < tableAction.getItemCount ()) {
					boolean visible = false;
					final TableItem item = tableAction.getItem (index);
					for (int i=0; i<tableAction.getColumnCount (); i++) {
						Rectangle rect = item.getBounds (i);
						if (editableActionColumn(i) && rect.contains (pt)) {
							final int column = i;
							final Text text = new Text (tableAction, SWT.NONE);
							Listener textListener = new Listener () {
								@Override
								public void handleEvent (final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											item.setText (column, editAction(column, text.getText (), item.getText(column)));
											text.dispose ();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText (column, editAction(column, text.getText (), item.getText(column)));
													break;
												case SWT.TRAVERSE_ESCAPE:
													e.doit = false;
													break;
											}
											text.dispose ();
											break;
									}
								}
							};
							text.addListener (SWT.FocusOut, textListener);
							text.addListener (SWT.Traverse, textListener);
							editorAction.setEditor (text, item, i);
							text.setText (item.getText (i));
							text.selectAll ();
							text.setFocus ();
							return;
						}
						if (!visible && rect.intersects (clientArea)) {
							visible = true;
						}
					}
					if (!visible) return;
					index++;
				}
			}
		});
		TableColumn clmID = new TableColumn(tableAction, SWT.NONE);
		clmID.setText("id(图片)");
		clmID.setWidth(70);
		TableColumn clmName = new TableColumn(tableAction, SWT.NONE);
		clmName.setText("name");
		clmName.setWidth(70);
		TableColumn clmTarget = new TableColumn(tableAction, SWT.NONE);
		clmTarget.setText("target");
		clmTarget.setWidth(70);
		TableColumn clmDir = new TableColumn(tableAction, SWT.NONE);
		clmDir.setText("hasDir");
		clmDir.setWidth(70);
		TableColumn clmLayerPos = new TableColumn(tableAction, SWT.NONE);
		clmLayerPos.setText("layerPos");
		clmLayerPos.setWidth(120);
		TableColumn clmDelay = new TableColumn(tableAction, SWT.NONE);
		clmDelay.setText("delay");
		clmDelay.setWidth(70);
		TableColumn clmOffx = new TableColumn(tableAction, SWT.NONE);
		clmOffx.setText("offx");
		clmOffx.setWidth(70);
		TableColumn clmOffy = new TableColumn(tableAction, SWT.NONE);
		clmOffy.setText("offy");
		clmOffy.setWidth(70);
		TableColumn clmActionID = new TableColumn(tableAction, SWT.NONE);
		clmActionID.setText("actionid");
		clmActionID.setWidth(70);
		TableColumn clmRepeat = new TableColumn(tableAction, SWT.NONE);
		clmRepeat.setText("repeat");
		clmRepeat.setWidth(70);
		TableColumn clmFly = new TableColumn(tableAction, SWT.NONE);
		clmFly.setText("isfly");
		clmFly.setWidth(70);

		buildTable();
	}
	
	void buildTable(){
		tableAction.removeAll();
		for(Control c : tableAction.getChildren()){
			c.dispose();
		}
		
		for(int i = 0, len = unitGroup.getComponents().size(); i < len; i++){
			Unit cell = unitGroup.getComponents().get(i);
			TableItem item = new TableItem(tableAction, SWT.NONE);
			Animation anim = cell.getAnimation();
			if(anim == null){
				cell.setAnimation(app.getProject().getAnimationGroup().get(cell.getAnimationID()));
			}
			final Animation ac = cell.getAnimation();
			if(ac == null)
				continue;
			item.setText(ac.getFramesResource());
			item.setText(1, ac.getName());
			TableEditor editor = new TableEditor(tableAction);
			editor.grabHorizontal = true;
			String s = "";
			if(ac.target == 0){
				s = "施法者";
			}else if(ac.target == 1){
				s = "目标";
			}
			item.setText(2, s);
			final CCombo c2 = new CCombo(tableAction, SWT.NONE);
			c2.setEditable(false);
			c2.setItems(new String[]{"施法者", "目标"});
			c2.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					switch(c2.getSelectionIndex()){
					case 0:
						ac.target = 0;
						break;
					case 1:
						ac.target = 1;
						break;
					}
				}
			});
			c2.setText(s);
			editor.setEditor(c2, item, 2);
			editor = new TableEditor(tableAction);
			editor.grabHorizontal = true;
			editor.horizontalAlignment = SWT.CENTER;
			s = "";
			if(ac.hasDir){
				s = "是";
			}else{
				s = "否";
			}
			item.setText(3, s);
			final CCombo c3 = new CCombo(tableAction, SWT.NONE);
			c3.setEditable(false);
			c3.setItems(new String[]{"是", "否"});
			c3.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					switch(c3.getSelectionIndex()){
					case 0:
						ac.hasDir = true;
						break;
					case 1:
						ac.hasDir = false;
						break;
					}
				}
			});
			c3.setText(s);
			editor.setEditor(c3, item, 3);
			editor = new TableEditor(tableAction);
			editor.grabHorizontal = true;
			editor.horizontalAlignment = SWT.CENTER;
			s = "";
			if(ac.layerPos == -1){
				s = "所有场景元素下层";
			}else if(ac.layerPos == 0){
				s = "人物下层";
			}else if(ac.layerPos == 1){
				s = "人物上层";
			}else if(ac.layerPos == 2){
				s = "所有场景元素上层";
			}
			item.setText(4, s);
			final CCombo c4 = new CCombo(tableAction, SWT.NONE);
			c4.setEditable(false);
			c4.setItems(new String[]{"所有场景元素下层", "人物下层", "人物上层", "所有场景元素上层"});
			c4.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					switch(c4.getSelectionIndex()){
					case 0:
						ac.layerPos = -1;
						break;
					case 1:
						ac.layerPos = 0;
						break;
					case 2:
						ac.layerPos = 1;
						break;
					case 3:
						ac.layerPos = 2;
						break;
					}
				}
			});
			c4.setText(s);
			editor.setEditor(c4, item, 4);
			item.setText(5, String.valueOf(ac.delay));
			item.setText(6, String.valueOf(unitGroup.getOffset(i)[0]));
			item.setText(7, String.valueOf(-unitGroup.getOffset(i)[1]));
			item.setText(8, String.valueOf(ac.actionID));
			editor = new TableEditor(tableAction);
			editor.grabHorizontal = true;
			editor.horizontalAlignment = SWT.CENTER;
			s = "";
			if(ac.repeat){
				s = "是";
			}else{
				s = "否";
			}
			item.setText(9, s);
			final CCombo c9 = new CCombo(tableAction, SWT.NONE);
			c9.setItems(new String[]{"是", "否"});
			c9.setEditable(false);
			c9.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					switch(c9.getSelectionIndex()){
					case 0:
						ac.repeat = true;
						break;
					case 1:
						ac.repeat = false;
						break;
					}
				}
			});
			c9.setText(s);
			editor.setEditor(c9, item, 9);
			editor = new TableEditor(tableAction);
			editor.grabHorizontal = true;
			editor.horizontalAlignment = SWT.CENTER;
			s = "";
			if(ac.isFly){
				s = "是";
			}else{
				s = "否";
			}
			item.setText(10, s);
			final CCombo c10 = new CCombo(tableAction, SWT.NONE);
			c10.setItems(new String[]{"是", "否"});
			c10.setEditable(false);
			c10.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					switch(c10.getSelectionIndex()){
					case 0:
						ac.isFly = true;
						break;
					case 1:
						ac.isFly = false;
						break;
					}
				}
			});
			c10.setText(s);
			editor.setEditor(c10, item, 10);
			
			item.setData(cell);
		}
	}
	
	boolean editableActionColumn(int i) {
		switch(i){
		case 0:
		case 1:
		case 5:
		case 6:
		case 7:
		case 8:
			return true;
		}
		return false;
	}
	
	String editAction(int column, String s, String former){
		int index = tableAction.getSelectionIndex();
		TableItem i = tableAction.getItem(index);
		Unit u = (Unit)i.getData();
		Animation action = u.getAnimation();
		switch(column){
		case 0:
			action.setFramesResource(s);
			return s;
		case 1:
			action.setName(s);
			return s;
		case 5:
			String delay = null;
			try{
				double d = Double.valueOf(s);
				action.delay = (int)d;
				delay = s;
			}catch(NumberFormatException e){
				delay = former;
			}
			return delay;
		case 6:
			String offx = null;
			try{
				double d = Double.valueOf(s);
				unitGroup.getOffset(index)[0] = (int)Math.round(d);
				offx = String.valueOf(d);
			}catch(NumberFormatException e){
				offx = former;
			}
			return offx;
		case 7:
			String offy = null;
			try{
				double d = Double.valueOf(s);
				unitGroup.getOffset(index)[1] = (int)Math.round(d);
				offy = String.valueOf(d);
			}catch(NumberFormatException e){
				offy = former;
			}
			return offy;
		case 8:
			String actionid = null;
			try{
				int d = Integer.valueOf(s);
				action.actionID = d;
				actionid = s;
			}catch(NumberFormatException e){
				actionid = former;
			}
			return actionid;
		}
		return former;
	}

}
