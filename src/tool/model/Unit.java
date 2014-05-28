package tool.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Instance of this class represents a component on the map, such as a building,
 * a element and so on.
 * 
 * @author caijw
 * 
 */
public class Unit extends Model{
	private static final long serialVersionUID = 8558083753998266865L;
	
	public final static int NORMAL = 0;
	public final static int MONSTER = 1;
	
	static HashMap<Integer, PropHelper> propProto = new HashMap<Integer, PropHelper>();
	
	MapLayer layer;
	transient Animation animation;
	int animID = -1;
	int resourceID = -1;
	int x;
	int y;
	int width;
	int height;
	int type = NORMAL;
	String resource = "";
	int rotation;
	int alpha = 255;
	float alphaF = 1f;
	float scale = 1;
	
	public Unit(MapLayer layer){
		this.layer = layer;
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public int getResourceID(){
		return resourceID;
	}
	
	/**
	 * @deprecated
	 * @param resID
	 */
	public void setResourceID(int resID){
		resourceID = resID;
	}
	
	public int getI(){
		if(layer == null)
			return y;
		return y / layer.map.getTileHeight();
	}
	
	public int getJ(){
		if(layer == null)
			return x;
		return x / layer.map.getTileWidth();
	}
	
	public int getX(){
		if(animation != null)
			x = animation.x;
		return x;
	}
	
	public void setX(int x){
		this.x = x;
		if(animation != null)
			animation.x = x;
	}
	
	public int getY(){
		if(animation != null)
			y = animation.y;
		return y;
	}
	
	public void setY(int y){
		this.y = y;
		if(animation != null)
			animation.y = y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setHeight(int height){
		this.height = height;
	}

	public int getTrans() {
		if(animation != null)
			rotation = animation.rotation;
		return rotation;
	}
	
	public void addAlphaChange(){
		
	}
	
	public void setRotation(int trans){
		this.rotation = trans;
	}

	public void setScale(float v) {
		scale = v;
	}
	
	public float getScale(){
		if(animation != null)
			scale = animation.scale;
		return scale;
	}
	
	public int getAlpha(){
		if(animation != null)
			alpha = animation.alpha;
		return alpha;
	}
	
	public float getAlphaF(){
		if(animation != null)
			alphaF = animation.alphaF;
		return alphaF;
	}
	
	public boolean isType(int type){
		return this.type == type;
	}
	
	/**
	 * Type is not exclusive.
	 * @param doit
	 * @param type
	 */
	public void setType(boolean doit, int type){
		switch(type){
		case MONSTER:
		case NORMAL:
			break;
		default:
			throw new IllegalArgumentException("Invalid unit type!");
		}
		if(!doit){
			if((this.type & type) == 0)
				return;
			delProps(type);
			type = ~type;
			this.type &= type;
		}else{
			if((this.type & type) != 0)
				return;
			setProps(type);
			this.type |= type;
		}
	}
	
	private void delProps(int type) {
		PropHelper p = propProto.get(type);
		for (int i = props.size() - 1; i >= 0 ; --i) {
			String key = props.get(i).name;
			for (String s : p.names) {
				if (key.equals(s)) {
					props.remove(i);
					break;
				}
			}
		}
	}

	private void setProps(int type){
		PropHelper p = propProto.get(type);
		getProperties();
		int i = 0;
		for (String n : p.names) {
			newProperty(n, p.types[i++]);
		}
	}

	public void onDelete() {
		layer.units.remove(this);
	}

	public Unit getCopy(MapLayer layer) {
		Unit u = new Unit(layer);
		u.resourceID = resourceID;
		u.resource = resource;
		u.x = x;
		u.y = y;
		u.width = width;
		u.height = height;
		u.alpha = alpha;
		u.alphaF = alphaF;
		u.scale = scale;
		u.type = type;
		u.animation = animation;
		u.animID = animID;
		u.props = new ArrayList<Property>(getProperties().size());
		for(Property p : props){
			u.props.add(p.getCopy());
		}
		return u;
	}

	public void setResource(String res) {
		resource = res;
	}

	public String getResource() {
		return resource.isEmpty() ? "empty" : resource;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation a) {
		animation = a;
		if(a != null){
			animID = a.getId();
			a.x = x;
			a.y = y;
		}
	}

	public void setAlpha(int v) {
		alpha = v;
		alphaF = v / (float)256;
	}
	
	public void extendLocus(){
		if(animation == null)
			createAnimation();
		animation.extendLocus();
	}
	
	public GradualChange addChange(byte type){
		if(animation == null)
			createAnimation();
		List<Queue> queues = animation.getQueues();
		if(queues.size() != 0)
			return queues.get(0).addChange(type);
		return null;
	}
	
	public Animation createAnimation(){
		animation = new Animation();
		animation.x = x;
		animation.y = y;
		animation.width = width;
		animation.height = height;
		animation.alpha = alpha;
		animation.alphaF = alphaF;
		animation.scale = scale;
		animation.rotation = rotation;
		return animation;
	}
	
	public static void parseProperties(String propFile){
		Model.loadProps(propFile, propProto);
	}
	
	public static String[] propNames(int type){
		return propProto.get(type).names;
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return Model.getDftValues(propProto.get(type), propName);
	}
	
	public int getAnimationID(){
		return animID;
	}

}
