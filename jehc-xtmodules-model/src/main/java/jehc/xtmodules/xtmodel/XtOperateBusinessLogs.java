package jehc.xtmodules.xtmodel;
import java.io.Serializable;

import jehc.xtmodules.xtcore.base.BaseEntity;

/**
* xt_operate_business_logs 平台业务操作日志表 
* 2016-09-16 16:39:20  邓纯杰
*/
public class XtOperateBusinessLogs extends BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String xt_operate_b_logs_id;/**平台业务操作日志ID**/
	private String xt_operate_b_logsTime;/**操作时间**/
	private String xt_userinfo_id;/**操作人ID外键**/
	private String xt_operate_b_logsModules;/**模块**/
	private String xt_operate_b_logsMethod;/**执行方法**/
	private String xt_operate_b_logsMethodPar;/**方法参数**/
	private String xt_operate_b_logsResult;/**执行的结果**/
	
	public void setXt_userinfo_id(String xt_userinfo_id){
		this.xt_userinfo_id=xt_userinfo_id;
	}
	public String getXt_userinfo_id(){
		return xt_userinfo_id;
	}
	public String getXt_operate_b_logs_id() {
		return xt_operate_b_logs_id;
	}
	public void setXt_operate_b_logs_id(String xt_operate_b_logs_id) {
		this.xt_operate_b_logs_id = xt_operate_b_logs_id;
	}
	public String getXt_operate_b_logsTime() {
		return xt_operate_b_logsTime;
	}
	public void setXt_operate_b_logsTime(String xt_operate_b_logsTime) {
		this.xt_operate_b_logsTime = xt_operate_b_logsTime;
	}
	public String getXt_operate_b_logsModules() {
		return xt_operate_b_logsModules;
	}
	public void setXt_operate_b_logsModules(String xt_operate_b_logsModules) {
		this.xt_operate_b_logsModules = xt_operate_b_logsModules;
	}
	public String getXt_operate_b_logsMethod() {
		return xt_operate_b_logsMethod;
	}
	public void setXt_operate_b_logsMethod(String xt_operate_b_logsMethod) {
		this.xt_operate_b_logsMethod = xt_operate_b_logsMethod;
	}
	public String getXt_operate_b_logsMethodPar() {
		return xt_operate_b_logsMethodPar;
	}
	public void setXt_operate_b_logsMethodPar(String xt_operate_b_logsMethodPar) {
		this.xt_operate_b_logsMethodPar = xt_operate_b_logsMethodPar;
	}
	public String getXt_operate_b_logsResult() {
		return xt_operate_b_logsResult;
	}
	public void setXt_operate_b_logsResult(String xt_operate_b_logsResult) {
		this.xt_operate_b_logsResult = xt_operate_b_logsResult;
	}
	
}
