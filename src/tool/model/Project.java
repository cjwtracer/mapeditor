package tool.model;

import java.util.LinkedHashMap;


public interface Project {
	String PRO = ".pro";
	String MAP_EXT = ".m";
	String RES_EXT = ".r";
	String MAP_DIR = "map";
	String RES_DIR = "res";
	String EXPORT_DIR = "export";
	String CONFIG_DIR = "configs";

	int getAvailableMapId();
	
	short getAvailableResourceSetId();
	
	String getMapDir();
	
	String getResourceDir();
	
	String getExportDir();
	
	String getWorkspaceDir();
	
	LinkedHashMap<Integer, WorldMap> getMapGroup();
}
