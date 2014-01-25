package tool.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;

public class ViewUtil {
	

	public static Point pointToLogic(int x, int y, int tileWidth, int tileHeight) {
		Point logic = new Point(0, 0);
		int kx = (x & (tileWidth - 1)) - (tileHeight - 1);
		int ky = (y & (tileHeight - 1)) - (tileHeight / 2 - 1);
		logic.x = x / tileWidth;
		logic.y = y / tileHeight;
		int cx = Math.abs(kx);
		int cy = Math.abs(ky);
		logic.x = logic.x * 2 + 1;

		if (cx + 2 * cy >= tileHeight) {
			if (kx > 0) {
				if (ky > 0) {
					logic.x++;
					logic.y++;
				} else {
					logic.x++;
				}
			} else if (ky < 0) {
				logic.x--;
			} else {
				logic.x--;
				logic.y++;
			}
		}

		return logic;
	}

	public static Point logicToStage(int x, int y, int tileWidth, int tileHeight) {
		Point stage = new Point(0, 0);
		stage.x = (x - 1) * tileWidth / 2;
		stage.y = y * tileHeight + ((x & 1) - 1) * (tileHeight / 2);
		return stage;
	}
	
	public static int[] translatePoints(int[] pointArray, int x, int y) {
		int[] pts = new int[pointArray.length];
		for (int i = 0; i < pointArray.length;) {
			pts[i] = pointArray[i++] + x;
			pts[i] = pointArray[i++] + y;
		}
		return pts;
	}
	
	public static int computeIntersectArea(Region region, Rectangle rect) {
		int val = 0;
		for (int y = 0; y < rect.height; y++) {
			for (int x = 0; x < rect.width; x++) {
				if (region.contains(x + rect.x, y + rect.y)) val++;
			}
		}
		return val;
	}
	
	public static Point transformPoint(int x, int y, int width, int height, int trans) {
		Point p = new Point(x, y);
		
		switch (trans) {
		case Constants.TRANS_NONE:
			break;
		case Constants.TRANS_ROT90:
			p.x = height - y;
			p.y = x;
			break;
		case Constants.TRANS_ROT180:
			p.x = x;
			p.y = height - y;
			break;
		case Constants.TRANS_ROT270:
			p.x = y;
			p.y = width - x;
			break;
		case Constants.TRANS_MIRROR:
			p.x = width - x;
			p.y = y;
			break;
		case Constants.TRANS_MIRROR_ROT90:
			p.x = height - y;
			p.y = width - x;
			break;
		case Constants.TRANS_MIRROR_ROT180:
			p.x = x;
			p.y = height - y;
			break;
		case Constants.TRANS_MIRROR_ROT270:
			p.x = y;
			p.y = x;
			break;
		default:
			break;
		}
		
		return p;
	}
	
	public static boolean isNumeric(String string){
		char [] chars = new char [string.length ()];
		string.getChars (0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!('0' <= chars [i] && chars [i] <= '9')) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		long t0 = System.currentTimeMillis();
		for(int i=0; i<10000000; i++){
			pointToLogic(0, 0, 32, 16);
		}
		System.out.println(System.currentTimeMillis() - t0);
	}
	/**
	 * Split a rectangle into four smaller one with equivalent size.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Rectangle[] splitRectangle(int x, int y, int width, int height) {
		Rectangle[] rects = new Rectangle[4];
		rects[0] = new Rectangle(x, y, width / 2, height / 2);
		rects[1] = new Rectangle(x + width / 2, y, width / 2, height / 2);
		rects[2] = new Rectangle(x + width / 2, y + height / 2, width / 2, height / 2);
		rects[3] = new Rectangle(x, y + height / 2, width / 2, height / 2);
		return rects;
	}

}
