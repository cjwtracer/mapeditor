package tool.util;


import java.io.IOException;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
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
	
	public static float globalScale = 1.0f;
	public static float resourceScale = 1.0f;

	public static void drawRectangle(Rectangle bounds) {
		drawRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * 
	 * @param color
	 *            Order: r, g, b
	 */
	public static void setColor(float[] color){
		if(color == null || color.length != 3)
			return;
		for(float c : color){
			if(c < 0 || c > 1)
				return;
		}
		GL11.glColor3f(color[0], color[1], color[2]);
	}
	
	/**
	 * @see #setColor(float[])
	 * @param color
	 */
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
		float axisX = x + texture.getImageWidth() / 2;
		float axisY = y + texture.getImageHeight() / 2;
		GL11.glTranslatef(axisX, axisY, 0);
		GL11.glRotatef(trans, 0, 0, 1);
		width *= resourceScale;
		height *= resourceScale;
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
		loadIdentity();
		glDisable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * 
	 * @param texture
	 * @param srcX
	 *            x of region from the texture to be rendered
	 * @param srcY
	 * @param srcW
	 *            Width of the source image, not the texture
	 * @param srcH
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param trans
	 * @param scale
	 * @param alpha
	 */
	public static void drawImage(Texture texture, float srcX, float srcY, float srcW, float srcH, 
			float x, float y, float w, float h, float trans, float scale, float alpha){
		if (texture == null)
			return;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Color.white.bind();
		texture.bind();
		drawImg(texture, srcX, srcY, srcW, srcH, x, y, w, h, trans, scale, alpha);
		glDisable(GL11.GL_TEXTURE_2D);
	}
	
	static void drawImg(Texture texture, float srcX, float srcY, float srcW, float srcH, 
			float x, float y, float w, float h, float trans, float scale, float alpha){
		if(srcW == 0 || srcH == 0 || w == 0 || h == 0)
			return;
		w *= resourceScale;
		h *= resourceScale;
		float tw = texture.getWidth(), th = texture.getHeight();
		int iw = texture.getImageWidth(), ih = texture.getImageHeight();
		float stx = tw * srcX / iw, sty = th * srcY / ih;
		float stw = tw * srcW / iw, sth = th * srcH / ih;
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glColor4f(1, 1, 1, alpha);
			GL11.glTexCoord2f(stx, sty);
			GL11.glVertex2f(x, y);
			GL11.glTexCoord2f(stx + stw, sty);
			GL11.glVertex2f(x + w, y);
			GL11.glTexCoord2f(stx + stw, sty + sth);
			GL11.glVertex2f(x + w, y + h);
			GL11.glTexCoord2f(stx, sty + sth);
			GL11.glVertex2f(x, y + h);
		}
		GL11.glEnd();
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
		for (int[] p : vertices) {
			glVertex2f(p[0], p[1]);
		}
		glEnd();
	}

	public static void drawImage(Texture texture, int x, int y) {
		if(texture != null)
			drawImage(texture, x, y, texture.getImageWidth(), texture.getImageHeight(), 0, 1, 1);
	}

	public static Texture loadTexture(String txt) {
		String ext = txt.substring(txt.lastIndexOf(".") + 1);
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture(ext, ResourceLoader.getResourceAsStream(txt));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return texture;
	}

	public static void drawString(Font font, int x, int y, String s, Color fontColor) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		font.drawString(x, y, s, fontColor);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public static void drawString(Graphics graphics, String s, int x, int y){
		drawString(graphics.getFont(), x, y, s, graphics.getColor());
	}
	
	public static void loadIdentity(){
		glLoadIdentity();
		GL11.glScalef(globalScale, globalScale, 1);
	}

	/**
	 * Draw a image according to the destination size by scaling the side parts.
	 * @param tex
	 * @param cw
	 *            Width of central rectangle
	 * @param ch
	 * @param src
	 *            Bounds of the source image, not the texture
	 * @param dest
	 */
	public static void drawPoint9Image(Texture tex, int cw, int ch, Rectangle src, Rectangle dest) {
		if(tex == null || src == null || dest == null)
			return;
		int w = (src.width - cw) / 2, h = (src.height - ch) / 2;
		int sx0 = w, sy0 = h;
		int sx1 = src.width - w, sy1 = sy0;
		int sx2 = sx0, sy2 = src.height - h;
		int sx3 = sx1, sy3 = sy2;
		int dx0 = dest.x + w, dy0 = dest.y + h;
		int dx1 = dest.x + dest.width - w, dy1 = dy0;
		int dx2 = dx0, dy2 = dest.y + dest.height - h;
		int dx3 = dx1, dy3 = dy2;
		int dw = dest.width - w - w, dh = dest.height - h - h;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Color.white.bind();
		tex.bind();
		drawImg(tex, 0, 0, w, h, dest.x, dest.y, w, h, 0, 1, 1);
		drawImg(tex, sx1, 0, w, h, dx1, dest.y, w, h, 0, 1, 1);
		drawImg(tex, 0, sy2, w, h, dest.x, dy2, w, h, 0, 1, 1);
		drawImg(tex, sx3, sy3, w, h, dx3, dy3, w, h, 0, 1, 1);
		drawImg(tex, sx0, 0, cw, h, dx0, dest.y, dw, h, 0, 1, 1);
		drawImg(tex, sx2, sy2, cw, h, dx2, dy2, dw, h, 0, 1, 1);
		drawImg(tex, 0, sy0, w, ch, dest.x, dy0, w, dh, 0, 0, 1);
		drawImg(tex, sx1, sy1, w, ch, dx1, dy1, w, dh, 0, 0, 1);
		drawImg(tex, sx0, sy0, cw, ch, dx0, dy0, dw, dh, 0, 1, 1);
		glDisable(GL11.GL_TEXTURE_2D);
	}
	
	/**
	 * Draw a image according to the destination size by duplicating the side parts.
	 * @param tex
	 * @param cw
	 * @param ch
	 * @param src
	 * @param dest
	 */
	public static void drawSlice9Image(Texture tex, int cw, int ch, Rectangle src, Rectangle dest){
		if(tex == null || src == null || dest == null)
			return;
		int w = (src.width - cw) / 2, h = (src.height - ch) / 2;
		int sx0 = w, sy0 = h;
		int sx1 = src.width - w, sy1 = sy0;
		int sx2 = sx0, sy2 = src.height - h;
		int sx3 = sx1, sy3 = sy2;
		int dx0 = dest.x + w, dy0 = dest.y + h;
		int dx1 = dest.x + dest.width - w, dy1 = dy0;
		int dx2 = dx0, dy2 = dest.y + dest.height - h;
		int dx3 = dx1, dy3 = dy2;
		int dw = dest.width - w - w, dh = dest.height - h - h;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Color.white.bind();
		tex.bind();
		drawImg(tex, 0, 0, w, h, dest.x, dest.y, w, h, 0, 1, 1);
		drawImg(tex, sx1, 0, w, h, dx1, dest.y, w, h, 0, 1, 1);
		drawImg(tex, 0, sy2, w, h, dest.x, dy2, w, h, 0, 1, 1);
		drawImg(tex, sx3, sy3, w, h, dx3, dy3, w, h, 0, 1, 1);
		if(cw > 0){
			int tail = dw % cw;
			int sum = 0;
			for(int i = 0, num = dw / cw; i <= num; i++){
				if(i == num){
					drawImg(tex, sx0, 0, tail, h, dx0 + sum, dest.y, tail, h, 0, 1, 1);
					drawImg(tex, sx2, sy2, tail, h, dx2 + sum, dy2, tail, h, 0, 1, 1);
				}else{
					drawImg(tex, sx0, 0, cw, h, dx0 + sum, dest.y, cw, h, 0, 1, 1);
					drawImg(tex, sx2, sy2, cw, h, dx2 + sum, dy2, cw, h, 0, 1, 1);
				}
				sum += cw;
			}
		}
		if(ch > 0){
			int tail = dh % ch;
			int sum = 0;
			for(int i = 0, num = dh / ch; i <= num; i++){
				if(i == num){
					drawImg(tex, 0, sy0, w, tail, dest.x, dy0 + sum, w, tail, 0, 0, 1);
					drawImg(tex, sx1, sy1, w, tail, dx1, dy1 + sum, w, tail, 0, 0, 1);
				}else{
					drawImg(tex, 0, sy0, w, ch, dest.x, dy0 + sum, w, ch, 0, 0, 1);
					drawImg(tex, sx1, sy1, w, ch, dx1, dy1 + sum, w, ch, 0, 0, 1);
				}
				sum += ch;
			}
		}
		drawImg(tex, sx0, sy0, cw, ch, dx0, dy0, dw, dh, 0, 1, 1);
		glDisable(GL11.GL_TEXTURE_2D);
	}

}
