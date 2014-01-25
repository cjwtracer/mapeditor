package tool.mapeditor;

import java.util.List;

import org.eclipse.swt.graphics.Point;

public interface Resource {
	interface Item extends Drawable{

		String getName();

		int getItemID();

		Point getSize();
	}

	String getName();
	
	List<? extends Item> getResourceItems();
	
	void addResourceItem(String resUrl, String name);
	
	public String getPrefix();
	
	public void setPrefix(String text);

}
