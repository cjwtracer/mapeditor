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

import tool.model.AlphaChange;
import tool.model.Animation;
import tool.model.GradualChange;
import tool.mapeditor.application.MainApplication;
import tool.util.StringUtil;
import tool.util.WidgetUtil;


class AlphaPanel extends Composite{
	Animation animation;
	GradualChange change;
	
	AlphaChange alphaChange;
	private Text text;

	AlphaPanel(Composite parent, GradualChange change, Animation anim) {
		super(parent, SWT.NONE);
		animation = anim;
		this.change = change;
		alphaChange = (AlphaChange)change;
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(23, 38, 61, 17);
		lblNewLabel.setText("起始透明度");
		
		final Scale scale1 = new Scale(this, SWT.NONE);
		scale1.setMaximum(255);
		scale1.setPageIncrement(1);
		scale1.setBounds(84, 25, 408, 42);
		scale1.setSelection(alphaChange.getStartState());
		scale1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale1.getSelection();
				animation.setAlpha(v);
				alphaChange.setStartState(animation.getAlpha());
				MainApplication.getInstance().repaintMapCanvas();
			}
		});
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(23, 84, 61, 17);
		lblNewLabel_1.setText("结束透明度");
		
		final Scale scale2 = new Scale(this, SWT.NONE);
		scale2.setMaximum(256);
		scale2.setPageIncrement(1);
		scale2.setBounds(84, 73, 408, 42);
		scale2.setSelection(alphaChange.getEndState());
		scale2.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int v = scale2.getSelection();
				animation.setAlpha(v);
				alphaChange.setEndState(animation.getAlpha());
				MainApplication.getInstance().repaintMapCanvas();
			}
		});
		
		final Button btnRepeat = new Button(this, SWT.CHECK);
		btnRepeat.setBounds(23, 146, 61, 17);
		btnRepeat.setText("反向");
		btnRepeat.setSelection(alphaChange.isReverse());
		btnRepeat.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				boolean reverse = btnRepeat.getSelection();
				alphaChange.setReverse(reverse);		}
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
					AlphaPanel.this.change.setGap(Integer.valueOf(t));
			}
		});
		
		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setBounds(90, 146, 61, 17);
		lblNewLabel_2.setText("变化率：");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 127, 450, 2);
	}
	
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
