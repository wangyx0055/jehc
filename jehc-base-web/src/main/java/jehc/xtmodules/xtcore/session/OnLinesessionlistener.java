package jehc.xtmodules.xtcore.session;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import jehc.xtmodules.xtcore.base.BaseHttpSessionEntity;
import jehc.xtmodules.xtcore.util.constant.SessionConstant;

public class OnLinesessionlistener implements HttpSessionListener {
	//创建集合保存session对象
	static Hashtable<String, HttpSession> sessionList=new Hashtable<String,HttpSession>();
	public OnLinesessionlistener(){
		
	}
	
	public void sessionCreated(HttpSessionEvent event) {  
		sessionList.put(event.getSession().getId(), event.getSession());
    }  
    @SuppressWarnings("unchecked")
	public void sessionDestroyed(HttpSessionEvent event) { 
    	HttpSession session = event.getSession(); 
    	ServletContext application = session.getServletContext();
    	if(null != session){
    		// 取得登录的用户名  
    		BaseHttpSessionEntity baseHttpSessionEntity = (BaseHttpSessionEntity) session.getAttribute(SessionConstant.BASE_HTTP_SESSION);
        	// 从在线列表中删除用户名  
        	List<BaseHttpSessionEntity> baseHttpSessionEntityList = (List<BaseHttpSessionEntity>) application.getAttribute(SessionConstant.XT_ONLINE_USERLIST);  
        	if(null != baseHttpSessionEntity && null != baseHttpSessionEntityList){
        		baseHttpSessionEntityList.remove(baseHttpSessionEntity);
        	}
        	session.invalidate();
    	}
    }
    
    //返回全部session对象集合
	static public Iterator<HttpSession> getSet(){
    	return sessionList.values().iterator();
    }
    
    //根据session对象的id返回session对象
    static public HttpSession getSession(String sessionId){
    	return (HttpSession)sessionList.get(sessionId);
    }
}
