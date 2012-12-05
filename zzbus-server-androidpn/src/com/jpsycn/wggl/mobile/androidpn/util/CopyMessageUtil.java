/**
 * 
 */
package com.jpsycn.wggl.mobile.androidpn.util;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.xmpp.packet.IQ;

import com.jpsycn.wggl.mobile.androidpn.model.NotificationMO;

/**
 * @author chengqiang.liu
 *	����ɵ�֪ͨID��notificationMO������� �������ݴ�ID�޸Ļ�ִ״̬��
 */
public class CopyMessageUtil {
	
	public static void IQ2Message(IQ iq,NotificationMO notificationMO){
		
		Element root = iq.getElement();
		Attribute idAttr=root.attribute("id");
		if(idAttr != null){
			String id = idAttr.getValue();
			notificationMO.setMessageId(id);
		}
	}
}
