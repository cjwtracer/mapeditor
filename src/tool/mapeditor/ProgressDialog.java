package tool.mapeditor;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;

import tool.util.WidgetUtil;

/**
 * 进度条对话框，用于处理长时间任务时的强制等待，任务异步于UI主线程
 * @author caijw
 *
 */
public abstract class ProgressDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	ProgressBar progressBar;
	int step;
	int max;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ProgressDialog(Shell parent, int max, String title) {
		super(parent, SWT.BORDER | SWT.TITLE);
		setText(title == null ? "progress" : title);
		if(max > 0)
			this.max = max;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetUtil.layoutCenter(shell);
		shell.open();
		new Thread(){
			public void run(){
				runTask();
			}
		}.start();
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
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setSize(500, 178);
		shell.setText(getText());
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(10, 49, 474, 36);
		progressBar.setMaximum(max);
	}
	
	public boolean setStep(int step){
		Display dis = getParent().getDisplay();
		if(dis.isDisposed())
			return false;
		this.step = step;
		if(step >= max){
			return false;
		}
		dis.asyncExec(new Runnable(){
			public void run(){
				if(progressBar.isDisposed())
					return;
				progressBar.setSelection(ProgressDialog.this.step);
			}
		});
		return true;
	}
	
	public void finish(){
		Display dis = getParent().getDisplay();
		dis.asyncExec(new Runnable(){
			public void run(){
				if(shell.isDisposed())
					return;
				shell.close();
			}
		});
	}
	
	/**
	 * Start a new asyncronised task.
	 */
	public abstract void runTask();
}
