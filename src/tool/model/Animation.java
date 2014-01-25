package tool.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Vector2f;

import tool.io.DataCrackException;

public class Animation implements Serializable{
	private static final long serialVersionUID = 5591907498492671230L;
	
	public static void load(String url, Animation anim) throws DataCrackException{
		if(url == null || anim == null){
			System.err.println("null args");
			return;
		}
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(url);
			ObjectInputStream ois = new ObjectInputStream(fis);
			int queueSize = ois.readInt();
			anim.sequence = new ChangeSequence(anim);
			anim.animationQueues = new ArrayList<Queue>(queueSize);
			for(int i = 0; i < queueSize; ++i){
				Queue q = Queue.load(anim, ois);
				anim.animationQueues.add(q);
				for(GradualChange c : q.changes){
					anim.sequence.changes.add(c);
				}
			}
			anim.locus = Locus.load(anim, ois);
			ois.close();
		}catch(ClassNotFoundException e1){
			throw new DataCrackException(e1);
		}catch(IOException e){
			throw new DataCrackException(e);
		}finally{
			try{if(fis != null)fis.close();}catch(IOException ex){}
		}
	}
	
	public static void save(String url, Animation anim) throws IOException{
		if(url == null || anim == null){
			System.err.println("null args");
			return;
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(url);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(anim.animationQueues.size());
			for(Queue q : anim.animationQueues){
				Queue.save(q, oos);
			}
			Locus.save(anim.locus, oos);
			oos.close();
		}finally{
			try{if(fos != null)fos.close();}catch(IOException ex){}
		}
	}

	/**
	 * File ID, must be exclusive.
	 */
	int id;
	String name;
	int x;
	int y;
	int width;
	int height;
	int rotation;
	int alpha = 255;
	float alphaF = 1f;
	float scale = 1;
	float step = 1;
	/**
	 * the underlying implementation mechanism
	 */
	transient ChangeSequence sequence = new ChangeSequence(this);
	transient Locus locus = new Locus(this);
	transient List<Queue> animationQueues = new ArrayList<Queue>();
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setAlpha(int v) {
		alpha = v;
		alphaF = v / (float)256;
	}

	public void init() {
		locus.currentSegment = 0;
		locus.currentCurve = 0;
		locus.inReverse = false;
		reset();
	}

	public void reset() {
		for(Queue q : animationQueues)
			q.inReverse = false;
		sequence.resetState();
	}
	
	public void extendLocus(){
		locus.addCurve(x, y, step);
	}

	public Locus getLocus() {
		return locus;
	}
	
	public List<GradualChange> getChanges(){
		return sequence.changes;
	}
	
	public ChangeSequence getChangeSequence(){
		return sequence;
	}

	/**
	 * Update the frame of the unit if it's animated.
	 */
	public void update() {
		sequence.updateState();
		for(Queue q : animationQueues){
			int s = q.changes.size();
			if(s > 0){
				if(q.reverse){
					if(q.inReverse){
						if(q.changes.get(0).reachLast()){
							if(q.repeat){
								q.inReverse = false;
								for(GradualChange c : q.changes)
									c.inReverse = false;
							}
						}
					}else{
						if (q.changes.get(s - 1).reachLast()) {
							for (GradualChange c : q.changes) {
								if (!c.reverse)
									c.inReverse = true;
								else
									c.inReverse = false;
							}
							q.inReverse = true;
						}
					}
				}else{
					if(q.changes.get(s - 1).reachLast()){
						if(q.repeat){
							q.toStartState();
						}
					}
				}
			}
		}
	}

	public int getAlpha() {
		return alpha;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int v) {
		rotation = v;
	}

	public float getScale() {
		return scale;
	}
	
	public void setScale(float v){
		scale = v;
	}

	public float getAlphaF() {
		return alphaF;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setAlphaF(float alphaF) {
		this.alphaF = alphaF;
	}

	public void setW(int w) {
		this.width = w;
	}

	public void setH(int h) {
		this.height = h;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void removeQueue(Queue queue) {
		if(queue == null)
			return;
		for(GradualChange c : queue.changes){
			sequence.changes.remove(c);
		}
		animationQueues.remove(queue);
		toStartState();
	}
	
	public void removeChange(GradualChange change){
		for(Queue q : animationQueues){
			if(q.changes.contains(change)){
				q.changes.remove(change);
				break;
			}
		}
		sequence.changes.remove(change);
		toStartState();
	}
	
	public List<Queue> getQueues(){
		return animationQueues;
	}
	
	public Queue newQueue(){
		Queue q = new Queue(this);
		animationQueues.add(q);
		return q;
	}
	
	public void toStartState(){
		if(animationQueues.size() == 0){
			setAlpha(256);
			setRotation(0);
			setScale(1);
		}else{
			boolean[] has = new boolean[4];
			for(Queue q : animationQueues){
				switch(q.toStartState()){
				case GradualChange.CHANGE_ALPHA:
					has[0] = true;
					break;
				case GradualChange.CHANGE_ROTATION:
					has[1] = true;
					break;
				case GradualChange.CHANGE_SCALE:
					has[2] = true;
					break;
				case GradualChange.CHANGE_POSITION:
					has[3] = true;
					break; 
				}
			}
			for(int i = 0; i < has.length; ++i){
				if(!has[i]){
				switch(i){
					case 0:
						setAlpha(256);
						break;
					case 1:
						setRotation(0);
						break;
					case 2:
						setScale(1);
						break;
					case 3:
						if(locus.curves.size() > 0){
							Curve cv = locus.curves.get(0);
							Vector2f p = cv.getStartPoint();
							x = (int)p.x;
							y = (int)p.y;
						}
						break;
					}
				}
			}
		}
	}
}
