package tool.util;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import tool.resourcemanager.SWTResourceManager;

import com.flagstone.transform.Background;
import com.flagstone.transform.Movie;
import com.flagstone.transform.MovieHeader;
import com.flagstone.transform.Place2;
import com.flagstone.transform.ShowFrame;
import com.flagstone.transform.datatype.Bounds;
import com.flagstone.transform.datatype.WebPalette;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.shape.ShapeTag;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageShape;

public class ImageUtil {
	private static final Image MISSING_IMAGE = SWTResourceManager.getImage(ImageUtil.class, "/icons/collide_15.png");
	public static final String[] SURPORTED_FORMAT = {".png", ".jpg"};
	
	public static Image createMissingImage(int width, int height) {
		Image image = new Image(Display.getDefault(), width, height);
		GC gc = new GC(image);
		gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		gc.drawLine(0, 0, width, height);
		gc.drawLine(0, height, width, 0);
		gc.dispose();
		return image;
	}
	/**
	 * Create an image of direct color, if not transparent,
	 * it will be set opaque completely.
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageData createTransparentImage(int width, int height, boolean transparent) {
		PaletteData palette = new PaletteData(0xff0000, 0x00ff00, 0x0000ff);
		ImageData data = new ImageData(width, height, 32, palette);
		
		int alpha = 0;
		if(!transparent) alpha = 255;
		for (int i = 0; i < width * height; i++){
			data.setAlpha(i % width, i / width, alpha);
		}
		for (int i = 0, len = width * height; i < len; i++){
			data.setPixel(i % width, i / width, 0x0000000);
		}

		return data;
	}
	/**
	 * Get an image of a rectangle area from the source.
	 * @param srcData
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 * @throws Exception
	 */
	public static ImageData sliceImage(ImageData srcData, int x, int y, int width, int height) throws Exception{
		int srcWidth = srcData.width;
		int srcHeight = srcData.height;
		if (x + width > srcWidth || y + height > srcHeight)
			throw new IllegalArgumentException("The source image cannot be sliced.");

		ImageData data = new ImageData(width, height, srcData.depth, srcData.palette);
		data.transparentPixel = srcData.transparentPixel;
		boolean alpha = srcData.alphaData != null;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				data.setPixel(j, i, srcData.getPixel(x + j, (y + i)));
				if (alpha) {
					data.setAlpha(j, i, srcData.getAlpha(x + j, (y + i)));
				}
			}
		}

		return data;
	}

	/**
	 * Return a new array of image datas that are sliced from the source image.
	 * 
	 * @param srcData
	 * @param width
	 * @param height
	 * @return
	 */
	public static ImageData[] splitImage(ImageData srcData, int width, int height, int anchor){
		int srcWidth = srcData.width;
		int srcHeight = srcData.height;
		int col = srcWidth / width;
		int row = srcHeight / height;
		int wEnd = srcWidth % width;
		int hEnd = srcHeight % height;
		if(wEnd != 0)
			++col;
		if(hEnd != 0)
			++row;

		ImageData[] datas = new ImageData[col * row];
		if (datas.length == 1) {
			datas[0] = srcData;
		} else {
			for (int r = 0; r < row; r++) {
				int h = height;
				if(r == row - 1 && hEnd != 0)
					h = hEnd;
				for (int c = 0; c < col; c++) {
					int w = width;
					if(c == col - 1 && wEnd != 0)
						w = wEnd;
					ImageData data = new ImageData(w, h, srcData.depth, srcData.palette);
					data.transparentPixel = srcData.transparentPixel;
					boolean alpha = srcData.alphaData != null;
					datas[c + r * col] = data;
					int ox = c * width;
					int oy = r * height;
					switch(anchor){
					case 1:
						break;
					case 2:
						break;
					case 3:
						oy = srcHeight - (r + 1) * height;
						if(oy < 0)
							oy = 0;
						break;
					}
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							int x1 = x + ox;
							int y1 = y + oy;
							data.setPixel(x, y, srcData.getPixel(x1, y1));
							if (alpha) {
								data.setAlpha(x, y, srcData.getAlpha(x1, y1));
							}
						}
					}
				}
			}
		}
		return datas;
	}
	/**
	 * Algrithm of blending pixels with alpha data.
	 * @param alpha
	 * @param rgb
	 * 			c(n + 1).
	 * @param alphaFormer
	 * @param rgbFormer
	 * 					D(n).
	 * @param result
	 * 				The instance of RGB object to restore the color blending result.
	 * @return
	 */
	public static int blendPixelsWithAlpha(int alpha, RGB rgb, 
			int alphaFormer, RGB rgbFormer, RGB result){
		if(rgb == null || rgbFormer == null || result == null)
			throw new IllegalArgumentException();
		float a = alpha / (float)255;//a(n + 1)
		float aFormer = alphaFormer / (float)255;//A(n)
		float A = 1 - (1 - a) * (1 - aFormer);//A(n + 1)
		/*
		 * D(n + 1)
		 */
		result.red = Math.round(((1 - a) * rgbFormer.red * aFormer + rgb.red * a) / A);
		result.green = Math.round(((1 - a) * rgbFormer.green * aFormer + rgb.green * a) / A);
		result.blue = Math.round(((1 - a) * rgbFormer.blue * aFormer + rgb.blue * a) / A);
//		//totally opaque
//		if(alpha == 255){
//			data.setAlpha(x, y, alpha);
//			data.setPixel(x, y, d.getPixel(dx, dy));
//		}else{
//			int alphaBack = data.getAlpha(x, y);
//			float a = alpha / (float)255;
//			float aBack = alphaBack / (float)255;
//			int newAlpha = Math.round(a)
//			RGB rgbBack = data.palette.getRGB(data.getPixel(x, y));
////						if(alphaBack != 0){
////							rgb.red = Math.round((a * rgb.red + (1 - a) * rgbBack.red));
////							rgb.green = Math.round((a * rgb.green + (1 - a) * rgbBack.green));
////							rgb.blue = Math.round((a * rgb.blue + (1 - a) * rgbBack.blue));
////							data.setAlpha(x, y, Math.round((alpha * a + (1 - a) * alphaBack)));
////						}else{
////							data.setAlpha(x, y, alpha);
////						}
//			data.setAlpha(x, y, Math.round((alpha * a + (1 - a) * alphaBack)));
//			int pixel = data.palette.getPixel(rgb);
//			data.setPixel(x, y, pixel);
//		}
		return Math.round(A * 255);
	}
	
	public static ImageData compositeImages(ImageData[] images, 
			Point[] coords, int destWidth, int destHeight){
		if(images == null || coords == null|| images.length == 0 || coords.length == 0 ||
				images.length != coords.length || destWidth <= 0 || destHeight <= 0)
			throw new IllegalArgumentException();
		
		ImageData data = createTransparentImage(destWidth, destHeight, true);
		for(int i = 0; i < images.length; i++){
			ImageData d = images[i];
			Point p = coords[i];
			if(d == null || p == null)
				continue;
			int xoff = Math.max(p.x, 0);
			int yoff = Math.max(p.y, 0);
			int w = Math.min(data.width, p.x + d.width);
			int h = Math.min(data.height, p.y + d.height);
			if (d.transparentPixel == -1) {
				for (int y = yoff; y < h; y++) {
					for (int x = xoff; x < w; x++) {
						int dx = x - p.x;
						int dy = y - p.y;
						int alpha = d.getAlpha(dx, dy);
						RGB rgb = d.palette.getRGB(d.getPixel(dx, dy));//c(n + 1)
						int alphaFormer = data.getAlpha(x, y);
						RGB rgbFormer = data.palette.getRGB(data.getPixel(x, y));//D(n)
						RGB resultRGB = new RGB(0, 0, 0);
						int resultAlpha = blendPixelsWithAlpha(alpha, rgb, alphaFormer, rgbFormer, resultRGB);
						data.setAlpha(x, y, resultAlpha);
						data.setPixel(x, y, data.palette.getPixel(resultRGB));
					}
				}
			} else {
				for (int y = yoff; y < h; y++) {
					for (int x = xoff; x < w; x++) {
						int srcPixel = d.getPixel(x - p.x, y - p.y);
						if(srcPixel == d.transparentPixel) continue;
						data.setAlpha(x, y, 255);
						RGB rgb = d.palette.getRGB(srcPixel);
						int pixel = data.palette.getPixel(rgb);
						data.setPixel(x, y, pixel);
					}
				}
			}
		}
		return data;
	}

	public static ImageData mergeImages(ImageData[] images, int tileWidth, int tileHeight) {
		int totalTiles = 0;
		for (int i = 0; i < images.length; i++) {
			totalTiles += (images[i].width * images[i].height) / (tileWidth * tileHeight);
		}

		LinkedHashMap<RGB, Integer> colors = new LinkedHashMap<RGB, Integer>();
		for (int i = 0; i < images.length; i++) {
			RGB[] rgbs = images[i].getRGBs();
			for (RGB rgb : rgbs) {
				if (!colors.containsKey(rgb)) {
					colors.put(rgb, colors.size());
				}
			}
		}
		RGB[] paletteColors = colors.keySet().toArray(new RGB[0]);
		PaletteData palette = new PaletteData(paletteColors);
		ImageData targetData = new ImageData(totalTiles * tileWidth, tileHeight, 8, palette);

		int xOffset = 0;
		for (int i = 0; i < images.length; i++) {
			boolean switchTransparent = false;
			int transparentPixel = images[i].transparentPixel;
			int targetTransparentPixel = -1;
			RGB transparentColor = null;
			if (transparentPixel != -1) {
				transparentColor = images[i].palette.getRGB(transparentPixel);
				targetTransparentPixel = colors.get(transparentColor);
				if (targetData.transparentPixel == -1) {
					targetData.transparentPixel = targetTransparentPixel;
				} else if (targetTransparentPixel != targetData.transparentPixel) {
					switchTransparent = true;
				}
			}

			for (int y = 0; y < images[i].height; y++) {
				int xPadding = (y / images[i].height * images[i].width);
				for (int x = 0; x < images[i].width; x++) {
					int pixel = images[i].getPixel(x, y);
					RGB rgb = images[i].palette.getRGB(pixel);
					int targetPixel = palette.getPixel(rgb);
					if (switchTransparent && rgb == transparentColor) {
						targetData.setPixel(x + xPadding + xOffset, y % tileHeight, targetTransparentPixel);
					} else {
						targetData.setPixel(x + xPadding + xOffset, y % tileHeight, targetPixel);
					}
				}
			}
			xOffset += (images[i].width * images[i].height / tileHeight);
		}
		return targetData;
	}

	/**
	 * Convert color from integer value to rgb value,
	 * R: (intvalue >> 16) & 0xff, G: (intvalue >> 8) & 0xff,
	 * B: intvalue & 0xff.
	 * @param data
	 * @return
	 */
	public static RGB[] batchConvIntToRGB(Integer[] data) {
		RGB[] array = new RGB[data.length];
		for (int i = 0; i < data.length; i++) {
			int[] rgb = convIntToIntArray(data[i]);
			array[i] = new RGB(rgb[0], rgb[1], rgb[2]);
		}
		return array;
	}
	
	public static int[] convIntToIntArray(int color){
		int red = color >> 16 & 0xff;
		int green = color >> 8 & 0xff;
		int blue = color & 0xff;
		return new int[]{red, green, blue};
	}
	
	public static RGB convIntToRGB(int color){
		int[] rgb = convIntToIntArray(color);
		return new RGB(rgb[0], rgb[1], rgb[2]);
	}
	
	public static int convIntArrayToInt(int[] rgb){
		return (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
	}

	/**
	 * Convert color type from direct to indexed;
	 * Decrease the number of colors to at most 256
	 * colors, it will lose image information of course
	 * when the number is larger than 256.
	 * Important: the maximum length of palette of png8 or
	 * 			  gif is 256.
	 * @param srcData
	 * @return
	 */
	public static ImageData convDepth32To8(ImageData srcData) {
		if(srcData == null) return null;
		if(srcData.depth <= 8) return srcData;
		
		int width = srcData.width;
		int height = srcData.height;

		//add unique colors into an array
		LinkedHashMap<Integer, Integer> rgb = new LinkedHashMap<Integer, Integer>();
		ImageData tmp = (ImageData)srcData.clone();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int p = srcData.getPixel(x, y);
				int a = srcData.getAlpha(x, y);
				RGB c = convIntToRGB(p);
				int r = c.red;
				int g = c.green;
				int b = c.blue;
				if(a != 255){
					r = Math.round(c.red * a /(float)255);
					g = Math.round(c.green * a /(float)255);
					b = Math.round(c.blue * a /(float)255);
				}
				int[] clr = convToWebColorMode(r, g, b);
				p = convIntArrayToInt(clr);
				tmp.setPixel(x, y, p);
				tmp.setAlpha(x, y, 255);
				if (!rgb.containsKey(p)){
					int s = rgb.size();
					rgb.put(p, s);
				}
			}
		}
		List<Integer> tempTable = new ArrayList<Integer>(rgb.keySet());
		Integer[] rgbArray = tempTable.toArray(new Integer[0]);
		RGB[] temp = batchConvIntToRGB(rgbArray);
		List<RGB> colorTable = new ArrayList<RGB>();
		for(RGB c : temp){
			colorTable.add(c);
		}
		PaletteData palette = new PaletteData(colorTable.toArray(new RGB[0]));
		ImageData data = new ImageData(srcData.width, srcData.height, 8, palette);

		//copy pixel values
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				RGB c = convIntToRGB(tmp.getPixel(x, y));
				int p = palette.getPixel(c); //index
				data.setPixel(x, y, p);
				data.setAlpha(x, y, 255);
			}
		}

		return data;
	}
	
	public static int[] convToWebColorMode(int red, int green, int blue){
		if(red < 0 || red >= 256 || green < 0 || green >= 256 || blue < 0 || blue >= 256)
			return new int[]{0, 0, 0};
		if(red >= 0 && red < 26){
			red = 0;
		}else if(red >= 26 && red < 77){
			red = 51;
		}else if(red >= 77 && red < 128){
			red = 102;
		}else if(red >= 128 && red < 179){
			red = 153;
		}else if(red >= 179 && red < 230){
			red = 204;
		}else{
			red = 255;
		}
		if(green >= 0 && green < 26){
			green = 0;
		}else if(green >= 26 && green < 77){
			green = 51;
		}else if(green >= 77 && green < 128){
			green = 102;
		}else if(green >= 128 && green < 179){
			green = 153;
		}else if(green >= 179 && green < 230){
			green = 204;
		}else{
			green = 255;
		}
		if(blue >= 0 && blue < 26){
			blue = 0;
		}else if(blue >= 26 && blue < 77){
			blue = 51;
		}else if(blue >= 77 && blue < 128){
			blue = 102;
		}else if(blue >= 128 && blue < 179){
			blue = 153;
		}else if(blue >= 179 && blue < 230){
			blue = 204;
		}else{
			blue = 255;
		}
		return new int[]{red, green, blue};
	}

	public static void batchConvPNG32ToPNG8(String srcDir, String destDir) throws PNGFileException{
		File[] srcFiles = FileUtil.listFiles(srcDir, ".png");
		File destFolder = new File(destDir);
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}

		for (File file : srcFiles) {
			ImageData srcData = new ImageData(file.getAbsolutePath());
			ImageData data = convDepth32To8(srcData);
			saveImageFile(data, destDir + File.separator + file.getName());
		}

	}

	public static void mergeImagesDirectory(String srcDir, String destFile, int tileWidth, int tileHeight) throws PNGFileException{
		File[] srcFiles = FileUtil.listFiles(srcDir, ".png");
		ImageData[] images = new ImageData[srcFiles.length];
		for (int i = 0; i < images.length; i++) {
			images[i] = new ImageData(srcFiles[i].getAbsolutePath());
		}
		ImageData destImage = mergeImages(images, tileWidth, tileHeight);
		saveImageFile(destImage, destFile);
	}
	/**
	 * @deprecated
	 * Save the png format file.
	 * @param data
	 * @param filePath
	 * 				The absolute path of the saved image.
	 */
	public static void saveImageFile(ImageData data, String fileName) throws PNGFileException{
		ImageLoader loader = new ImageLoader();
		PNGFileFormat ff = new PNGFileFormat(data);
		loader.load(new ByteArrayInputStream(ff.recover()));
		loader.save(fileName, SWT.IMAGE_PNG);
	}

	/**
	 * It may lose datas parameter data is generated from a image object by
	 * Image.getImageData().
	 * 
	 * @param data
	 * @param fileName
	 */
	public static void saveImage(ImageData data, String fileName){
		if(data == null || fileName == null)
			throw new IllegalArgumentException("Invalid arguments!");
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[]{data};
		loader.save(fileName, data.type);
	}
	/**
	 * @see #loadImageData(byte[])
	 * @param fileData
	 * @return
	 */
	public static Image loadImage(byte[] fileData) {
		ImageData imageData = loadImageData(fileData);
		if (imageData != null) {
			return new Image(Display.getDefault(), imageData);
		} else {
			return null;
		}
	}
	/**
	 * Convert the specified byte array into a image.
	 * @param fileData
	 * @return
	 */
	public static ImageData loadImageData(byte[] fileData) {
		ImageLoader loader = new ImageLoader();
		loader.load(new ByteArrayInputStream(fileData));
		if (loader.data.length > 0) {
			return loader.data[0];
		} else {
			return null;
		}
	}
	
	public static boolean isTransparent(ImageData data, int pixelRow, int pixelColumn, int transparencyType){
		if(pixelRow < 0 || pixelColumn < 0 || pixelColumn >= data.width || pixelRow >= data.height) return false;
		if(transparencyType == SWT.TRANSPARENCY_ALPHA){//color type: true color
			if(data.getAlpha(pixelColumn, pixelRow) == 0){
				return true;
			}
		}else if(transparencyType == SWT.TRANSPARENCY_PIXEL){//color type: index
			if(data.getPixel(pixelColumn, pixelRow) == data.transparentPixel){
				return true;
			}
		}
		return false;
	}

	public static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y, rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}
	
	public static ImageData getCompositeImage(ImageData[] datas){
		if(datas.length == 1) return datas[0];
		ImageData src = datas[0];
		int width = src.width, height = src.height, btsPerLine = src.bytesPerLine;
		for(int i = 1; i < datas.length; i++){
			if(datas[i].width != width || datas[i].height != height || datas[i].bytesPerLine != btsPerLine)
				return null;
		}
		int bytesPerPixel = src.bytesPerLine / src.width;
		int bytesPerLine = src.width * bytesPerPixel;
		byte[] newData = new byte[src.data.length];
		ImageData newImageData = new ImageData(src.width, src.height, src.depth, src.palette, bytesPerLine, newData);
		for(int srcY = 0; srcY < src.height; srcY++){
			for(int srcX = 0; srcX < src.bytesPerLine; srcX++){
				int idx = srcY * src.height + srcX;
				float alpha = 0.5f;
					//datas[1].getAlpha(srcX / bytesPerPixel, srcY) / (float)255;
				newImageData.data[idx] = (byte)(alpha * datas[1].data[idx] + (1 - alpha) * src.data[idx]);
			}
		}
//		for(int i = 2; i < datas.length; i++){
//			bytesPerPixel = newImageData.bytesPerLine / newImageData.width;
//			bytesPerLine = newImageData.width * bytesPerPixel;
//			newData = new byte[newImageData.data.length];
//			ImageData dest = new ImageData(newImageData.width, newImageData.height, 
//					newImageData.depth, newImageData.palette, bytesPerLine, newData);
//			for(int srcY = 0; srcY < newImageData.height; srcY++){
//				for(int srcX = 0; srcX < newImageData.bytesPerLine; srcX++){
//					int idx = srcY * newImageData.height + srcX;
//					float alpha = datas[i].getAlpha(srcX / bytesPerPixel, srcY) / (float)255;
//					dest.data[idx] = (byte)(alpha * datas[i].data[idx] + (1 - alpha) * newImageData.data[idx]);
//				}
//			}
//			newImageData = dest;
//		}
		
		return newImageData;
	}

	public static ImageData convertToSWT(BufferedImage bufferedImage) {
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
			return data;
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		}
		return null;
	}

	public static ImageData rotate(ImageData srcData, int direction) {
		int width = srcData.width;
		int height = srcData.height;
		if (direction != SWT.DOWN) {
			width = srcData.height;
			height = srcData.width;
		}
		ImageData data = new ImageData(width, height, srcData.depth, srcData.palette);
		data.transparentPixel = srcData.transparentPixel;
		boolean alpha = srcData.alphaData != null;

		for (int y = 0; y < srcData.height; y++) {
			for (int x = 0; x < srcData.width; x++) {
				int destX = 0, destY = 0;
				if (direction == SWT.LEFT) {
					destX = y;
					destY = srcData.width - 1 - x;
				} else if (direction == SWT.RIGHT) {
					destX = srcData.height - 1 - y;
					destY = x;
				} else if (direction == SWT.DOWN) {
					destX = x;
					destY = srcData.height - 1 - y;
				}
				data.setPixel(destX, destY, srcData.getPixel(x, y));
				if (alpha) {
					data.setAlpha(destX, destY, srcData.getAlpha(x, y));
				}
			}
		}

		return data;
	}

	public static ImageData flip(ImageData srcData, boolean vertical) {
		ImageData data = new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette);
		data.transparentPixel = srcData.transparentPixel;
		boolean alpha = srcData.alphaData != null;

		for (int i = 0; i < srcData.height; i++) {
			for (int j = 0; j < srcData.width; j++) {
				if (vertical) {
					data.setPixel(j, i, srcData.getPixel(j, srcData.height - 1 - i));
					if (alpha) {
						data.setAlpha(j, i, srcData.getAlpha(j, srcData.height - 1 - i));
					}
				} else {
					data.setPixel(j, i, srcData.getPixel(srcData.width - 1 - j, i));
					if (alpha) {
						data.setAlpha(j, i, srcData.getAlpha(srcData.width - 1 - j, i));
					}
				}
			}
		}

		return data;
	}

	public static ImageData getTranslateImageData(ImageData srcData, byte transType) {

		ImageData transData = null;
		switch (transType) {
		case Constants.TRANS_ROT90:
			transData = ImageUtil.rotate(srcData, SWT.RIGHT);
			break;
		case Constants.TRANS_ROT180:
			transData = ImageUtil.rotate(srcData, SWT.DOWN);
			transData = ImageUtil.flip(transData, false);
			break;
		case Constants.TRANS_ROT270:
			transData = ImageUtil.rotate(srcData, SWT.LEFT);
			break;
		case Constants.TRANS_MIRROR:
			transData = ImageUtil.flip(srcData, false);
			break;
		case Constants.TRANS_MIRROR_ROT90:
			transData = ImageUtil.flip(srcData, false);
			transData = ImageUtil.rotate(transData, SWT.RIGHT);
			break;
		case Constants.TRANS_MIRROR_ROT180:
			transData = ImageUtil.flip(srcData, true);
			break;
		case Constants.TRANS_MIRROR_ROT270:
			transData = ImageUtil.flip(srcData, false);
			transData = ImageUtil.rotate(transData, SWT.LEFT);
			break;
		default:
			transData = srcData;
			break;
		}
		return transData;

	}
	/**
	 * Compare the two images' RGB values of corresponding pixels.
	 * @param data1
	 * @param data2
	 * @param tolarate
	 * 		  If TRUE, it may accept some difference between images in specified
	 * 		  extent. 
	 * @return
	 * 		  TRUE if all pixel values are identical, FALSE otherwise.
	 */
	public static boolean compareTwoImages(ImageData data1, ImageData data2, boolean tolarate)throws Exception{
		if(data1 == null || data2 == null) return false;
		if(data1.width != data2.width || data1.height != data2.height) return false;
		
//		int[] directPixels1 = null;
//		byte[] indexPixels1 = null;
		int size = data1.width * data1.height;
//		if(data1.depth == 8 || data1.depth == 4){
//			indexPixels1 = new byte[size];
//			data1.getPixels(0, 0, data1.width, indexPixels1, 0);
//		}else if(data1.depth == 24 || data1.depth == 32){
//			directPixels1 = new int[size];
//			data1.getPixels(0, 0, data1.width, directPixels1, 0);
//		}
//		int[] directPixels2 = null;
//		byte[] alphas1 = null;
//		byte[] alphas2 = null;
//		if(data1.alphaData != null){
//			alphas1 = new byte[size];
//			data1.getAlphas(0, 0, data1.width, alphas1, 0);
//		}
//		if(data2.alphaData != null){
//			alphas2 = new byte[size];
//			data2.getAlphas(0, 0, data2.width, alphas2, 0);
//		}
//		byte[] indexPixels2 = null;
//		if(data2.depth == 8 || data2.depth == 4){
//			indexPixels2 = new byte[size];
//			data2.getPixels(0, 0, data2.width, indexPixels2, 0);
//		}else if(data2.depth == 24 || data2.depth == 32){
//			directPixels2 = new int[size];
//			data2.getPixels(0, 0, data2.width, directPixels2, 0);
//		}
		

		int diffBound = size / 10;
		int diffPixel = 10;
//		int i = 0;
//		if(alphas1 != null){
//			if(alphas2 != null){
//				for(i = 0; i < alphas1.length; i++){
//					if(alphas1[i] != alphas2[i]) return false;
//				}
//			}else{
//				return false;
//			}
//		}else{
//			return false;
//		}
//		if(directPixels1 != null){
//			if(directPixels2 != null){
//				for(i = 0; i < directPixels1.length; i++){
//					RGB rgb1 = data1.palette.getRGB(directPixels1[i]);
//					RGB rgb2 = data2.palette.getRGB(directPixels2[i]);
//					if(tolarate){
//						boolean diff = !(Math.abs(rgb1.red - rgb2.red) <= diffPixel && 
//								Math.abs(rgb1.green - rgb2.green) <= diffPixel && Math.abs(rgb1.blue - rgb2.blue) <= diffPixel);
//						if(diff){
//							++count;
//							if(count > diffBound) return false;
//						}
//					}else{
//						if(!(rgb1.equals(rgb2))){
//							return false;
//						}
//					}
//				}
			int count = 0;
			int i = 0, j = 0;
			for(i = 0; i < data1.width; i++){
				for(j = 0; j < data1.height; j++){
					RGB rgb1 = data1.palette.getRGB(data1.getPixel(i, j));
					RGB rgb2 = data2.palette.getRGB(data2.getPixel(i, j));
					if(tolarate){
						boolean diff = !(Math.abs(rgb1.red - rgb2.red) <= diffPixel && 
								Math.abs(rgb1.green - rgb2.green) <= diffPixel && Math.abs(rgb1.blue - rgb2.blue) <= diffPixel);
						if(diff){
							++count;
							if(count > diffBound) return false;
						}
					}else{
						if(!(rgb1.equals(rgb2))) return false;
					}
				}
			}
//			}
//			else if(indexPixels2 != null){
//				int count = 0;
//				for(i = 0; i < directPixels1.length; i++){
//					RGB rgb1 = data1.palette.getRGB(directPixels1[i]);
//					RGB rgb2 = data2.palette.getRGB(indexPixels2[i]);
//					if(tolarate){
//						boolean diff = !(Math.abs(rgb1.red - rgb2.red) <= diffPixel && 
//								Math.abs(rgb1.green - rgb2.green) <= diffPixel && Math.abs(rgb1.blue - rgb2.blue) <= diffPixel);
//						if(diff){
//							++count;
//							if(count > diffBound) return false;
//						}
//					}else{
//						if(!(rgb1.equals(rgb2))) return false;
//					}
//				}
//			}
//			else{
//				return false;
//			}
//		}
//		else if(indexPixels1 != null){
//			if(directPixels2 != null){
//				int count = 0;
//				for(i = 0; i < indexPixels1.length; i++){
//					RGB rgb1 = data1.palette.getRGB(indexPixels1[i]);
//					RGB rgb2 = data2.palette.getRGB(directPixels2[i]);
//					if(tolarate){
//						boolean diff = !(Math.abs(rgb1.red - rgb2.red) <= diffPixel && 
//								Math.abs(rgb1.green - rgb2.green) <= diffPixel && Math.abs(rgb1.blue - rgb2.blue) <= diffPixel);
//						if(diff){
//							++count;
//							if(count > diffBound) return false;
//						}
//					}else{
//						if(!(rgb1.equals(rgb2))) return false;
//					}
//				}
//			}else if(indexPixels2 != null){
//				int count = 0;
//				for(i = 0; i < indexPixels1.length; i++){
//					try{
//					RGB rgb1 = data1.palette.getRGB(indexPixels1[i]);
//					RGB rgb2 = data2.palette.getRGB(indexPixels2[i]);
//					if(tolarate){
//						boolean diff = !(Math.abs(rgb1.red - rgb2.red) <= diffPixel && 
//								Math.abs(rgb1.green - rgb2.green) <= diffPixel && Math.abs(rgb1.blue - rgb2.blue) <= diffPixel);
//						if(diff){
//							++count;
//							if(count > diffBound) return false;
//						}
//					}else{
//						if(!(rgb1.equals(rgb2))) return false;
//					}
//					}catch(Exception e){
////						System.out.println(indexPixels1[i]);
//						throw e;
//					}
//				}
//			}else{
//				return false;
//			}
//		}
//		else{
//			return false;
//		}

		return true;
	}

	public static Point getImageSize(String filePath) {
		Point size = new Point(0, 0);
		ImageData data = new ImageData(filePath);
		if (data != null) {
			size.x = data.width;
			size.y = data.height;
		}

		return size;
	}
	/**
	 * Compute the gray values of the image.
	 * @param data
	 * @param precision
	 * @return
	 */
	public static int[] getGrayValues(ImageData data, int precision) {
		if (precision < 1) {
			precision = 1;
		}
		int n = 2 << ((2 << (precision - 1)) - 1);
		int[] gray = new int[n];
	
		int w = data.width;
		int h = data.height;
		int sqrt = (int) Math.sqrt(n);
		int cw = w / sqrt;
		int ch = h / sqrt;
		
		for (int i = 0; i < n; i++) {
			int xoff = (i % sqrt) * cw;
			int yoff = (i / sqrt) * ch;
			for (int y = 0; y < ch; y++) {
				for (int x = 0; x < cw; x++) {
					RGB color = data.palette.getRGB(data.getPixel(x + xoff, y + yoff));
					gray[i] += (int) (0.299f * color.red + 0.587f * color.green + 0.114f * color.blue);
				}
			}
		}
		return gray;
	}
	
	public static void drawImage(GC gc, Image image, int clipX, int clipY, int clipWidth, int clipHeight, int destX, int destY, byte transType) {
		try{
			ImageData imgData = sliceImage(image.getImageData(), clipX, clipY, clipWidth, clipHeight);		
			//saveImageFile(imgData, "d:/var/test.png");
			Image clipImage = new Image(Display.getDefault(), imgData);
			drawImage(gc, clipImage, destX, destY, transType);
			clipImage.dispose();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Draw the image widthout zooming.
	 * @param gc
	 * @param image
	 * @param x
	 * @param y
	 * @param transType
	 */
	public static void drawImage(GC gc, Image image, int x, int y, byte transType){
		if(!valid(image)) return;
		if (transType == Constants.TRANS_NONE) {
			gc.drawImage(image, x, y);
			return;
		}
		Rectangle rect = image.getBounds();
		int width = rect.width;
		int height = rect.height;
		drawImage(gc, image, 0, 0, rect.width, rect.height, x, y, width, height, transType);
	}
	/**
	 * Draw an region of the image to the specified position
	 * with zooming.
	 * @param gc
	 * @param image
	 * @param srcX
	 * @param srcY
	 * @param srcWidth
	 * @param srcHeight
	 * @param x X of the destination
	 * @param y Y of the destination
	 * @param destWidth
	 * @param destHeight
	 * @param transType
	 */
	public static void drawImage(GC gc, Image image, int srcX, int srcY, int srcWidth, 
			int srcHeight, int x, int y, int destWidth, int destHeight, byte transType) {
		//check parameters
		if(!valid(image) || (gc == null || gc.isDisposed())) return;
		Rectangle rect = image.getBounds();
		if(srcX + srcWidth > rect.width || srcY + srcHeight > rect.height) return;
		if(srcWidth == 0 || srcHeight == 0 || destWidth == 0 || destHeight == 0) return;
		
		if (transType == Constants.TRANS_NONE) {
			gc.drawImage(image, srcX, srcY, srcWidth, srcHeight, x, y, destWidth, destHeight);
			return;
		}
		
		Transform transform = new Transform(Display.getDefault());
		int x0 = x;
		int y0 = y;
		int width = destWidth;
		int height = destHeight;
		switch (transType) {
		case Constants.TRANS_MIRROR:
			transform.setElements(-1, 0, 0, 1, 0, 0);
			x0 = -x - width;
			break;
		case Constants.TRANS_MIRROR_ROT90:
			transform.setElements(0, -1, -1, 0, 0, 0);
			x0 = -y - width;
			y0 = -x - height;
			break;
		case Constants.TRANS_MIRROR_ROT180:
			transform.setElements(1, 0, 0, -1, 0, 0);
			y0 = -y - height;
			break;
		case Constants.TRANS_MIRROR_ROT270:
			transform.setElements(0, 1, 1, 0, 0, 0);
			x0 = y;
			y0 = x;
			break;
		case Constants.TRANS_ROT90:
			transform.setElements(0, 1, -1, 0, 0, 0);
			x0 = y;
			y0 = -x - height;
			break;
		case Constants.TRANS_ROT180:
			transform.setElements(-1, 0, 0, -1, 0, 0);
			x0 = -x - width;
			y0 = -y - height;
			break;
		case Constants.TRANS_ROT270:
			transform.setElements(0, -1, 1, 0, 0, 0);
			x0 = -y - width;
			y0 = x;
			break;
		default:
			break;
		}

		gc.setTransform(transform);
		gc.drawImage(image, srcX, srcY, srcWidth, srcHeight, x0, y0, destWidth, destHeight);

		gc.setTransform(null);
		transform.dispose();

	}
	
	public static void getBasePoint(Image image, Point p){
		ImageData data = image.getImageData();
		int i = data.height - 1;
		for(; i >= 0; i--){
			for(int j = 0; j < data.width; j++){
				if(data.getPixel(j, i) != data.transparentPixel){
					p.x = j;
					p.y = data.height - i;
					break;
				}
			}
		}
	}
	
	public static boolean valid(Image image){
		return image != null && !image.isDisposed();
	}
	
	public static int[] createColorHistogram(RGB[] rgbs){
		int[] ch = new int[3];
		for(int i = 0; i < rgbs.length; i++){
			ch[0] += rgbs[i].red;
			ch[1] += rgbs[i].green;
			ch[2] += rgbs[i].blue;
		}
		return ch;
	}
	
	/**
	 * Just estimating the similarity of the two images roughly.
	 * according to their distribution of colors.
	 * @param 
	 * @param 
	 * @param similarity: the value of it is between 0 and 255, both included.
	 * @return: The rate of unsimilarity, or the maximum value of the integer
	 * 			if the unsimilarity exceeds the threshold.
	 */
	public static int compareImageColors(RGB[] rgbs1, RGB[] rgbs2, int similarity){
		if(similarity > 255 || similarity < 0) return Integer.MAX_VALUE;
		if(rgbs1.length != rgbs2.length) return Integer.MAX_VALUE;
		int[] colorHistogram1 = createColorHistogram(rgbs1);
		int[] colorHistogram2 = createColorHistogram(rgbs2);
		int unSimi = 0;
		for(int i = 0; i < 3; i++) unSimi += Math.abs(colorHistogram1[i] - colorHistogram2[i]);
		return (255 - similarity) * rgbs1.length >= unSimi ? unSimi : Integer.MAX_VALUE;
	}
	
	/**
	 * 
	 * @param img1
	 * @param img2
	 * @return: The difference between the two sums of gray values.
	 */
	public static int compareImageGrays(RGB[] rgbs1, RGB[] rgbs2){
		int result = Integer.MAX_VALUE;
		if(rgbs1.length != rgbs2.length) return result;
		int gray1 = 0;
		int gray2 = 0;
		int len = rgbs1.length;
		for(int i = 0; i < len; i++){
			gray1 += 1 * (rgbs1[i].red / 10) + 8 * (rgbs1[i].green / 10) + (rgbs1[i].blue / 10);
			gray2 += 1 * (rgbs2[i].red / 10) + 8 * (rgbs2[i].green / 10) + (rgbs2[i].blue / 10);
		}
		result = (gray1 - gray2) / 5;
		return result * result;
	}
	
	public static RGB[] getRGBs(ImageData data){
		RGB[] rgbs = new RGB[data.width * data.height];
		for(int i = 0; i < rgbs.length; i++){
			rgbs[i] = data.palette.getRGB(data.getPixel(i % data.width, i / data.width));
		}
		return rgbs;
	}
	
	public static int getGray(int red, int green, int blue){
		return ((red * 77 + green * 151 + blue * 28) >> 8);
	}
	
	public static int getGray(RGB rgb){
		return getGray(rgb.red, rgb.green, rgb.blue);
	}
	
	public static boolean dispose(Image image){
		boolean suc = true;
		try{
			if(valid(image)){
				image.dispose();
				image = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			suc = false;
		}
		return suc;
	}
	/**
	 * Write an animated GIF file.
	 * @param data
	 * @param path
	 */
	public static boolean writeGIFImage(ImageData[] datas, String path){
		if(path == null) return false;
		if(datas == null || datas.length < 1)
			return false;
		for(ImageData d : datas){
			if(d == null)
				return false;
		}
		
		boolean succeed = true;
		
		final int w = datas[0].width;
		final int h = datas[0].height;
		for(int i = 0; i < datas.length; i++){
			if(datas[i].depth > 8){
				datas[i] = convDepth32To8(datas[i]);
			}
		}
		ImageLoader loader = new ImageLoader();
		loader.data = datas;
		loader.backgroundPixel = 0;
		loader.logicalScreenWidth = w;
		loader.logicalScreenHeight = h;
		loader.repeatCount = 0;
		try{
			loader.save(path + ".gif", SWT.IMAGE_GIF);
		}catch(SWTException e){
			e.printStackTrace();
			succeed = false;
		}
		return succeed;
	}
	/**
	 * Write the specified images to an flash movie.
	 * @param datas
	 * @param path
	 * 			  the destination of saved swf file,
	 * 			  including file name without extension.
	 * @param milliseconds
	 * 					  duration of each frame.
	 * @param delete
	 * 				whether delete the temporary image
	 * @return
	 */
	public static boolean writeFlashFile(ImageData[] datas, String path, 
			int milliseconds, String tempName, String tempPath, boolean delete){
		if(milliseconds <= 0 || tempPath == null) return false;
		File tmpP = new File(tempPath);
		if(!tmpP.exists()) tmpP.mkdirs();
		if(path == null) return false;
		if(datas == null || datas.length < 1)
			return false;
		for(ImageData d : datas){
			if(d == null)
				return false;
		}
		
		boolean succeed = true;
		final int w = datas[0].width;
		final int h = datas[0].height;
		final ImageLoader loader = new ImageLoader();
		loader.logicalScreenWidth = w;
		loader.logicalScreenHeight = h;
		ImageData[] ds = new ImageData[1];
		int uid = 1;
		int layer = 1;
		final MovieHeader header = new MovieHeader();
		header.setFrameRate((float)(1000 / milliseconds));
		header.setFrameSize(new Bounds(0, 0, w * 20, h * 20));
		final Movie movie = new Movie();
		movie.add(header);
		movie.add(new Background(WebPalette.BLACK.color()));
		final ImageFactory factory = new ImageFactory();
		try{
			for(ImageData d : datas){
				ds[0] = d;
				loader.data = ds;
				String temp = tempPath + tempName + ".png";
				loader.save(temp, SWT.IMAGE_PNG);
				File f = new File(temp);
				factory.read(f);
				ImageTag image = factory.defineImage(uid++);
				movie.add(image);
				ShapeTag shape = new ImageShape().defineShape(uid++, image);
				movie.add(shape);
				movie.add(Place2.show(shape.getIdentifier(), layer++, 0, 0));
				movie.add(ShowFrame.getInstance());
				if(delete) f.delete();
			}
			movie.encodeToFile(new File(path + ".swf"));
		}catch(Exception e){
			succeed = false;
			e.printStackTrace();
		}
		
		return succeed;
	}
	
	public static Image getMissingImage(){
		return MISSING_IMAGE;
	}
	
	public static int getFormat(String ext) {
		if(ext.equals(".jpg") || ext.equals(".jpeg"))
			return SWT.IMAGE_JPEG;
		if(ext.equals(".png"))
			return SWT.IMAGE_PNG;
		return -1;
	}
	
	public static boolean imageType(String ext){
		if(ext == null)
			return false;
		String low = ext.toLowerCase();
		if(low.equals("png") || low.equals("bmp") || low.equals("jpg") || low.equals("jpeg"))
			return true;
		return false;
	}
	
	public static String getExt(int type) {
		String rslt = "";
		switch(type){
		case SWT.IMAGE_BMP:
			rslt = ".bmp";
			break;
		case SWT.IMAGE_PNG:
			rslt = ".png";
			break;
		case SWT.IMAGE_JPEG:
			rslt = ".jpg";
			break;
		}
		return rslt;
	}
	
	
	public static void main(String[] args) {
//		ImageData test = new ImageData("d:/tile.png");
//		int[] gray = getGrayValues(test, 3);
//		for(int i : gray) {
//			System.out.println(Integer.MAX_VALUE);
//		}
		//splitImage(new ImageData("d:/var/4.png"), 16, 16);
//		ImageData data = new ImageData("d:/tile.png");
//		RGB[][] image = new RGB[data.height][];
//		for(int i = 0; i < data.height; i++){
//			image[i] = new RGB[data.width];
//		}
//		for(int i = 0; i < data.height; i++)
//			for(int j = 0; j < data.width; j++){
//				image[i][j] = data.palette.getRGB(data.getPixel(j, i));
//			}
//		initArgs(data.width, data.height);
//		int[] grays = getSignificantMap(image, 0.0f, 0.0f, 1.0f);
//		int[] pixels = new int[grays.length];
//		PaletteData p = new PaletteData(0xff0000, 0xff00, 0xff);
//		ImageData d = new ImageData(data.width, data.height, 24, p);
//		int a = 0;
//		for(int i = 0; i < grays.length; i++){
//			pixels[i] = (grays[i] << 16) + (grays[i] << 8) + grays[i];
//			d.setPixel(i % data.width, i / data.width, pixels[i]);
//			if(grays[i]>200)System.out.println(a++ + "~~"+grays[i]);
//		}
//		saveImageFile(d, "d:/test.png");
		// batchConvPNG32ToPNG8("D:\\var\\resources\\buildings",
		// "D:\\var\\resources\\buildings2");
		// mergeImagesDirectory("d:/var/cc", "d:/var/test.png", 32, 15);
		// saveImageFile(convPNG32ToPNG8(new ImageData("d:/var/1.png")),
		// "d:/var/1-1.png");
		// saveImageFile(new ImageData(16, 16, 8, new PaletteData(new RGB[]{new
		// RGB(0,0,0),new RGB(255,0,0),new RGB(0,255,0),new RGB(0,0,255)})),
		// "d:/var/test.png");
		// shrinkPNGFile("d:/var/lv.png", "d:/var/test.png");
		// saveImageFile(sliceImage(new ImageData("d:/var/1.png"), 300, 0, 30,
		// 30), "d:/var/test.png");
		// saveImageFile(createTransparentImage(20, 20), "d:/var/test.png");
		// saveImageFile(new Image(Display.getDefault(), 20, 20).getImageData(),
		// "d:/var/test.png");
	}
}
