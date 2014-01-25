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
 * Handle version changes of file system. Important: only adding change is permitted, removing may cause damages.
 */
public class Version {
	static final String VERSION_FILE = System.getProperty("user.home") + File.separator + ".MapEditor" + File.separator + "res.version";
	/**
	 * the version of the workspace files
	 */
	private int version = 0;
	/**
	 * the version of editor's file system
	 */
	private int modifiedVersion = 4;
	
	private Properties resourceVersion = new Properties();
	/**
	 * if need updating, it will save datas regardless of
	 * account permissions
	 */
	boolean update;
	
	/**
	 * Load the configuration from the given path.
	 * @param projectPath
	 * 					With the separator.
	 * @throws FileNotFoundException 
	 */
	public void load() throws FileNotFoundException{
		InputStream is = null;
		try{
			String versionFile = VERSION_FILE;
			is = new FileInputStream(versionFile);
			resourceVersion.load(is);
			String value = resourceVersion.getProperty("resVersion");
			version = Integer.parseInt(value);
		}catch(FileNotFoundException ex1){
			throw ex1;
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{if(is != null) is.close();}catch(IOException ex1){}
		}
	}
	
	/**
	 * Save the configuration to the given path.
	 * @param projectPath
	 */
	public void save(){
		OutputStream os = null;
		try{
			os = new FileOutputStream(VERSION_FILE);
			resourceVersion.setProperty("resVersion", String.valueOf(modifiedVersion));
			resourceVersion.store(os, null);
		}catch(IOException ex){
			ex.printStackTrace();
		}finally{
			try{if(os != null) os.close();}catch(IOException ex1){}
		}
	}
	
	/**
	 * Check whether there is a need to update the files of the loaded
	 * project to maintain the consistency of the datas. Programmer is
	 * responsible for examine the validity of the updating operation
	 * by ensuring version of current file system if higher than the
	 * project loaded @see {@link #legalVersion()}.
	 * @return
	 */
	public boolean updateDatas(){
		return update = version < modifiedVersion;
	}
	
	/**
	 * Return false if the version of the loaded project is higher
	 * the editor's file system.
	 * @return
	 */
	public boolean legalVersion(){
		return modifiedVersion >= version;
	}

	public int currentVersion() {
		return version;
	}

	public void sycVersion() {
		version = modifiedVersion;
	}

}
