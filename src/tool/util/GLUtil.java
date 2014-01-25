package tool.util;


import java.io.File;
import java.io.IOException;
import java.util.List;


import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.opengl.GL11.*;

public class GLUtil {
	public static final float[] RED = {1, 0, 0};
	public static final float[] BLUE = {0, 0, 1};
	public static final float[] PURPLE = {1, 0, 1};
	public static final float[] YELLOW = {1, 1, 0};
	public static final float[] GREEN = {0, 1, 0};
	public static final float[] BLACK = {0, 0, 0};

	public static void drawRectangle(Rectangle bounds) {
		drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public static void setColor(float[] color){
		if(color == null || color.length != 3)
			return;
		for(float c : color){
			if(c < 0 || c > 1)
				return;
		}
		GL11.glColor3f(color[0], color[1], color[2]);
	}
	
	public static void setColor(int[] color){
		if(color == null || color.length != 3)
			return;
		GL11.glColor3f(color[0] / (float)255, color[1] / (float)255, color[2] / (float)255);
	}

	public static void drawRectangle(float x, float y, float width, float height) {
		float x1 = x + width;
		float y1 = y + height;
		glBegin(GL_LINE_LOOP);
		{
			glVertex2f(x, y);
			glVertex2f(x1, y);
			glVertex2f(x1, y1);
			glVertex2f(x, y1);
		}
		glEnd();
	}

	public static void drawLine(float x, float y, float x1, float y1) {
		glBegin(GL_LINES);
		{
			glVertex2f(x, y);
			glVertex2f(x1, y1);
		}
		glEnd();
	}

	/**
	 * 
	 * @param texture
	 * @param x
	 * @param y
	 * @param width
	 *            width of destination
	 * @param height
	 *            height of destination
	 * @param trans
	 * @param scale
	 *            scaling of the hole image, cooperating with the destination
	 *            width and height
	 * @param alpha
	 */
	public static void drawImage(Texture texture, float x, float y,
			float width, float height, float trans, float scale, float alpha) {
		if (texture == null)
			return;
		Color.white.bind();
		texture.bind();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glLoadIdentity();
		float axisX = x + texture.getImageWidth() / 2;
		float axisY = y + texture.getImageHeight() / 2;
		GL11.glTranslatef(axisX, axisY, 0);
		GL11.glRotatef(trans, 0, 0, 1);
		GL11.glScalef(scale, scale, 1);
		GL11.glTranslatef(-axisX, -axisY, 0);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(1, 1, 1, alpha);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(texture.getWidth(), 0);
			GL11.glVertex2f(x + width, y);
			GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
			GL11.glVertex2f(x + width, y + height);
			GL11.glTexCoord2f(0, texture.getHeight());
			GL11.glVertex2f(x, y + height);
		}
		GL11.glEnd();
		glLoadIdentity();
		glDisable(GL11.GL_TEXTURE_2D);
	}

	public static void fillRectangle(float x, float y, float width, float height) {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(x + width, y);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(x + width, y + height);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(x, y + height);
		}
		GL11.glEnd();
	}

	public static void fillPolygon(List<int[]> vertices) {
		glBegin(GL11.GL_POLYGON);
		
		{
			for(int[] p : vertices){
				glVertex2f(p[0], p[1]);
			}
		}
		glEnd();
	}

	public static void drawImage(Texture texture, int offx, int offy) {
		drawImage(texture, offx, offy, texture.getImageWidth(), texture.getImageHeight(), 0, 1, 1);
	}

	public static Texture loadTexture(String file) {
		String ext = file.substring(file.lastIndexOf(".") + 1);
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture(ext, ResourceLoader.getResourceAsStream(file));
		}catch (IOException e) {
			e.printStackTrace();
		}
		return texture;
	}

	public static void drawString(Font font, int x, int y, String s, Color fontColor) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.drawString(x, y, s, fontColor);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

}
