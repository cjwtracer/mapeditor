package tool.mapeditor.application;

/**
 * A descriptor is a wrapper of its cooresponding model object, providing
 * extending functions such as platform specific features.
 * 
 * @author caijw
 * 
 */
public abstract class Descriptor {
	MainApplication mainApp = MainApplication.getInstance();
	
	protected Descriptor(){}

}
