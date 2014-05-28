package tool.mapeditor.application;

public class MapDescriptorUtil {
	/**
	 * SEP must at the head of the operation list
	 * @author caijw
	 *
	 */
	static enum OPERS{
		SEP, 编辑动画效果, 添加动画序列, 
		延拓轨迹, 轨迹属性, 复制,
		删除, 元素属性, 碰撞, 遮挡, 
		降落点, 清空, 安全区, 属性,
		传送
	}

}
