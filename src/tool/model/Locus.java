package tool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Vector2f;


/**
 * A locus is a kind of geomitry consisting of several Bezier curves.
 * 
 * @author caijw
 * 
 */
public class Locus implements Serializable{
	private static final long serialVersionUID = 8546021378316238914L;
	
	public static final int MARKER_SIZE = 10;
	public static final int VER_START = 0;
	public static final int VER_END = 1;
	public static final int VER_CTRL1 = 2;
	public static final int VER_CTRL2 = 3;
	static final int INIT_LEN = 40;
	
	static Locus load(Animation anim, ObjectInputStream ois) throws IOException, ClassNotFoundException{
		Locus l = (Locus)ois.readObject();
		l.animation = anim;
		return l;
	}
	
	static void save(Locus locus, ObjectOutputStream oos) throws IOException{
		oos.writeObject(locus);
	}
	
	List<Curve> curves = new ArrayList<Curve>();
	float[] segmentIncres;
	boolean reverse;
	boolean repeat;
	
	transient boolean inReverse;
	/**
	 * the current position in the current curve in the locus, represented by percentage
	 */
	transient float currentSegment;
	/**
	 * the index of current curve in the locus
	 */
	transient int currentCurve;
	transient Animation animation;
	
	Locus(Animation anim){
		animation = anim;
	}

	void addCurve(float startX, float startY, float step) {
		float x = startX, y = startY;
		int s = curves.size();
		if(s != 0){
			Curve last = curves.get(s - 1);
			Vector2f end = last.getEndPoint();
			x = end.x;
			y = end.y;
		}
		Vector2f p1 = new Vector2f(x, y);
		Vector2f c1 = new Vector2f(x, y);
		Vector2f c2 = new Vector2f(x + INIT_LEN, y + INIT_LEN);
		Vector2f p2 = new Vector2f(x + INIT_LEN, y + INIT_LEN);
		curves.add(new Curve(p1, c1, c2, p2));
		if(segmentIncres == null){
			segmentIncres = new float[1];
			updateSteps(step);
		}else{
			float[] tmp = segmentIncres;
			segmentIncres = new float[curves.size()];
			System.arraycopy(tmp, 0, segmentIncres, 0, tmp.length);
			setSegmentIncres(segmentIncres.length - 1, step);
		}
	}
	
	public List<Curve> getCurves(){
		return curves;
	}
	
	public void removeCurve(int index){
		curves.remove(index);
	}

	Vector2f getLocation(int curveIndex, float t) {
		Curve c = curves.get(curveIndex);
		return c.pointAt(t);
	}

	/**
	 * @deprecated
	 * @param step
	 */
	void updateSteps(float step) {
		for(int i = 0, len = curves.size(); i < len; ++i){
			setSegmentIncres(i, step);
		}
	}
	
	/**
	 * @deprecated
	 * @param i
	 * @param step
	 */
	void setSegmentIncres(int i, float step){
		Curve c = curves.get(i);
		Vector2f p1 = c.getStartPoint();
		Vector2f p2 = c.getEndPoint();
		float gX = p1.x - p2.x;
		float gY = p1.y - p2.y;
		float len = (float)Math.sqrt(gX * gX + gY * gY);
		segmentIncres[i] = step / len;
	}
	
	void updateReverse(Animation anim){
		if(segmentIncres == null)
			return;
		if(currentCurve >= 0){
			currentSegment -= segmentIncres[currentCurve];
			if(currentSegment < 0){
				currentSegment = 1;
				--currentCurve;
			}
			if(currentCurve >= 0){
				Vector2f p = getLocation(currentCurve, currentSegment);
				anim.x = (int)p.x;
				anim.y = (int)p.y;
			}else{
				if(repeat){
					inReverse = false;
					++currentCurve;
					currentSegment = 0;
				}
			}
		}
	}
	
	void update(Animation anim){
		if(segmentIncres == null)
			return;
		if (currentCurve < segmentIncres.length) {
			currentSegment += segmentIncres[currentCurve];
			if (currentSegment > 1) {
				currentSegment = 0;
				++currentCurve;
			}
			if (currentCurve < segmentIncres.length) {
				Vector2f p = getLocation(currentCurve, currentSegment);
				anim.x = (int) p.x;
				anim.y = (int) p.y;
			} else {
				if (reverse) {
					inReverse = true;
					--currentCurve;
					currentSegment = 1;
				} else {
					if (repeat) {
						Curve c = curves.get(0);
						Vector2f p = c.getStartPoint();
						anim.x = (int) p.x;
						anim.y = (int) p.y;
						currentCurve = 0;
						currentSegment = 0;
					}
				}
			}
		}
	}

	public boolean isReverse() {
		return reverse;
	}
	
	public void setReverse(boolean b){
		reverse = b;
	}
	
	public boolean isRepeat(){
		return repeat;
	}
	
	public void setRepeat(boolean b){
		repeat = b;
	}

}
