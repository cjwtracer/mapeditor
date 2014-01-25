package tool.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

public class PNGFileFormat {
	
	public static final int MOD_ORIGINAL = 1;
	public static final int MOD_SHRINKED = 2;

	static final byte SIGNATURE[] = {(byte) '\211', (byte) 'P', (byte) 'N', (byte) 'G', (byte) '\r', (byte) '\n', (byte) '\032', (byte) '\n'};
	static final byte TAG_IHDR[] = {(byte) 'I', (byte) 'H', (byte) 'D', (byte) 'R'};
	static final byte TAG_PLTE[] = {(byte) 'P', (byte) 'L', (byte) 'T', (byte) 'E'};
	static final byte TAG_TRNS[] = {(byte) 't', (byte) 'R', (byte) 'N', (byte) 'S'};
	static final byte TAG_IDAT[] = {(byte) 'I', (byte) 'D', (byte) 'A', (byte) 'T'};
	static final byte TAG_IEND[] = {(byte) 'I', (byte) 'E', (byte) 'N', (byte) 'D', (byte) 0xAE, (byte) 0x42, (byte) 0x60, (byte) 0x82};
	
	static final int[] CRC_TABLE;
	static {
		CRC_TABLE = new int[256];
		for (int i = 0; i < 256; i++) {
			CRC_TABLE[i] = i;
			for (int j = 0; j < 8; j++) {
				if ((CRC_TABLE[i] & 0x1) == 0) {
					CRC_TABLE[i] = (CRC_TABLE[i] >> 1) & 0x7FFFFFFF;
				} else {
					CRC_TABLE[i] = 0xEDB88320 ^ ((CRC_TABLE[i] >> 1) & 0x7FFFFFFF);
				}
			}
		}	
	}
	
	private byte[] ihdrData;
	private byte[] plteData;
	private byte[] trnsData;
	private byte[] idatData;
	/**
	 * Read from original file.
	 * @param file
	 * @param mod
	 * @throws PNGFileException
	 */
	public PNGFileFormat(File file, int mod) throws PNGFileException {
		
		InputStream is = null;
		try {
			if (mod == MOD_ORIGINAL) {
				readFromOriginal(file);
			} else if (mod == MOD_SHRINKED) {
				is = new FileInputStream(file);
				readFromShrinked(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(is != null) try{is.close();}catch(IOException e){}
		}
	}
	
	public PNGFileFormat(ImageData imageData) throws PNGFileException{
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[]{imageData};
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		loader.save(bos, SWT.IMAGE_PNG);
		
		byte[] data = bos.toByteArray();
		RandomAccessBufferedStream bs = new RandomAccessBufferedStream(data);
		readFromOriginal(bs);
	}
	/**
	 * Read from image.
	 * @param image
	 * @throws PNGFileException
	 */
	public PNGFileFormat(Image image) throws PNGFileException {
		this(image.getImageData());
	}
	
	public PNGFileFormat(byte[] shrinkedData) throws PNGFileException {
		ByteArrayInputStream bis = new ByteArrayInputStream(shrinkedData);
		readFromShrinked(bis);
	}
	
	private void readFromShrinked(InputStream is) throws PNGFileException {
	
		try {
			DataInputStream dis = new DataInputStream(is);
			
			int dataLen = 0;
			dataLen = dis.readInt();
			ihdrData = new byte[dataLen];
			dis.readFully(ihdrData);
			
			dataLen = dis.readInt();
			plteData = new byte[dataLen];
			dis.readFully(plteData);
			
			dataLen = dis.readInt();
			trnsData = new byte[dataLen];
			dis.readFully(trnsData);

			dataLen = dis.readInt();
			idatData = new byte[dataLen];
			dis.readFully(idatData);
			
			dis.close();
		} catch (IOException e) {
			throw new PNGFileException("This file is not a valid shrinked PNG file.");
		} 
	}
	/**
	 * If the source comes from the image object, it will filter the
	 * empty byte in chunk tRNS and chunk PLTE.
	 * Caution: It is imposible that there are multiple IDAT chunks int
	 * the image object, check {@link #readFromOriginal(File)}.
	 * @param bs
	 * @throws PNGFileException
	 */
	private void readFromOriginal(RandomAccessBufferedStream bs) throws PNGFileException {
		try {

			byte[] buf = new byte[8];
			bs.read(buf);
			if (!compare(buf, 0, 8, SIGNATURE, 0, 8)) {
				throw new PNGFileException("The file is not a valid png file.");
			}

			int dataLen = 0;
			if (seek(TAG_IHDR, bs)) {
				dataLen = bs.readInt();
				ihdrData = new byte[dataLen];
				bs.skipBytes(TAG_IHDR.length);
				bs.readFully(ihdrData);
			} else {
				ihdrData = new byte[0];
			}

			if (seek(TAG_PLTE, bs)) {
				dataLen = bs.readInt();
				plteData = new byte[dataLen];
				bs.skipBytes(TAG_PLTE.length);
				bs.readFully(plteData);
			} else {
				plteData = new byte[0];
			}

			if (seek(TAG_TRNS, bs)) {
				dataLen = bs.readInt();
				trnsData = new byte[dataLen];
				bs.skipBytes(TAG_TRNS.length);
				bs.readFully(trnsData);
			} else {
				trnsData = new byte[0];
			}

			if (seek(TAG_IDAT, bs)) {
				dataLen = bs.readInt();
				idatData = new byte[dataLen];
				bs.skipBytes(TAG_IDAT.length);
				bs.readFully(idatData);
			} else {
				idatData = new byte[0];
			}
			
//			filterEmpty();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Fileter out the empty bytes in chunk PLTE and chunk tRNS.
	 * Caution: Usually, the default transparent color is white
	 * (i.e. ff ff ff) and is restored at last.
	 * Only for png8, and waiting for optimizing.
	 */
	private void filterEmpty(){
//		recoverTRNSChunk();
		
		int cursor = trnsData.length;
		if(cursor <= 1) return;
		for(int i = cursor - 1; i >= 0; i--){
			if(trnsData[i] != 0 || (i > 0 && trnsData[i] == 0 && trnsData[i - 1] != 0)) break;
			cursor--;
		}
		byte[] tmp = new byte[cursor];
		System.arraycopy(trnsData, 0, tmp, 0, cursor);
		trnsData = tmp;
		
		cursor = plteData.length;
		for(int i = cursor - 1; i >= 0; i -= 3){
			if(!(plteData[i] == 0 && plteData[i - 1] == 0 && plteData[i - 2] == 0) || 
					(i > 2 && (plteData[i] == 0 && plteData[i - 1] == 0 && plteData[i - 2] == 0) &&
					!(plteData[i - 3] == 0 && plteData[i - 4] == 0 && plteData[i - 5] == 0)))
				break;
			cursor -= 3;
		}
		tmp = new byte[cursor];
		System.arraycopy(plteData, 0, tmp, 0, tmp.length);
		plteData = tmp;
		if(trnsData.length * 3 < plteData.length){
			byte[] tmp1 = new byte[plteData.length / 3];
			System.arraycopy(trnsData, 0, tmp1, 0, trnsData.length);
			for(int i = trnsData.length; i < tmp1.length; i++){
				tmp1[i] = trnsData[trnsData.length - 1];
			}
			trnsData = tmp1;
		}
		
	}
	/**
	 * Only for png8.
	 */
	private void recoverTRNSChunk(){
		if(trnsData.length != 0) return;
		int len = plteData.length, initLen = len;
		if(len == 0) return;
		for(int i = len - 1; i >= 0; i -= 3){
			if(!(plteData[i] == 0 && plteData[i - 1] == 0 && plteData[i - 2] == 0)) break;
			len -= 3;
		}
		if(len == 0) return;
		if(len < initLen){
			plteData[len + 2] = -1;
			plteData[len + 1] = -1;
			plteData[len] = -1;
		}
		int cursor = len / 3;
		int length = cursor + 1;
		if(len == initLen) length = cursor;
		trnsData = new byte[length];
		if(len == initLen){
			trnsData[length - 1] = -1;
		}else{
			trnsData[length - 1] = 0;
		}
		for(int i = length - 2; i >= 0; i--){
			trnsData[i] = -1;
		}
	}
	/**
	 * Read the required chunks from the original png image file 
	 * and filter out the irrelevant information.
	 * 
	 * @param originfile
	 * @throws PNGFileException
	 */
	private void readFromOriginal(File originfile) throws PNGFileException {
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(originfile, "r");
			
			byte[] buf = new byte[8];
			file.read(buf);
			if (!compare(buf, 0, 8, SIGNATURE, 0, 8)) {
				throw new PNGFileException("The file is not a valid png file.");
			}
			
			int dataLen = 0;
			if (seek(TAG_IHDR, file)) {
				dataLen = file.readInt();
				ihdrData = new byte[dataLen];
				file.skipBytes(TAG_IHDR.length);
				file.readFully(ihdrData);
			} else {
				ihdrData = new byte[0];
			}

			if (seek(TAG_PLTE, file)) {
				dataLen = file.readInt();
				plteData = new byte[dataLen];
				file.skipBytes(TAG_PLTE.length);
				file.readFully(plteData);
			} else {
				plteData = new byte[0];
			}

			if (seek(TAG_TRNS, file)) {
				dataLen = file.readInt();
				trnsData = new byte[dataLen];
				file.skipBytes(TAG_TRNS.length);
				file.readFully(trnsData);
			} else {
				trnsData = new byte[0];
			}
			/*
			 * there may be multiple IDAT chunks in the source image file,
			 * but if it has been read as an image object by the SWT, there
			 * is only one IDAT chunk
			 */
			if (seek(TAG_IDAT, file)) {
				dataLen = file.readInt();
				idatData = new byte[dataLen];
				file.skipBytes(TAG_IDAT.length);
				file.readFully(idatData);
			} else {
				idatData = new byte[0];
			}
			while(seek(TAG_IDAT, file)){
				dataLen = file.readInt();
				byte[] tmpData = idatData;
				idatData = new byte[dataLen + tmpData.length];
				file.skipBytes(TAG_IDAT.length);
				file.readFully(idatData, tmpData.length, dataLen);
				System.arraycopy(tmpData, 0, idatData, 0, tmpData.length);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (file != null) try{file.close();}catch(IOException e){}
		}
	}
	/**
	 * Search data chunk.
	 * @param tag
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	private static boolean seek(byte[] tag, RandomAccessBufferedStream bs) throws IOException {
		
		int lastPosition = bs.mark();
		int c;
		byte[] buf = new byte[tag.length - 1];
		while ((c = bs.read()) != -1) {
			if (c == tag[0]) {
				int n = bs.read(buf);
				if (n == buf.length && compare(buf, 0, buf.length, tag, 1, tag.length - 1)) {
					bs.seek(bs.mark() - tag.length - 4);
					return true;
				}
				bs.seek(bs.mark() - n);
			}
		}
		bs.seek(lastPosition);
		return false;
	}
	/**
	 * Search the specified tag.
	 * @param tag
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private static boolean seek(byte[] tag, RandomAccessFile is) throws IOException {
		long lastPosition = is.getFilePointer();
		int c;
		byte[] buf = new byte[tag.length - 1];
		while ((c = is.read()) != -1) {
			if (c == tag[0]) {
				int n = is.read(buf);
				if (n == buf.length && compare(buf, 0, buf.length, tag, 1, tag.length - 1)) {
					is.seek(is.getFilePointer() - tag.length - 4);
					return true;
				}
				is.seek(is.getFilePointer() - n);
			}
		}
		is.seek(lastPosition);
		return false;
	}

	private static boolean compare(byte[] buf1, int offset1, int length1, byte[] buf2, int offset2, int length2) {
		if (length1 != length2) {
			return false;
		}

		for (int i = 0; i < length1; i++) {
			if (buf1[offset1 + i] != buf2[offset2 + i]) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Write the image datas into a byte array.
	 * @return
	 */
	public byte[] shrink() {
		byte[] data = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);

			dos.writeInt(ihdrData.length);
			dos.write(ihdrData);

			dos.writeInt(plteData.length);
			dos.write(plteData);

			dos.writeInt(trnsData.length);
			dos.write(trnsData);

			dos.writeInt(idatData.length);
			dos.write(idatData);

			dos.flush();
			dos.close();

			data = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private int computeCRC(byte[] tag, byte[] data) {
		int crc = 0xFFFFFFFF;
		byte[] buf = new byte[tag.length + data.length];
		System.arraycopy(tag, 0, buf, 0, tag.length);
		System.arraycopy(data, 0, buf, tag.length, data.length);

		for (int i = 0; i < buf.length; i++) {
			int index = (crc ^ buf[i]) & 0xFF;
			crc = CRC_TABLE[index] ^ ((crc >> 8) & 0x00FFFFFF);
		}
		return ~crc;
	}
	
	public byte[] recover() {
		byte[] data = null;
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
				
			dos.write(SIGNATURE);
			
			dos.writeInt(ihdrData.length);
			dos.write(TAG_IHDR);
			dos.write(ihdrData);
			dos.writeInt(computeCRC(TAG_IHDR, ihdrData));

			if (plteData.length > 0) {
				dos.writeInt(plteData.length);
				dos.write(TAG_PLTE);
				dos.write(plteData);
				dos.writeInt(computeCRC(TAG_PLTE, plteData));
			}

			if (trnsData.length > 0) {
				dos.writeInt(trnsData.length);
				dos.write(TAG_TRNS);
				dos.write(trnsData);
				dos.writeInt(computeCRC(TAG_TRNS, trnsData));
			}

			dos.writeInt(idatData.length);
			dos.write(TAG_IDAT);
			dos.write(idatData);
			dos.writeInt(computeCRC(TAG_IDAT, idatData));
			
			dos.writeInt(0);
			dos.write(TAG_IEND);
			
			dos.flush();
			dos.close();
			
			data = bos.toByteArray();
		} catch(IOException e){
			e.printStackTrace();
		} 
		return data;
	}

}
