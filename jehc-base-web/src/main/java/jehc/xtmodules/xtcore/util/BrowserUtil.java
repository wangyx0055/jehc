package jehc.xtmodules.xtcore.util;

import javax.servlet.http.HttpServletRequest;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;

public class BrowserUtil {
	
	/**
	 * 获取浏览器对象
	 * @param request
	 * @return
	 */
	public Browser callBrowser(HttpServletRequest request){
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));  
        Browser browser = userAgent.getBrowser();  
        return browser;
	}
	
	/**
	 * 获取操作系统
	 * @param request
	 * @return
	 */
	public OperatingSystem callOperatingSystem(HttpServletRequest request){
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));  
        OperatingSystem os = userAgent.getOperatingSystem();
        return os;
	}
	/**
	 * 判断是否手机端
	 * @param request
	 * @return
	 */
	public static boolean isPhone(HttpServletRequest request) {  
        boolean isMoblie = false;  
        String[] mobileAgents = { "iphone", "android", "phone", "mobile", "wap", "netfront", "java", "opera mobi",  
	        "opera mini", "ucweb", "windows ce", "symbian", "series", "webos", "sony", "blackberry", "dopod",  
	        "nokia", "samsung", "palmsource", "xda", "pieplus", "meizu", "midp", "cldc", "motorola", "foma",  
	        "docomo", "up.browser", "up.link", "blazer", "helio", "hosin", "huawei", "novarra", "coolpad", "webos",  
	        "techfaith", "palmsource", "alcatel", "amoi", "ktouch", "nexian", "ericsson", "philips", "sagem",  
	        "wellcom", "bunjalloo", "maui", "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",  
	        "pantech", "gionee", "portalmmm", "jig browser", "hiptop", "benq", "haier", "^lct", "320x320",  
	        "240x320", "176x220", "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac",  
	        "blaz", "brew", "cell", "cldc", "cmd-", "dang", "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs",  
	        "kddi", "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo", "midp", "mits", "mmef", "mobi",  
	        "mot-", "moto", "mwbp", "nec-", "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play", "port",  
	        "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri", "sgh-", "shar", "sie-", "siem",  
	        "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1", "upsi", "vk-v",  
	        "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda", "xda-",  
	        "Googlebot-Mobile" };  
        if (request.getHeader("User-Agent") != null) {  
            for (String mobileAgent : mobileAgents) {  
                if (request.getHeader("User-Agent").toLowerCase().indexOf(mobileAgent) >= 0) {  
                    isMoblie = true;  
                    break;  
                }  
            }  
        }  
        return isMoblie;  
    }  
}
