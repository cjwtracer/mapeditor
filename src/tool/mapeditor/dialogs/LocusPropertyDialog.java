package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tool.model.Animation;
import tool.model.Locus;
import tool.model.Unit;
import tool.util.WidgetUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LocusPropertyDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	Animation animation;
	private Text text;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LocusPropertyDialog(Shell parent, Animation animation) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		if(animation == null)
			throw new IllegalArgumentException("Unit can't be null!");
		this.animation = animation;
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
		shell.setSize(350, 250);
		shell.setText("轨迹属性");
		
		final Button checkReverse = new Button(shell, SWT.CHECK);
		checkReverse.setBounds(32, 10, 53, 17);
		checkReverse.setText("反向");
		checkReverse.setSelection(animation.getLocus().isReverse());
		checkReverse.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				animation.getLocus().setReverse(checkReverse.getSelection());
			}
		});
		
		final Button checkRepeat = new Button(shell, SWT.CHECK);
		checkRepeat.setBounds(190, 10, 53, 17);
		checkRepeat.setText("循环");
		checkRepeat.setSelection(animation.getLocus().isRepeat());
		checkRepeat.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				animation.getLocus().setRepeat(checkRepeat.getSelection());
			}
		});
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 33, 324, 2);
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(24, 41, 61, 17);
		lblNewLabel.setText("移动步长");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(91, 38, 73, 23);
//		text.setText(String.valueOf(animation.getStep()));
//		text.addListener(SWT.Verify, WidgetUtil.getVerifyDigitListener());
//		text.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent arg0) {
//				String t = text.getText();
//				if(t.isEmpty()){
//					return;
//				}
//				animation.setStep(Integer.valueOf(t));
//			}
//		});
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setBounds(254, 185, 80, 27);
		btnClose.setText("关闭");
		btnClose.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
	}
}
