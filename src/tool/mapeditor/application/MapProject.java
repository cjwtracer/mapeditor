package tool.mapeditor.application;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;

import tool.model.Project;
import tool.model.ResourceSet;
import tool.model.WorldMap;
import tool.util.FileUtil;

/**
 * The implementation of the project model.
 * @author caijw
 *
 */
public class MapProject implements Serializable, Project{
	private static final long serialVersionUID = 7098661493970950136L;
	
	transient String path;
	transient String mapDir;
	transient String resDir;
	transient String expDir;
	LinkedHashMap<Integer, WorldMap> mapGroup;
	LinkedHashMap<Short, ResourceSet> resourceGroup;
	
	public MapProject(String workspace){
		new File(workspace + File.separator + MAP_DIR).mkdir();
		new File(workspace + File.separator + RES_DIR).mkdir();
		new File(workspace + File.separator + EXPORT_DIR).mkdir();
		path = workspace + File.separator;
	}
	
	public LinkedHashMap<Integer, WorldMap> getMapGroup(){
		if(mapGroup == null)
			mapGroup = new LinkedHashMap<Integer, WorldMap>();
		return mapGroup;
	}
	
	public String getMapDir() {
		mapDir = path + MAP_DIR + File.separator;
		return mapDir;
	}

	public int getAvailableMapId() {
		int id = FileUtil.getAvailableFileNameIndex(getMapDir(), MAP_EXT);
		while(getMapGroup().keySet().contains(id)){
			++id;
		}
		return id;
	}

	public LinkedHashMap<Short, ResourceSet> getResourceGroup() {
		if(resourceGroup == null)
			resourceGroup = new LinkedHashMap<Short, ResourceSet>();
		return resourceGroup;
	}
	
	public String getWorkspaceDir(){
		return path;
	}

	public String getResourceDir() {
		resDir = path + RES_DIR + File.separator;
		return resDir;
	}

	public short getAvailableResourceSetId() {
		int id = FileUtil.getAvailableFileNameIndex(getResourceDir(), null);
		if(id > Short.MAX_VALUE)
			throw new RuntimeException("Too many resource sets have been created, dump some unused!");
		return (short)id;
	}

	public String getExportDir() {
		expDir = path + EXPORT_DIR + File.separator;
		return expDir;
	}

	public String getConfigDir() {
		return new StringBuffer(path).append(CONFIG_DIR).append(File.separator).toString();
	}

}
