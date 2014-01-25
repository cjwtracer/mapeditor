package tool.util;

public class ByteUtil {

	public static byte[] intToByte(int i) {
		byte[] b = new byte[4];
		b[3] = (byte) (0xff & i);
		b[2] = (byte) ((0xff00 & i) >> 8);
		b[1] = (byte) ((0xff0000 & i) >> 16);
		b[0] = (byte) ((0xff000000 & i) >> 24);
		return b;
	}

	public static int bytesToInt(byte[] bytes) {
		int num = bytes[3] & 0xFF;
		num |= ((bytes[2] << 8) & 0xFF00);
		num |= ((bytes[1] << 16) & 0xFF0000);
		num |= ((bytes[0] << 24) & 0xFF000000);
		return num;
	}
	
	public static short bytesToShort(byte[] bytes) {
		short num = (short) (bytes[1] & 0xFF);
		num |= ((bytes[0] << 8) & 0xFF00);
		return num;
	}
	
	public static int r2(int n) {
		return n == 0 ? 1 : (2 << (n - 1));
	}
	
	public static void main(String[] args) {
		byte[] b = intToByte(10000000);
		System.out.println(bytesToInt(b));
	}

}
