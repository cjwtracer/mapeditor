package tool.model;

import java.io.FileNotFoundException;

import tool.io.ProjectReader;
import tool.io.ProjectWriter;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		Project pro;
		try {
			pro = ProjectReader.loadProject("E:\\tmp\\wk\\MapEditor\\MapEditor\\.pro");
			int removeid = -1;
			for(Integer k : pro.getMapGroup().keySet()){
				WorldMap m = pro.getMapGroup().get(k);
				if(m.getFileID() != k){
					removeid = k;
				}
			}
			if(removeid >= 0){
				pro.getMapGroup().remove(removeid);
			}
			ProjectWriter.writeProject("E:\\tmp\\wk\\MapEditor\\MapEditor\\.pro", pro);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
