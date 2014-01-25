package tool.mapeditor.application;

import java.util.List;


import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Vector2f;

import tool.mapeditor.Drawable;
import tool.model.Animation;
import tool.model.Locus;

public class LocusDescriptor implements Drawable {
	private static enum OPERS{SEP, 删除};
	static Enum<?>[] list = {OPERS.删除};
	
	Locus locus;
	Animation animation;
	int vertexIndex = -1;
	int curveIndex = -1;
	Vector2f firedVertex;
	Rectangle firedMarkerBounds = new Rectangle(0, 0, 0, 0);
	static int halfSize = Locus.MARKER_SIZE / 2;
	
	LocusDescriptor(Locus locus, Animation animation){
		this.locus = locus;
		this.animation = animation;
	}

	@Override
	public void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans) {}

	@Override
	public Rectangle getBounds() {
		return firedMarkerBounds;
	}

	public void setLocation(int x, int y) {
		if(firedVertex != null){
			firedVertex.x = x + halfSize;
			firedVertex.y = y + halfSize;
			firedMarkerBounds.x = x;
			firedMarkerBounds.y = y;
			locus.getCurves().get(curveIndex).resetPoints();
			if(vertexIndex == Locus.VER_END && curveIndex != locus.getCurves().size() - 1){
				Curve next = locus.getCurves().get(curveIndex + 1);
				Vector2f p = next.getStartPoint();
				p.x = x + halfSize;
				p.y = y + halfSize;
				next.resetPoints();
			}
		}
	}

	public void paint(Graphics graphics, int x, int y, int width, int height, float trans) {
		graphics.translate(x, y);
		for(Curve c : locus.getCurves()){
			graphics.setColor(Color.blue);
			graphics.draw(c);
			graphics.setColor(Color.yellow);
			drawMarker(graphics, c.getStartPoint());
			drawMarker(graphics, c.getEndPoint());
			drawMarker(graphics, c.getControlFirst());
			drawMarker(graphics, c.getControlSecond());
		}
		graphics.translate(-x, -y);
	}
	
	void drawMarker(Graphics g, Vector2f p){
		g.drawRect(p.x - halfSize, p.y - halfSize, Locus.MARKER_SIZE, Locus.MARKER_SIZE);
	}

	public void setScale(float v) {
	}

	public List<int[]> getVertices() {
		return null;
	}

	public void resetVertex(int i, int x, int y) {
	}

	boolean contains(int x, int y) {
		int i = 0;
		firedMarkerBounds.width = Locus.MARKER_SIZE;
		firedMarkerBounds.height = Locus.MARKER_SIZE;
		for(Curve c : locus.getCurves()){
			curveIndex = i;
			Vector2f p2 = c.getEndPoint();
			if(p2.x - halfSize < x && p2.x + halfSize > x && p2.y - halfSize < y && p2.y + halfSize > y){
				firedVertex = p2;
				vertexIndex = Locus.VER_END;
				firedMarkerBounds.x = (int)(p2.x - halfSize);
				firedMarkerBounds.y = (int)(p2.y - halfSize);
				return true;
			}
			Vector2f c1 = c.getControlFirst();
			if(c1.x - halfSize < x && c1.x + halfSize > x && c1.y - halfSize < y && c1.y + halfSize > y){
				firedVertex = c1;
				firedMarkerBounds.x = (int)(c1.x - halfSize);
				firedMarkerBounds.y = (int)(c1.y - halfSize);
				return true;
			}
			Vector2f c2 = c.getControlSecond();
			if(c2.x - halfSize < x && c2.x + halfSize > x && c2.y - halfSize < y && c2.y + halfSize > y){
				firedVertex = c2;
				vertexIndex = Locus.VER_CTRL2;
				firedMarkerBounds.x = (int)(c2.x - halfSize);
				firedMarkerBounds.y = (int)(c2.y - halfSize);
				return true;
			}
			Vector2f p1 = c.getStartPoint();
			if(p1.x - halfSize < x && p1.x + halfSize > x && p1.y - halfSize < y && p1.y + halfSize > y){
				firedVertex = p1;
				vertexIndex = Locus.VER_START;
				firedMarkerBounds.x = (int)(p1.x - halfSize);
				firedMarkerBounds.y = (int)(p1.y - halfSize);
				return true;
			}
			++i;
		}
		firedVertex = null;
		vertexIndex = -1;
		curveIndex = -1;
		firedMarkerBounds.x = 0;
		firedMarkerBounds.y = 0;
		firedMarkerBounds.width = 0;
		firedMarkerBounds.height = 0;
		return false;
	}

	public Enum<?>[] getOperationList() {
		return list;
	}

	public boolean operation(int i) {
		boolean b = false;
		switch(i){
		case 0:
			locus.removeCurve(curveIndex);
			b = true;
			break;
		}
		return b;
	}

	public boolean[] getEditabilities() {
		boolean[] bs = new boolean[list.length];
		bs[0] = true;
		return bs;
	}

	@Override
	public boolean operate(Enum<?> e) {
		boolean b = false;
		switch(Enum.valueOf(OPERS.class, e.name())){
		case 删除:
			locus.removeCurve(curveIndex);
			b = true;
			break;
		}
		return b;
	}

	@Override
	public void setRotation(int v) {}

	@Override
	public void setAlpha(int v) {}

	@Override
	public void reset() {}

	@Override
	public void onDragOver() {}

	@Override
	public void onDelete() {}

	@Override
	public Drawable getModelCopy() {
		return null;
	}

	@Override
	public void releaseCopy() {}

	@Override
	public String getName() {
		return null;
	}

}

