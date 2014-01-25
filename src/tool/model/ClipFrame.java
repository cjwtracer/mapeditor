package tool.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClipFrame implements Serializable{
	private static final long serialVersionUID = -7590854346044367911L;
	
	public static class FrameLayer implements Serializable{
		private static final long serialVersionUID = -4376962675015966774L;
		
		List<Clip> cells = new ArrayList<Clip>();

		public boolean isVisible() {
			return true;
		}
		
		public List<Clip> getCells(){
			return cells;
		}

		public Clip newClipAt(int x, int y, String resource) {
			Clip c = new Clip();
			c.x = x;
			c.y = y;
			c.resource = resource;
			cells.add(c);
			return c;
		}
	}
	
	List<FrameLayer> layers = new ArrayList<FrameLayer>();
	long duration = FrameChange.GAP;
	transient int cycle;
	
	ClipFrame(){
		layers.add(new FrameLayer());
	}
	
	public List<FrameLayer> getLayers(){
		return layers;
	}

	public FrameLayer deleteLayer(int index) {
		if(index < 0 || index >= layers.size())
			return null;
		return layers.remove(index);
	}

	public FrameLayer addLayer(int index) {
		FrameLayer l = new FrameLayer();
		if(index < 0)
			layers.add(l);
		else
			layers.add(index, l);
		return l;
	}

}
