package tool.mapeditor.application;

import java.util.ArrayList;
import java.util.List;

/**
 * A descriptor is a wrapper of its cooresponding model object, providing
 * extending functions such as platform specific features.
 * 
 * @author caijw
 * 
 */
public abstract class Descriptor {
	
	static Enum<?>[] append(Enum<?>[] base, Enum<?>[] add){
		int len = base.length + add.length;
		List<Enum<?>> ev = new ArrayList<Enum<?>>(len);
		for(Enum<?> e : base)
			ev.add(e);
		for(Enum<?> e : add)
			ev.add(e);
		return ev.toArray(new Enum<?>[len]);
	}
	
	MainApplication mainApp = MainApplication.getInstance();
	
	protected Descriptor(){}

}
