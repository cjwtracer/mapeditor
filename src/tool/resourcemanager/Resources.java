package tool.resourcemanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import tool.util.Constants;
import tool.util.FileUtil;

/**
 * Images gotten from SWTResourceManager.
 * @author Administrator
 *
 */
public class Resources {
	public static final Color RED = SWTResourceManager.getColor(SWT.COLOR_RED);
	public static final Color GRAY = SWTResourceManager.getColor(SWT.COLOR_GRAY);
	public static final Color WHITE = SWTResourceManager.getColor(SWT.COLOR_WHITE);
	public static final Color BLUE = SWTResourceManager.getColor(SWT.COLOR_BLUE);
	public static final Color YELLOW = SWTResourceManager.getColor(SWT.COLOR_YELLOW);
	public static final Color GREEN = SWTResourceManager.getColor(SWT.COLOR_GREEN);
	public static final Color BLACK = SWTResourceManager.getColor(SWT.COLOR_BLACK);
	public static final Color BG_BLUE = SWTResourceManager.getColor(0xF1, 0xF5, 0xFB);
	public static final Color BG_GREEN = SWTResourceManager.getColor(0xCC, 0xE8, 0xCF);
	
	public static final Cursor CURSOR_ARROW = SWTResourceManager.getCursor(SWT.CURSOR_ARROW);
	public static final Cursor CURSOR_CROSS = SWTResourceManager.getCursor(SWT.CURSOR_CROSS);
	public static final Cursor CURSOR_MOVE = SWTResourceManager.getCursor(SWT.CURSOR_SIZEALL);
	public static final Cursor CURSOR_RESIZE_NWSE = SWTResourceManager.getCursor(SWT.CURSOR_SIZENWSE);
	public static final Cursor CURSOR_RESIZE_NS = SWTResourceManager.getCursor(SWT.CURSOR_SIZENS);
	public static final Cursor CURSOR_RESIZE_NESW = SWTResourceManager.getCursor(SWT.CURSOR_SIZENESW);
	public static final Cursor CURSOR_RESIZE_WE = SWTResourceManager.getCursor(SWT.CURSOR_SIZEWE);
	public static final Cursor CURSOR_HAND = SWTResourceManager.getCursor(SWT.CURSOR_HAND);
	
	static final String KEY_SEP = "|";
	static String[] fonts;
	
	static HashMap<String, Font> fontMap = new HashMap<String, Font>();
	static HashMap<String, Texture> imageMap = new HashMap<String, Texture>();
	
	/**
	 * Get an image from the given relative path.
	 * @param imagePath
	 * 				The relative path of the image.
	 * @return
	 */
	public static Image getImage(String imagePath)
	{
		return SWTResourceManager.getImage(Resources.class, imagePath);
	}
	
	public static org.newdawn.slick.Font getFont(int fontSize)
	{
		return getFont("/font/Yahei Consolas Hybrid.ttf", fontSize);
	}
	
	public static Font getFont(String fontName, int fontSize)
	{
		return getFont(fontName, fontSize, false, false);
	}
	
	public static Font getFont(String fontName, int fontSize, boolean bold, boolean italic)
	{
		if(fontName == null || fontSize <= 0)
			throw new IllegalArgumentException();
		String key = new StringBuffer(fontName).append(KEY_SEP).append(fontSize)
					.append(KEY_SEP).append(bold).append(KEY_SEP).append(italic).toString();
		Font font = fontMap.get(key);
		if(font == null){
			try {
				UnicodeFont f = new UnicodeFont(fontName, fontSize, bold, italic);
				f.getEffects().add(new ColorEffect(java.awt.Color.white));
				font = f;
				fontMap.put(key, font);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		return font;
	}
	
	public static Texture getTexture(String path)
	{
		StringBuffer buf = new StringBuffer(Resources.class.getName()).append(KEY_SEP).append(path);
		String key = buf.toString();
		Texture tex = imageMap.get(key);
		if(tex == null){
			String ext = path.substring(path.lastIndexOf(".") + 1);
			try {
				tex = TextureLoader.getTexture(ext, Resources.class.getResourceAsStream(path));
				imageMap.put(key, tex);
			} catch (IOException e) {
				System.err.println("load texture failed");
				tex = null;
			}
		}
		return tex;
	}

	public static String[] getFontNames() 
	{
		if(fonts == null){
			Properties prop = new Properties();
			FileInputStream fis = null;
			try{
				fis = new FileInputStream(FileUtil.getCurrentDirectory() + "/font/fonts.config");
				InputStreamReader reader = new InputStreamReader(fis, Constants.ENCODING);
				prop.load(reader);
				String names = prop.getProperty("names");
				fonts = names.split("\\|");
				reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try{if(fis != null)fis.close();}catch(IOException ex){}
			}
		}
		return fonts;
	}

	public static String fullFontName(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		return name + ".ttf";
	}

}
