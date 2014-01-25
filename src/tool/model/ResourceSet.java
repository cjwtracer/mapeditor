package tool.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tool.io.DataCrackException;

/**
 * Group of resource items.
 * 
 * @author caijw
 * 
 */
public class ResourceSet extends Model implements Serializable {
	private static final long serialVersionUID = 7696930080835842241L;
	
	/*----------------------------------------------
	 * ----------------------------------------------*/
	public static class ResourceIO{
		public static void loadResourceSet(Project project, ResourceSet resourceSet) throws DataCrackException{
			if(project == null || resourceSet == null)
				throw new IllegalArgumentException("Invalid arguments!");
			String resDir = project.getResourceDir() + resourceSet.id;
			String resPath = resDir + File.separator + resourceSet.id + Project.RES_EXT;
			loadResourceSet(resPath, resourceSet);
		}
		
		public static void loadResourceSet(String resFile, ResourceSet resourceSet) throws DataCrackException{
			if(resFile == null || resourceSet == null || !new File(resFile).exists())
				throw new IllegalArgumentException("Invalid arguments!");
			FileInputStream fis = null;
			try{
				fis = new FileInputStream(resFile);
				DataInputStream dis = new DataInputStream(fis);
				resourceSet.id = dis.readShort();
				resourceSet.name = dis.readUTF();
				for(ResourceItem item : resourceSet.getItems()){
					item.load(dis);
				}
				dis.close();
			}catch(FileNotFoundException ex){
				ex.printStackTrace();
			}catch(IOException ex1){
				throw new DataCrackException(ex1);
			}catch(Exception ex2){
				ex2.printStackTrace();
			}finally{
				try{if(fis != null)fis.close();}catch(IOException e){}
			}
		}
		
		public static void saveResourceSet(Project project, ResourceSet resourceSet) throws DataCrackException{
			if(project == null || resourceSet == null)
				throw new IllegalArgumentException("Invalid arguments!");
			String resDir = project.getResourceDir() + resourceSet.id;
			File f = new File(resDir);
			if(!f.exists())
				f.mkdir();
			String resPath = resDir + File.separator + resourceSet.id + Project.RES_EXT;
			FileOutputStream fos = null;
			try{
				fos = new FileOutputStream(resPath);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeShort(resourceSet.id);
				dos.writeUTF(resourceSet.name);
				for(ResourceItem i : resourceSet.items){
					i.save(dos);
				}
				dos.close();
			}catch(Exception ex){
				throw new DataCrackException(ex);
			}finally{
				try{if(fos != null)fos.close();}catch(IOException e){}
			}
		}
	}
	
	/**
	 * Generate the id of the resource set which is exclusive.
	 * @param pro
	 * @param resSet
	 */
	public static void generateId(Project pro, ResourceSet resSet){
		if(resSet == null)
			throw new IllegalArgumentException("Resource set can't be null!");
		resSet.id = pro.getAvailableResourceSetId();
		int i = 0;
		for(ResourceItem item : resSet.items){
			short idInSet = (short)i;
			item.id = composedID(resSet.id, idInSet);
			++i;
		}
	}
	
	static int composedID(int part1, int part2){
		return ((part1 << 16) & 0xffffffff) | part2;
	}

	transient protected int version;
	private short id;
	private String name;
	String prefix;
	List<ResourceItem> items;
	
	public ResourceSet(String name, String[] names){
		if(name == null || name.equals("") || names == null)
			throw new IllegalArgumentException("Invalid arguments!");
		if(names.length > Short.MAX_VALUE)
			throw new IllegalArgumentException("Too many resource items in a single set!");
		this.name = name;
		items = new ArrayList<ResourceItem>(names.length);
		for(int i = 0; i < names.length; ++i){
			ResourceItem item = new ResourceItem();
			item.name = names[i];
			items.add(item);
		}
	}
	
	public List<ResourceItem> getItems(){
		if(items == null)
			items = new ArrayList<ResourceItem>();
		return items;
	}
	
	public short getId(){
		return id;
	}

	public void setName(String name) {
		if(name == null || name.equals(""))
			throw new IllegalArgumentException("Name can't be null!");
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setVersion(int version){
		this.version = version;
	}

	@Override
	protected String[] getDefaultValues(String propName) {
		return null;
	}

	public String getPrefix() {
		if(prefix == null)
			prefix = "";
		return prefix;
	}

	public void setPrefix(String text) {
		if(text == null)
			text = "";
		prefix = text;
	}

	public void removeItem(ResourceItem resourceItem) {
		items.remove(resourceItem);
	}
	
	public ResourceItem addResourceItem(String name){
		if(name == null)
			return null;
		ResourceItem item = new ResourceItem();
		item.name = name;
		item.id = composedID(id, items.size());
		items.add(item);
		return item;
	}
}
