package tool.model;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import tool.util.MathUtil;

/**
 * A polygon object.
 * 
 * @author caijw
 *
 */
public class RegionPolygon extends Model{
	private static final long serialVersionUID = 880918042814512338L;
	
	public static final int TYPE_NONE = -1;
	public static final int TYPE_COLLISION = 0;
	public static final int TYPE_SHUTTER = 1;
	public static final int TYPE_TELEPORT = 2;
	public static final int TYPE_LAND = 3;
	public static final int TYPE_SAFE = 4;
	public static final int[] TYPE_LIST = {TYPE_COLLISION, TYPE_SHUTTER, TYPE_TELEPORT, TYPE_LAND};
	
	static HashMap<Integer, PropHelper> propProto = new HashMap<Integer, PropHelper>();
	
	public static void parseProperties(String propFile){
		Model.loadProps(propFile, propProto);
	}
	
	public static String[] propNames(int type){
		return propProto.get(type).names;
	}

	MapLayer layer;
	
	List<int[]> vertices;
	int tileI, tileI1, tileJ, tileJ1;
	int[] mapping;
	int type = TYPE_COLLISION;
	
	/**
	 * Counter clockwise.
	 * @return
	 */
	public List<int[]> getVertices(){
		if(vertices == null)
			vertices = new ArrayList<int[]>();
		return vertices;
	}

	public boolean isType(int type) {
		return this.type == type;
	}

	public int getType() {
		return type;
	}
	
	/**
	 * Set the vertices of the polygon.
	 * @param point
	 */
	public void sortPointArray(int[] point) {
		if (vertices.isEmpty())
			return;
		int[] p1 = vertices.get(0);
		for (int i = 1; i < vertices.size(); i++) {
			int[] p2 = vertices.get(i);
			double angleInsert = MathUtil.getAngle(point, p1);
			double angle = MathUtil.getAngle(p2, p1);
			if (angleInsert < angle) {
				vertices.add(i, point);
				break;
			} else if (i == vertices.size() - 1) {
				vertices.add(point);
				break;
			}
		}
	}
	
	/**
	 * Delete the vertex around the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean deletePoint(int x, int y) {
		boolean delete = false;
		for(int[] p : vertices){
			if(Math.abs(p[0] - x) < 4 && Math.abs(p[1] - y) < 4){
				if(vertices.size() > 3){
					vertices.remove(p);
				}else{
					delete = true;
				}
				break;
			}
		}
		return delete;
	}

	public void setType(int type) {
		if(type == this.type)
			return;
		switch(type){
		case TYPE_COLLISION:
		case TYPE_SHUTTER:
		case TYPE_LAND:
		case TYPE_TELEPORT:
		case TYPE_SAFE:
			break;
		default:
			throw new IllegalArgumentException("Invalid region type!");
		}
		this.type = type;
		clearProps();
		PropHelper p = propProto.get(type);
		if(p != null){
			clearProps();
			int i = 0;
			for(String n : p.names){
				newProperty(n, p.types[i++]);
			}
		}
	}

	/**
	 * Generate the mapping from polygon onto ground tiles.
	 * @param tileJ
	 * @param tileJ1
	 * @param tileI
	 * @param tileI1
	 * @return
	 */
	public int[] generateMapping(int tileJ, int tileJ1, int tileI, int tileI1) {
		this.tileI = tileI;
		this.tileI1 = tileI1;
		this.tileJ = tileJ;
		this.tileJ1 = tileJ1;
		mapping = new int[(tileJ1 - tileJ + 1) * (tileI1 - tileI + 1)];
		return mapping;
	}
	
	public int getMappingTileLeft(){
		return tileJ;
	}
	
	public int getMappingTileRight(){
		return tileJ1;
	}
	
	public int getMappingTileTop(){
		return tileI;
	}
	
	public int getMappingTileBottom(){
		return tileI1;
	}

	/**
	 * Get mapped region on tile at row i and column j.
	 * @param i
	 * @param j
	 * @return
	 */
	public int getMapping(int i, int j) {
		if(mapping == null)
			return 0;
		int y = i - tileI;
		int x = j - tileJ;
		int w = tileJ1 - tileJ + 1;
		int idx = y * w + x;
		if(idx < 0 || idx >= mapping.length)
			return 0;
		return mapping[idx];
	}
	
	/**
	 * Starting inclued while end excluded, counted by tiles.
	 * @return
	 */
	public Rectangle getBounds(){
		return new Rectangle(tileJ, tileI, tileJ1 - tileJ + 1, tileI1 - tileI + 1);
	}

	public void writeProperties(DataOutputStream dos) {
		try {
			if(props != null){
				for(Property p : props){
					p.write(dos);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return Model.getDftValues(propProto.get(type), propName);
	}
}
