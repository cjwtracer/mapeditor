package tool.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class ResourceItem implements Serializable{
	private static final long serialVersionUID = -2864930115266270860L;
	
	int id;
	String name;
	
	ResourceItem(){}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public void load(DataInputStream dis) throws IOException{
		id = dis.readInt();
		name = dis.readUTF();
	}
	
	public void save(DataOutputStream dos) throws IOException{
		dos.writeInt(id);
		dos.writeUTF(name);
	}
	
	/**
	 * @deprecated
	 * @param itemID
	 * @return
	 */
	public static short getResourceSetID(int itemID){
		return (short)((itemID >> 16) & 0xffffffff);
	}

}
