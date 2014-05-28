package tool.mapeditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import tool.model.Animation;
import tool.model.FrameChange;
import tool.model.GradualChange;
import tool.model.PositionChange;
import tool.model.Queue;
import tool.mapeditor.application.AnimationDescriptor;
import tool.mapeditor.application.MainApplication;
import tool.util.Constants;
import tool.util.WidgetUtil;

public class ChangePropertyDialog extends Dialog {
	MainApplication app = MainApplication.getInstance();
	
	protected Queue result;
	protected Shell shell;
	Tree tree;
	TreeItem previous;
	private TabFolder tabFolder;
	private TabItem tabItem;
	Menu menuQueue;
	Menu menuChange;
	Animation anim;
	AnimationDescriptor animDes;
	boolean cancel = true;
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ChangePropertyDialog(Shell parent, AnimationDescriptor anim) {
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.anim = anim.getUnderlying();
		animDes = anim;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		WidgetUtil.layoutRightTop(shell);
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(700, 400);
		shell.setText("编辑动画");
		shell.setLayout(new GridLayout(2, false));
		shell.addShellListener(new ShellAdapter(){
			public void shellClosed(ShellEvent e){
				anim.reset();
				anim.toStartState();
				MainApplication.getInstance().repaintMapCanvas();
			}
		});
		
		buildPopupMenus();
		
		tree = new Tree(shell, SWT.BORDER);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
		gd_tree.widthHint = 130;
		tree.setLayoutData(gd_tree);
		tree.addMenuDetectListener(new MenuDetectListener(){
			public void menuDetected(MenuDetectEvent e){
				TreeItem[] items = tree.getSelection();
				if(items.length > 0){
					if(items[0].getData() instanceof Queue){
						menuQueue.setLocation(e.x, e.y);
						menuQueue.setVisible(true);
					}else if(items[0].getData() instanceof GradualChange){
						menuChange.setLocation(e.x, e.y);
						menuChange.setVisible(true);
					}
				}
			}
		});
		tree.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				TreeItem itm = (TreeItem)e.item;
				if(itm == previous)
					return;
				previous = itm;
				Object data = itm.getData();
				if(data instanceof GradualChange){
					onViewChange((GradualChange)data);
				}else if(data instanceof Queue){
					onViewQueue((Queue)data);
				}
			}
		});
		rebuildTree();
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tabItem = new TabItem(tabFolder, SWT.NONE);
		Composite cmp = new Composite(shell, SWT.NONE);
		cmp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
		
		Button btnOK = new Button(cmp, SWT.NONE);
		btnOK.setBounds(345, 17, 80, 27);
		btnOK.setText("确定");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				cancel = false;
				shell.close();
			}
		});
		
		Button btnCancel = new Button(cmp, SWT.NONE);
		btnCancel.setBounds(438, 17, 80, 27);
		btnCancel.setText("取消");
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});
	}
	
	private void buildPopupMenus() {
		menuQueue = new Menu(shell, SWT.POP_UP);
		buildMenuQueue();
		menuChange = new Menu(shell, SWT.POP_UP);
		MenuItem itemDelCg = new MenuItem(menuChange, SWT.PUSH);
		itemDelCg.setText("删除渐变");
		itemDelCg.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					delete((GradualChange)is[0].getData());
			}
		});
	}

	private void buildMenuQueue() {
		MenuItem itemAdd = new MenuItem(menuQueue, SWT.PUSH);
		itemAdd.setText("添加帧变换");
		itemAdd.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					add((Queue)is[0].getData(), GradualChange.CHANGE_FRAME);
			}
		});
		
		new MenuItem(menuQueue, SWT.SEPARATOR);
		
		itemAdd = new MenuItem(menuQueue, SWT.PUSH);
		itemAdd.setText("添加透明渐变");
		itemAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					add((Queue)is[0].getData(), GradualChange.CHANGE_ALPHA);
			}
		});
		itemAdd = new MenuItem(menuQueue, SWT.PUSH);
		itemAdd.setText("添加旋转渐变");
		itemAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					add((Queue)is[0].getData(), GradualChange.CHANGE_ROTATION);
			}
		});
		itemAdd = new MenuItem(menuQueue, SWT.PUSH);
		itemAdd.setText("添加缩放渐变");
		itemAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					add((Queue)is[0].getData(), GradualChange.CHANGE_SCALE);
			}
		});
		itemAdd = new MenuItem(menuQueue, SWT.PUSH);
		itemAdd.setText("添加位置渐变");
		itemAdd.addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					add((Queue)is[0].getData(), GradualChange.CHANGE_POSITION);
			}
		});
		
		new MenuItem(menuQueue, SWT.SEPARATOR);
		
		MenuItem itemDel = new MenuItem(menuQueue, SWT.PUSH);
		itemDel.setText("删除序列");
		itemDel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] is = tree.getSelection();
				if (is.length > 0)
					delete((Queue)is[0].getData());
			}
		});
	}

	void delete(GradualChange change) {
		anim.removeChange(change);
		if(change.type == GradualChange.CHANGE_FRAME)
			animDes.setFrames(null);
		rebuildTree();
		tabItem.setControl(null);
		MainApplication.getInstance().repaintMapCanvas();
	}

	void add(Queue queue, byte type) {
		if(type == GradualChange.CHANGE_FRAME)
			if(animDes.getFrames() != null){
				app.alert("only one frame change can be added into an animation");
				return;
			}
		GradualChange cg = queue.addChange(type);
		rebuildTree();
		Composite panel = null;
		switch(type){
		case GradualChange.CHANGE_ALPHA:
			panel = new AlphaPanel(tabFolder, cg, anim);
			break;
		case GradualChange.CHANGE_ROTATION:
			panel = new RotationPanel(tabFolder, cg, anim);
			break;
		case GradualChange.CHANGE_SCALE:
			panel = new ScalePanel(tabFolder, cg, anim);
			break;
		case GradualChange.CHANGE_POSITION:
			panel = new PositionPanel(tabFolder, cg, anim);
			break;
		case GradualChange.CHANGE_FRAME:
			panel = new FramePanel(tabFolder, cg, animDes.newFrames((FrameChange)cg));
			break;
		}
		tabItem.setControl(panel);
	}

	void delete(Queue queue){
		anim.removeQueue(queue);
		rebuildTree();
		tabItem.setControl(null);
		MainApplication.getInstance().repaintMapCanvas();
	}
	
	void rebuildTree(){
		tree.removeAll();
		int i = 0;
		for(Queue q : anim.getQueues()){
			TreeItem parent = new TreeItem(tree, SWT.NONE);
			parent.setText("序列" + i++);
			for(GradualChange c : q.getChanges()){
				TreeItem itm = new TreeItem(parent, SWT.NONE);
				itm.setText(c.getText());
				itm.setData(c);
			}
			parent.setExpanded(true);
			parent.setData(q);
		}
	}
	
	void onViewChange(GradualChange change){
		Composite panel = null;
		switch(change.type){
		case GradualChange.CHANGE_ALPHA:
			panel = new AlphaPanel(tabFolder, change, anim);
			break;
		case GradualChange.CHANGE_ROTATION:
			panel = new RotationPanel(tabFolder, change, anim);
			break;
		case GradualChange.CHANGE_SCALE:
			panel = new ScalePanel(tabFolder, change, anim);
			break;
		case GradualChange.CHANGE_POSITION:
			panel = new PositionPanel(tabFolder, change, anim);
			break;
		case GradualChange.CHANGE_FRAME:
			panel = new FramePanel(tabFolder, change, animDes.getFrames());
			break;
		}
		if(panel != null){
			tabItem.setControl(panel);
			tabItem.setText(change.getText());
		}else{
			app.alert("invalid change type");
		}
	}
	
	void onViewQueue(Queue queue){
		tabItem.setControl(new QueuePanel(tabFolder, queue));
		tabItem.setText("序列" + anim.getQueues().indexOf(queue));
	}
}
