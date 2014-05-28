package tool.mapeditor;

import org.eclipse.swt.graphics.Point;

/**
 * Define the applicable operations the user can perform by the window system to
 * the underlying map object.
 * 
 * @author caijw
 * 
 */
public interface WorldMapEdit {
	byte TYPE_TILE = 0;
	byte TYPE_UNIT = 1;

	byte MODE_PLACE = 0;
	byte MODE_MOVE = 1;
	byte MODE_REGION_RECT = 2;
	byte MODE_VERTEX_ADD = 3;
	byte MODE_VERTEX_DEL = 4;
	byte MODE_SHAPE_REGION = 5;
	byte MODE_MAPPING = 6;

	/**
	 * Get the type of current editting.
	 * 
	 * @return
	 */
	byte getEditType();

	/**
	 * Locate the resource at the specified tile.
	 * 
	 * @param i
	 * @param j
	 * @param srouce
	 */
	void placeTile(int i, int j, Drawable srouce);

	/**
	 * Locate the resource and create an unit at the specified position.
	 * 
	 * @param x
	 * @param y
	 * @param source
	 */
	void placeUnit(int x, int y, Drawable source);

	/**
	 * Get the number of tiles in horizontal of the map.
	 * 
	 * @return
	 */
	int getMapWidth();

	/**
	 * Get the number of tiles in vertical of the map.
	 * 
	 * @return
	 */
	int getMapHeight();

	/**
	 * Set the type of editting performing on the map.
	 * 
	 * @param type
	 */
	void setEditType(byte type);

	/**
	 * Set the mode of editting, align to the grid or not.
	 * 
	 * @param type
	 */
	void setEditMode(byte type);

	/**
	 * Select the drawable at the specified position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	Drawable select(int x, int y);

	int getTileWidth();

	int getTileHeight();

	/**
	 * Create a map layer of the map.
	 * 
	 * @param pos
	 */
	void createLayer(int pos);

	void removeLayer(int index);

	int getCurrentLayerIndex();

	String getMapName();

	int getMapID();

	int getLayerCount();

	/**
	 * Set the current layer to the specified index.
	 * 
	 * @param i
	 */
	void setCurrentLayerIndex(int i);

	/**
	 * Set the visibility of the the layer according the specified index.
	 * 
	 * @param visible
	 * @param index
	 */
	void setLayerVisible(boolean visible, int index);

	/**
	 * Get the name the layer of index pos.
	 * 
	 * @param pos
	 * @return
	 */
	String getLayerName(int pos);

	boolean isLayerVisible(int pos);

	void setLayerName(String name);

	/**
	 * Judge the whether the map is in the specified mode.
	 * 
	 * @param mode
	 * @return
	 */
	boolean isMode(byte mode);

	/**
	 * Select the region contains the specified location.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	Drawable selectRegion(int x, int y);

	/**
	 * Create a rectangle region.
	 * 
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @return
	 */
	Drawable createRegion(int x, int y, int x1, int y1);

	/**
	 * 
	 * @param currentRegion
	 */
	void deleteRegion(Drawable currentRegion);

	/**
	 * Get the vertex according the specified position.
	 * 
	 * @param point
	 * @return
	 */
	int[] getSelectedVertex(Point point);

	/**
	 * Add a vertex to the region.
	 * 
	 * @param p
	 * @param region
	 */
	void addVertex(Point p, Drawable region);

	/**
	 * Delete a vertex of the region.
	 * 
	 * @param p
	 * @param region
	 */
	void deleteVertex(Point p, Drawable region);

	/**
	 * Update the states of animations on the map.
	 */
	void updateAnimations();

	/**
	 * Reset the animations on the map.
	 */
	void resetAnimations();

	/**
	 * Initialize the animations on the map.
	 */
	void initAnimations();

	/**
	 * Get the drawable when the mouse is moving over it.
	 * @param x
	 * @param y
	 * @return
	 */
	Drawable getOveredDrawable(int x, int y);
	/**
	 * Get the message to be set to the status bar according to the given information.
	 * @param x
	 * @param y
	 * @return
	 */
	String getStatusLineMessage(int x, int y);

}
