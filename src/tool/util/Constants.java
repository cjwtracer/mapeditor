package tool.util;

public class Constants {
	
	public static final String APP_NAME = "GameEditor";
	public static final String COPYRIGHT = "Copyright All Reserved.";
	
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 2;
//	public static final int VERSION_FILESYS = 1;
	public static final int BUILD_ID = 1203;
	
	public static final byte VIEW_RECTANGLE = 0x1;
	public static final byte VIEW_ISOMETRIC = 0x2;
	
	public static final byte DATA_GROUND = 1;// layer background
	public static final byte DATA_BUILDING = 2;// layer building
	public static final byte DATA_ANIMATION = 3; // 
	public static final byte DATA_NOCOLLISION_BUILDING = 4;// layer building of no collision
	
	/**
	 * clockwise rotation;
	 * 8 orientations;
	 */
	public final static byte TRANS_MIRROR = 2; 
	public final static byte TRANS_NONE = 0; 
	public final static byte TRANS_ROT90 = 5; 
	public final static byte TRANS_ROT180 = 3; 
	public final static byte TRANS_ROT270 = 6; 
	public final static byte TRANS_MIRROR_ROT90 = 7; 
	public final static byte TRANS_MIRROR_ROT180 = 1; 
	public final static byte TRANS_MIRROR_ROT270 = 4; 
	/**
	 * easy for computing
	 */
	public final static byte NEW_TRANS_MIRROR = 4; //horizontal-mirror
	public final static byte NEW_TRANS_NONE = 0; 
	public final static byte NEW_TRANS_ROT90 = 1; 
	public final static byte NEW_TRANS_ROT180 = 2; 
	public final static byte NEW_TRANS_ROT270 = 3; 
	public final static byte NEW_TRANS_MIRROR_ROT90 = 5; 
	public final static byte NEW_TRANS_MIRROR_ROT180 = 6; 
	public final static byte NEW_TRANS_MIRROR_ROT270 = 7; 
	
	public static final byte CONTROL_CONTAINER = 1;
	public static final byte CONTROL_TEXT = 2;
	public static final byte CONTROL_ITEM = 3;
	/**
	 * map expansion or shrink orientations
	 */
	public final static byte ORIENTATION_NORTH = 1;
	public final static byte ORIENTATION_SOUTH = 2;
	public final static byte ORIENTATION_WEST = 3;
	public final static byte ORIENTATION_EAST = 4;
	
	public final static float INTERSECT_FACTOR = 0.2f;
	public static final long TIME_GAP = 20;
	public static final String ENCODING = "UTF-8";
	public static final String SEP = "\\|";
	
	public static byte f2n(byte f){
		byte n = 0;
		switch(f){
		case TRANS_MIRROR:
			n = NEW_TRANS_MIRROR;
			break;
		case TRANS_NONE:
			n = NEW_TRANS_NONE;
			break;
		case TRANS_ROT90:
			n = NEW_TRANS_ROT90;
			break;
		case TRANS_ROT180:
			n = NEW_TRANS_ROT180;
			break;
		case TRANS_ROT270:
			n = NEW_TRANS_ROT270;
			break;
		case TRANS_MIRROR_ROT90:
			n = NEW_TRANS_MIRROR_ROT90;
			break;
		case TRANS_MIRROR_ROT180:
			n = NEW_TRANS_MIRROR_ROT180;
			break;
		case TRANS_MIRROR_ROT270:
			n = NEW_TRANS_MIRROR_ROT270;
			break;
		}
		return n;
	}
	
	public static byte n2f(byte n){
		byte f = 0;
		switch(n){
		case NEW_TRANS_MIRROR:
			f = TRANS_MIRROR;
			break;
		case NEW_TRANS_NONE:
			f = TRANS_NONE;
			break;
		case NEW_TRANS_ROT90:
			f = TRANS_ROT90;
			break;
		case NEW_TRANS_ROT180:
			f = TRANS_ROT180;
			break;
		case NEW_TRANS_ROT270:
			f = TRANS_ROT270;
			break;
		case NEW_TRANS_MIRROR_ROT90:
			f = TRANS_MIRROR_ROT90;
			break;
		case NEW_TRANS_MIRROR_ROT180:
			f = TRANS_MIRROR_ROT180;
			break;
		case NEW_TRANS_MIRROR_ROT270:
			f = TRANS_MIRROR_ROT270;
			break;
		}
		return f;
	}
	
	public static byte n2f(int n){
		if(n >= 128) return -1;
		return n2f((byte)n);
	}
	
}
