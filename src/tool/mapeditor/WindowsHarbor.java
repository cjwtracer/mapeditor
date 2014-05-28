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

	/**
	 * 在地图层面板中移除选中的层
	 */
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
	
	/**
	 * 获取资源面板引用
	 * @return
	 */
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

	/**
	 * 设置当前的编辑模式，在不同的编辑模式下，地图上可编辑的元素是不同的
	 * @param mode
	 */
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

	/**
	 * 获取当前选中的地图区域
	 * @return
	 */
	public Drawable getCurrentRegion() {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		return v.canvas.firedRegion;
	}

	/**
	 * 启动地图动画
	 */
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

	/**
	 * 警告对话框
	 * @param message
	 */
	public void alert(String message) {
		MessageDialog.openError(window.getShell(), "警告", message);
	}
	
	/**
	 * 提示对话框
	 * @param message
	 */
	public void inform(String message){
		MessageDialog.openInformation(window.getShell(), "提示", message);
	}

	/**
	 * 获取当前选择的资源组
	 * @return
	 */
	public Resource getCurrentResourceSet() {
		ResourceView v = (ResourceView)window.getActivePage().findView(ResourceView.ID);
		return v.resourceSet;
	}

	/**
	 * 滚动地图，使得指定区域在地图面板中可见
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void scollMapToShow(int x, int y, int width, int height) {
		MapView v = (MapView)window.getActivePage().findView(MapView.ID);
		Rectangle client = v.canvas.rectCanvas;
		v.canvas.scrollCanvas(x - client.width / 2, y - client.height / 2);
		v.canvas.drawHighLight(x, y, width, height);
		v.canvas.redraw();
	}

	/**
	 * 在当前选择的资源组中，添加指定名称和来源的图片
	 * 
	 * @param f
	 *            图片来源
	 * @param name
	 *            资源项名称
	 */
	public void addResourceItem(String f, String name) {
		ResourceView v = findResourceView();
		v.addResourceItem(f, name);
	}

	public void setCanvasCursor(Cursor cursor) {}

}
