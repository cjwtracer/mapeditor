package tool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * interfaces
 */
public class Queue implements Serializable{
	private static final long serialVersionUID = 135230042359962345L;
	
	static Queue load(Animation anim, ObjectInputStream ois) throws IOException, ClassNotFoundException{
		Queue q = (Queue)ois.readObject();
		q.anim = anim;
		int s = ois.readInt();
		q.changes = new ArrayList<GradualChange>(s);
		for(int i = 0; i < s; ++i){
			GradualChange c = GradualChange.load(q, ois);
			q.changes.add(c);
		}
		return q;
	}
	
	static void save(Queue queue, ObjectOutputStream oos) throws IOException{
		oos.writeObject(queue);
		oos.writeInt(queue.changes.size());
		for(GradualChange c : queue.changes)
			GradualChange.save(c, oos);
	}
	
	public String id;
	public boolean repeat;
	public boolean reverse;
	boolean inReverse;
	transient Animation anim;
	transient List<GradualChange> changes = new ArrayList<GradualChange>();
	
	Queue(Animation anim){
		this.anim = anim;
		id = anim.getId() + "-" + anim.animationQueues.size();
	}
	
	public List<GradualChange> getChanges(){
		return changes;
	}
	
	public GradualChange addChange(byte type){
		GradualChange c = anim.sequence.addChange(type);
		c.queue = this;
		changes.add(c);
		anim.toStartState();
		return c;
	}
	
	byte toStartState(){
		byte rslt = -1;
		int s = changes.size();
		if(s > 0){
			for(int i = s - 1; i >= 0; --i){
				GradualChange c  = changes.get(i);
				c.resetState();
				rslt = c.type;
			}
		}
		return rslt;
	}
}