package tool.mapeditor.application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;

import tool.io.DataCrackException;
import tool.mapeditor.Drawable;
import tool.mapeditor.WorldMapEdit;
import tool.mapeditor.WorldMapPainter;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.model.MapLayer;
import tool.model.RegionPolygon;
import tool.model.Unit;
import tool.model.WorldMap;
import tool.util.GLUtil;

/**
 * Descriptor of model world map.
 * @author caijw
 *
 */
public class MapDescriptor extends Descriptor implements WorldMapPainter, WorldMapEdit {
	static HashMap<Integer, MapDescriptor> stack = new HashMap<Integer, MapDescriptor>();
	
	/**
	 * Descriptor of model map layer.
	 * @author caijw
	 *
	 */
	public class LayerDescriptor{
		MapLayer layer;
		private TileDescriptor[] tiles;
		List<UnitDescriptor> units;
		List<RegionDescriptor> regions;
		
		private LayerDescriptor(MapLayer layer){
			if(layer == null)
				throw new IllegalArgumentException("Layer can't be null");
			this.layer = layer;
			tiles = new TileDescriptor[worldMap.getWidth() * worldMap.getHeight()];
			for(int i = 0; i < tiles.length; ++i){
				tiles[i] = new TileDescriptor(layer.getTile(i));
			}
			List<Unit> srcUnits = layer.getUnits();
			units = new ArrayList<UnitDescriptor>(srcUnits.size());
			for(Unit su : srcUnits){
				units.add(new UnitDescriptor(su, this));
			}
			regions = new ArrayList<RegionDescriptor>();
			for(RegionPolygon r : layer.getRegions()){
				regions.add(new RegionDescriptor(r, this));
			}
		}

		public void removeRegion(RegionDescriptor r) {
			regions.remove(r);
			layer.getRegions().remove(r.region);
		}

		public String getLayerName() {
			return layer.getName();
		}

		public boolean isVisible() {
			return layer.visible;
		}

		public void setVisible(boolean visible) {
			layer.visible = visible;
		}
		
		/**
		 * Put the resource into the tile at row i, column j on the receiver.
		 * @param i
		 * @param j
		 * @param res
		 */
		public void setTileAt(int i, int j, ItemDescriptor res){
			TileDescriptor t = tiles[i * worldMap.getWidth() + j];
			t.texture = res.texture;
			t.tile.setResource(res.resourceItem.getName());
		}
		
		/**
		 * Add an unit to the receiver at position (x, y).
		 * @param x
		 * @param y
		 * @param res
		 */
		public void addUnitAt(int x, int y, ItemDescriptor res){
			Unit srcU = new Unit(layer);
			srcU.setX(x);
			srcU.setY(y);
			Rectangle b = res.getBounds();
			srcU.setWidth(b.width);
			srcU.setHeight(b.height);
			srcU.setResource(res.getResource());
			UnitDescriptor u = new UnitDescriptor(srcU, this);
			layer.getUnits().add(srcU);
			units.add(u);
		}

		public void addUnitAt(int x, int y, UnitDescriptor source) {
			Unit srcU = source.unit.getCopy(layer);
			srcU.setX(x);
			srcU.setY(y);
			UnitDescriptor u = new UnitDescriptor(srcU, this);
			layer.getUnits().add(srcU);
			units.add(u);
		}
		
		/**
		 * Add a region(descriptor) onto the receiver.
		 * @param region
		 */
		public void addRegion(RegionDescriptor region){
			if(region == null)
				throw new IllegalArgumentException("Region can't be null!");
			regions.add(region);
			layer.getRegions().add(region.region);
		}
		
		private void paintLayer(Graphics graphics, int offx, int offy){
			int w = worldMap.getWidth();
			int tw = worldMap.getTileWidth();
			int th = worldMap.getTileHeight();
			int i = 0;
			for(TileDescriptor t : tiles){
				t.paint(graphics, offx + i % w * tw, offy + i / w * th, tw, th, t.getTrans());
				++i;
			}
			for(UnitDescriptor u : units){
				u.paint(graphics, offx, offy, -1, -1, -1);
			}
			for(RegionDescriptor r : regions){
				r.paint(offx, offy);
			}
		}
	}
	
	/**
	 * the underline model of the descriptor
	 */
	WorldMap worldMap;
	List<LayerDescriptor> layers;
	/**
	 * the selected index of layer in the layers stack
	 */
	int currentLayerIndex = -1;
	/**
	 * the selected layer
	 */
	private LayerDescriptor currentLayer;
	byte editType = TYPE_UNIT;
	byte editMode = MODE_PLACE;
	/**
	 * the background image of the map
	 */
	Texture background;
	/**
	 * flag determine whether to play the animations on the map
	 */
	boolean playAnimation;
	
	/**
	 * Get a descriptor of the specified map, if the descriptor doesn't exist,
	 * it creates a new one.
	 * 
	 * @param map
	 * @return
	 */
	public static MapDescriptor getDescriptor(WorldMap map){
		if(map == null)
			throw new IllegalArgumentException("Map is null!");
		MapDescriptor md = stack.get(map.getFileID());
		if(md == null){
			md = new MapDescriptor(map);
			stack.put(map.getFileID(), md);
		}
		if(md.background == null && md.worldMap.getBackground() != null){
			String bg = md.mainApp.project.getMapDir() + md.worldMap.getBackground();
			if(new File(bg).exists()){
				md.background = GLUtil.loadTexture(bg);
				md.worldMap.setImageWidth(md.background.getImageWidth());
				md.worldMap.setImageHeight(md.background.getImageHeight());
			}else{
				System.err.println("background not exist");
			}
		}
		return md;
	}

	/**
	 * Create a object of descriptor according to the specified model, and modify
	 * the background name according to the map file id.
	 * 
	 * @param worldMap
	 */
	private MapDescriptor(WorldMap worldMap){
		this.worldMap = worldMap;
		layers = new ArrayList<LayerDescriptor>(worldMap.getLayers().size());
		for(MapLayer ly : worldMap.getLayers()){
			layers.add(new LayerDescriptor(ly));
		}
	}
	
	public void setCurrentLayerIndex(int index){
		currentLayer = layers.get(index);
		currentLayerIndex = index;
	}
	
	public int getCurrentLayerIndex(){
		return currentLayerIndex;
	}

	public String getMapName() {
		return worldMap.getName();
	}

	public int getMapFileId() {
		return worldMap.getFileID();
	}
	
	/**
	 * Get the showing id.
	 */
	public int getMapID(){
		return worldMap.getExpID();
	}

	public void paintMap(Graphics graphics, int offx, int offy) {
		if(background != null){
			GLUtil.drawImage(background, offx, offy);
		}
		for(LayerDescriptor ly : layers){
			if(!ly.isVisible())
				continue;
			ly.paintLayer(graphics, offx, offy);
		}
	}
	
	public void paintGrid(int offx, int offy){
		GL11.glColor4f(0, 1, 0, 0.5f);
		int tw = worldMap.getTileWidth(), th = worldMap.getTileHeight();
		int w = worldMap.getWidth() * tw;
		int h = worldMap.getHeight() * th;
		for(int i = 0, s = worldMap.getWidth() + 1; i < s; ++i){
			int x = offx + i * tw;
			GLUtil.drawLine(x, offy, x, offy + h);
		}
		for(int i = 0, s = worldMap.getHeight() + 1; i < s; ++i){
			int y = offy + i * th;
			GLUtil.drawLine(offx, y, offx + w, y);
		}
	}
	
	/**
	 * Save the underlining map model.
	 * @param pro
	 * @throws DataCrackException
	 */
	public void save(MapProject pro) throws DataCrackException{
		WorldMap.save(pro, worldMap);
	}

	/**
	 * Get the layer at the specified index.
	 * @param index
	 * @return
	 */
	public LayerDescriptor getLayer(int index) {
		if(layers != null)
			return layers.get(index);
		return null;
	}
	
	public List<LayerDescriptor> getLayers() {
		return layers;
	}
	
	public int getLayerCount(){
		return worldMap.getLayers().size();
	}
	
	public void createLayer(int pos){
		MapLayer ly = WorldMap.newMapLayer(worldMap);
		worldMap.getLayers().add(pos, ly);
		layers.add(pos, new LayerDescriptor(ly));
	}

	public void removeLayer(int index) {
		worldMap.getLayers().remove(index);
		layers.remove(index);
	}
	
	public byte getEditType(){
		return editType;
	}
	
	public void setEditType(byte type){
		editType = type;
	}

	public void placeTile(int i, int j, Drawable source) {
		currentLayer.setTileAt(i, j, (ItemDescriptor)source);
	}

	public void placeUnit(int x, int y, Drawable source) {
		if(x < 0 || x >= worldMap.getPixelWidth() || y < 0 || y >= worldMap.getPixelHeight())
			return;
		if(source instanceof ItemDescriptor){
			currentLayer.addUnitAt(x, y, (ItemDescriptor)source);
		}else if(source instanceof UnitDescriptor){
			currentLayer.addUnitAt(x, y, (UnitDescriptor)source);
		}
	}

	public int getMapHeight() {
		return worldMap.getHeight();
	}

	public int getMapWidth() {
		return worldMap.getWidth();
	}
	
	public int getPixelWidth(){
		return worldMap.getPixelWidth();
	}
	
	public int getPixelHeight(){
		return worldMap.getPixelHeight();
	}

	public void paintHovering(Graphics graphics, int x, int y, int width, int height, Drawable drawable) {
		drawable.paint(graphics, x, y, width, height, 0);
	}

	public void setEditMode(byte mode) {
		editMode = mode;
	}

	public Drawable select(int x, int y) {
		Drawable d = null;
		int i = currentLayer.units.size() - 1;
		for (; i >= 0; --i) {
			UnitDescriptor u = currentLayer.units.get(i);
			if (u.anim != null && u.anim.locus.contains(x, y)) {
				d = u.anim.locus;
				break;
			} else if (u.getBounds().contains(x, y)) {
				d = u;
				break;
			}
		}
		if(editMode != MODE_MOVE && d == null){
			int w = worldMap.getWidth();
			int tw = worldMap.getTileWidth();
			int th = worldMap.getTileHeight();	
			if(x >= w * tw || y >= worldMap.getHeight() * th)
				return null;
			else{
				d = currentLayer.tiles[(y / th) * w + (x / tw)];
			}
		}
		return d;
	}

	public int getTileHeight() {
		return worldMap.getTileWidth();
	}

	public int getTileWidth() {
		return worldMap.getTileHeight();
	}
	
	public void setLayerVisible(boolean visible, int index) {
		LayerDescriptor ly = layers.get(index);
		ly.setVisible(visible);
	}
	
	public String getLayerName(int pos) {
		LayerDescriptor ld = layers.get(pos);
		return ld.layer.getName();
	}

	public boolean isLayerVisible(int pos) {
		LayerDescriptor ld = layers.get(pos);
		return ld.isVisible();
	}

	public void setLayerName(String name) {
		if(name == null || name.equals(""))
			return;
		worldMap.getLayers().get(currentLayerIndex).setName(name);
	}

	public boolean isMode(byte mode) {
		return mode == editMode;
	}

	public Drawable selectRegion(int x, int y) {
		List<RegionDescriptor> rgs = layers.get(currentLayerIndex).regions;
		for(RegionDescriptor r : rgs){
			r.focused = false;
		}
		for(RegionDescriptor r : rgs){
			if(r.contains(x, y)){
				r.focused = true;
				return r;
			}
		}
		return null;
	}
	
	public Drawable createRegion(int x, int y, int x1, int y1) {
		RegionPolygon region = new RegionPolygon();
		List<int[]> vs = region.getVertices();
		vs.add(new int[]{x, y});
		vs.add(new int[]{x1, y});
		vs.add(new int[]{x1, y1});
		vs.add(new int[]{x, y1});
		LayerDescriptor ly = layers.get(currentLayerIndex);
		RegionDescriptor rgd = new RegionDescriptor(region, ly);
		ly.addRegion(rgd);
		return rgd;
	}
	
	public void deleteRegion(Drawable region) {
		if(!(region instanceof RegionDescriptor))
			return;
		RegionDescriptor r = (RegionDescriptor)region;
		r.onDelete();
	}

	public int[] getSelectedVertex(Point point) {
		if(point == null)
			throw new IllegalArgumentException("Point can't be null!");
		for(RegionDescriptor r : layers.get(currentLayerIndex).regions){
			int[] p = r.getSelectedVertex(point);
			if(p != null)
				return p;
		}
		return null;
	}

	public void addVertex(Point p, Drawable region) {
		if(!(region instanceof RegionDescriptor))
			return;
		RegionDescriptor r = (RegionDescriptor)region;
		r.addPoint(p);
	}

	public void deleteVertex(Point p, Drawable region) {
		if(!(region instanceof RegionDescriptor))
			return;
		RegionDescriptor r = (RegionDescriptor)region;
		boolean del = r.deletePoint(p);
		if(del){
			deleteRegion(region);
		}
	}
	
	/**
	 * Map all the region on the map if the argument as a flag is true, while
	 * false cancel the mapping.
	 * 
	 * @param mapping
	 */
	void mappingRegion(boolean mapping) {
		int tw = worldMap.getTileWidth();
		int th = worldMap.getTileHeight();
		for(LayerDescriptor ly : layers){
			for(RegionDescriptor r : ly.regions){
				r.mappingRegion(mapping, tw, th);
			}
		}
	}

	public boolean isPlay() {
		return playAnimation;
	}

	/**
	 * Set whether to play the animations on the map.
	 * @param play
	 */
	public void setPlay(boolean play) {
		playAnimation = play;
	}

	public void updateAnimations() {
		worldMap.updateAnimations();
	}

	public void initAnimations() {
		worldMap.initAnimations();
	}

	public void resetAnimations() {
		worldMap.resetAnimations();
	}

	public Drawable getOveredDrawable(int x, int y) {
		if(x < 0 || y < 0)
			return null;
		int j = x / worldMap.getTileWidth();
		int i = y / worldMap.getTileHeight();
		if(j >= worldMap.getWidth() || i >= worldMap.getHeight())
			return null;
		return currentLayer.tiles[i * worldMap.getWidth() + j];
	}

	public void release() {
		if(background != null)
			background.release();
		background = null;
	}

	public String getStatusLineMessage(int x, int y) {
		StringBuffer sb = new StringBuffer();
		sb.append("x: ").append(x).append(", y:").append(y).append("  |  ").append("j: ").append(x / worldMap.getTileWidth()).append(", ").append("i: ").append(y / worldMap.getTileHeight());
		return sb.toString();
	}

	@Override
	public void getAllDrawables(List drawableList) {
		if(drawableList == null)
			throw new NullPointerException();
		for(LayerDescriptor ly : getLayers()){
			for(UnitDescriptor u : ly.units){
				drawableList.add(u);
			}
		}
	}

	protected void updateImage() {
		for(LayerDescriptor ly : getLayers()){
			for(TileDescriptor t : ly.tiles){
				t.updateImage();
			}
			for(UnitDescriptor u : ly.units){
				u.updateImage();
			}
		}
	}

}
