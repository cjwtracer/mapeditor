package tool.mapeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class SearchBox extends Composite{
	private Tree searchTree;
	private Text text;
	private List<TreeItem> items = new ArrayList<TreeItem>();
	private int count;
	private String textFormer;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SearchBox(Composite parent) {
		super(parent, SWT.NONE);
		text = new Text(this, SWT.BORDER);
		text.setBounds(10, 3, 70, 22);
		Button btn = new Button(this, SWT.PUSH);
		btn.setBounds(86, 3, 36, 22);
		btn.setText("搜索");
		btn.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e) {
				String content = text.getText();
				if (content.equals(""))
					return;
				if (!content.equals(textFormer)) {
					reset(content);
				}
				int size = items.size();
				if (size > 0) {
					if (size == count)
						count = 0;
					searchTree.select(items.get(count++));
					searchTree.setFocus();
					searchTree.showSelection();
				}
			}
		});
		setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
	}
	
	private void find(TreeItem item, String content){
		if(item.getText().contains(content))
			items.add(item);
		for(int i = 0, count = item.getItemCount(); i < count; i++){
			find(item.getItem(i), content);
		}
	}
	
	public void setSearchTree(Tree tree){
		searchTree = tree;
		reset(text.getText());
	}
	
	private void reset(String content){
		textFormer = content;
		items.clear();
		count = 0;
		int len = searchTree.getItemCount();
		for(int i = 0; i < len; i++){
			TreeItem item = searchTree.getItem(i);
			find(item, content);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
