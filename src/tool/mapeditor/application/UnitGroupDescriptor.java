package tool.mapeditor.application;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.newdawn.slick.Graphics;

import tool.mapeditor.application.MapDescriptor.LayerDescriptor;
import tool.mapeditor.application.MapDescriptorUtil.OPERS;
import tool.model.Unit;
import tool.model.UnitGroup;
import tool.util.GLUtil;

public class UnitGroupDescriptor extends UnitDescriptor {
	UnitGroup unitGroup;
	List<UnitDescriptor> cells = new ArrayList<UnitDescriptor>();

	public UnitGroupDescriptor(UnitGroup unitGroup, LayerDescriptor layer) {
		super(unitGroup, layer);
		this.unitGroup = unitGroup;
		for(Unit u : unitGroup.getComponents()){
			cells.add(new UnitDescriptor(u, layer));
		}
	}
	
	public void paint(Graphics graphics, int offx, int offy, int width, int height, float trans){
		int x = offx + unitGroup.getX(), y = offy + unitGroup.getY();
		for(UnitDescriptor ud : cells){
			Rectangle b = ud.getBounds();
			Unit u = ud.unit;
			GLUtil.drawImage(ud.texture, x + u.getX(), y + u.getY(), b.width, b.height, u.getTrans(), u.getScale(), u.getAlphaF());
		}
		if(anim != null){
			anim.paint(graphics, offx, offy, -1, -1, -1);
		}
	}
	
	protected void updateAnimation(){
		super.updateAnimation();
		for(UnitDescriptor ud : cells){
			ud.updateAnimation();
		}
	}
	
//	public Rectangle getBounds(){
//		Rectangle bounds = super.getBounds();
//		for(UnitDescriptor c : cells){
//			Rectangle b = c.getBounds();
//			if(bounds.x + bounds.width < b.x + b.width)
//				bounds.width = b.x + b.width - bounds.x;
//			if(bounds.y + bounds.height < b.y + b.height)
//				bounds.height = b.y + b.height - b.y;
//		}
//		return bounds;
//	}
	
	public Enum<?>[] getOperationList(){
		return list;
	}
	
	public boolean operate(Enum<?> i) {
		boolean b = false;
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 编辑动画效果:
			mainApp.showChangePropertyDialog(anim);
			b = true;
			break;
		case 添加动画序列:
			addAnimQueue();
			break;
		case 延拓轨迹:
			extendLocus();
			b = true;
			break;
		case 删除:
			onDelete();
			b = true;
			break;
		case 元素属性:
			mainApp.showUnitGroupDialog(unitGroup.protoID);
			break;
		}
		return b;
	}

	public boolean getEditabilities(Enum<?> i) {
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 编辑动画效果:
			return unit.getAnimation() != null && unit.getAnimation().getChanges().size() > 0;
		case 复制:
			return false;
		}
		return true;
	}

}
