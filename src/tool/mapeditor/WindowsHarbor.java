package tool.mapeditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * The interface transitting information between the main application and the
 * window system.
 * 
 * @author caijw
 * 
 */
public class WindowsHarbor {
	IWorkbenchWindow window;
	boolean gridAlign;
	byte editMode;

	public WindowsHarbor(IWorkbenchWindow window) {
		if(window == null)
			throw new IllegalArgumentException("Window is null!");
		this.window = window;
	}
	
	public MapView showMapView(){
		MapView v = null;
		try {
			v = (MapView)window.getActivePage().showView(MapView.ID);
			v.harbor = this;
		} catch (PartInitException e) {
			e.printStackTrace();
			v = null;
		}
		return v;
	}
	
	public MapView findMapView(){
		return (MapView)window.getActivePage().findView(MapView.ID);
	}
	
	public LayerView showLayerView(){
		LayerView lv = null;
		try {
			lv = (LayerView)window.getActivePage().showView(LayerView.ID);
			lv.harbor = this;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return lv;
	}
	
	public ResourceView showSourceView(){
		ResourceView sv = null;
		try {
			sv = (ResourceView)window.getActivePage().showView(ResourceView.ID);
			sv.harbor = this;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return sv;
	}

	public void createMapLayer() {
		try {
			LayerView view = (LayerView)window.getActivePage().showView(LayerView.ID);
			view.createLayer();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	public WorldMapEdit getCurrentMapStruct(){
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		return v.canvas.mapEdit;
	}
	
	public Drawable getSelectedDrawable(){
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		return v.canvas.firedUnit;
	}

	public void removeLayer() {
		try {
			LayerView view = (LayerView)window.getActivePage().showView(LayerView.ID);
			view.removeLayer();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the selected resource to be placed onto map.
	 * @return
	 */
	Drawable getCurrentResource(){
		ResourceView v = (ResourceView)window.getActivePage().findView(ResourceView.ID);
		return v.currentResource;
	}
	
	public ResourceView findResourceView(){
		return (ResourceView)window.getActivePage().findView(ResourceView.ID);
	}
	
	boolean isPlacing(){
		ResourceView v = (ResourceView)window.getActivePage().findView(ResourceView.ID);
		MapView mv = (MapView)window.getActivePage().findView(MapView.ID);
		Drawable d = mv.canvas.firedUnit;
		return v.currentResource  != null || (d != null && d.getModelCopy() != null);
	}
	
	void releaseCurrentResource(){
		ResourceView v = (ResourceView)window.getActivePage().findView(ResourceView.ID);
		v.currentResource = null;
		MapView mv = (MapView)window.getActivePage().findView(MapView.ID);
		if(mv.canvas.firedUnit != null){
			mv.canvas.firedUnit.releaseCopy();
			mv.canvas.firedUnit = null;
		}
	}
	
	public void setGridAlign(boolean align){
		gridAlign = align;
	}

	public void setEditMode(byte mode) {
		editMode = mode;
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		v.uncheckExcept(mode);
		if(mode != WorldMapEdit.MODE_PLACE){
			releaseCurrentResource();
		}
	}

	public void redrawMap() {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		v.canvas.redraw();
	}

	public Drawable getCurrentRegion() {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		return v.canvas.firedRegion;
	}

	public void notifyPlayThread() {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		if(v.mapPainter != null){
			v.animationPlayer.notifyPlay();
		}
	}

	public Shell getShell() {
		return window.getShell();
	}
	
	public IWorkbenchWindow getWorkbenchWindow(){
		return window;
	}

	public void alert(String message) {
		MessageDialog.openError(window.getShell(), "警告", message);
	}
	
	public void inform(String message){
		MessageDialog.openInformation(window.getShell(), "提示", message);
	}
	
	public void setMapTitle(String title){
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		v.setMapTitle(title);
	}

	public Resource getCurrentResourceSet() {
		ResourceView v = (ResourceView)window.getActivePage().findView(ResourceView.ID);
		return v.resourceSet;
	}

	public void scollMapToShow(int x, int y, int width, int height) {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		Rectangle client = v.canvas.rectCanvas;
		v.canvas.scrollCanvas(x - client.width / 2, y - client.height / 2);
		v.canvas.drawHighLight(x, y, width, height);
		v.canvas.redraw();
	}

	public void addResourceItem(String f, String name) {
		ResourceView v = findResourceView();
		v.addResourceItem(f, name);
	}

	public void setCanvasCursor(Cursor cursor) {}

}
