package tool.util;

public class StringUtil {
	
	public static int toNum(String s){
		int value = -1;
		try{
			value = Integer.valueOf(s);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return value;
	}
	
	public static boolean isNumeric(String s){
		boolean b = true;
		try{
			Integer.parseInt(s);
		}catch(NumberFormatException e){
			b = false;
		}
		return b;
	}
	
	public static boolean isNumeric(String s, int radix){
		boolean b = true;
		try{
			Integer.parseInt(s, radix);
		}catch(NumberFormatException e){
			b = false;
		}
		return b;
	}

	public static boolean valid(String s) {
		return s != null && !s.isEmpty();
	}

}
