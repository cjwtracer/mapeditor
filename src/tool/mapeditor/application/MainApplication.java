package tool.mapeditor.application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import tool.account.Login;
import tool.io.Configuration;
import tool.io.DataCrackException;
import tool.io.ProjectReader;
import tool.io.ProjectWriter;
import tool.io.Version;
import tool.mapeditor.Application;
import tool.mapeditor.Drawable;
import tool.mapeditor.LayerView;
import tool.mapeditor.MapView;
import tool.mapeditor.ResourceView;
import tool.mapeditor.WindowsHarbor;
import tool.mapeditor.WorldMapEdit;
import tool.mapeditor.dialogs.ChangePropertyDialog;
import tool.mapeditor.dialogs.LocusPropertyDialog;
import tool.mapeditor.dialogs.UnitPropertyDialog;
import tool.mapeditor.dialogs.mapProperties.MapPropertyDialog;
import tool.mapeditor.dialogs.mapProperties.PropertyEditDialog;
import tool.model.Animation;
import tool.model.Model;
import tool.model.Project;
import tool.model.RegionPolygon;
import tool.model.ResourceSet;
import tool.model.Tile;
import tool.model.Unit;
import tool.model.WorldMap;
import tool.mapeditor.dialogs.ConfirmDialog;
import tool.resourcemanager.SWTResourceManager;
import tool.util.FileUtil;
import tool.util.GLUtil;
import tool.util.ImageUtil;
import tool.util.WidgetUtil;

/**
 * The main application of the map editor handling information transition and
 * processing the logics.
 * 
 * @author caijw
 * 
 */
public class MainApplication {
	public static final String ROOT = "MapEditor";
	public static final String CONFIG_ROOT = ".MapEditor";
	
	private static MainApplication instance;
	MapProject project;
	Configuration configuration;
	Version version;
	/**
	 * flag indicating whether to create a new work space when it doesn't exists
	 */
	boolean createWorkspace;
	/**
	 * the handler to the window system
	 */
	WindowsHarbor windowsHarbor;
	
	private MainApplication(){}
	
	public static final MainApplication getInstance(){
		if(instance == null){
			instance = new MainApplication();
		}
		return instance;
	}
	
	public void prepareProject(){
		configuration = new Configuration();
		try{
			configuration.load();
		}catch(FileNotFoundException ex){
			createWorkspace = true;
			return;
		}
		version = new Version();
		try {
			version.load();
		} catch (FileNotFoundException e) {
			System.err.println("Version file doesn't exist!\n" + e);
		}
		if(!version.legalVersion()){
			System.err.println("Please update your software!");
			Application.getInstance().stop();
		}
		try{
			project = (MapProject)ProjectReader.loadProject(configuration.getWorkspacePath() + File.separator + MapProject.PRO);
			project.path = configuration.getWorkspacePath() + File.separator;
			
			Tile.parseProperties(project.getConfigDir() + "tile.prop");
			RegionPolygon.parseProperties(project.getConfigDir() + "region.prop");
			Unit.parseProperties(project.getConfigDir() + "unit.prop");
			
			for(ResourceSet res : project.getResourceGroup().values()){
				res.setVersion(version.currentVersion());
				ResourceSet.ResourceIO.loadResourceSet(project, res);
			}
		}catch(FileNotFoundException ex){
			System.err.println("Workspace does not exist!\n" + ex);
			createWorkspace(configuration.getWorkspacePath());
		}catch(DataCrackException ex1){
			throw new RuntimeException(ex1);
		}
		
		clearMapDir();
	}
	
	void clearMapDir(){
		String dir = project.getMapDir();
		for(File file : new File(dir).listFiles(new FileFilter(){

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(Project.MAP_EXT);
			}
			
		})){
			String fn = file.getName();
			int fID = Integer.valueOf(fn.substring(0, fn.indexOf(Project.MAP_EXT)));
			if(project.getMapGroup().get(fID) == null){
				FileUtil.deleteFile(file);
				String bg = new StringBuffer(file.getParent()).append(File.separator).append(fID).append(WorldMap.BACKGROUND_EXT).toString();
				for(String ext : ImageUtil.SURPORTED_FORMAT){
					FileUtil.deleteFile(bg + ext);
				}
			}
		}
	}
	
	public boolean isCreateWorkspace(){
		return createWorkspace;
	}

	/**
	 * Create a work space by the given directory name.
	 * @param dir
	 */
	public void createWorkspace(String dir) {
		File file = new File(dir);
		file.mkdirs();
		project = new MapProject(dir);
		String proFile = dir + File.separator + MapProject.PRO;
		ProjectWriter.writeProject(proFile, project);
		configuration.setWorkspacePath(dir);
		configuration.save();
		version = new Version();
		version.sycVersion();
		version.save();
	}
	
	public MapProject getProject(){
		return project;
	}
	
	public boolean beforeExit(){
		for(WorldMap m : project.getMapGroup().values()){
			if(m.dirty){
				boolean b = confirm("是否保存？");
				if(b){
					saveProject();
				}
				break;
			}
		}
		return true;
	}
	
	public void release(){
		ResourceDescriptor.releaseResources();
		SWTResourceManager.dispose();
	}
	
	public void setWidgetsHarbor(WindowsHarbor widgetsHarbor){
		if(widgetsHarbor == null)
			throw new IllegalArgumentException();
		this.windowsHarbor = widgetsHarbor;
	}

	/**
	 * Add the created resource set to the window system.
	 * @param resource
	 */
	public void addResource(ResourceDescriptor resource) {
		ResourceView view = windowsHarbor.showSourceView();
		view.addResource(resource);
		for(MapDescriptor m : MapDescriptor.stack.values()){
			m.updateImage();
		}
		windowsHarbor.redrawMap();
	}

	/**
	 * Create a map layer at the selected position of the layer array.
	 */
	public void createMapLayer() {
		WorldMapEdit md = windowsHarbor.getCurrentMapStruct();
		if(md != null){
			md.createLayer(md.getCurrentLayerIndex() + 1);
			windowsHarbor.createMapLayer();
		}
	}

	/**
	 * Remove the selected map layer.
	 */
	public void removeLayer() {
		WorldMapEdit md = windowsHarbor.getCurrentMapStruct();
		if(md != null){
			md.removeLayer(md.getCurrentLayerIndex());
			windowsHarbor.removeLayer();
		}
	}

	public void saveProject() {
		boolean ok = true;
		for (ResourceSet rs : project.getResourceGroup().values()) {
			String f = rs.getId() + Project.RES_EXT;
			String srcf =  project.getResourceDir() + rs.getId() + File.separator + f;
			String tmpf = project.getResourceDir() + rs.getId() + "/tmp" + f;
			FileUtil.copyFile(srcf, tmpf);
			try {
				ResourceSet.ResourceIO.saveResourceSet(project, rs);
			} catch (DataCrackException ex) {
				ok = false;
				FileUtil.copyFile(tmpf, srcf);
			}finally{
				FileUtil.deleteFile(tmpf);
			}
		}
		for(WorldMap m : project.getMapGroup().values()){
			if(!m.dirty)
				continue;
			boolean suc = true;
			String f = m.getFileID() + Project.MAP_EXT;
			String srcf = project.getMapDir() + f;
			String tmpf = project.getMapDir() + "tmp" + f;
			if(new File(srcf).exists()){
				FileUtil.copyFile(srcf, tmpf);
			}
			try{
				WorldMap.save(project, m);
			}catch(DataCrackException ex){
				ok = false;
				suc = false;
			}
			if(suc){
				FileUtil.deleteFile(tmpf);
				if(m != getCurrentMap().worldMap)
					m.dirty = false;
			}
		}
		if(ok){
			version.save();
		}
		ProjectWriter.writeProject(project.path + MapProject.PRO, project);
		savePrimaryView();
	}
	
	void savePrimaryView(){
		if(getCurrentMap() != null){
			FileWriter fw = null;
			try{
				fw = new FileWriter(project.getMapDir() + "map.pri");
				fw.write(getCurrentMap().getMapFileId());
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{if(fw != null)fw.close();}catch(IOException ex){}
			}
		}
	}
	
	public void setGridAlign(boolean align){
		windowsHarbor.setGridAlign(align);
		WorldMapEdit m = windowsHarbor.getCurrentMapStruct();
		if(m != null){
			m.setEditType(align ? WorldMapEdit.TYPE_TILE : WorldMapEdit.TYPE_UNIT);
		}
	}

	/**
	 * Set the current edit mode of the map view.
	 * @param checked
	 */
	public void setEditModeSelect(boolean checked) {
		byte mode = checked ? WorldMapEdit.MODE_MOVE : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		setCurrentMapEditMode(mode);
	}

	/**
	 * @see #setEditModeSelect(boolean)
	 * @param checked
	 */
	public void setEditModeRegionRect(boolean checked) {
		byte mode = checked ? WorldMapEdit.MODE_REGION_RECT : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		setCurrentMapEditMode(mode);
	}

	/**
	 * @see #setEditModeSelect(boolean)
	 * @param checked
	 */
	public void setEditModeVertexAdd(boolean checked) {
		byte mode = checked ? WorldMapEdit.MODE_VERTEX_ADD : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		setCurrentMapEditMode(mode);
	}

	/**
	 * @see #setEditModeSelect(boolean)
	 * @param checked
	 */
	public void setEditModeVertexDel(boolean checked) {
		byte mode = checked ? WorldMapEdit.MODE_VERTEX_DEL : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		setCurrentMapEditMode(mode);
	}

	/**
	 * @see #setEditModeSelect(boolean)
	 * @param checked
	 */
	public void setEditModeRegionShape(boolean checked) {
		byte mode = checked ? WorldMapEdit.MODE_SHAPE_REGION : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		setCurrentMapEditMode(mode);
	}
	
	/**
	 * Set the edit mode according to the given value to the current map.
	 * @param mode
	 * @return
	 */
	MapDescriptor setCurrentMapEditMode(byte mode){
		WorldMapEdit m = windowsHarbor.getCurrentMapStruct();
		if(m != null){
			m.setEditMode(mode);
		}
		return (MapDescriptor)m;
	}

	/**
	 * Set the rotation of the selected drawable.
	 * @param v
	 */
	public void setCurrentRotation(int v) {
		Drawable d = windowsHarbor.getSelectedDrawable();
		if(d == null)
			return;
		d.setRotation(v);
		windowsHarbor.redrawMap();
	}

	/**
	 * Set the alpha value of the selected drawable.
	 * 
	 * @param v
	 */
	public void setCurrentTransparency(int v) {
		Drawable d = windowsHarbor.getSelectedDrawable();
		if(d == null)
			return;
		d.setAlpha(v);
		windowsHarbor.redrawMap();
	}

	/**
	 * Set the scaling of the selected drawable.
	 * @param v
	 */
	public void setCurrentScale(float v) {
		Drawable d = windowsHarbor.getSelectedDrawable();
		if(d == null)
			return;
		d.setScale(v);
		windowsHarbor.redrawMap();
	}
	
	/**
	 * Launch the window system.
	 */
	public void launchWorkbench(){
		windowsHarbor.showSourceView();
		windowsHarbor.showMapView();
		windowsHarbor.showLayerView();
	}

	/**
	 * Load the datas and display them to the window system.
	 */
	public void launchProject() {
		launchResources();
		int id = getPrimaryMap();
		openMap(id);
	}
	
	/**
	 * Load datas.
	 */
	private void launchResources(){
		ResourceView sv = windowsHarbor.showSourceView();
		for(ResourceSet rs : project.getResourceGroup().values()){
			ResourceDescriptor rsd = ResourceDescriptor.getDescriptor(rs);
			rsd.load(project);
			sv.addResource(rsd);
		}
	}
	
	/**
	 * Get the primarily presented map.
	 * @return
	 */
	private int getPrimaryMap(){
		int m = -1;
		String mapPri = project.getMapDir() + "map.pri";
		if(new File(mapPri).exists()){
			FileReader fr = null;
			try{
				fr = new FileReader(mapPri);
				int id = fr.read();
				if(id != -1){
					m = id;
				}
			}catch(IOException ex){
				ex.printStackTrace();
			}finally{
				try{if(fr != null)fr.close();}catch(IOException ex1){}
			}
		}else{
			int i = 0;
			int s = project.getMapGroup().size();
			do{
				m = i++;
			}while(project.getMapGroup().get(m) == null && i < s);
		}
		
		return m;
	}
	
	/**
	 * Show all the view parts consisting the operation interface of the map.
	 * 
	 * @param map
	 */
	public void showFullViews(MapDescriptor map){
		MapView mv = windowsHarbor.showMapView();
		mv.setWorldMap(map, map);
		LayerView lv = windowsHarbor.showLayerView();
		lv.setWorldMap(map, map);
	}

	/**
	 * Repaint the canvas of the window system.
	 */
	public void repaintMapCanvas() {
		windowsHarbor.redrawMap();
	}

	public RegionDescriptor getCurrentRegion() {
		return (RegionDescriptor)windowsHarbor.getCurrentRegion();
	}

	/**
	 * Map the polygon region of the current map to tiles.
	 * 
	 * @param mapping
	 */
	public void mappingRegion(boolean mapping) {
		byte mode = mapping ? WorldMapEdit.MODE_MAPPING : WorldMapEdit.MODE_PLACE;
		windowsHarbor.setEditMode(mode);
		MapDescriptor m = setCurrentMapEditMode(mode);
		m.mappingRegion(mapping);
		windowsHarbor.redrawMap();
	}
	
	/**
	 * Export the datas according to the requirements.
	 */
	public void exportMapDatas(){
		try {
			for(WorldMap m : project.getMapGroup().values()){
				DataWriter.exportMapDatas(m, project);
			}
		} catch (DataCrackException e) {
			alert("exportMap failed");
			e.printStackTrace();
		}
	}

	/**
	 * Import the background of the current map.
	 * 
	 * @param img
	 *            absolute path of image
	 * @param fileName
	 *            the string representing the background ID
	 */
	public void createMapBackground(String img, String fileName, MapDescriptor md) {
		if(img == null || fileName == null || md == null)
			throw new IllegalArgumentException("Invalid image!");
		if(!FileUtil.isNumericFileName(fileName)){//there is a ID for background
			windowsHarbor.alert("图片名必须为数字");
			return;
		}
		String ext = img.substring(img.lastIndexOf("."));
		if(md != null){
			md.background = GLUtil.loadTexture(img);
			md.worldMap.setBackground(md.getMapFileId() + WorldMap.BACKGROUND_EXT + ext);
			int lastIdx = fileName.lastIndexOf(".");
			md.worldMap.setBackgroundID(Integer.valueOf(fileName.substring(0, lastIdx == -1 ? fileName.length() : lastIdx)));
		}
		FileUtil.copyFile(img, project.getMapDir() + md.getMapFileId() + WorldMap.BACKGROUND_EXT + ext);
		windowsHarbor.redrawMap();
	}

	/**
	 * Start or stop the displaying of the animations on the current map.
	 * @param isPlay
	 */
	public void playAnimation(boolean isPlay) {
		MapDescriptor md = (MapDescriptor)windowsHarbor.getCurrentMapStruct();
		md.setPlay(isPlay);
		if(isPlay){
			md.initAnimations();
			windowsHarbor.notifyPlayThread();
		}else{
			md.resetAnimations();
		}
		windowsHarbor.redrawMap();
	}
	
	void showChangePropertyDialog(AnimationDescriptor anim){
		Shell sh = windowsHarbor.getShell();
		ChangePropertyDialog dlg = new ChangePropertyDialog(sh, anim);
		dlg.open();
	}

	void showLocusPropertyDialog(Animation animation) {
		LocusPropertyDialog dlg = new LocusPropertyDialog(windowsHarbor.getShell(), animation);
		dlg.open();
	}

	void showUnitPropertyDialog(Unit unit) {
		UnitPropertyDialog dlg = new UnitPropertyDialog(windowsHarbor.getShell(), unit);
		dlg.open();
	}

	public void exportNPCDatas() {
		try {
			for(WorldMap map : project.getMapGroup().values()){
				DataWriter.writeNPC(map, project);
			}
		} catch (DataCrackException e) {
			alert("export monsters failed");
			e.printStackTrace();
		}
	}

	public void mergeNPCDatas() {
		try {
			DataWriter.writeAllNPC(project.getMapGroup().values(), project);
		} catch (Exception e) {
			alert("merge monsters failed");
			e.printStackTrace();
		}
	}

	public void importNPCModel() {
		
	}

	public void showMapPropertyDialog() {
		MapDescriptor md = getCurrentMap();
		WorldMap m = md.worldMap;
		MapPropertyDialog dlg = new MapPropertyDialog(windowsHarbor.getShell(), m, md.getCurrentLayerIndex());
		dlg.open();
	}

	/**
	 * Delete the map with the given map ID, without saving the project.
	 * @param mapID
	 */
	public void deleteMap(int mapID) {
		WorldMap map = project.getMapGroup().remove(mapID);
		if(map != null){
			if(MapDescriptor.stack.get(mapID) != null){
				MapDescriptor md = MapDescriptor.getDescriptor(map);
				md.release();
				if(md == getCurrentMap())
					showFullViews(null);
			}
			MapDescriptor.stack.remove(mapID);
		}
	}
	
	public MapDescriptor getCurrentMap(){
		if(windowsHarbor == null)
			return null;
		return (MapDescriptor)windowsHarbor.getCurrentMapStruct();
	}

	/**
	 * Export maps.
	 */
	public void exportMaps(List<WorldMap> maps, String dir) {
		if(maps == null || dir == null){
			throw new IllegalArgumentException("args can't be null");
		}
		if(maps.size() == 0)
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		dir = new StringBuffer(dir).append(File.separator).append(sdf.format(new Date())).append(File.separator).toString();
		new File(dir).mkdir();
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(dir + "maps.index");
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(maps.size());
			for(WorldMap m : maps){
				dos.writeInt(m.getFileID());
			}
			dos.close();
		}catch(IOException e){
			alert("create index failed");
			return;
		}finally{
			try{if(fos != null)fos.close();}catch(IOException ex){}
		}
		for(WorldMap m : maps){
			String p = new StringBuffer(dir).append(m.getFileID()).append(Project.MAP_EXT).toString();
			try{
				WorldMap.exportMap(p, project, m);
			}catch(DataCrackException e){
				alert("export map failed: " + m.getFileID());
				break;
			}
			String bg = project.getMapDir() + m.getBackground();
			if(new File(bg).exists())
				FileUtil.copyFile(bg, dir + m.getBackground());
		}
	}

	/**
	 * Import existing maps and its relative datas into workspace, and save its
	 * data to the current project. Note: except for importing or creating a map, 
	 * the hole project won't be saved.
	 */
	public void importMap() {
		FileDialog dlg = new FileDialog(windowsHarbor.getShell(), SWT.OPEN);
		dlg.setFilterNames(new String[]{"index file"});
		dlg.setFilterExtensions(new String[]{"*.index"});
		String p = dlg.open();
		if(p == null)
			return;
		
		List<Integer> ids = new ArrayList<Integer>();
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(p);
			DataInputStream dis = new DataInputStream(fis);
			int s = dis.readInt();
			for(int i = 0; i < s; ++i){
				ids.add(dis.readInt());
			}
		}catch(IOException e){
			alert("read index file failed");
			return;
		}finally{
			try{if(fis != null)fis.close();}catch(IOException ex){};
		}
		
		String dir = p.substring(0, p.indexOf(dlg.getFileName()));
		WorldMap last = null;
		for(int id : ids){
			String mp = new StringBuffer(dir).append(id).append(Project.MAP_EXT).toString();
			try{
				WorldMap m = WorldMap.importMap(mp, project);
				try{
					WorldMap.save(project, m);
				}catch(DataCrackException e){
					alert("processing storage failed: " + m.getFileID());
					continue;
				}
				project.getMapGroup().put(m.getFileID(), m);
				//import background image
				String bg = m.getBackground();
				if(bg != null){
					String from = dir + bg;
					m.setBackground(m.composeBgName(bg.substring(bg.lastIndexOf("."))));
					FileUtil.copyFile(from, project.getMapDir() + m.getBackground());
				}
				
				last = m;
			}catch(DataCrackException e){
				alert("import map failed: " + id);
				continue;
			} catch (ClassNotFoundException e) {
				alert("invalid format: " + id);
				continue;
			}
		}
		ProjectWriter.writeProject(project.path + MapProject.PRO, project);
		
		showMap(last);
		savePrimaryView();
	}

	/**
	 * Create a new map in the memory and, also must, create its datas file in
	 * the map directory immediately.
	 * 
	 * @param name
	 * @param width
	 * @param height
	 * @param cellW
	 * @param cellH
	 * @param tileWidth
	 * @param tileHeight
	 * @param viewType
	 * @return
	 * @throws DataCrackException
	 */
	public WorldMap newMap(String name, int width, int height, int cellW, int cellH, int tileWidth, int tileHeight, byte viewType) throws DataCrackException{
		WorldMap map = new WorldMap(name, (short)width, (short)height, (short)cellW, (short)cellH, (short)tileWidth, (short)tileHeight, viewType, version.currentVersion());
		WorldMap.generateId(project, map);
		project.getMapGroup().put(map.getFileID(), map);
		WorldMap.save(project, map);
		return map;
	}

	public int getFileVersion() {
		return version.currentVersion();
	}

	public void resetMapExpID(int id) {
		MapDescriptor md = getCurrentMap();
		windowsHarbor.setMapTitle(md.getMapName() + " [" + id + "]");
	}

	public void resetMapName(String name) {
		MapDescriptor md = getCurrentMap();
		windowsHarbor.setMapTitle(name + " [" + md.getMapID() + "]");
	}

	public void verify() {
//		Login login = new Login();
//		try {
//			if(!login.verify()){
//				Application.getInstance().stop();
//			}
//		} catch(FileNotFoundException ex1){
//			ex1.printStackTrace();
//		}catch (IOException ex) {
//			ex.printStackTrace();
//			Application.getInstance().stop();
//		}
	}

	/**
	 * The result of operation won't be saved immediately.
	 */
	public void deleteResourceSet() {
		ResourceDescriptor set = (ResourceDescriptor)windowsHarbor.getCurrentResourceSet();
		if(set == null)
			return;
		short resourceID = (short)set.resourceSet.getId();
		project.getResourceGroup().remove(resourceID);
		set.dispose();
		ResourceDescriptor.stack.remove(resourceID);
		ResourceView v = windowsHarbor.findResourceView();
		v.setResource(null);
		for(MapDescriptor m : MapDescriptor.stack.values()){
			m.updateImage();
		}
		windowsHarbor.redrawMap();
	}

	public void showPropertyEditDialog(Shell shell, Model model, String title) {
		if(shell == null)
			shell = windowsHarbor.getShell();
		PropertyEditDialog dlg = new PropertyEditDialog(shell, model, title);
		dlg.open();
	}

	public void showInView(int x, int y, int width, int height) {
		windowsHarbor.scollMapToShow(x, y, width, height);
	}
	
	public void alert(String msg){
		windowsHarbor.alert(msg);
	}
	
	public void inform(String msg){
		windowsHarbor.inform(msg);
	}

	/**
	 * Load and show the map specified by the file ID.
	 */
	public void openMap(int fileID) {
		WorldMap map = project.getMapGroup().get(fileID);
		if(map == null)
			return;
		try {
			map.setVersion(getFileVersion());
			WorldMap.load(project, map);
			showMap(map);
		} catch (DataCrackException e) {
			map.dirty = false;
			e.printStackTrace();
			alert("load map failed: " + fileID);
		}
	}

	public void showMap(WorldMap map) {
		if(map == null)
			return;
		MapDescriptor former = getCurrentMap();
		if (former != null){
			if(former.worldMap == map)
				return;
			former.release();
		}
		map.dirty = true;
		showFullViews(MapDescriptor.getDescriptor(map));
	}
	
	public boolean confirm(String msg){
		ConfirmDialog dlg = new ConfirmDialog(windowsHarbor.getShell(), msg);
		return dlg.open();
	}

	public void addResourceItem() {
		FileDialog dlg = WidgetUtil.imageDialog(windowsHarbor.getShell(), SWT.OPEN);
		String f = dlg.open();
		if(f != null && new File(f).exists()){
			String name = dlg.getFileName();
			name = name.substring(0, name.lastIndexOf("."));
			windowsHarbor.addResourceItem(f, name);
		}
	}

	public ResourceDescriptor getCurrentResource() {
		return (ResourceDescriptor)windowsHarbor.getCurrentResourceSet();
	}

	public void resizeResourceViewImages(boolean adaptToContainer) {
		windowsHarbor.findResourceView().resizePanel(adaptToContainer);
	}

	public void updateMap() {
		// TODO Auto-generated method stub
		
	}

	public Shell getShell() {
		return windowsHarbor.getShell();
	}
	
	public IWorkbenchWindow getWorkbenchWindow(){
		return windowsHarbor.getWorkbenchWindow();
	}

	public ResourceDescriptor newResourceSet(String setName, Map<String, File> importedFiles) {
		int s = importedFiles.size();
		String[] names = new String[s];
		int i = 0;
		for(String fp : importedFiles.keySet()){
			String fn = importedFiles.get(fp).getName();
			names[i++] = fn.substring(0, fn.lastIndexOf("."));
		}
		ResourceSet resSet = new ResourceSet(setName, names);
		ResourceSet.generateId(project, resSet);
		project.getResourceGroup().put(resSet.getId(), resSet);
		ResourceDescriptor resource = ResourceDescriptor.getDescriptor(resSet);
		
		List<String> imgs = new ArrayList<String>(s);
		String resDir = project.getResourceDir() + resSet.getId() + File.separator;
		FileUtil.checkPath(resDir);
		for(String fp : importedFiles.keySet()){
			String fn = importedFiles.get(fp).getName();
			String to = resDir + fn;
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[]{new ImageData(fp)};
			loader.save(to, ImageUtil.getFormat(fn.substring(fn.lastIndexOf("."))));
			imgs.add(to);
		}
		resource.setResourceItems(imgs);
		
		return resource;
	}
	
}
