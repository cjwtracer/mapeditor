package tool.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	private static SimpleDateFormat sdf = new SimpleDateFormat("[yy-MM-dd HH:mm:ss] ");
	
	public static void v(String message) {
		System.out.println(sdf.format(new Date()) + message);
	}

	public static void v(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));

		System.out.println(sdf.format(new Date()) + "程序发生异常错误");
		System.out.println(sw.toString());
	}
	/**
	 * Save the exception message.
	 * @param e
	 */
	public static void logException(Throwable e){
		StringWriter sw = new StringWriter();
		sw.write("抱歉！ 程序运行时发生以下异常:\r\n");
		e.printStackTrace(new PrintWriter(sw));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String crashFile = FileUtil.getCurrentDirectory() + "crash" + sdf.format(new Date()) + ".txt";
		FileUtil.saveFile(crashFile, sw.toString().getBytes());
		
//		Runtime rt = Runtime.getRuntime();
//		try {
//			rt.exec("notepad \"" + crashFile.substring(1) + "\"");
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}
}
