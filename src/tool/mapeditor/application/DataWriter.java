package tool.mapeditor.application;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import tool.io.DataCrackException;
import tool.model.GradualChange;
import tool.model.MapLayer;
import tool.model.Project;
import tool.model.Property;
import tool.model.RegionPolygon;
import tool.model.ResourceSet;
import tool.model.Tile;
import tool.model.Unit;
import tool.model.UnitGroup;
import tool.model.WorldMap;
import tool.util.FileUtil;
import tool.util.MathUtil;

/**
 * Export datas according to requirements.
 * 
 * @author caijw
 * 
 */
public class DataWriter {
	
	public static void exportResourceDatas(ResourceSet set, MapProject pro){
		String pth = pro.getResourceDir() + set.getId() + Project.RES_EXT;
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(pth);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{if(fos != null)fos.close();}catch(IOException e){}
		}
	}

	public static void exportMapDatas(WorldMap m, MapProject project) throws DataCrackException {
		if(!m.isLoaded())
			WorldMap.load(project, m);
		String pth = project.getExportDir() + m.getExpID() + Project.MAP_EXT;
		FileOutputStream fos = null;
		FileWriter fw = null;
		try{
			fos = new FileOutputStream(pth);
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			fw = new FileWriter(project.getExportDir() + m.getExpID() + "log.txt");
			DataOutputStream dos = new DataOutputStream(gos);
			PrintWriter pw = new PrintWriter(fw);
			dos.writeInt(m.getExpID());
			pw.println("ID");
			pw.println(m.getExpID());
			dos.writeInt(m.getBackgroundID());
			pw.println("scene");
			pw.println(m.getBackgroundID());
			boolean hasMusic = false;
			for(Property p : m.getProperties()){
				if(p.getName().equals("Music")){
					hasMusic = true;
					dos.writeInt((Integer)p.getValue());//music
					pw.println("music");
					pw.println((Integer)p.getValue());
					break;
				}
			}
			if(!hasMusic){
				dos.writeInt(-1);
				pw.println("music");
				pw.println(-1);
			}
			dos.writeUTF(m.getName());
			pw.println("map name");
			pw.println(m.getName());
			boolean hasMapType = false, hasCamp = false;
			for(Property p : m.getProperties()){
				if(p.getName().equals("MapType")){
					hasMapType = true;
					dos.writeByte((Byte)p.getValue());//type
					pw.println("map type");
					pw.println((Byte)p.getValue());
				}
				if(p.getName().equals("Camp")){
					hasCamp = true;
					dos.writeByte((Byte)p.getValue());//camp
					pw.println("camp");
					pw.println((Byte)p.getValue());
				}
			}
			if(!hasMapType){
				dos.writeByte(-1);
				pw.println("map type");
				pw.println(-1);
			}
			if(!hasCamp){
				dos.writeByte(-1);
				pw.println("camp");
				pw.println(-1);
			}
			if(m.getWidth() == 0 || m.getHeight() == 0){
				System.err.println(m.getName() + m.getFileID());
			}
			dos.writeShort(m.getWidth());
			pw.println("map w");
			pw.println(m.getWidth());
			dos.writeShort(m.getHeight());
			pw.println("map h");
			pw.println(m.getHeight());
			dos.writeShort(m.getImageWidth());
			pw.println("pixel w");
			pw.println(m.getImageWidth());
			dos.writeShort(m.getImageHeight());
			pw.println("pixel h");
			pw.println(m.getImageHeight());
			dos.writeShort(m.getCellWidth());
			pw.println("block w");
			pw.println(m.getCellWidth());
			dos.writeByte(m.getTileWidth());
			pw.println("tile w");
			pw.println(m.getTileWidth());
			
			MapLayer l = m.getLayers().get(0);
			int count = 0;
			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
				int tp = l.getTile(i).getType();
				if(tp == RegionPolygon.TYPE_NONE)
					tp = 1;
				else if(tp == RegionPolygon.TYPE_SHUTTER)
					tp = 3;
				else if(tp == RegionPolygon.TYPE_LAND)
					tp = 4;
				dos.writeByte((byte)tp);
				pw.println(i + ": " + tp);
			}
//			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
//				if(l.getTile(i).getType() == RegionPolygon.TYPE_COLLISION){
//					++count;
//				}
//			}
//			pw.println("collision count");
//			dos.writeShort(count);
//			pw.println(count);
//			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
//				Tile t = l.getTile(i);
//				if(t.getType() == RegionPolygon.TYPE_COLLISION){
//					dos.writeShort(t.getJ());
//					pw.println("tile j");
//					pw.println(t.getJ());
//					dos.writeShort(t.getI());
//					pw.println("tile i");
//					pw.println(t.getI());
//				}
//			}
//			count = 0;
//			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
//				if(l.getTile(i).getType() == RegionPolygon.TYPE_SHUTTER){
//					++count;
//				}
//			}
//			pw.println("shutter count");
//			dos.writeInt(count);
//			pw.println(count);
//			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
//				Tile t = l.getTile(i);
//				if(t.getType() == RegionPolygon.TYPE_SHUTTER){
//					dos.writeShort(t.getJ());
//					pw.println("tile j");
//					pw.println(t.getJ());
//					dos.writeShort(t.getI());
//					pw.println("tile i");
//					pw.println(t.getI());
//				}
//			}
			Tile tChu = null, tHan = null, tTmp = null;
			for(int i = 0, len = l.getTilesCount(); i < len; ++i){
				Tile t = l.getTile(i);
				if(t.getType() == RegionPolygon.TYPE_LAND){
					tTmp = t;
					for(Property p : t.getProperties()){
						if(p.getName().equals("阵营")){
							if(p.getValue().equals("楚")){
								tChu = t;
							}else if(p.getValue().equals("汉")){
								tHan = t;
							}
							break;
						}
					}
				}
			}
			pw.println("land point chu");
			if(tChu != null){
				dos.writeShort((short)tChu.getJ());
				pw.println((short)tChu.getJ());
				dos.writeShort((short)tChu.getI());
				pw.println((short)tChu.getI());
			}else{
				if(tHan == null){
					if(tTmp != null){
						dos.writeShort((short)tTmp.getJ());
						pw.println((short)tTmp.getJ());
						dos.writeShort((short)tTmp.getI());
						pw.println((short)tTmp.getI());
					}else{
						dos.writeShort(-1);
						pw.println(-1);
						dos.writeShort(-1);
						pw.println(-1);
					}
				}else{
					dos.writeShort(-1);
					pw.println(-1);
					dos.writeShort(-1);
					pw.println(-1);
				}
			}
			pw.println("land point han");
			if(tHan != null){
				dos.writeShort((short)tHan.getJ());
				pw.println((short)tHan.getJ());
				dos.writeShort((short)tHan.getI());
				pw.println((short)tHan.getI());
			}else{
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
			}
			RegionPolygon rHan = null, rChu = null;
			for(RegionPolygon r : l.getRegions()){
				if(r.getType() == RegionPolygon.TYPE_SAFE){
					for(Property p : r.getProperties()){
						if(p.getName().equals("阵营")){
							if(p.getValue().equals("楚")){
								rChu = r;
							}else if(p.getValue().equals("汉")){
								rHan = r;
							}
							break;
						}
					}
				}
			}
			pw.println("safe region chu");
			if(rChu != null){
				Rectangle b = rChu.getBounds();
				dos.writeShort((short)b.x);
				pw.println(b.x);
				dos.writeShort((short)b.y);
				pw.println(b.y);
				dos.writeShort((short)(b.x + b.width - 1));
				pw.println((short)(b.x + b.width - 1));
				dos.writeShort((short)(b.y + b.height - 1));
				pw.println((short)(b.y + b.height - 1));
			}else{
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
			}
			pw.println("safe region han");
			if(rHan != null){
				Rectangle b = rHan.getBounds();
				dos.writeShort((short)b.x);
				pw.println(b.x);
				dos.writeShort((short)b.y);
				pw.println(b.y);
				dos.writeShort((short)(b.x + b.width - 1));
				pw.println((short)(b.x + b.width - 1));
				dos.writeShort((short)(b.y + b.height - 1));
				pw.println((short)(b.y + b.height - 1));
			}else{
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
				dos.writeShort(-1);
				pw.println(-1);
			}
			count = 0;
			for(RegionPolygon r : l.getRegions()){
				if(r.getType() == RegionPolygon.TYPE_TELEPORT){
					++count;
				}
			}
			dos.writeInt(count);
			pw.println("teleports count");
			pw.println(count);
			for(RegionPolygon r : l.getRegions()){
				if(r.getType() == RegionPolygon.TYPE_TELEPORT){
					Rectangle b = r.getBounds();
					dos.writeShort((short)b.x);
					pw.println("j");
					pw.println((short)b.x);
					dos.writeShort((short)b.y);
					pw.println("i");
					pw.println((short)b.y);
					dos.writeShort((short)(b.x + b.width - 1));
					pw.println("j1");
					pw.println((short)(b.x + b.width - 1));
					dos.writeShort((short)(b.y + b.height - 1));
					pw.println("i1");
					pw.println((short)(b.y + b.height - 1));
					for(Property p : r.getProperties()){
						String type = p.getType();
						Object value = p.getValue();
						if(type.equals("long")){
							dos.writeLong((Long)value);
						}if(type.equals("int")){
							dos.writeInt((Integer)value);
						}else if(type.equals("short")){
							dos.writeShort((Short)value);
						}else if(type.equals("byte")){
							dos.writeByte((Byte)value);
						}else if(type.equals("boolean")){
							dos.writeBoolean((Boolean)value);
						}else if(type.equals("String")){
							dos.writeUTF((String)value);
						}
						p.print(pw);
					}
				}
			}
			//TODO effect
//			count = 0;
//			for(Unit u : l.getUnits()){
//				if(u instanceof UnitGroup){
//					count++;
//				}
//			}
//			dos.writeInt(count);
//			pw.println("effects count");
//			pw.println(count);
//			for(Unit u : l.getUnits()){
//				if(u instanceof UnitGroup){
//					dos.writeShort((short)u.getX());
//					pw.println("ej");
//					pw.println(u.getX());
//					dos.writeShort((short)u.getY());
//					pw.println("ei");
//					pw.println(u.getY());
//				}
//			}
			
			dos.close();
			pw.flush();
			pw.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{if(fos != null)fos.close();}catch(IOException e){}
			try{if(fw != null)fw.close();}catch(IOException e){}
		}
	}
	
	static void mapping(RegionPolygon r, Vector array){
		Rectangle b = r.getBounds();
		for(int y = b.y; y < b.height; ++y){
			for(int x = b.x; x < b.width; ++x){
				int v = r.getMapping(y, x);
				if(v > 6){
					array.add(new short[]{(short)x, (short)y});
				}
			}
		}
	}
	
	static void writeMapping(RegionPolygon r, DataOutputStream dos, int mh, int mw) throws IOException{
		Rectangle b = r.getBounds();
		for(int y = b.y + b.height; y >= b.y; --y)
			for(int x = b.x; x < b.width; ++x){
				int v = r.getMapping(y, x);
				if(v > 6){
//					Point p = trans(x, y, 0, mh);
//					dos.writeShort(p.y * mw + p.x);
				}
				r.writeProperties(dos);
			}
	}
	
//	static Point trans(int x, int y, int vx, int vy){
//		Point p = new Point(x, y);
//		MathUtil.transit(p, vx, vy);
//		MathUtil.transform(p, 1, 0, 0, -1);
//		return p;
//	}
	
	static void writeAllNPC(Collection<WorldMap> maps, MapProject project) throws Exception{
		String pth = project.getExportDir() + "npc/" + "all.npcs";
		FileUtil.checkPath(project.getExportDir() + "npc/");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(pth);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(maps.size());
			for(WorldMap map : maps){
				if(!map.isLoaded())
					WorldMap.load(project, map);
				int count = 0;
				for(MapLayer l : map.getLayers()){
					for(Unit u : l.getUnits()){
						if(u.isType(Unit.MONSTER)){
							++count;
						}
					}
				}
				dos.writeInt(map.getExpID());
				dos.writeInt(count);
				for(MapLayer l : map.getLayers()){
					for(Unit u : l.getUnits()){
						if(u.isType(Unit.MONSTER)){
							writeMonsterData(u, dos);
						}
					}
				}
			}
			dos.close();
		}catch(Exception ex){
			throw ex;
		}finally{
			try{if(fos != null)fos.close();}catch(IOException e){}
		}
	}
	
	static void writeNPC(WorldMap map, MapProject project) throws DataCrackException{
		if(!map.isLoaded())
			WorldMap.load(project, map);
		String pth = project.getExportDir() + "npc/" + map.getExpID() + ".npcs";
		FileUtil.checkPath(project.getExportDir() + "npc/");
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(pth);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeInt(map.getExpID());
			int count = 0;
			for(MapLayer l : map.getLayers()){
				for(Unit u : l.getUnits()){
					if(u.isType(Unit.MONSTER)){
						++count;
					}
				}
			}
			dos.writeInt(count);
			for(MapLayer l : map.getLayers()){
				for(Unit u : l.getUnits()){
					if(u.isType(Unit.MONSTER)){
						writeMonsterData(u, dos);
					}
				}
			}
			dos.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{if(fos != null)fos.close();}catch(IOException e){}
		}
	}
	
	static void writeMonsterData(Unit u, DataOutputStream dos) throws IOException{
		List<Property> props = u.getProperties();
		for(Property p : props){
			String s = p.getName();
			Object v = p.getValue();
			if(s.equals("怪物ID")){
				dos.writeInt((Integer)v);
				break;
			}
		}
		dos.writeShort(u.getJ());
		dos.writeShort(u.getI());
	}

}
