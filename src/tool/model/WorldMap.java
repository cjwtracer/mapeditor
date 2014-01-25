package tool.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import tool.io.DataCrackException;

/**
 * This class representing the data constuct of a map.
 * 
 * @author caijw
 * 
 */
public class WorldMap extends Model{
	private static final long serialVersionUID = -2130049493094308860L;
	
	public static final byte MDO_ORTHO = 0;
	public static final byte MDO_ISO = 1;
	public static final String BACKGROUND_EXT = "_bg";
	
	public static WorldMap importMap(String url, Project pro) throws DataCrackException, ClassNotFoundException
	{
		if(url == null || !new File(url).exists() || pro == null)
			throw new IllegalArgumentException("illegal args");
		WorldMap m = null;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(url);
			ObjectInputStream ois = new ObjectInputStream(fis);
			m = (WorldMap) ois.readObject();
			m.mapLayers = (List<MapLayer>) ois.readObject();
			generateId(pro, m);
			ois.close();
			m.loaded = true;
		}catch(IOException e){
			throw new DataCrackException(e);
		}finally{
			try{if(fis != null)fis.close();}catch(IOException ex){}
		}
		return m;
	}
	
	public static void exportMap(String url, Project project, WorldMap map) throws DataCrackException
	{
		if(url == null || map == null)
			throw new IllegalArgumentException("illegal args");
		if(!map.loaded)
			load(project, map);
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(url);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(map);
			oos.writeObject(map.getLayers());
			oos.close();
		}catch(IOException e){
			throw new DataCrackException(e);
		}finally{
			try{if(fos != null)fos.close();}catch(IOException ex){}
		}
	}
	
	public static void save(Project project, WorldMap worldMap) throws DataCrackException
	{
		if(worldMap == null || project == null)
			throw new IllegalArgumentException("Invalid arguments!");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(new StringBuffer(
					project.getMapDir()).append(worldMap.id).append(Project.MAP_EXT).toString());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(worldMap.props);
			oos.writeObject(worldMap.mapLayers);
			oos.close();
		}catch(IOException e){
			throw new DataCrackException(e);
		}finally{
			try{if(fos != null)fos.close();}catch(IOException e){}
		}
	}
	
	public static void load(Project project, WorldMap worldMap) throws DataCrackException
	{
		if(worldMap == null || project == null)
			throw new IllegalArgumentException("Invalid arguments!");
		if(worldMap.loaded)
			return;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(new StringBuffer(
					project.getMapDir()).append(worldMap.id).append(Project.MAP_EXT).toString());
			ObjectInputStream ois = new ObjectInputStream(fis);
			worldMap.props = (List<Property>)ois.readObject();
			worldMap.mapLayers = (List<MapLayer>)ois.readObject();
			ois.close();
			worldMap.loaded = true;
		}catch (ClassNotFoundException e) {
			throw new DataCrackException(e);
		}catch(IOException e){
			throw new DataCrackException(e);
		}finally{
			try{if(fis != null)fis.close();}catch(IOException e){}
		}
	}
	
	public static MapLayer newMapLayer(WorldMap map){
		if(map == null)
			throw new IllegalArgumentException("Map can't be null!");
		MapLayer ly = new MapLayer(map);
		ly.tiles = new Tile[map.width * map.height];
		for(int i = 0; i < ly.tiles.length; ++i){
			ly.tiles[i] = new Tile(ly, i / map.width, i % map.width);
		}
		ly.units = new ArrayList<Unit>();
		return ly;
	}
	
	public static void generateId(Project pro, WorldMap map){
		int id = pro.getAvailableMapId();
		if(pro.getMapGroup().get(id) == null)//protecting existing id
			map.id = pro.getAvailableMapId();
	}

	/**
	 * indicate whether the map datas have being read into memory
	 */
	transient private boolean loaded;
	/**
	 * indicate whether there are any modifications on the map datas
	 */
	transient public boolean dirty;
	transient private List<MapLayer> mapLayers;
	
	int version;
	/**
	 * id of file
	 */
	private int id;
	/**
	 * id for exporting and showing
	 */
	private int exportID;
	private String name = "";
	private String background;
	short width;
	short height;
	int pixelWidth;
	int pixelHeight;
	short tileWidth;
	short tileHeight;
	short cellWidth;
	short cellHeight;
	short cellCountHor;
	short cellCountVer;
	int imageWidth;
	int imageHeight;
	private short viewType;
	int backgroundID;
	
	public WorldMap(String name, short width, short height, short cellW, short cellH, short tileW, short tileH, byte viewType, int version){
		if(name == null || width <= 0 || height <= 0 || cellW <= 0 || 
				cellH <= 0 || tileW <= 0 || tileH <= 0 || !legalViewType(viewType))
			throw new IllegalArgumentException();
		this.version = version;
		this.name = name;
		this.width = width;
		this.height = height;
		tileWidth = tileW;
		tileHeight = tileH;
		cellWidth = cellW;
		cellHeight = cellH;
		pixelWidth = width * tileW;
		pixelHeight = height * tileH;
		cellCountHor = (short)(pixelWidth / cellWidth + 1);
		cellCountVer = (short)(pixelHeight / cellHeight + 1);
		this.viewType = viewType;
		getLayers();
		mapLayers.add(newMapLayer(this));
		initProperties();
		loaded = true;
	}
	
	public WorldMap() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean isLoaded(){
		return loaded;
	}

	void initProperties(){
		getProperties();
		Property p1 = new Property("Music", "int", -1);
		props.add(p1);
		Property p2 = new Property("MapType", "byte", (byte)-1);
		props.add(p2);
		Property p3 = new Property("Camp", "byte", (byte)-1);
		props.add(p3);
	}
	
	private boolean legalViewType(byte viewType){
		return (viewType == MDO_ORTHO || viewType == MDO_ISO);
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		if(name == null || name.equals(""))
			throw new IllegalArgumentException("Argument is null!");
		this.name = name;
	}
	
	public short getWidth(){
		return width;
	}
	
	public int getPixelWidth(){
		return pixelWidth;
	}
	
	public int getPixelHeight(){
		return pixelHeight;
	}
	
	public void setWidth(short w){
		if(w <= 0)
			throw new IllegalArgumentException("Argument must be positive!");
		width = w;
	}
	
	public short getHeight(){
		return height;
	}
	
	public void setHeight(short h){
		if(h <= 0)
			throw new IllegalArgumentException("Argument must be positive!");
		height = h;
	}
	
	public short getTileWidth(){
		return tileWidth;
	}
	
	public void setTileWidth(byte tw){
		if(tw <= 0)
			throw new IllegalArgumentException("Argument must be positive!");
		tileWidth = tw;
	}
	
	public short getTileHeight(){
		return tileHeight;
	}
	
	public void setTileHeight(short th){
		if(th <= 0)
			throw new IllegalArgumentException("Argument must be positive!");
		tileHeight = th;
	}
	
	public short getCellWidth(){
		return cellWidth;
	}
	
	public short getCellHeight(){
		return cellHeight;
	}
	
	public short getCellCountHor(){
		return cellCountHor;
	}
	
	public short getCellCountVer(){
		return cellCountVer;
	}
	
	/**
	 * From bottom to top.
	 * @return
	 */
	public List<MapLayer> getLayers(){
		if(mapLayers == null)
			mapLayers = new ArrayList<MapLayer>();
		return mapLayers;
	}
	
	public short getViewType(){
		return viewType;
	}
	
	public int getExpID(){
		return exportID;
	}

	public void setExpID(int id) {
		this.exportID = id;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	/**
	 * Get the name of the background.
	 * @return
	 */
	public String getBackground() {
		return background;
	}

	public void updateAnimations() {
		for(MapLayer ly : getLayers()){
			ly.updateAnimations();
		}
	}

	public void initAnimations() {
		for(MapLayer ly : getLayers()){
			ly.initAnimations();
		}
	}

	public void resetAnimations() {
		for(MapLayer ly : getLayers()){
			ly.resetAnimations();
		}
	}

	public int getBackgroundID() {
		return backgroundID;
	}
	
	public void setBackgroundID(int id){
		backgroundID = id;
	}
	
	public void setImageWidth(int width){
		imageWidth = width;
	}
	
	public int getImageWidth(){
		return imageWidth;
	}
	
	public void setImageHeight(int height){
		imageHeight = height;
	}
	
	public int getImageHeight(){
		return imageHeight;
	}
	
	public void setVersion(int version){
		this.version = version;
	}

	public int getFileID() {
		return id;
	}

	public void setCellWidth(int width) {
		cellWidth = (short)width;
	}
	
	public void setCellHeight(int height){
		cellHeight = (short)height;
	}

	public String composeBgName(String ext) {
		return new StringBuffer("").append(id).append(BACKGROUND_EXT).append(ext == null ? "" : ext).toString();
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return null;
	}
}
