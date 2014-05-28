package tool.mapeditor;

import java.util.List;

import org.eclipse.swt.graphics.Point;

/**
 * 定义了操作资源的接口
 * @author caijw
 *
 */
public interface Resource {
	interface Item extends Drawable{

		String getName();

		int getItemID();

		Point getSize();
	}

	String getName();
	
	List<? extends Item> getResourceItems();
	/**
	 * 以指定名称，添加指定URL的图片到资源组
	 * @param resUrl
	 * @param name
	 */
	void addResourceItem(String resUrl, String name);
	
	public String getPrefix();
	
	public void setPrefix(String text);

}
