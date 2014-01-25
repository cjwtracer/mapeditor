package tool.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import tool.util.Constants;
import tool.util.StringUtil;

/**
 * A kind of object that may have self-defined properties.
 * 
 * Property system: Each type of a model has its specific properies. The types
 * and properies are define in the config/*.prop files, and modification in the
 * .prop files, including add types(type must exist in the model and be added to
 * last) and change type propties, will be presented in the editor, and only in
 * the new created models.
 * 
 * @author caijw
 * 
 */
public abstract class Model implements Parsable, Serializable{
	private static final long serialVersionUID = -5201498350344374081L;

	public static final String SEP = "\\|";
	public static final String DEFAULT_SEP = "!";
	
	static void loadProps(String propFile, HashMap<Integer, PropHelper> proto){
		FileInputStream fis = null;
		Properties prop = new Properties();
		try{
			fis = new FileInputStream(propFile);
			InputStreamReader reader = new InputStreamReader(fis, Constants.ENCODING);
			prop.load(reader);
			reader.close();
		}catch(IOException e){
			if(fis != null)try{fis.close();}catch(IOException ex){}
		}
		ArrayList<String> types = new ArrayList<String>();
		for(String s : prop.stringPropertyNames()){
			if(s.startsWith("names")){
				String tp = s.substring(5);
				types.add(tp);
				PropHelper p = new PropHelper();
				p.names = prop.getProperty(s).split(SEP);
				p.types = prop.getProperty("dataTypes" + tp).split(SEP);
				String dflt = prop.getProperty("defaults" + tp);
				if(dflt != null){
					String[] defaults = dflt.split(SEP);
					p.defaults = new String[defaults.length][];
					for(int i = 0; i < defaults.length; i++){
						if(defaults[i].isEmpty())
							p.defaults[i] = null;
						else
							p.defaults[i] = defaults[i].split(DEFAULT_SEP);
					}
				}
				proto.put(StringUtil.parseToNumeric(tp), p);
			}
		}
	}
	
	static String[] getDftValues(PropHelper ph, String propName){
		if(propName != null && ph != null && ph.defaults != null){
			for (int i = 0; i < ph.names.length; i++) {
				if (ph.names[i].equals(propName)) {
					return ph.defaults[i];
				}
			}
		}
		return null;
	}
	
	protected List<Property> props;
	
	public List<Property> getProperties(){
		if(props == null)
			props = new ArrayList<Property>();
		/*
		 * temparory
		 */
		for(Property p : props)
			p.model = this;
		return props;
	}

	protected void clearProps() {
		if(props != null)
			props.clear();
	}
	
	public Property newProperty(String name, String type, Object value) {
		if(name == null || type == null || value == null)
			throw new IllegalArgumentException("Invalid arguments!");
		if(!Property.validType(type))
			throw new IllegalArgumentException("Invalid type!");
		if(!Property.validValue(type, value))
			throw new IllegalArgumentException("Invalid value!");
		Property p = new Property(name, type, value);
		getProperties().add(p);
		return p;
	}
	
	Property newProperty(String name, String type){
		if(name == null || type == null)
			throw new IllegalArgumentException("Invalid arguments!");
		if(!Property.validType(type))
			throw new IllegalArgumentException("Invalid type!");
		Property p = new Property(name, type, Property.defaultValue(type));
		String[] vs = getDefaultValues(name);
		if(vs != null){
			p.setValue(type, vs[0]);
		}
		p.model = this;
		getProperties().add(p);
		return p;
	}
	
	public void removeProperty(int index){
		if(props != null && index > -1 && index < props.size()){
			props.remove(index);
		}
	}
	
	public void setVersion(int version){
	}

	/**
	 * Get the default values of the property specified by the given property
	 * name, if the property doesn't exist it returns null.
	 */
	protected abstract String[] getDefaultValues(String propName);

}
