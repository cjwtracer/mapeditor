package tool.mapeditor;

import java.util.List;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.newdawn.slick.Graphics;

/**
 * Object of this interface representing the operatable and drawable object of
 * the window system of the map editor.
 * 
 * @author caijw
 * 
 */
public interface Drawable {

	/**
	 * Paint the drawable with GC object.
	 * 
	 * @param gc
	 * @param destX
	 * @param destY
	 * @param destWidth
	 * @param destHeight
	 * @param trans
	 */
	void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans);
	
	/**
	 * Get the bounds of the drawable.
	 * @return
	 */
	Rectangle getBounds();

	/**
	 * Set the location of the drawable.
	 * @param x
	 * @param y
	 */
	void setLocation(int x, int y);

	/**
	 * Paint the drawable within an opengl context.
	 * @param graphics
	 * @param offx
	 * @param offy
	 * @param width
	 * @param height
	 * @param trans
	 */
	void paint(Graphics graphics, int offx, int offy, int width, int height, float trans);
	
	/**
	 * Set the rotation of the drawale.
	 * @param v
	 */
	void setRotation(int v);

	/**
	 * Set the transparecy of the drawable.
	 * @param v
	 */
	void setAlpha(int v);

	/**
	 * Set the scale of the drawable.
	 * @param v
	 */
	void setScale(float v);
	
	/**
	 * Get the vertices representing the drawable.
	 * @return
	 */
	List<int[]> getVertices();

	/**
	 * Modify the location of the vertex of index i.
	 * @param i
	 * @param x
	 * @param y
	 */
	void resetVertex(int i, int x, int y);

	/**
	 * Reset the state of the drawable.
	 */
	void reset();

	/**
	 * Get the operation names representing the applicable operations on the drawable.
	 * @return
	 */
	Enum<?>[] getOperationList();

	/**
	 * Perform the operation of index i.
	 * 
	 * @param i
	 * @return TRUE to redraw canvas.
	 */
	boolean operate(Enum<?> i);

	/**
	 * Get the editabilites of the operations of the drawable.
	 * @return
	 */
	boolean[] getEditabilities();
	
	/**
	 * Specify the action when the mouse is dragged over the receiver.
	 */
	void onDragOver();

	void onDelete();
	
	Drawable getModelCopy();

	void releaseCopy();

	String getName();

}
