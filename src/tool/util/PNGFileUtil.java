package tool.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.swt.graphics.ImageData;

public class PNGFileUtil {

	public static void shrinkFiles(File[] files, String destFile) {
		FileOutputStream fos = null;
		try {			
			fos = new FileOutputStream(destFile);
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(gos);			
		
			dos.writeInt(files.length);
			
			for (File file : files) {
				PNGFileFormat ff = new PNGFileFormat(file, PNGFileFormat.MOD_ORIGINAL);
				byte[] data = ff.shrink();
				dos.writeInt(data.length);
				dos.write(data);
			}

			gos.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				try { fos.close(); } catch (IOException e) { }
			}
		}
	}
	
	public static ImageData[] recoverFiles(String srcFile) {
		FileInputStream fis = null;
		ImageData[] images = null;
		try {
			fis = new FileInputStream(srcFile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			DataInputStream dis = new DataInputStream(gis);
			
			int size = dis.readInt();
			images = new ImageData[size];
			
			for (int i = 0; i < size; i++) {
				int len = dis.readInt();
				byte[] buf = new byte[len];
				dis.readFully(buf);

				PNGFileFormat ff = new PNGFileFormat(buf);
				byte[] data = ff.recover();

				images[i] = ImageUtil.loadImageData(data);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try { fis.close(); } catch (IOException e) { }
			}
		}
		return images;
	}
	
	public static void getPng24(String fname, DataInputStream in) {
		if (in == null)
			return;
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					fname));
			// 文件头
			// IHDR块
			in.skip(16);
			int w = in.readInt();
			int h = in.readInt();
			int ss = in.readByte();// 色深
			int af = in.readByte();
			int ys = in.readByte();
			int lb = in.readByte();
			int sm = in.readByte();
			out.writeInt(w);
			out.writeInt(h);
			out.writeByte(ss);
			out.writeByte(af);
			out.writeByte(ys);
			out.writeByte(lb);
			out.writeByte(sm);
			int crc = in.readInt();
			out.writeInt(crc);

			int len = in.readInt();
			int type = in.readInt();
			boolean finished = false;
			int dataPLTE[] = null, datatRNS[] = null, dataIDAT[] = null;
			Vector dataV = new Vector();
			int crcPLTE = 0, crcRNS = 0, crcIDAT = 0;
			while (!finished) {
				if (type == 0x504C5445) { // PLTE块：
					dataPLTE = new int[len];
					for (int i = 0; i < len; i++)
						dataPLTE[i] = in.readByte();
					crcPLTE = in.readInt();
				} else if (type == 0x74524e53) { // 透明：
					datatRNS = new int[len];
					for (int i = 0; i < len; i++)
						datatRNS[i] = in.readByte();
					crcRNS = in.readInt();
				} else if (type == 0x49444154) { // IDAT块：
					dataIDAT = new int[len];
					for (int i = 0; i < len; i++)
						dataIDAT[i] = in.readByte();
					crcIDAT = in.readInt();
					dataV.addElement(new Object[] { dataIDAT,
							new Integer(crcIDAT) });
				} else
					in.skip(len + 4);
				try {
					len = in.readInt();
					type = in.readInt();
					if (len == 0 && type == 0x49454e44)
						finished = true;
				} catch (Exception e) {
					finished = true;
					System.out.println("end");
					e.printStackTrace();
					break;
				}
			}
			// PLTE块：
			// System.out.println("dataPLTE.length="+dataPLTE.length);
			if (dataPLTE != null) {
				out.writeInt(dataPLTE.length);
				for (int j = 0; j < dataPLTE.length; j++) {
					try {
						out.writeByte(dataPLTE[j]);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				out.writeInt(crcPLTE);
			}
			// 透明长度=PLTE块长度/3,0xff个数=透明长度-1，00个数=1
			if (datatRNS != null) {
				out.writeBoolean(true);
				out.writeInt(datatRNS.length);
				// ///////////////////////////////////修正部分
				for (int i = 0; i < datatRNS.length; i++)
					out.writeByte(datatRNS[i]);
				// /////////////////////////////////////
				out.writeInt(crcRNS);
			} else
				out.writeBoolean(false);
			// IDAT块：
			out.writeByte(dataV.size());
			for (int i = 0; i < dataV.size(); i++) {
				Object ob[] = (Object[]) dataV.elementAt(i);
				dataIDAT = (int[]) ob[0];
				crcIDAT = ((Integer) ob[1]).intValue();
				out.writeInt(dataIDAT.length);
				for (int j = 0; j < dataIDAT.length; j++) {
					try {
						out.writeByte(dataIDAT[j]);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				out.writeInt(crcIDAT);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {

	}

}
