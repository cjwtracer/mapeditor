package tool.mapeditor.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.newdawn.slick.geom.Curve;

import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.ResourceDescriptor;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.model.AlphaChange;
import tool.model.Animation;
import tool.model.ClipFrame;
import tool.model.FrameChange;
import tool.model.GradualChange;
import tool.model.Locus;
import tool.model.PositionChange;
import tool.model.Queue;
import tool.model.ResourceSet;
import tool.model.RotationChange;
import tool.model.ScaleChange;
import tool.model.Unit;
import tool.model.UnitGroup;
import tool.model.ClipFrame.FrameLayer;
import tool.util.ImageUtil;
import tool.util.WidgetUtil;

public class ImportAnimationDialog extends Dialog {
	MainApplication app = MainApplication.getInstance();

	protected Object result;
	protected Shell shell;
	private Table table;
	List<String> names = new ArrayList<String>();
	List<File> files = new ArrayList<File>();
	List<String> imgDirs = new ArrayList<String>();
	String rootDir;
	boolean override = true;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ImportAnimationDialog(Shell parent) {
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		setText("导入动画");
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(559, 403);
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn colName = new TableColumn(table, SWT.NONE);
		colName.setText("名称");
		colName.setWidth(100);
		TableColumn colImg = new TableColumn(table, SWT.NONE);
		colImg.setText("图片文件夹");
		colImg.setWidth(300);
		
		Composite composite = new Composite(shell, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite.widthHint = 84;
		composite.setLayoutData(gd_composite);
		
		Button btnAdd = new Button(composite, SWT.NONE);
		btnAdd.setBounds(10, 39, 64, 27);
		btnAdd.setText("添加");
		btnAdd.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				onImport();
			}
		});
		
		Button btnDel = new Button(composite, SWT.NONE);
		btnDel.setBounds(10, 87, 64, 27);
		btnDel.setText("删除");
		btnDel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				onDelete();
			}
		});
		
		Composite composite_1 = new Composite(shell, SWT.BORDER);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Button btnOK = new Button(composite_1, SWT.NONE);
		btnOK.setBounds(355, 10, 80, 27);
		btnOK.setText("确定");
		btnOK.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				onOK();
			}
		});
		
		Button btnCancel = new Button(composite_1, SWT.NONE);
		btnCancel.setBounds(449, 10, 80, 27);
		btnCancel.setText("取消");
		btnCancel.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				shell.close();
			}
		});
		
		final Button btnOverride = new Button(composite_1, SWT.CHECK);
		btnOverride.setBounds(10, 10, 141, 17);
		btnOverride.setText("覆盖已存在的同名动画");
		btnOverride.setSelection(override);
		btnOverride.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				override = btnOverride.getSelection();
			}
		});

	}
	
	void buildTable(){
		table.removeAll();
		for(Control c : table.getChildren())
			c.dispose();
		for(int i = 0, len = names.size(); i < len; i++){
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setText(names.get(i));
			item.setText(1, imgDirs.get(i));
			Button btn = WidgetUtil.getTableButton(table, item, 1, "...");
			final int idx = i;
			btn.addListener(SWT.Selection, new Listener(){
				public void handleEvent(Event e){
					DirectoryDialog dlg = new DirectoryDialog(shell);
					dlg.setFilterPath(rootDir);
					String dir = dlg.open();
					if(dir != null){
						File fd = new File(dir);
						String n = fd.getAbsolutePath();
						imgDirs.set(idx, n);
						item.setText(1, n);
					}
				}
			});
		}
	}
	
	void onImport(){
		DirectoryDialog dlg = new DirectoryDialog(shell);
		String dir = dlg.open();
		if(dir != null){
			names.clear();
			files.clear();
			imgDirs.clear();
			rootDir = dir;
			for(File f : new File(dir).listFiles(new FileFilter(){
				public boolean accept(File arg){
					return arg.getName().endsWith(".json");
				}
			})){
				String fn = f.getName();
				String n = fn.substring(0, fn.lastIndexOf("."));
				names.add(n);
				files.add(f);
				String d = dir + File.separator + n;
				if(!new File(d).exists())
					d = "";
				imgDirs.add(d);
			}
			buildTable();
		}
	}
	
	void onDelete(){
		int i = table.getSelectionIndex();
		if(i >= 0){
			names.remove(i);
			files.remove(i);
			imgDirs.remove(i);
			buildTable();
		}
	}
	
	void onOK(){
		int index = 0;
		for(File f : files){
			FileInputStream fis = null;
			StringBuffer buf = new StringBuffer();
			boolean succeed = true;
			try {
				fis = new FileInputStream(f);
				InputStreamReader reader = new InputStreamReader(fis, "utf-8");
				BufferedReader bufReader = new BufferedReader(reader);
				String line = null;
				while((line = bufReader.readLine()) != null){
					buf.append(line);
				}
			} catch (Exception e) {
				succeed = false;
			}finally{
				if(fis != null)try{fis.close();}catch(IOException ex){}
			}
			if(succeed){
				String groupName = names.get(index);
				String groupDir = imgDirs.get(index);
				
				JSONObject obj = JSONObject.fromObject(buf.toString());
				UnitGroup ug = new UnitGroup(null);
				boolean addGroup = true;
				String ugName = obj.getString("name");
				int id = -1;
				if(override){
					for(UnitGroup ugp : app.getProject().getUnitsGroup().values()){
						if(ugp.getName().equals(ugName)){
							id = ugp.getId();
							break;
						}
					}
				}
				if(id == -1)
					id = app.getProject().getAvailableUnitGroupId();
				ug.setId(id);
				ug.setName(ugName);
				
				JSONArray jus = obj.getJSONArray("layers");
				for(int i = 0, len = jus.size(); i < len; i++){
					JSONObject ju = jus.getJSONObject(i);
					
					String juName = ju.getString("name");
					File imgFolder = new File(groupDir + File.separator + juName);
					String setName = imgFolder.getName();
					//if resource set with the name exists, it will reuse the existing one
					ResourceSet resSet = app.checkResourceSet(setName);
					if((resSet == null) && imgFolder.exists()){
						Map<String, File> imgs = new HashMap<String, File>();
						for(File imgf : imgFolder.listFiles(new FileFilter(){
							public boolean accept(File arg){
								return ImageUtil.supportedImageFormat(arg.getName());
							}
						})){
							imgs.put(imgf.getAbsolutePath(), imgf);
						}
						ResourceDescriptor rs = app.newResourceSet(setName, imgs);
						app.addResource(rs);
						resSet = rs.getUnderlying();
					}
					if(resSet == null){//if image folder doesn't exist
						app.alert("image folder " + juName + " doesn't exist");
						addGroup = false;
						break;
					}
					
					Unit u = new Unit(null);
					double anchorX = ju.getDouble("anchorX");
					double anchorY = ju.getDouble("anchorY");
					double positionX = ju.getDouble("positionX");
					double positionY = ju.getDouble("positionY");
					int ux = (int)(positionX - anchorX), uy = (int)(positionY - anchorY);
					u.setX(ux);
					u.setY(uy);
					ug.addOffsets(-1, new int[]{ux, uy});
					u.setWidth((int)(ju.getDouble("width")));
					u.setHeight((int)(ju.getDouble("height")));
					String head = setName + ResourceDescriptor.RES_SEP;
					u.setResource(head + 0);
					
					Animation a = u.createAnimation();
					a.setId(app.getProject().getAvailableAnimationID());
					a.setName(juName);
					app.getProject().getAnimationGroup().put(a.getId(), a);
					u.setAnimation(a);
					ug.getComponents().add(u);
					
					int resNum = resSet.getItems().size();
					Queue qf = a.newQueue();
					qf.repeat = true;
					FrameChange fc = (FrameChange)qf.addChange(GradualChange.CHANGE_FRAME);
					for(int j = 0; j < resNum; j++){
						ClipFrame frame = fc.addFrame(-1);
						FrameLayer l = frame.getLayers().get(0);
						if(l.getCells().size() == 0){
							l.newClipAt(0, 0, head + j);
						}else{
							l.getCells().get(0).setResource(head + j);
						}
					}
					//key frames of position
					if(ju.containsKey("keyFrames")){
						JSONArray kfs = ju.getJSONArray("keyFrames");
						Queue qp = a.newQueue();
						for(int j = 0, lenj = kfs.size() - 1; j < lenj; j++){
							PositionChange pc = (PositionChange)qp.addChange(GradualChange.CHANGE_POSITION);
							JSONObject frm = kfs.getJSONObject(j);
							JSONObject nfrm = kfs.getJSONObject(j + 1);
							pc.duration = nfrm.getDouble("time") - frm.getDouble("time");
							double posX = frm.getDouble("posX") - anchorX;
							double posY = frm.getDouble("posY") - anchorY;
							if(j == 0){//set the position of the unit to the first frame value
								u.setX((int)posX);
								u.setY((int)posY);
								ug.getOffset(i)[0] = (int)posX;
								ug.getOffset(i)[1] = (int)posY;
							}
							double nposX = nfrm.getDouble("posX") - anchorX;
							double nposY = nfrm.getDouble("posY") - anchorY;
							Locus locus = a.extendLocus();
							Curve c = locus.getCurves().get(j);
							double otx = frm.getDouble("outTangentX");
							double oty = frm.getDouble("outTangentY");
							double itx = nfrm.getDouble("inTangentX");
							double ity = nfrm.getDouble("inTangentY");
							c.getStartPoint().x = (float)posX;
							c.getStartPoint().y = (float)posY;
							c.getControlFirst().x = (float)(posX + otx);
							c.getControlFirst().y = (float)(posY + oty);
							c.getControlSecond().x = (float)(nposX + itx);
							c.getControlSecond().y = (float)(nposY + ity);
							c.getEndPoint().x = (float)nposX;
							c.getEndPoint().y = (float)nposY;
							pc.setCurve(j);
						}
					}
					//key frame of opacity
					if(ju.containsKey("keyFramesOpacity")){
						JSONArray kfs = ju.getJSONArray("keyFramesOpacity");
						Queue qo = a.newQueue();
						qo.repeat = true;
						for(int j = 0, lenj = kfs.size() - 1; j < lenj; j++){
							AlphaChange cg = (AlphaChange)qo.addChange(GradualChange.CHANGE_ALPHA);
							JSONObject frm = kfs.getJSONObject(j);
							JSONObject nfrm = kfs.getJSONObject(j + 1);
							cg.duration = nfrm.getDouble("time") - frm.getDouble("time");
							cg.setStartState((int)(frm.getDouble("alpha") * 255 / 100));
							cg.setEndState((int)(nfrm.getDouble("alpha") * 255 / 100));
						}
					}
					//key frames of scale
					if(ju.containsKey("keyFramesScale")){
						JSONArray kfs = ju.getJSONArray("keyFramesScale");
						Queue qs = a.newQueue();
						qs.repeat = true;
						for(int j = 0, lenj = kfs.size() - 1; j < lenj; j++){
							ScaleChange cg = (ScaleChange)qs.addChange(GradualChange.CHANGE_SCALE);
							JSONObject frm = kfs.getJSONObject(j);
							JSONObject nfrm = kfs.getJSONObject(j + 1);
							cg.duration = nfrm.getDouble("time") - frm.getDouble("time");
							cg.setStartState(frm.getInt("scaleX") / (float)100);
							cg.setEndState(nfrm.getInt("scaleX") / (float)100);
						}
					}
					//key frames of rotation
					if(ju.containsKey("keyFramesRotation")){
						JSONArray kfs = ju.getJSONArray("keyFramesRotation");
						Queue qr = a.newQueue();
						qr.repeat = true;
						for(int j = 0, lenj = kfs.size() - 1; j < lenj; j++){
							RotationChange cg = (RotationChange)qr.addChange(GradualChange.CHANGE_ROTATION);
							JSONObject frm = kfs.getJSONObject(j);
							JSONObject nfrm = kfs.getJSONObject(j + 1);
							cg.duration = nfrm.getDouble("time") - frm.getDouble("time");
							cg.setStartState(frm.getInt("angle"));
							cg.setEndState(nfrm.getInt("angle"));
						}
					}
					
					u.setAnimation(a);
				}
				if(addGroup)
					app.getProject().getUnitsGroup().put(ug.getId(), ug);
			}
			index++;
		}
		shell.close();
	}
}
