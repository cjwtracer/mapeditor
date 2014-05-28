package tool.mapeditor.dialogs.mapProperties;

import java.util.Properties;

import tool.util.FileUtil;
import tool.util.StringUtil;

class PropHelper {
	static final String SEP = "\\|";
	
	Properties prop;
	String[] nameList;
	int[] typeList;
	
	PropHelper(String propFile){
		prop = new Properties();
		FileUtil.loadProperties(prop, propFile);
		String v = prop.getProperty("listDescript");
		nameList = v.split(SEP);
		String[] types = prop.getProperty("listInMapProp").split(SEP);
		typeList = new int[types.length];
		int i = 0;
		for(String t : types){
			if(!StringUtil.isNumeric(t))
				throw new IllegalArgumentException("invalid type");
			typeList[i++] = StringUtil.toNum(t);
		}
	}
}