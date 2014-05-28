package tool.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapLayer extends Model{
	private static final long serialVersionUID = 7202951877120272937L;
	
	String name;
	public boolean visible = true;
	Tile[] tiles;
	List<Unit> units;
	List<RegionPolygon> regions;
	WorldMap map;
	
	MapLayer(WorldMap map){
		name = "新地图层";
		this.map = map;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Tile getTile(int i, int j){
		return tiles[i * map.width + j];
	}
	
	public Tile getTile(int index){
		return tiles[index];
	}
	
	public int getTilesCount(){
		return tiles.length;
	}
	
	public List<Unit> getUnits(){
		return units;
	}
	
	public List<RegionPolygon> getRegions(){
		if(regions == null)
			regions = new ArrayList<RegionPolygon>();
		return regions;
	}

	void updateAnimations() {
		for(Unit u : units){
			if(u.animation != null){
				u.animation.update();
			}
			if(u instanceof UnitGroup){
				UnitGroup ug = (UnitGroup)u;
				for(Unit cell : ug.getComponents()){
					if(cell.animation != null)
						cell.animation.update();
				}
			}
		}
	}

	void initAnimations() {
		for(Unit u : units){
			if(u.animation != null)
				u.animation.init();
			if(u instanceof UnitGroup){
				UnitGroup ug = (UnitGroup)u;
				for(Unit cell : ug.getComponents()){
					if(cell.animation != null)
						cell.animation.init();
				}
			}
		}
	}

	void resetAnimations() {
		for(Unit u : units){
			if(u.animation != null)
				u.animation.reset();
			if(u instanceof UnitGroup){
				UnitGroup ug = (UnitGroup)u;
				for(Unit cell : ug.getComponents()){
					if(cell.animation != null)
						cell.animation.reset();
				}
			}
		}
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return null;
	} 

}
