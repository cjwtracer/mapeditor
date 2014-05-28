package tool.mapeditor.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Vector2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tool.model.AlphaChange;
import tool.model.Animation;
import tool.model.GradualChange;
import tool.model.PositionChange;
import tool.model.Queue;
import tool.model.RotationChange;
import tool.model.ScaleChange;
import tool.model.Unit;
import tool.model.UnitGroup;

/**
 * Export animation datas created by imported AE animations.
 * @author caijw
 *
 */
public class ExportAnimationAction extends AbstractAction {

	public ExportAnimationAction() {
		super(ICommands.CMD_EXPORT_ANIM, "导出动画原型", "/icons/folder_anim.png");
	}
	
	public void run(){
		DirectoryDialog dlg = new DirectoryDialog(mainApp.getShell());
		String to = dlg.open();
		if(to == null)
			return;
		to += File.separator;
		
		List<Integer> existingActions = new ArrayList<Integer>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document dom = null;
		Document domAction = null;
		try {
			db = dbf.newDocumentBuilder();
			dom = db.newDocument();
			domAction = db.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		Element re = dom.createElement("root");
		dom.appendChild(re);
		Element are = domAction.createElement("root");
		domAction.appendChild(are);
		for(UnitGroup ug : mainApp.getProject().getUnitsGroup().values()){
			Element ee = dom.createElement("effect");
			ee.setAttribute("id", String.valueOf(ug.expID));
			ee.setAttribute("name", ug.getName());
			for(int i = 0, len = ug.getComponents().size(); i < len; i++){
				Unit u = ug.getComponents().get(i);
				Animation a = u.getAnimation();
				if(a == null){
					a = mainApp.getProject().getAnimationGroup().get(u.getAnimationID());
					u.setAnimation(a);
				}
				Element ei = dom.createElement("item");
				ei.setAttribute("id", a.getFramesResource());
				ei.setAttribute("target", String.valueOf(a.target));
				ei.setAttribute("hasDir", String.valueOf(a.hasDir));
				ei.setAttribute("layerPos", String.valueOf(a.layerPos));
				ei.setAttribute("delay", String.valueOf(a.delay));
				ei.setAttribute("offx", String.valueOf(ug.getOffset(i)[0]));
				ei.setAttribute("offy", String.valueOf(-ug.getOffset(i)[1]));
				ei.setAttribute("actionid", String.valueOf(a.actionID));
				if(a.actionID != 0 && !existingActions.contains(a.actionID)){
					are.appendChild(getActionElement(a, domAction));
					existingActions.add(a.actionID);
				}
				ei.setAttribute("repeat", String.valueOf(a.repeat));
				ei.setAttribute("isFly", String.valueOf(a.isFly));
				ee.appendChild(ei);
			}
			re.appendChild(ee);
		}
		OutputFormat format = new OutputFormat(dom);
		format.setIndenting(true);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(to + "effect.xml");
			XMLSerializer serializer = new XMLSerializer(fos, format);
			serializer.serialize(dom);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {if (fos != null)fos.close();} catch (IOException ex){}
		}
		
		format = new OutputFormat(domAction);
		format.setIndenting(true);
		fos = null;
		try {
			fos = new FileOutputStream(to + "action.xml");
			XMLSerializer serializer = new XMLSerializer(fos, format);
			serializer.serialize(domAction);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {if (fos != null)fos.close();} catch (IOException ex){}
		}
	}
	
	private Element getActionElement(Animation a, Document dom){
		Element ea = dom.createElement("action");
		ea.setAttribute("id", String.valueOf(a.actionID));
		ea.setAttribute("name", a.getName());
		int qs = a.getQueues().size();
		if(qs == 2){//animation with non-zero actionID has at least 2 sequences, sequence of frame change and anything else
			Queue q = a.getQueues().get(1);//change at index 0 is frame change
			getQueueElement(dom, ea, a, q);
		}else{// qs > 2
			Element eleSpawn = dom.createElement("item");
			eleSpawn.setAttribute("type", "4");
			for(int i = 1, len = a.getQueues().size(); i < len; i++){
				getQueueElement(dom, eleSpawn, a, a.getQueues().get(i));
			}
			ea.appendChild(eleSpawn);
		}
		
		return ea;
	}
	
	private void getQueueElement(Document dom, Element e, Animation a, Queue q){
		int s = q.getChanges().size();
		if(s == 0)
			return;
		if(s == 1){
			getChangeElement(dom, e, a, q.getChanges().get(0));
		}else{
			Element es = dom.createElement("item");
			es.setAttribute("type", "1");
			for(GradualChange cg : q.getChanges()){
				getChangeElement(dom, es, a, cg);
			}
			e.appendChild(es);
		}
	}
	
	private void getChangeElement(Document dom, Element e, Animation a, GradualChange cg){
		Element eleChange = dom.createElement("item");
		switch(cg.type){
		case GradualChange.CHANGE_POSITION:
			PositionChange pcg = (PositionChange)cg;
			Curve c = a.getLocus().getCurves().get(pcg.getCurve());
			Vector2f start = c.getStartPoint();
			Vector2f ctrl1 = c.getControlFirst();
			Vector2f ctrl2 = c.getControlSecond();
			Vector2f end = c.getEndPoint();
			if(start.x == ctrl1.x && start.y == ctrl1.y && ctrl2.x == end.x && ctrl2.y == end.y){
				eleChange.setAttribute("type", "6");
				eleChange.setAttribute("duration", String.valueOf(pcg.duration));
				eleChange.setAttribute("posx", String.valueOf((int)start.x));
				eleChange.setAttribute("posy", String.valueOf((int)-start.y));
			}else{
				eleChange.setAttribute("type", "9");
				eleChange.setAttribute("duration", String.valueOf(pcg.duration));
				eleChange.setAttribute("cposx1", String.valueOf((int)ctrl1.x));
				eleChange.setAttribute("cposy1", String.valueOf((int)-ctrl1.y));
				eleChange.setAttribute("cposx2", String.valueOf((int)ctrl2.x));
				eleChange.setAttribute("cposy2", String.valueOf((int)-ctrl2.y));
				eleChange.setAttribute("endposx", String.valueOf((int)end.x));
				eleChange.setAttribute("endposy", String.valueOf((int)-end.y));
			}
			break;
		case GradualChange.CHANGE_ALPHA:
			AlphaChange acg = (AlphaChange)cg;
			eleChange.setAttribute("type", "14");
			eleChange.setAttribute("duration", String.valueOf(acg.duration));
			eleChange.setAttribute("opacity", String.valueOf(acg.getEndState()));
			break;
		case GradualChange.CHANGE_ROTATION:
			RotationChange rcg = (RotationChange)cg;
			eleChange.setAttribute("type", "5");
			eleChange.setAttribute("duration", String.valueOf(rcg.duration));
			eleChange.setAttribute("angle", String.valueOf(rcg.getEndState()));
			break;
		case GradualChange.CHANGE_SCALE:
			ScaleChange scg = (ScaleChange)cg;
			eleChange.setAttribute("type", "10");
			eleChange.setAttribute("duration", String.valueOf(scg.duration));
			eleChange.setAttribute("scalex", String.valueOf(scg.getEndState()));
			eleChange.setAttribute("scaley", String.valueOf(scg.getEndState()));
			break;
		}
		e.appendChild(eleChange);
	}

}
