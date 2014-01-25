package tool.io;

import tool.model.Project;
import tool.util.FileUtil;

/**
 * Write the project file.
 * 
 * @author caijw
 * 
 */
public class ProjectWriter {
	
	public static void writeProject(String path, Project pro){
		FileUtil.saveObject(path, pro);
	}

}
