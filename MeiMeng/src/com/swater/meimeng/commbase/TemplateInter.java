package com.swater.meimeng.commbase;

import com.swater.meimeng.commbase.HeaderPopMenu.OnHeaderItemClick;
import com.swater.meimeng.database.ShareUserConstant;
import com.swater.meimeng.database.XmlPersonDataType;
import com.swater.meimeng.mutils.constant.User_Types;
import com.swater.meimeng.mutils.net.MUrlPostAddr;

import android.view.View.OnClickListener;
/**
 *@category  定义接口
 *用于后面扩展 
 */
public interface TemplateInter extends  BroadCmd, XmlPersonDataType,OnClickListener,RespConstant,MUrlPostAddr,ShareUserConstant {
	
	void  iniView();
	void bindClick();
	User_Types user_type=User_Types.UN_KNOWN;
	

}
