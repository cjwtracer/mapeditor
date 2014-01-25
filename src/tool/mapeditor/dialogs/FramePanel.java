package tool.mapeditor.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.layout.GridData;

import tool.model.ClipFrame;
import tool.model.FrameChange;
import tool.model.GradualChange;
import tool.model.ResourceSet;
import tool.resourcemanager.Resources;
import tool.mapeditor.ResourceLabel;
import tool.mapeditor.application.AnimationDescriptor;
import tool.mapeditor.application.FrameDescriptor;
import tool.mapeditor.application.FramesDescriptor;
import tool.mapeditor.application.MainApplication;
import tool.mapeditor.application.ResourceDescriptor;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.util.WidgetUtil;

import org.eclipse.swt.custom.ScrolledComposite;

public class FramePanel extends Composite {
	
	Combo comboFrame;
	Combo comboResource;
	Canvas canvas;
	ScrolledComposite scrolledComposite;
	Composite panel;
	ResourceLabel previousLabel;
	
	List<ResourceDescriptor> resources = new ArrayList<ResourceDescriptor>();
	FrameChange change;
	FrameDescriptor frame;
	FramesDescriptor frames;
	
	Listener listener = new Listener(){
		public void handleEvent(Event e){
			ResourceLabel label = (ResourceLabel)e.widget;
			label.setSelected(true);
			if(WidgetUtil.valid(previousLabel) && previousLabel != label){
				previousLabel.setSelected(false);
			}
			previousLabel = label;
			if(frame != null){
				frame.setResource((ItemDescriptor)label.getData());
				canvas.redraw();
			}
		}
	};

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public FramePanel(Composite parent, GradualChange change, FramesDescriptor frames) {
		super(parent, SWT.NONE);
		this.change = (FrameChange)change;
		this.frames = frames;
		
		for(ResourceSet set : MainApplication.getInstance().getProject().getResourceGroup().values()){
			this.resources.add(ResourceDescriptor.getDescriptor(set));
		}
		
		setLayout(new GridLayout(4, false));
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		ToolItem itemAdd = new ToolItem(toolBar, SWT.PUSH);
		itemAdd.setImage(Resources.getImage("/icons/add.png"));
		itemAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				addFrame();
			}
		});
		ToolItem itemDel = new ToolItem(toolBar, SWT.PUSH);
		itemDel.setImage(Resources.getImage("/icons/delete.png"));
		itemDel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				delFrame();
			}
		});
		
		comboFrame = new Combo(this, SWT.READ_ONLY);
		comboFrame.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 2));
		
		comboResource = new Combo(this, SWT.READ_ONLY);
		comboResource.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		canvas = new Canvas(this, SWT.BORDER);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		canvas.addListener(SWT.Paint, new Listener(){
			public void handleEvent(Event e){
				onPaint(e.gc);
			}
		});
		
		scrolledComposite = new ScrolledComposite(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		panel = new Composite(scrolledComposite, SWT.NONE);
		panel.setLayout(new GridLayout(3, true));
		panel.setBackground(Resources.BG_BLUE);
		panel.addListener(SWT.Resize, new Listener(){
			public void handleEvent(Event e){
				for(Control c : panel.getChildren())
					c.redraw();
			}
		});
		scrolledComposite.setContent(panel);
		
		buildResources();
		buildFrames();

	}

	protected void onPaint(GC gc) {
		if(frame != null){
			Rectangle r = canvas.getClientArea();
			frame.paint(gc, 0, 0, r.width, r.height);
		}
	}

	private void buildFrames() {
		for(int i = 0, len = change.getFrames().size(); i < len; ++i){
			comboFrame.add(String.valueOf(i));
		}
		comboFrame.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int i = comboFrame.getSelectionIndex();
				if(i >= 0){
					frame = frames.getFrames().get(i);
					canvas.redraw();
				}
			}
		});
		comboFrame.select(0);
		frame = frames.getFrames().get(0);
	}

	private void buildResources() {
		if(resources.size() == 0)
			return;
		for(ResourceDescriptor r : resources){
			comboResource.add(r.getName());
		}
		comboResource.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int i = comboResource.getSelectionIndex();
				if(i >= 0)
					setResource(resources.get(i));
			}
		});
		setResource(resources.get(0));
	}

	void setResource(ResourceDescriptor resource) {
		for(Control c : panel.getChildren())
			c.dispose();
		comboResource.setText(resource.getName());
		for(ItemDescriptor item : resource.getResourceItems()){
			ResourceLabel label = new ResourceLabel(panel, item);
			label.addListener(SWT.MouseUp, listener);
			int w = item.getBounds().width, h = item.getBounds().height;
			label.setSize(w, h);
			GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
			gd.minimumWidth = w;
			gd.minimumHeight = h;
			label.setLayoutData(gd);
			label.setData(item);
			label.setToolTipText(item.getName());
		}
		scrolledComposite.setMinSize(panel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	protected void delFrame() {
		int index = comboFrame.getSelectionIndex();
		if(index < 0)
			return;
		change.deleteFrame(index);
		frames.getFrames().remove(index);
		frame = null;
		comboFrame.removeAll();
		for(int i = 0, len = change.getFrames().size(); i < len; ++i){
			comboFrame.add(String.valueOf(i));
		}
		canvas.redraw();
	}

	protected void addFrame() {
		ClipFrame f = change.addFrame(-1);
		frame = new FrameDescriptor(f);
		frames.getFrames().add(frame);
		int index = comboFrame.getItemCount();
		comboFrame.add(String.valueOf(index));
		comboFrame.select(index);
		canvas.redraw();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
