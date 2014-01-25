package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tool.model.Animation;
import tool.model.GradualChange;
import tool.model.PositionChange;
import tool.util.StringUtil;
import tool.util.WidgetUtil;
import org.eclipse.swt.custom.CCombo;

public class PositionPanel extends Composite {
	Animation anim;
	
	Text text, text1, text2;
	PositionChange change;
	CCombo combo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	PositionPanel(Composite parent, GradualChange change, Animation animation) {
		super(parent, SWT.NONE);
		this.anim = animation;
		this.change = (PositionChange)change;

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(291, 56, 55, 23);
		lblNewLabel.setText("移动步长");
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(352, 53, 73, 23);
		text.setText(String.valueOf(change.getGap()));
		text.addListener(SWT.Verify, WidgetUtil.getVerifyDigitListener());
		text.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				String t = text.getText();
				if(t.isEmpty()){
					return;
				}
				if(StringUtil.isNumeric(t))
					PositionPanel.this.change.setGap(Integer.valueOf(t));
			}
		});
		
		combo = new CCombo(this, SWT.BORDER);
		combo.setBounds(151, 55, 103, 21);
		initCombo();
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(84, 56, 61, 17);
		lblNewLabel_1.setText("选择曲线");
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 100, 430, 2);
	}
	
	void initCombo(){
		int s = anim.getLocus().getCurves().size();
		if(s > 0){
			if(change.getCurveIndex() != -1){
				combo.setText("曲线" + change.getCurveIndex());
			}
			for(int i = 0; i < s; ++i){
				combo.add("曲线" + i);
			}
		}
		combo.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				change.setCurve(combo.getSelectionIndex());
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
