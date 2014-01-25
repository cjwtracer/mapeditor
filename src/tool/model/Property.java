package tool.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

/**
 * Define a property of a model.
 * @author caijw
 *
 */
public class Property implements Serializable{
	private static final long serialVersionUID = 6846783814677736235L;
	
	public final static String[] TYPE_LIST = {"String", "long", "int", "short", "byte", "boolean"};
	
	static boolean validType(String type){
		for(String s : TYPE_LIST){
			if(type.equals(s))
				return true;
		}
		return false;
	}
	
	static boolean validValue(String type, Object value){
		try{
			if(type.equals("long")){
				Long.valueOf((Long)value);
			}else if(type.equals("int")){
				Integer.valueOf((Integer)value);
			}else if(type.equals("short")){
				Short.valueOf((Short)value);
			}else if(type.equals("byte")){
				Byte.valueOf((Byte)value);
			}else if(type.equals("boolean")){
				Boolean.valueOf((Boolean)value);
			}else if(type.equals("String")){
				String.valueOf(value);
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return validType(type);
	}
	
	public static Object defaultValue(String type){
		if(!validType(type))
			return null;
		if(type.equals("String")){
			return "";
		}else if(type.equals("boolean")){
			return true;
		}else{
			return 0;
		}
	}
	
	String name;
	String type;
	Object value;
	
	Model model;
	
	Property(String name, String type, Object value){
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getValue(){
		return value;
	}

	public void setName(String text) {
		name = text;
	}

	/**
	 * Set the given value of the given type.
	 * @param type
	 * @param v
	 */
	public boolean setValue(String type, String v) {
		if(!validType(type))
			return false;
		Object o = v;
		try{
			if(type.equals("long")){
				o = Long.valueOf(v);
			}else if(type.equals("int")){
				o = Integer.valueOf(Integer.valueOf(v));
			}else if(type.equals("short")){
				o = Short.valueOf(Short.valueOf(v));
			}else if(type.equals("byte")){
				o = Byte.valueOf(Byte.valueOf(v));
			}else if(type.equals("boolean")){
				o = Boolean.valueOf(Boolean.valueOf(v));
			}
		}catch(Exception ex){
			System.err.println("Illegal Format");
			ex.printStackTrace();
			return false;
		}
		if(!validValue(type, o))
			return false;
		this.type = type;
		value = o;
		return true;
	}

	/**
	 * 
	 * @param value
	 * @return True if setting succeeds.
	 */
	public boolean setValue(String value){
		return setValue(type, value);
	}

	public String getType() {
		return type;
	}

	public Property getCopy() {
		return new Property(name, type, value);
	}
	
	public void print(PrintWriter pw) throws IOException{
		pw.println(name);
		pw.println(type);
		if(type.equals("long")){
			pw.println((Long)value);
		}else if(type.equals("int")){
			pw.println((Integer)value);
		}else if(type.equals("short")){
			pw.println((Short)value);
		}else if(type.equals("byte")){
			pw.println((Byte)value);
		}else if(type.equals("boolean")){
			pw.println((Boolean)value);
		}else if(type.equals("String")){
			pw.println((String)value);
		}
	}

	public void write(DataOutputStream dos) {
		try {
			dos.writeUTF(name);
			dos.writeUTF(type);
			if(type.equals("long")){
				dos.writeLong((Long)value);
			}else if(type.equals("int")){
				dos.writeInt((Integer)value);
			}else if(type.equals("short")){
				dos.writeShort((Short)value);
			}else if(type.equals("byte")){
				dos.writeByte((Byte)value);
			}else if(type.equals("boolean")){
				dos.writeBoolean((Boolean)value);
			}else if(type.equals("String")){
				dos.writeUTF((String)value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the default values of the property specified by the given property
	 * name, if the property doesn't exist it returns null.
	 * 
	 * @return
	 */
	public String[] getDefaultValues() {
		return model.getDefaultValues(name);
	}
}