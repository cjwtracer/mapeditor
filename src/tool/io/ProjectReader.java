package tool.io;

import java.io.FileNotFoundException;

import tool.model.Project;
import tool.util.FileUtil;

/**
 * Read the project file.
 * @author caijw
 *
 */
public class ProjectReader {
	
	public static Project loadProject(String workspacePath) throws FileNotFoundException{
		Project pro = (Project)FileUtil.readObject(workspacePath);
		return pro;
	}

}
