package tool.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

import tool.util.Log;

public class FileUtil {
	/**
	 * Get the directory within which the executing package is. With the file separator.
	 * @return
	 */
	public static String getCurrentDirectory() {
		String path;
		try {
			path = URLDecoder.decode(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			path = "";
		}
		if (!path.endsWith(File.separator)) {
			int pos = path.lastIndexOf(File.separator);
			if (pos != -1) {
				path = path.substring(0, pos);
			}
			path += File.separator;
		}
		return path;
	}

	public static String getAbsolutePath(String relativePath) {
		File currentPath = new File(relativePath);
		try {
			return currentPath.getCanonicalPath();
		} catch (IOException e) {
			return "";
		}
	}
	
	public static String getAbsoluteParentPath(String relativePath){
		String parent = new File(relativePath).getParent();
		try{
			return new File(parent).getCanonicalPath();
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * The method serializes all the serializable object of project.
	 * @param file
	 * @param obj
	 * @return
	 */
	public static boolean saveObject(String file, Object obj) {
		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(new FileOutputStream(file));
			os.writeObject(obj);
			return true;
		} catch (IOException e) {
			Log.logException(e);
			e.printStackTrace();
			return false;
		} finally {
			if (os != null) {
				try {os.close();} catch (IOException e) {/* IGNORED */}
			}
		}
	}
	/**
	 * 
	 * @param filePath
	 * @return
	 * 		The object serialized, or null when there is any
	 * 		exception existing.
	 */
	public static Object readObject(String filePath) throws FileNotFoundException{
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(filePath));
			return in.readObject();
		} catch(FileNotFoundException e1){
			Log.logException(e1);
			throw e1;
		}catch (Exception e) {
			Log.logException(e);
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {in.close();} catch (IOException e) {/* IGNORED */}
			}
		}
	}
	
	public static void saveFile(String outputFile, byte[] data) {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(outputFile);
			os.write(data);
			os.flush();
		} catch (IOException e) { 
			e.printStackTrace();
		} finally {
			if(os != null) {
				try { os.close(); } catch (IOException e) { }
			}
		}
	}
	
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}
	
	public static void deleteFile(File file){
		if(file == null)
			return;
		if(file.exists())
			file.delete();
	}
	
	/**
	 * Remove the the given directory and all its files, but it will not do it
	 * recursively, so the directory must not contains sub directories.
	 * 
	 * @param directory
	 */
	public static void removeDirectory(String directory) {
		File dir = new File(directory);
		File[] files = dir.listFiles();
		for (File f : files) {
			f.delete();
		}
		dir.delete();
	}
	
	/**
	 * Read a file into a byte array.
	 * @param inputFile
	 * @return
	 */
	public static byte[] readFile(String inputFile) {
		byte[] data = null;
		FileInputStream is = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			is = new FileInputStream(inputFile);
			byte[] buf = new byte[512];
			int nRead = 0;
			while ((nRead = is.read(buf)) != -1) {
				os.write(buf, 0, nRead);
			}

			data = os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {is.close();} catch (IOException e) {/* IGNORED */}
			}			
		}
		
		return data;
	}
	
	public static void copyFile(String srcFilePath, String destFilePath) {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(srcFilePath);
			fos = new FileOutputStream(destFilePath);
			int nRead = 0; 
			byte[] buf = new byte[512];
			while ((nRead = fis.read(buf)) != -1) {
				fos.write(buf, 0, nRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {fis.close();} catch (IOException e) {/* IGNORED */}
			}
			if (fos != null) {
				try {fos.close();} catch (IOException e) {/* IGNORED */}
			}			
		}
	}
	
	/**
	 * Get the file with the given extension name.
	 * @param dirPath
	 * @param extension
	 * @return
	 */
	public static File[] listFiles(String dirPath, final String extension) {
		File dir = new File(dirPath);
		if (!dir.exists()) return null;
		if(extension == null){
			return dir.listFiles(new FileFilter(){
				public boolean accept(File file){
					return file.isDirectory();
				}
			});
		}else{
			final String ext = extension.toLowerCase();
			return dir.listFiles(new FileFilter() {
	
				public boolean accept(File file) {
					String name = file.getName().toLowerCase();
					return name.endsWith(ext);
				}
			});
		}
	}
	
	public static String getParentDirectory(String filePath) {
		File file = new File(filePath);		
		return file.getParent();
	}

	public static int getMaxFileNameIndex(String dirPath, String extName) {
		File[] files = FileUtil.listFiles(dirPath, extName);
		int maxIndex = 0;
		if (files != null) {
			for (File f : files) {
				try {
					String name = f.getName();
					int idx = Integer.parseInt(name.substring(0, name.length() - extName.length()));
					if (idx > maxIndex) {
						maxIndex = idx;
					}
				} catch (Exception e) {
				}
			}
		}
		return maxIndex;
	}
	
	public static int getAvailableFolderNameIndex(String dir){
		int index = 0;
		File dirFile = new File(dir);
		if(!dirFile.exists() || !dirFile.isDirectory())
			throw new IllegalArgumentException();
		File[] files = dirFile.listFiles(new FileFilter(){
			public boolean accept(File file){
				return file.isDirectory();
			}
		});
		if(files.length > 0){
			boolean exist = false;
			do{
				exist = false;
				for(File f : files){
					String nm = f.getName();
					try{
						int tmp = Integer.parseInt(nm);
						if(tmp == index){
							exist = true;
							index = ++tmp;
						}
					}catch(NumberFormatException e){}
				}
			}while(exist);
		}
		return index;
	}
	
	public static int getAvailableFileNameIndex(String dirPath, String extName) {
		int index = 0;
		File[] files = listFiles(dirPath, extName);
		if (files != null) {
			boolean existed = false;
			do {
				existed = false;
				for (File f : files) {
					try {
						String name = f.getName();
						int extLength = extName == null ? 0 : extName.length();
						int nameIndex = Integer.parseInt(name.substring(0, name.length() - extLength));
						if (nameIndex == index) {
							existed = true;
							index++;
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} while (existed);
		}
		return index;
	}
	
	/**
	 * Get the file name from the given absolute file path.
	 * @param path
	 * @return
	 */
	public static String pathToName(String path){
		if(path == null || path.isEmpty()) return "";
		
		StringBuffer buf = new StringBuffer(path);
		int last = buf.lastIndexOf("\\");
		return buf.substring(last + 1);
	} 
	/**
	 * Verify the valid numeric file name, with extension.
	 * @param name
	 * @return
	 */
	public static boolean isNumericFileName(String name){
		if(name == null)
			return false;
		int lastIdx = name.lastIndexOf(".");
		String id = name.substring(0, lastIdx == -1 ? name.length() : lastIdx);
		char[] chars = new char[id.length()];
		id.getChars(0, chars.length, chars, 0);
		for(int i = 0; i < chars.length; i++){
			if(!(chars[i] <= '9' && chars[i] >= '0')) return false;
			if(i == 0){
				if(chars[0] == 0 && chars.length > 1)return false;
			}
		}
		return true;
	}
	/**
	 * Check the file name to ensure it be a numeric one.
	 * @param fileName
	 * @return
	 */
	public static boolean validFileName(String fileName){
		boolean result  = true;
		
		String idStr = fileName.substring(0, fileName.lastIndexOf("."));
		if(idStr.equals("")) result = false;
		char[] chars = new char[idStr.length()];
		idStr.getChars(0, chars.length, chars, 0);
		for(int k = 0; k < chars.length; k++){
			if((chars[0] == 0 && chars.length > 1) || !(chars[k] >= '0' && chars[k] <= '9')){
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Remove the extension part of the file name.
	 * @param fileName
	 * @return
	 */
	public static String removeExtension(String fileName){
		if(!isNumericFileName(fileName)) return null;
		if(fileName.lastIndexOf(".") < 0) return fileName;
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	/**
	 * Check the existance of the given path, if not, create it.
	 * @param path
	 * @return
	 */
	public static String checkPath(String path){
		if(path != null){
			File f = new File(path);
			if(!f.exists())
				f.mkdirs();
		}
		return path;
	}
	
	/**
	 * Utility method of loading a property from a file.
	 * @param prop
	 * @param filename
	 */
	public static void loadProperties(Properties prop, String filename){
		if(prop == null || filename == null)
			throw new IllegalArgumentException();
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(filename);
			InputStreamReader reader = new InputStreamReader(fis, Constants.ENCODING);
			prop.load(reader);
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(fis != null)try {fis.close();} catch (IOException e) {}
		}
	}

	/**
	 * Utility method of writing a property into a file.
	 * @param p
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveProperties(Properties p, String fileName) throws IOException {
		if(p == null || fileName == null){
			System.err.println("null args");
			return;
		}
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
			OutputStreamWriter writer = new OutputStreamWriter(fos, Constants.ENCODING);
			p.store(writer, null);
			writer.close();
		}catch(IOException e){
			throw e;
		}finally{
			if(fos != null)try {fos.close();} catch (IOException e) {}
		}
	}
	
	/**
	 * Get the last file separator of the given absolute file name.
	 * @param absFileName
	 * @return
	 */
	public static int lastSeparator(String absFileName){
		if(absFileName == null)
			return -1;
		int i = absFileName.lastIndexOf(File.separator);
		if(i < 0)
			i = absFileName.lastIndexOf("/");
		return i;
	}
	
	public static void main(String[] args) {
		//System.out.println(getMaxFileNameIndex("D:\\var\\myproject\\items\\", ".2"));
		System.out.println(getAvailableFileNameIndex("D:\\var\\myproject\\anim\\", ".a"));
	}
}
