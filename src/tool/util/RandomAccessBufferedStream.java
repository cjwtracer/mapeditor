package tool.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RandomAccessBufferedStream extends ByteArrayInputStream {

	public RandomAccessBufferedStream(byte[] buf) {
		super(buf);
	}

	public void seek(int n) {
		if (n >= count) {
			n = count - 1;
		}
		if (n < 0) {
			n = 0;
		}
		pos = n;
	}
	
	public long skipBytes(int n) {
		return skip(n);
	}
	
	public int mark() {
		return pos;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public int readFully(byte[] b) {
		return read(b, 0, b.length);
	}

	public int readInt() {
		byte[] buf = new byte[4];
		read(buf, 0, 4);
		return ByteUtil.bytesToInt(buf);
	}
	
	public short readShort() {
		byte[] buf = new byte[2];
		read(buf, 0, 2);
		return ByteUtil.bytesToShort(buf);
	}
	

}
