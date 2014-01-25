package tool.mapeditor.dialogs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tool.mapeditor.application.MainApplication;
import tool.model.WorldMap;
import tool.util.ImageUtil;
import tool.util.WidgetUtil;

public class ImageSliceDialog extends Dialog {

	MainApplication app = MainApplication.getInstance();
	protected Object result;
	protected Shell shell;
	private Text textFrom;
	private Text textTo;
	private Text textW;
	private Text textH;
	
	String imagePath;
	List<String> images;
	String imageFile;
	String destFolder;
	
	boolean isBatch;
	boolean isMapBg;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ImageSliceDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM);
		setText("SWT Dialog");
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
		shell.setSize(450, 300);
		shell.setText("大图切割");
		
		final Button checkBtn = new Button(shell, SWT.CHECK);
		checkBtn.setBounds(10, 28, 80, 29);
		checkBtn.setText("批量处理");
		checkBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				isBatch = checkBtn.getSelection();
			}
		});
		
		textFrom = new Text(shell, SWT.BORDER);
		textFrom.setBounds(96, 30, 219, 27);
		
		textTo = new Text(shell, SWT.BORDER);
		textTo.setBounds(96, 85, 219, 27);
		
		final Button btnImage = new Button(shell, SWT.NONE);
		btnImage.setBounds(337, 28, 80, 27);
		btnImage.setText("选择图片");
		btnImage.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(isBatch){
					batchSelect();
				}else{
					FileDialog dlg = new FileDialog(shell, SWT.OPEN);
					imagePath = dlg.open();
					if(imagePath != null){
						textFrom.setText(imagePath);
						imageFile = dlg.getFileName();
					}
				}
			}
		});
		
		final Button btnDest = new Button(shell, SWT.NONE);
		btnDest.setBounds(337, 83, 80, 27);
		btnDest.setText("保存路径");
		btnDest.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				DirectoryDialog dlg = new DirectoryDialog(shell);
				String dest = dlg.open();
				if(dest != null){
					textTo.setText(dest);
					destFolder = dest; 
				}
			}
		});
		
		final Button btnCheckButton = new Button(shell, SWT.CHECK);
		btnCheckButton.setBounds(24, 213, 142, 17);
		btnCheckButton.setText("切割地图背景图");
		btnCheckButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				isMapBg = btnCheckButton.getSelection();
				textFrom.setEnabled(!isMapBg);
				btnImage.setEnabled(!isMapBg);
				checkBtn.setEnabled(!isMapBg);
				textW.setEnabled(!isMapBg);
				textH.setEnabled(!isMapBg);
			}
		});
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(24, 146, 48, 23);
		lblNewLabel.setText("图块宽：");
		
		textW = new Text(shell, SWT.BORDER);
		textW.setBounds(79, 146, 73, 23);
		
		Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setBounds(239, 146, 48, 23);
		lblNewLabel_1.setText("图块高：");
		
		textH = new Text(shell, SWT.BORDER);
		textH.setBounds(293, 146, 73, 23);
		
		Button btnOK = new Button(shell, SWT.NONE);
		btnOK.setBounds(261, 224, 80, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				onOK();
			}
		});
		
		Button btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setBounds(354, 224, 80, 27);
		btnCancel.setText("返回");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});

	}
	
	void batchSelect(){
		DirectoryDialog dlg = new DirectoryDialog(shell, SWT.SAVE);
		String dir = dlg.open();
		if(dir != null){
			File folder = new File(dir);
			images = new ArrayList<String>();
			for(String f : folder.list(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					for(String s : ImageUtil.SURPORTED_FORMAT){
						if(name.endsWith(s)){
							return true;
						}
					}
					return false;
				}
			})){
				images.add(f);
			}
		}
		textFrom.setText(dir);
	}
	
	void slice(String imgFile, String name, int w, int h){
		ImageData data = new ImageData(imgFile);
		ImageData[] datas = ImageUtil.splitImage(data, w, h, 1);
		String ext = imgFile.substring(imgFile.lastIndexOf(".")).toLowerCase();
//		int end = name.indexOf("_bg");
//		if(end == -1)
//			end = name.length();
		String destF = destFolder + File.separator + name + File.separator;
		File f = new File(destF);
		if(!f.exists())
			f.mkdir();
		ImageLoader loader = new ImageLoader();
		ImageData[] tmp = new ImageData[1];
		int i = 0;
		int col = data.width / w + (data.width % w == 0 ? 0 : 1);
		for(ImageData d : datas){
			tmp[0] = d;
			loader.data = tmp;
			loader.save(destF + i / col + "_" + i % col + ext, ImageUtil.getFormat(ext));
			++i;
		}
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(destF + File.separator + name + ".des");
			pw.println(name);
			pw.print(data.width + ", ");
			pw.println(data.height);
			pw.print(w + ", ");
			pw.println(h);
			pw.println(datas.length + ":");
			for(int j = 0; j < datas.length; ++j){
				pw.println(j);
			}
			pw.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	void onOK(){
		if(textTo.getText().isEmpty()){
			app.alert("invalid arguments");
			return;
		}
		if(!isMapBg){
			if(textFrom.getText().isEmpty()){
				app.alert("invalid arguments");
				return;
			}
		}
		if(isMapBg){
			List<Integer> idCache = new ArrayList<Integer>(app.getProject().getMapGroup().size());
			for(WorldMap m : app.getProject().getMapGroup().values()){
				int id = m.getBackgroundID();
				if(idCache.contains(id))
					continue;
				String dir = app.getProject().getMapDir();
				String f = new StringBuffer(dir).append(m.getBackground()).toString();
				slice(f, String.valueOf(id), m.getCellWidth(), m.getCellHeight());
				idCache.add(id);
			}
		}else{
			if(textW.getText().isEmpty() || textH.getText().isEmpty()){
				app.alert("invalid arguments");
				return;
			}
			int w = Integer.parseInt(textW.getText());
			int h = Integer.parseInt(textH.getText());
			if(isBatch){
				for(String s : images){
					String dir = textFrom.getText();
					String f = new StringBuffer(dir).append(File.separator).append(s).toString();
					slice(f, s.substring(0, s.lastIndexOf(".")), w, h);
				}
			}else{
				String name = imagePath.substring(imagePath.lastIndexOf(File.separator) + 1, imagePath.lastIndexOf("."));
				slice(imagePath, name, w, h);
			}
		}
		shell.close();
	}
}
