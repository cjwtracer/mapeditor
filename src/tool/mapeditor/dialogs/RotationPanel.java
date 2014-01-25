package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import tool.mapeditor.application.MainApplication;
import tool.model.Animation;
import tool.model.GradualChange;
import tool.model.RotationChange;
import tool.util.StringUtil;

class RotationPanel extends Composite {
	Animation animation;
	GradualChange change;
	
	private Text text;
	RotationChange rotationChange;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	RotationPanel(Composite parent, GradualChange cg, Animation anim) {
		super(parent, SWT.NONE);
		animation = anim;
		this.change = cg;
		rotationChange = (RotationChange)cg;
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(29, 37, 55, 17);
		lblNewLabel.setText("起始角度");
		
		final Scale scale1 = new Scale(this, SWT.NONE);
		scale1.setMaximum(359);
		scale1.setPageIncrement(1);
		scale1.setBounds(90, 24, 366, 42);
		scale1.setSelection(rotationChange.getStartState());
		scale1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale1.getSelection();
				animation.setRotation(v);
				rotationChange.setStartState(animation.getRotation());
				MainApplication.getInstance().repaintMapCanvas();
				rotationChange.setGap(1);
			}
		});
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(29, 86, 55, 17);
		lblNewLabel_1.setText("结束角度");
		
		final Scale scale2 = new Scale(this, SWT.NONE);
		scale2.setMaximum(359);
		scale2.setPageIncrement(1);
		scale2.setBounds(90, 79, 366, 42);
		scale2.setSelection(rotationChange.getEndState());
		scale2.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale2.getSelection();
				animation.setRotation(v);
				rotationChange.setEndState(animation.getRotation());
				MainApplication.getInstance().repaintMapCanvas();
				rotationChange.setGap(1);
			}
		});
		
		final Button btnReverse = new Button(this, SWT.CHECK);
		btnReverse.setBounds(29, 146, 61, 17);
		btnReverse.setText("反向");
		btnReverse.setSelection(rotationChange.isReverse());
		btnReverse.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				boolean reverse = btnReverse.getSelection();
				rotationChange.setReverse(reverse);
			}
		});
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(143, 143, 73, 23);
		text.setText(String.valueOf(change.getGap()));
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				String t = text.getText();
				if(t.isEmpty()){
					return;
				}
				if(StringUtil.isNumeric(t))
					RotationPanel.this.change.setGap(Integer.valueOf(t));
			}
		});
		
		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setBounds(90, 146, 61, 17);
		lblNewLabel_2.setText("变化率：");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 127, 450, 2);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
