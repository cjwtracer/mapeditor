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
import tool.model.ScaleChange;
import tool.util.StringUtil;
import tool.util.WidgetUtil;

class ScalePanel extends Composite {
	Animation animation;
	GradualChange change;
	
	private Text text;
	ScaleChange scaleChange;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	ScalePanel(Composite parent, GradualChange change, Animation anim) {
		super(parent, SWT.NONE);
		animation = anim;
		this.change = change;
		scaleChange = (ScaleChange)change;
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(27, 36, 55, 17);
		lblNewLabel.setText("起始大小");
		
		final Scale scale1 = new Scale(this, SWT.NONE);
		scale1.setMaximum(40);
		scale1.setPageIncrement(1);
		scale1.setBounds(79, 25, 361, 42);
		int slt = scaleChange.getStartState() >= 1 ? (int)((scaleChange.getStartState() - 1) * 5) + 20 : (int)(20 * scaleChange.getStartState());
		scale1.setSelection(slt);
		scale1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale1.getSelection();
				float s = v >= 20 ? 1 + (v - 20) * 0.2f : v / (float)20;
				animation.setScale(s);
				scaleChange.setStartState(animation.getScale());
				MainApplication.getInstance().repaintMapCanvas();
				scaleChange.setGap(1);
			}
		});
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(27, 93, 55, 17);
		lblNewLabel_1.setText("结束大小");
		
		final Scale scale2 = new Scale(this, SWT.NONE);
		scale2.setMaximum(40);
		scale2.setPageIncrement(1);
		scale2.setBounds(79, 79, 361, 42);
		int slt1 = scaleChange.getEndState() >= 1 ? (int)((scaleChange.getEndState() - 1) * 5) + 20 : (int)(20 * scaleChange.getEndState());
		scale2.setSelection(slt1);
		scale2.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale2.getSelection();
				float s = v >= 20 ? 1 + (v - 20) * 0.2f : v / (float)20;
				animation.setScale(s);
				scaleChange.setEndState(animation.getScale());
				MainApplication.getInstance().repaintMapCanvas();
				scaleChange.setGap(1);
			}
		});
		
		final Button btnReverse = new Button(this, SWT.CHECK);
		btnReverse.setBounds(29, 146, 61, 17);
		btnReverse.setText("反向");
		btnReverse.setSelection(scaleChange.isReverse());
		btnReverse.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				boolean reverse = btnReverse.getSelection();
				scaleChange.setReverse(reverse);
			}
		});
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(143, 143, 73, 23);
		text.setText(String.valueOf(this.change.getGap()));
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				String t = text.getText();
				if(t.isEmpty()){
					return;
				}
				if(StringUtil.isNumeric(t))
					ScalePanel.this.change.setGap(Integer.valueOf(t));
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
