package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tool.io.DataCrackException;
import tool.io.ProjectWriter;
import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.MapDescriptor;
import tool.mapeditor.application.MapProject;
import tool.model.WorldMap;
import tool.resourcemanager.Resources;
import tool.util.WidgetUtil;

public class NewMapDialog extends Dialog {
	

	protected WorldMap worldMap;
	protected Shell shell;
	Text textMapName;
	Text textMapWidth;
	Text textMapHeight;
	Text textTileWidth;
	Text textTileHeight;
	Button radioRectangle;
	Button radioIsometric;
	Label labelHead;
	
	String textError = "";
	private Text txtCellW;
	private Text txtCellH;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public NewMapDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("新建地图");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public WorldMap open() {
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
		return worldMap;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(380, 340);
		shell.setText(getText());
		{
			labelHead = new Label(shell, SWT.RIGHT);
			labelHead.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setForeground(Resources.RED);
					e.gc.drawString(textError, 10, 20);
				}
			});
			labelHead.setImage(Resources.getImage("/icons/dialog_new_map.png"));
			labelHead.setBounds(0, 0, 374, 57);
		}
		{
			Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(0, 57, 374, 2);
		}
		
		{
			Label labelName = new Label(shell, SWT.NONE);
			labelName.setBounds(10, 68, 24, 17);
			labelName.setText("名称");
		}
		textMapName = new Text(shell, SWT.BORDER);
		textMapName.setBounds(50, 63, 324, 22);
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(10, 91);
			label.setSize(48, 17);
			label.setText("类型");
		}
		radioRectangle = new Button(shell, SWT.RADIO);
		radioRectangle.setSelection(true);
		radioRectangle.setLocation(82, 91);
		radioRectangle.setSize(65, 17);
		radioRectangle.setText("正视");
		radioIsometric = new Button(shell, SWT.RADIO);
		radioIsometric.setLocation(177, 91);
		radioIsometric.setSize(65, 17);
		radioIsometric.setText("斜视");
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(10, 114);
			label.setSize(54, 17);
			label.setText("地图宽");
		}
		textMapWidth = new Text(shell, SWT.BORDER);
		textMapWidth.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(textMapHeight.getData() == null || textMapHeight.getData().toString().isEmpty()) {			
					textMapHeight.setText(textMapWidth.getText());
				}
			}
		});
		textMapWidth.setLocation(70, 114);
		textMapWidth.setSize(73, 22);
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(148, 114);
			label.setSize(24, 17);
			label.setText("格");
		}
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(194, 114);
			label.setSize(51, 17);
			label.setText("地图高");
		}
		
		Label lbCellW = new Label(shell, SWT.NONE);
		lbCellW.setBounds(10, 199, 48, 17);
		lbCellW.setText("图块宽");
		
		Label lbCellH = new Label(shell, SWT.NONE);
		lbCellH.setBounds(194, 199, 48, 17);
		lbCellH.setText("图块高");
		
		txtCellW = new Text(shell, SWT.BORDER);
		txtCellW.setBounds(70, 196, 73, 23);
		txtCellW.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				txtCellH.setText(txtCellW.getText());
			}
		});
		
		txtCellH = new Text(shell, SWT.BORDER);
		txtCellH.setBounds(261, 196, 73, 23);
//		txtCellH.addKeyListener(new KeyAdapter() {
//			public void keyReleased(KeyEvent e) {
//				txtCellH.setText(txtCellW.getText());
//			}
//		});
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(148, 157, 24, 17);
		label_1.setText("像素");
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(340, 154, 24, 17);
		label_2.setText("像素");
		textMapHeight = new Text(shell, SWT.BORDER);
		textMapHeight.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				textMapHeight.setData(textMapHeight.getText());
			}
		});
		textMapHeight.setLocation(261, 114);
		textMapHeight.setSize(73, 22);
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(340, 114);
			label.setSize(24, 17);
			label.setText("格");
		}
		{
			Label label = new Label(shell, SWT.NONE);
			label.setBounds(10, 157, 48, 17);
			label.setText("网格宽");
		}
		textTileWidth = new Text(shell, SWT.BORDER);
		textTileWidth.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(textTileHeight.getData() == null || textTileHeight.getData().toString().isEmpty()) {		
					int h = WidgetUtil.getIntValue(textTileWidth);
					if(radioIsometric.getSelection()){
						h = h / 2;
					}
					textTileHeight.setText(Integer.toString(h));
				}
			}
		});
		textTileWidth.setLocation(70, 154);
		textTileWidth.setSize(73, 22);
		{
			Label label = new Label(shell, SWT.NONE);
			label.setBounds(148, 199, 24, 17);
			label.setText("像素");
		}
		{
			Label label = new Label(shell, SWT.NONE);
			label.setLocation(194, 157);
			label.setSize(50, 17);
			label.setText("网格高");
		}
		textTileHeight = new Text(shell, SWT.BORDER);
//		textTileHeight.addKeyListener(new KeyAdapter() {
//			public void keyReleased(KeyEvent e) {
//				textTileHeight.setData(textTileHeight.getText());
//			}
//		});
		textTileHeight.setLocation(261, 154);
		textTileHeight.setSize(73, 22);
		{
			Label label = new Label(shell, SWT.NONE);
			label.setBounds(340, 199, 24, 17);
			label.setText("像素");
		}

		{
			Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
			label.setBounds(0, 260, 374, 2);
		}

		Button btnOK = new Button(shell, SWT.NONE);
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				onOK();
			}
		});
		btnOK.setBounds(218, 273, 70, 25);
		btnOK.setText("确定");
		shell.setDefaultButton(btnOK);

		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				worldMap = null;
				shell.close();
			}
		});
		btnCancel.setBounds(294, 273, 70, 25);
		btnCancel.setText("取消");
	}
	
	private void showError(){
		textError = "请检查是否正确填写了所有参数!";
		labelHead.redraw();
	}
	
	private void onOK(){
		String mapName = textMapName.getText().trim();
		int width = -1, height = -1, cellW = -1, cellH = -1, tw = -1, th = -1;
		try{
			width = WidgetUtil.getIntValue(textMapWidth);
			height = WidgetUtil.getIntValue(textMapHeight);
			cellW = WidgetUtil.getIntValue(txtCellW);
			cellH = WidgetUtil.getIntValue(txtCellH);
			tw = WidgetUtil.getIntValue(textTileWidth);
			th = WidgetUtil.getIntValue(textTileHeight);
		}catch(NumberFormatException ex){
			showError();
			return;
		}
		byte viewType = WorldMap.MDO_ORTHO;
		if (radioRectangle.getSelection()) {
			viewType = WorldMap.MDO_ORTHO;
		} else if (radioIsometric.getSelection()) {
			viewType = WorldMap.MDO_ISO;
			height = width / 2;
		}
		if (mapName.length() == 0 || width == 0 || height == 0 || tw == 0
				|| cellW == 0 || cellH == 0 || th == 0 || width >= 256
				|| height >= 256 || tw >= 256 || th >= 256) {
			showError();
			return;
		} else {
			textError = "";
		}
		labelHead.redraw();
		try {
			worldMap = MainApplication.getInstance().newMap(mapName, width, height, cellW, cellH, tw, th, viewType);
		} catch (DataCrackException e) {
			MainApplication.getInstance().alert("新建地图失败");
			worldMap = null;
			e.printStackTrace();
		}
		shell.close();
	}
}
