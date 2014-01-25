package tool.mapeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import tool.mapeditor.Resource.Item;
import tool.mapeditor.actions.AddResourceItemAction;
import tool.mapeditor.actions.DeleteSourceAction;
import tool.mapeditor.actions.NewResourceAction;
import tool.mapeditor.actions.ResourcePrefixAction;
import tool.mapeditor.actions.ResourceViewResizeAction;
import tool.mapeditor.application.ResourceDescriptor;
import tool.resourcemanager.Resources;
import tool.util.WidgetUtil;

public class ResourceView extends ViewPart {

	public static final String ID = "mapeditor.SourceView"; //$NON-NLS-1$
static final int COLUMN_NUM = 3;
	
	Combo resourceList;
	List<Resource> resources = new ArrayList<Resource>();
	Composite resourcePanel;
	ScrolledComposite scrolledComposite;
	ResourceLabel previousLabel;
	Listener resourceListener = new Listener(){
		public void handleEvent(Event e){
			harbor.releaseCurrentResource();
			ResourceLabel label = (ResourceLabel)e.widget;
			label.setSelected(true);
			if(WidgetUtil.valid(previousLabel) && previousLabel != label){
				previousLabel.setSelected(false);
			}
			previousLabel = label;
			if(e.button == 1){
				currentResource = (Drawable)label.getData();
				harbor.setCanvasCursor(Resources.CURSOR_HAND);
			}else if(e.button == 3){
				showOperationList((Drawable)label.getData());
			}
		}
	};
	Menu popupMenu;
	
	NewResourceAction newResourceAction;
	DeleteSourceAction deleteResourceAction;
	AddResourceItemAction addResourceItemAction;
	ResourcePrefixAction resPropAction;
	ResourceViewResizeAction resourceViewResizeAction;
	
	Drawable currentResource;
	WindowsHarbor harbor;
	/**
	 * current resource group
	 */
	Resource resourceSet;
	boolean adaptToContainer = true;

	public ResourceView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		container.setLayout(gl);
		resourceList = new Combo(container, SWT.READ_ONLY);
		resourceList.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		resourceList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				int i = resourceList.getSelectionIndex();
				if(i >= 0){
					Resource r = resources.get(i);
					setResource(r);
				}
			}
		});
		scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		scrolledComposite.getHorizontalBar().addListener(SWT.Selection, new Listener(){
			public void handleEvent(Event e){
				harbor.redrawMap();
			}
		});
		resourcePanel = new Composite(scrolledComposite, SWT.NONE);
		resourcePanel.setLayout(new GridLayout(COLUMN_NUM, true));
		resourcePanel.setBackground(Resources.BG_BLUE);
		resourcePanel.addListener(SWT.Resize, new Listener(){
			public void handleEvent(Event e){
				for(Control c : resourcePanel.getChildren())
					c.redraw();
			}
		});
		scrolledComposite.setContent(resourcePanel);
		scrolledComposite.setMinSize(resourcePanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		popupMenu = new Menu(resourcePanel.getShell(), SWT.POP_UP);
		popupMenu.setVisible(false);
		resourcePanel.setMenu(popupMenu);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		newResourceAction = new NewResourceAction(window);
		deleteResourceAction = new DeleteSourceAction();
		addResourceItemAction = new AddResourceItemAction();
		resPropAction = new ResourcePrefixAction(window);
		resourceViewResizeAction = new ResourceViewResizeAction();
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(resourceViewResizeAction);
		toolbarManager.add(resPropAction);
		toolbarManager.add(new Separator());
		toolbarManager.add(newResourceAction);
		toolbarManager.add(deleteResourceAction);
		toolbarManager.add(addResourceItemAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {}

	@Override
	public void setFocus() {
		// Set the focus
	}
	
	public void addResource(Resource resource){
		resourceList.add(resource.getName());
		resources.add(resource);
		setResource(resource);
	}
	
	public void setResource(Resource resource){
		resourceSet = resource;
		for(Control c : resourcePanel.getChildren())
			c.dispose();
		if(resource != null){
			resourceList.setText(resource.getName());
			for(Item item : resource.getResourceItems()){
				createResourceLabel(item);
			}
		}else{
			String item = resourceList.getText();
			if(!item.isEmpty())
				resourceList.remove(item);
		}
		resourcePanel.layout();
		scrolledComposite.setMinSize(resourcePanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	void createResourceLabel(Drawable drawable){
		ResourceLabel label = new ResourceLabel(resourcePanel, drawable);
		label.addListener(SWT.MouseUp, resourceListener);
		label.setToolTipText(drawable.getName());
		Rectangle bounds = drawable.getBounds();
		int w = bounds.width, h = bounds.height;
		if(adaptToContainer){
			int maxW = scrolledComposite.getClientArea().width / COLUMN_NUM;
			if(w > maxW)
				w = maxW;
			h = (int) (w / (float) bounds.width * bounds.height);
		}
		label.setSize(w, h);
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		gd.minimumWidth = w;
		gd.minimumHeight = h;
		label.setLayoutData(gd);
		label.setData(drawable);
	}
	
	void clearCurrentResource(){
		currentResource = null;
		harbor.setCanvasCursor(Resources.CURSOR_ARROW);
	}

	void addResourceItem(String f, String name) {
		if(resourceSet == null){
			harbor.alert("add resource set first");
			return;
		}
		resourceSet.addResourceItem(f, name);
		setResource(resourceSet);
	}
	
	void showOperationList(final Drawable d){
		Enum<?>[] list = d.getOperationList();
		if(list == null)
			return;
		for(MenuItem item : popupMenu.getItems()){
			item.dispose();
		}
		int idx = 0;
		for(final Enum<?> e : list){
			if(e.ordinal() == 0){
				new MenuItem(popupMenu, SWT.SEPARATOR);
			}else{
				String s = e.name();
				MenuItem item = new MenuItem(popupMenu, SWT.PUSH);
				item.setText(s);
				item.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent event){
						if(d.operate(e)){
							setResource(resourceSet);
						}else{
							previousLabel.setSelected(true);
						}
						resourcePanel.layout();
					}
				});
			}
			++idx;
		}
		popupMenu.setVisible(true);
	}
	
	public void resizePanel(boolean adaptToContainer){
		if(this.adaptToContainer == adaptToContainer)
			return;
		this.adaptToContainer = adaptToContainer;
		setResource(resourceSet);
	}

}
