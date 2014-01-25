package tool.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Configurations of the map editor, such as the work space path.
 * 
 * @author caijw
 * 
 */
public class Configuration {
	static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".MapEditor" + File.separator;
	static final String WORKSPACE = "workspace";
	private Properties prop = new Properties();
	private String workspacePath = "";
	
	public Configuration(){
	}
	
	public String getWorkspacePath(){
		return workspacePath;
	}
	
	/**
	 * Set the work space path of the map editor.
	 * 
	 * @param workspace
	 */
	public void setWorkspacePath(String workspace){
		workspacePath = workspace;
	}

	/**
	 * Load the configurations of the editor.
	 * 
	 * @throws FileNotFoundException
	 */
	public void load() throws FileNotFoundException{
		InputStream is = null;
		try{
			is = new FileInputStream(CONFIG_DIR + "conf.properties");
			prop.load(is);
			workspacePath = prop.getProperty(WORKSPACE, "");
		}catch(FileNotFoundException ex){
			throw ex;
		} catch (IOException ex2) {
			ex2.printStackTrace();
		}catch(Exception ex1){
			ex1.printStackTrace();
		} finally {
			if(is != null) try{is.close();}catch(IOException e) {}
		}
	}
	
	/**
	 * Save the configurations of the editor.
	 */
	public void save() {
		prop.setProperty(WORKSPACE, workspacePath);
		OutputStream os = null;
		try {
			File outFile = null;
			File f = new File(CONFIG_DIR);
			if(!f.exists())
				f.mkdir();
			String filePath = CONFIG_DIR + "conf.properties";
			outFile = new File(filePath);
			os = new FileOutputStream(outFile);
			prop.store(os, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(os != null) try{os.close();}catch(IOException e){}
		}
	}
}
