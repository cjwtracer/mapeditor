package tool.util;

import org.eclipse.swt.graphics.Point;

public class MathUtil {
	
	public static void transMatrix(Object[][] src, Object[][] dest, int trans){
		int i = 0, j = 0;
		switch(trans){
		case Constants.TRANS_MIRROR_ROT180:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - i - 1][j];
			break;
		case Constants.TRANS_MIRROR:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[i][src[0].length - j - 1];
			break;
		case Constants.TRANS_ROT180:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - i - 1][src[0].length - j - 1];
			break;
		case Constants.TRANS_MIRROR_ROT270:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - j - 1][src[0].length - i - 1];
			break;
		case Constants.TRANS_ROT90:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[j][src[0].length - i - 1];
			break;
		case Constants.TRANS_ROT270:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - j - 1][i];
			break;
		case Constants.TRANS_MIRROR_ROT90:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[j][i];
			break;
		}
	}
	
	public static void transMatrix(boolean[][] src, boolean[][]dest, int trans){
		int i = 0, j = 0;
		switch(trans){
		case Constants.TRANS_MIRROR_ROT180:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - i - 1][j];
			break;
		case Constants.TRANS_MIRROR:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[i][src[0].length - j - 1];
			break;
		case Constants.TRANS_ROT180:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - i - 1][src[0].length - j - 1];
			break;
		case Constants.TRANS_MIRROR_ROT270:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - j - 1][src[0].length - i - 1];
			break;
		case Constants.TRANS_ROT90:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[j][src[0].length - i - 1];
			break;
		case Constants.TRANS_ROT270:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[src.length - j - 1][i];
			break;
		case Constants.TRANS_MIRROR_ROT90:
			for(i = 0; i < src.length; i++)
				for(j = 0; j < src[0].length; j++)
					dest[i][j] = src[j][i];
			break;
		}
	}

	public static double getAngle(int[] p, int[] p0) {
		double angle = 0;
		if (p[0] >= p0[0]) {
			if (p[1] <= p0[1])
				angle = Math.atan((double) (p[0] - p0[0]) / (double) (p0[1] - p[1]));
			else
				angle = 3.1415926 - Math.atan((double) (p[0] - p0[0]) / (double) (p[1] - p0[1]));
		} else {
			if (p[1] <= p0[1])
				angle = 3.1415926 * -Math.atan((double) (p0[0] - p[0]) / (double) (p0[1] - p[1]));
			else
				angle = 3.1415926 + Math.atan((double) (p0[0] - p[0]) / (double) (p[1] - p0[1]));
		}
		return angle;
	}
	
	public static void transit(Point p, int vx, int vy){
		p.x += vx;
		p.y += vy;
	}
	
	public static void transform(Point p, double x11, double x12, double x21, double x22){
		if(p == null)
			throw new IllegalArgumentException("Point can't be null!");
		double det = x11 * x22 - x12 * x21;
		if(det == 0)
			throw new IllegalArgumentException("Invalid matrix!");
		double y11 = x22 / det;
		double y12 = - x12 / det;
		double y21 = - x21 / det;
		double y22 = x11 / det;
		p.x = (int)(p.x * y11 + p.y * y12);
		p.y = (int)(p.x * y21 + p.y * y22);
	}
	
	public static void main(String[] args){
	}
}
