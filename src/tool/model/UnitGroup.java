package tool.model;

import java.util.ArrayList;
import java.util.List;

public class UnitGroup extends Unit {

	private static final long serialVersionUID = -6930943972673520404L;
	
	List<Unit> cells;
	List<int[]> offsets;
	String name = "";
	int id;
	public int expID;
	public int protoID;

	public UnitGroup(MapLayer layer) {
		super(layer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ID should not be negative value.
	 * @return
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public List<Unit> getComponents(){
		if(cells == null)
			cells = new ArrayList<Unit>();
		return cells;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UnitGroup getCopy(MapLayer layer) {
		UnitGroup ug = new UnitGroup(layer);
		
		for(Unit u : getComponents()){
			ug.getComponents().add(u.getCopy(layer));
		}
		return ug;
	}
	
	public int[] getOffset(int index){
		if(offsets == null)
			offsets = new ArrayList<int[]>();
		if(index < 0 || index >= offsets.size())
			return null;
		return offsets.get(index);
	}
	
	public void addOffsets(int index, int[] pos){
		if(pos == null){
			throw new IllegalArgumentException();
		}
		if(offsets == null)
			offsets = new ArrayList<int[]>();
		if(index < 0 || index >= offsets.size())
			offsets.add(pos);
		else
			offsets.add(index, pos);
	}

}
