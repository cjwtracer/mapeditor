package tool.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public class WidgetUtil {
	
	/**
	 * Set the location of the given shell to the center of the screen.
	 * @param shell
	 */
	public static void layoutCenter(Shell shell) {
		Display display = Display.getDefault();
		Rectangle rectScreen = display.getBounds();
		Rectangle rectShell = shell.getBounds();
		rectShell.x = (rectScreen.width - rectShell.width) / 2;
		rectShell.y = (rectScreen.height - rectShell.height) / 2;

		shell.setBounds(rectShell);
	}
	
	/**
	 * Set the location of the given shell to the right top corner of the screen.
	 * @param shell
	 */
	public static void layoutRightTop(Shell shell){
		Display display = Display.getDefault();
		Rectangle rectScreen = display.getBounds();
		Rectangle rectShell = shell.getBounds();
		rectShell.x = rectScreen.width - rectShell.width - 10;
		rectShell.y = 50;

		shell.setBounds(rectShell);
	}
	
	/**
	 * Set the location of the shell to the position of mouse(It doesn't perform well).
	 * @param shell
	 */
	public static void layoutToCursor(Shell shell){
		if(shell == null)
			return;
		Point cursorLoc = Display.getCurrent().getCursorLocation();
		shell.setLocation(cursorLoc);
	}
	
	public static Listener getVerifyDigitListener(){
		return new Listener(){
			public void handleEvent(Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
			
		};
	}


	/*
	 * Deprecated for ant compile errors
	@SuppressWarnings("unchecked")
	protected static <T extends Object, E extends Object> T getTextValue(String val, Class<?> c, E... args) {

		T t = null;
		if (c.equals(Long.class)) {
			try {
				t = (T) Long.valueOf(val);
			} catch (Exception e) {
				t = (T) new Long(0);
			}
		} else if (c.equals(Double.class)) {
			try {
				t = (T) Double.valueOf(val);
			} catch (Exception e) {
				t = (T) new Double(0);
			}
		} else if (c.equals(Integer.class)) {
			try {
				t = (T) Integer.valueOf(val);
			} catch (Exception e) {
				t = (T) new Integer(0);
			}
		} else if (c.equals(Date.class)) {
			try {
				t = (T) ((SimpleDateFormat) args[1]).parse(val);
			} catch (Exception e) {				
			}
		} else if (c.equals(String.class)) {
			t = (T) val;
		}
		return (T) t;
	}
	*/
	
	/**
	 * Check the if the widget is null or disposed or not.
	 * @param w
	 * @return
	 */
	public static boolean valid(Widget w) {
		return (w != null && !w.isDisposed());
	}
	
	public static Listener getNumericVerifyListener(){
		return new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		};
	}
	
	public static boolean dispose(Widget w){
		boolean suc = true;
		try{
			if(valid(w)){
				w.dispose();
				w = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			suc = false;
		}
		return suc;
	}
	
	/**
	 * Get the result of the given filter type and dialog type.
	 * @param parent
	 * @param fileType
	 * @param dialogType
	 * @return
	 */
	public static String openFileDialog(Shell parent, String fileType, int dialogType){
		FileDialog dlg = new FileDialog(parent, dialogType);
		dlg.setFilterExtensions(new String[]{fileType});
		return dlg.open();
	}

	/**
	 * Utility method of getting combo in a table.
	 * @param table
	 * @param i
	 * @param column
	 * @param options
	 * @return
	 */
	public static CCombo getTableCombo(Table table, TableItem i, int column, String[] options) {
		if(table == null || i == null || column < 0 || options == null)
			throw new IllegalArgumentException();
		TableEditor editor = new TableEditor (table);
		CCombo combo = new CCombo(table, SWT.NONE);
		combo.setEditable(false);
		for(String s : options){
			if(s == null)
				s = "";
			combo.add(s);
		}
		editor.grabHorizontal = true;
		editor.setEditor(combo, i, column);
		return combo;
	}

	/**
	 * Utility method of getting button in a table.
	 * @param table
	 * @param tableItem
	 * @param column
	 * @param text
	 * @return
	 */
	public static Button getTableButton(Table table, TableItem tableItem, int column, String text) {
		if(table == null || tableItem == null || column < 0)
			throw new IllegalArgumentException();
		TableEditor editor = new TableEditor(table);
		Button btn = new Button(table, SWT.PUSH);
		btn.setSize(btn.computeSize(table.getItemHeight(), SWT.DEFAULT));
		btn.setText(text);
		editor.minimumWidth = btn.getSize().x;
		editor.horizontalAlignment = SWT.RIGHT;
		editor.setEditor(btn, tableItem, column);
		return btn;
	}
	
	/**
	 * Utility method of getting checkbox in a table.
	 * @param table
	 * @param tableItem
	 * @param column
	 * @param checked
	 * @return
	 */
	public static Button getTableCheckBox(Table table, TableItem tableItem, int column, boolean checked){
		if(table == null || tableItem == null || column < 0)
			throw new IllegalArgumentException();
		TableEditor editor = new TableEditor(table);
		Button btn = new Button(table, SWT.CHECK);
		btn.setSize(btn.computeSize(table.getItemHeight(), SWT.DEFAULT));
		btn.setSelection(checked);
		editor.minimumWidth = btn.getSize().x;
		editor.horizontalAlignment = SWT.LEFT;
		editor.setEditor(btn, tableItem, column);
		return btn;
	}
	
	/**
	 * Utility method of getting file dialog filtering image files.
	 * @param shell
	 * @param type
	 * @return
	 */
	public static FileDialog imageDialog(Shell shell, int type){
		if(!(type == SWT.OPEN || type == SWT.SAVE))
			return null;
		FileDialog dlg = new FileDialog(shell, type);
		dlg.setFilterExtensions(new String[] {"*.gif;*.png;*.xpm;*.jpg;*.jpeg;"});
		dlg.setFilterNames(new String[] { "图片文件"});
		return dlg;
	}
}
