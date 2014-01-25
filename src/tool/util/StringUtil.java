package tool.util;

public class StringUtil {
	
	public static int parseToNumeric(String s){
		int value = Integer.MAX_VALUE;
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

}
