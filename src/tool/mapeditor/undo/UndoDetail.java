package tool.mapeditor.undo;

public interface UndoDetail {
	
	void addLayer();
	void removeLayer();
	
	void addTile();
	void removeTile();
	
	void addUnit();
	void removeUnit();

}
