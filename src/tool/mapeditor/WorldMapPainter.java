package tool.mapeditor;

import org.newdawn.slick.Graphics;

/**
 * Performing the rendering of the map, in the opengl context currently.
 * @author caijw
 *
 */
public interface WorldMapPainter {
	
	/**
	 * 
	 * @param graphics
	 * @param offx
	 * @param offy
	 */
	void paintMap(Graphics graphics, int offx, int offy);
	
	/**
	 * Paint the grid.
	 * @param offx
	 * @param offy
	 */
	void paintGrid(int offx, int offy);
	
	/**
	 * Paint the hovering drawable.
	 * @param graphics
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param drawable
	 */
	void paintHovering(Graphics graphics, int x, int y, int width, int height, Drawable drawable);

	/**
	 * Get the width of map by measuring of pixels.
	 * @return
	 */
	int getPixelWidth();

	/**
	 * Get the height of map by measuring of pixels.
	 * @return
	 */
	int getPixelHeight();

	/**
	 * Return whether the animations on the map are under playing.
	 * @return
	 */
	boolean isPlay();

}
