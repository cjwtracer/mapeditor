package tool.mapeditor.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import tool.io.DataCrackException;
import tool.mapeditor.Drawable;
import tool.mapeditor.Resource;
import tool.model.ResourceItem;
import tool.model.ResourceSet;
import tool.mapeditor.Resource.Item;
import tool.mapeditor.application.Descriptor;
import tool.mapeditor.application.ResourceDescriptor;
import tool.mapeditor.application.MapProject;
import tool.util.Constants;
import tool.util.FileUtil;
import tool.util.GLUtil;
import tool.util.ImageUtil;
import tool.util.WidgetUtil;

/**
 * The wrapper of a given resource set.
 * 
 * important: Principle of resource creation and erasing --- no saving, no
 * modification, except for replacing.
 * 
 * @author caijw
 * 
 */
public class ResourceDescriptor extends Descriptor implements Resource{
	public final static String RES_SEP = ":";
	final static String BACKGROUND_NAME = "name";
	final static String BACKGROUND_INDEX = "index.prop";
	final static String CLASSES_KEY = "classes";
	final static String DEFAULT_CLASS = "default";
	
	public static String[] resourceClasses = {};
	static int currentRes = 0;
	
	static HashMap<Short, ResourceDescriptor> stack = new HashMap<Short, ResourceDescriptor>();

	static Texture background;
	
	/**
	 * Descriptor of the resource item model.
	 */
	public static class ItemDescriptor extends Descriptor implements Drawable, Item {
		private static enum OPERS{SEP, 替换图片, 删除};
		static final Enum<?>[] opers = new Enum<?>[]{OPERS.替换图片, OPERS.SEP, OPERS.删除};
		/**
		 * the underlying model
		 */
		ResourceItem resourceItem;
		Image image;
		ImageData imageData;
		Texture texture;
		Map<String, Texture> texes;
		Rectangle bounds;
		ResourceDescriptor resSet;
		
		private ItemDescriptor(ResourceDescriptor set, ResourceItem item, String imgFile){
			if(item == null || imgFile == null)
				throw new IllegalArgumentException("Invalid arguments!");
			resourceItem = item;
			texes = new HashMap<String, Texture>();
			for(String s : resourceClasses){
				
			}
			loadResource(imgFile);
			resSet = set;
		}

		public void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans) {
			ImageUtil.drawImage(gc, image, 0, 0, bounds.width, bounds.height, destX, destY, destWidth, destHeight, (byte)trans);
		}

		@Override
		public void paint(Graphics graphics, int offx, int offy, int width, int height, float trans){
			GLUtil.drawImage(texture, offx, offy, bounds.width, bounds.height, trans, 1, 1);
		}

		public Rectangle getBounds(){
			return bounds;
		}
		
		public void dispose(){
			if(ImageUtil.valid(image)){
				image.dispose();
				image = null;
			}
			if(texture != null){
				texture.release();
				texture = null;
			}
		}
		
		/**
		 * Has mutilplied the resource ratio factor.
		 */
		public Point getSize(){
			return new Point(bounds.width, bounds.height);
		}

		public void setLocation(int i, int j) {
			
		}

		public Enum<?>[] getOperationList() {
			return opers;
		}
		
		public String getName(){
			return resourceItem.getName();
		}

		public String getSetName() {
			return resSet.getName();
		}

		/**
		 * Return the ID to be setted into map elements for getting resources, the
		 * format is: setname:itemname.
		 * 
		 * @return
		 */
		public String getResource(){
			return resSet.getName() + ":" + resourceItem.getName();
		}

		@Override
		public int getItemID() {
			return resourceItem.getId();
		}

		@Override
		public boolean operate(Enum<?> i) {
			boolean b = false;
			i = (OPERS)i;
			switch(Enum.valueOf(OPERS.class, i.name())){
			case 替换图片:
				replaceImage();
				break;
			case 删除:
				onDelete();
				b = true;
				break;
			}
			return b;
		}
		
		/**
		 * 删除该资源项
		 */
		public void onDelete(){
			resSet.resourceSet.removeItem(resourceItem);
			resSet.resourceItems.remove(this);
			mainApp.updateMap();
		}
		
		void loadResource(String path){
			imageData = new ImageData(path);
			image = new Image(Display.getCurrent(), imageData);
			texture = GLUtil.loadTexture(path);
			bounds = image.getBounds();
		}
		
		void replaceImage(){
			FileDialog dlg = WidgetUtil.imageDialog(mainApp.windowsHarbor.getShell(), SWT.OPEN);
			String f = dlg.open();
			if(f != null){
				dispose();
				loadResource(f);
				saveResource(resourceItem.getName() + ImageUtil.getExt(imageData.type));
				mainApp.repaintMapCanvas();
			}
		}
		
		void saveResource(String fileName){
			StringBuffer sb = new StringBuffer(mainApp.getProject().getResourceDir());
			String p =  sb.append(resSet.getID()).append(File.separator).append(fileName).toString();
			ImageUtil.saveImage(imageData, p);
		}

		@Override
		public void setRotation(int v) {}

		@Override
		public void setAlpha(int v) {}

		@Override
		public void setScale(float v) {}

		@Override
		public List<int[]> getVertices() {
			return null;
		}

		@Override
		public void resetVertex(int i, int x, int y) {}

		@Override
		public void reset() {}

		@Override
		public boolean getEditabilities(Enum<?> i) {
			return false;
		}

		@Override
		public void onDragOver() {}

		@Override
		public Drawable getModelCopy() {
			return null;
		}

		@Override
		public void releaseCopy() {}

	}

	/**
	 * @deprecated
	 * @param imgFile
	 * @param toDir
	 */
	public static void getAndSaveBackground(String imgFile, String toDir) {
		background = GLUtil.loadTexture(imgFile);
		StringBuffer sb = new StringBuffer(toDir);
		String name = sb.append(BACKGROUND_NAME).append(imgFile.substring(imgFile.lastIndexOf("."))).toString();
		Properties prop  = new Properties();
		prop.setProperty(BACKGROUND_NAME, name);
		try {
			FileUtil.checkPath(toDir);
			prop.store(new FileWriter(toDir + BACKGROUND_INDEX), "");
			ImageUtil.saveImage(new ImageData(imgFile), name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentClass() {
		return resourceClasses[currentRes];
	}

	public static void setCurrentClass(String s) {
		boolean exist = false;
		int i = -1;
		for(String cls : resourceClasses){
			if(cls.equals(s)){
				exist = true;
				currentRes = i;
				break;
			}
			i++;
		}
		if(exist){
			for(ResourceDescriptor rd : stack.values()){
				for(ItemDescriptor item : rd.resourceItems){
					if(item.texes.containsKey(s))
						item.texture = item.texes.get(s);
				}
			}
		}
	}

	public static void loadResourceClasses(String dir) {
		Properties p = new Properties();
		FileUtil.loadProperties(p, dir + "classes.config");
		String clses = p.getProperty(CLASSES_KEY);
		if(clses != null){
			String[] cs = clses.split(Constants.SEP);
			resourceClasses = new String[1 + cs.length];
			resourceClasses[0] = DEFAULT_CLASS;
			System.arraycopy(resourceClasses, 0, cs, 0, cs.length);
		}else{
			resourceClasses = new String[]{DEFAULT_CLASS};
		}
	}
	
	public static void saveResourceClasses(String dir) throws IOException{
		Properties p = new Properties();
		StringBuffer rslt = new StringBuffer(resourceClasses.length == 0 ? "" : resourceClasses[0]);
		for(int i = 1; i < resourceClasses.length; i++)
			rslt.append(Constants.SEP).append(resourceClasses[i]);
		p.setProperty(CLASSES_KEY, rslt.toString());
		FileUtil.saveProperties(p, dir + "classes.config");
	}

	/**
	 * @deprecated
	 * @param base
	 * @throws IOException
	 */
	public static void loadBackground(String base) throws IOException{
		Properties prop = new Properties();
		try{
			FileReader reader = new FileReader(base + BACKGROUND_INDEX);
			prop.load(reader);
		}catch(FileNotFoundException e){
			return;
		}
		String path = prop.getProperty(BACKGROUND_NAME);
		if(new File(path).exists())
			background = GLUtil.loadTexture(path);
	}
	
	/**
	 * 根据指定的资源名称获取资源项
	 * @param resource
	 * @return
	 */
	public static ItemDescriptor getResourceItem(String resource){
		String[] resName = parseResourceName(resource);
		ResourceDescriptor resSet = null;
		for(ResourceDescriptor set : stack.values()){
			if(set.getName().equals(resName[0])){
				resSet = set;
				break;
			}
		}
		if(resSet == null)
			return null;
		for(Drawable d : resSet.getResourceItems()){
			ItemDescriptor rd = (ItemDescriptor)d;
			if(rd.getName().equals(resName[1])){
				return rd;
			}
		}
		return null;
	}
	
	/**
	 * 释放所有资源组的图片资源
	 */
	public static void releaseResources(){
		for(ResourceDescriptor rsd : stack.values()){
			rsd.dispose();
		}
		if(background != null)
			background.release();
	}
	
	static String[] parseResourceName(String resource){
		if(resource == null){
			System.err.println("invalid resource");
			return new String[2];
		}
		return resource.split(RES_SEP);
	}

	/**
	 * 获取指定资源组的包装器，如果该资源组已存在，则从缓存中取得
	 * @param rs
	 * @return
	 */
	public static ResourceDescriptor getDescriptor(ResourceSet rs) {
		ResourceDescriptor dsc = stack.get(rs.getId());
		if(dsc == null){
			dsc = new ResourceDescriptor(rs);
			stack.put(rs.getId(), dsc);
		}
		return dsc;
	}
	
	ResourceSet resourceSet;
	List<ItemDescriptor> resourceItems;
	
	private ResourceDescriptor(ResourceSet resourceSet){
		this.resourceSet = resourceSet;
	}
	
	public List<ItemDescriptor> getResourceItems(){
		if(resourceItems == null)
			resourceItems = new ArrayList<ItemDescriptor>();
		return resourceItems;
	}
	
	void setResourceItems(List<String> itemImages){
		if(itemImages == null)
			throw new NullPointerException();
		int i = 0;
		for(String img : itemImages){
			if(img == null)
				throw new IllegalArgumentException("Image at " + i + " is null");
			++i;
		}
		resourceItems = new ArrayList<ItemDescriptor>(itemImages.size());
		int j = 0;
		for(String img : itemImages){
			resourceItems.add(new ItemDescriptor(this, resourceSet.getItems().get(j++), img));
		}
	}

	/**
	 * Save resource images to the res directory.
	 * @param pro
	 * @throws DataCrackException
	 */
	public void save(MapProject pro) throws DataCrackException {
		if(resourceItems != null){
			String p = pro.getResourceDir() + resourceSet.getId() + File.separator;
			for(Drawable d : resourceItems){
				ItemDescriptor rd = (ItemDescriptor)d;
				ImageUtil.saveImage(rd.imageData, p + rd.getName() + ImageUtil.getExt(rd.imageData.type));
			}
		}
	}

	public void load(MapProject pro) {
		String setP = pro.getResourceDir() + resourceSet.getId() + File.separator;
		resourceItems = new ArrayList<ItemDescriptor>(resourceSet.getItems().size());
		for(ResourceItem i : resourceSet.getItems()){
			String imgP = setP + i.getName();
			for(String ext : ImageUtil.SURPORTED_FORMAT){
				if(!new File(imgP + ext).exists())
					continue;
				imgP += ext;
				break;
			}
			ItemDescriptor itemd = new ItemDescriptor(this, i, imgP);
			resourceItems.add(itemd);
		}
	}
	
	public String getResourceName(){
		return resourceSet.getName();
	}
	
	/**
	 * Release the system resources kept within every resource item.
	 */
	public void dispose(){
		if(resourceItems != null)
			for(Drawable d : resourceItems){
				ItemDescriptor rd = (ItemDescriptor)d;
				rd.dispose();
			}
	}

	@Override
	public String getName() {
		return resourceSet.getName();
	}

	public int getID() {
		return resourceSet.getId();
	}

	@Override
	public void addResourceItem(String resUrl, String name) {
		File f = new File(resUrl);
		if(!f.exists())
			return;
		String fn = f.getName();
		int idx = fn.lastIndexOf(".");
		if(idx < 0)
			return;
		String type = fn.substring(idx + 1);
		if(ImageUtil.imageType(type)){
			ResourceItem item = resourceSet.addResourceItem(name);
			if(resourceItems == null){
				List<String> rs = new ArrayList<String>();
				rs.add(resUrl);
				setResourceItems(rs);
			}else{
				ItemDescriptor itemDes = new ItemDescriptor(this, item, resUrl);
				resourceItems.add(itemDes);
				itemDes.saveResource(fn);
				mainApp.updateMap();
			}
		}else{
			mainApp.alert("illegal file format");
		}
	}

	/**
	 * 获取资源组前缀（暂未使用）
	 */
	public String getPrefix() {
		return resourceSet.getPrefix();
	}

	public void setPrefix(String text) {
		resourceSet.setPrefix(text);
	}
	
	public ResourceSet getUnderlying(){
		return resourceSet;
	}
	
	
}
