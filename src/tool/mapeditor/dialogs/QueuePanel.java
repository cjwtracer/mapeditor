package tool.mapeditor.dialogs;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import tool.model.Queue;

class QueuePanel extends Composite {
	
	Queue queue;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	QueuePanel(Composite parent, Queue queue) {
		super(parent, SWT.NONE);
		this.queue = queue;
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setBounds(41, 38, 41, 17);
		lblNewLabel.setText("反向：");
		
		final Button btnReverse = new Button(this, SWT.CHECK);
		btnReverse.setBounds(88, 38, 98, 17);
		btnReverse.setSelection(queue.reverse);
		btnReverse.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				QueuePanel.this.queue.reverse = btnReverse.getSelection();
			}
		});
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(274, 38, 41, 17);
		lblNewLabel_1.setText("循环：");
		
		final Button btnRepeat = new Button(this, SWT.CHECK);
		btnRepeat.setBounds(321, 38, 98, 17);
		btnRepeat.setSelection(queue.repeat);
		btnRepeat.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				QueuePanel.this.queue.repeat = btnRepeat.getSelection();
			}
		});
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(10, 78, 429, 2);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
