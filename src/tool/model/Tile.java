package tool.model;

import java.util.HashMap;

/**
 * Instance of this class represents a logical tile on a map.
 * 
 * @author caijw
 * 
 */
public class Tile extends Model{
	private static final long serialVersionUID = 2688762919078740462L;
	
	public static int currentType = -1;
	static HashMap<Integer, PropHelper> propProto = new HashMap<Integer, PropHelper>();
	
	public static void parseProperties(String propFile){
		Model.loadProps(propFile, propProto);
	}
	
	public static String[] propNames(int type){
		return propProto.get(type).names;
	}
	
	public static HashMap<Integer, PropHelper> getProtos(){
		return propProto;
	}

	/**
	 * do not update props immediately when the type is changed for performance
	 * consideration
	 */
	transient boolean needUpdateProps;

	MapLayer layer;
	int i, j;
	float trans;
	byte collide;
	int alpha = 256;
	float alphaF = 1f;
	float scale = 1;
	int type = -1;
	String resource;
	
	Tile(MapLayer layer, int i, int j){
		this.layer = layer;
		this.i = i;
		this.j = j;
	}
	
	public float getTrans(){
		return trans;
	}

	public void setTrans(int v) {
		trans = v;
	}

	public void setScale(float v) {
		scale = v;
	}
	
	public float getScale(){
		return scale;
	}

	public void setAlpha(int v) {
		alpha = v;
		alphaF = v / (float)256;
	}
	
	public int getAlpha(){
		return alpha;
	}
	
	public float getAlphaF(){
		return alphaF;
	}
	
	public void setType(int type){
		this.type = type;
		needUpdateProps = true;
	}

	public int getX() {
		return j * layer.map.tileWidth;
	}

	public int getY() {
		return i * layer.map.tileHeight;
	}
	
	public int getI(){
		return i;
	}
	
	public int getJ(){
		return j;
	}

	public int getWidth() {
		return layer.map.tileWidth;
	}

	public int getHeight() {
		return layer.map.tileHeight;
	}

	public int getType() {
		return type;
	}
	
	/**
	 * Only one tile on one layer is permitted to have the specified type.
	 * @param typeLand
	 */
	public void excludeType(int type) {
		for(Tile t : layer.tiles){
			if(t == this)
				continue;
			if(t.type == type)
				t.type = RegionPolygon.TYPE_NONE;
		}
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String res) {
		resource = res;
	}
	
	/**
	 * Update props according to current type.
	 */
	public void updateProps(){
		if(!needUpdateProps)
			return;
		PropHelper p = propProto.get(type);
		if(p != null){
			clearProps();
			int i = 0;
			for(String n : p.names){
				newProperty(n, p.types[i++]);
			}
		}
		needUpdateProps = false;
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return Model.getDftValues(propProto.get(type), propName);
	}

}
