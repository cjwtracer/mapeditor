package tool.mapeditor.application;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import tool.model.Clip;
import tool.model.ClipFrame;
import tool.model.ClipFrame.FrameLayer;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.util.ImageUtil;

public class FrameDescriptor {
	
	ClipFrame frame;
	ItemDescriptor resource;
	
	public FrameDescriptor(ClipFrame frame){
		if(frame == null)
			throw new IllegalArgumentException("frame is null");
		this.frame = frame;
		if(frame.getLayers().size() > 0){
			FrameLayer layer = frame.getLayers().get(0);
			if(layer.getCells().size() > 0){
				Clip c = layer.getCells().get(0);
				resource = ResourceDescriptor.getResourceItem(c.getResource());
			}
		}
	}
	
	public void paint(GC gc, int offx, int offy, int destW, int destH){
		if(resource == null)
			return;
		if(ImageUtil.valid(resource.image)){
			Rectangle r = resource.getBounds();
			gc.drawImage(resource.image, 0, 0, r.width, r.height, offx, offy, destW, destH);
		}
	}
	
	public void setResource(ItemDescriptor resource){
		this.resource = resource;
		FrameLayer l = frame.getLayers().get(0);
		if(l.getCells().size() == 0){
			l.newClipAt(0, 0, resource.getResource());
		}else{
			l.getCells().get(0).setResource(resource.getResource());
		}
	}

}
