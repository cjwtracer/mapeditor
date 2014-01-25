package tool.mapeditor.undo;

import java.util.LinkedHashMap;

public class UndoHandler {
	
	LinkedHashMap<Integer, Content> stack;
	UndoDetail detail;
	
	public static UndoHandler getHandler(){
		return null;
	}
	
	public UndoHandler(UndoDetail detail){
		if(detail == null)
			throw new IllegalArgumentException("Details of undo can't be null!");
		this.detail = detail;
	}
	
	public synchronized void undo(){
		
	}
	
	public synchronized void redo(){
		
	}
	
	public synchronized void clearOperationStack(){
		
	}
	
	public synchronized void push(int key, Content content){
		
	}

}
