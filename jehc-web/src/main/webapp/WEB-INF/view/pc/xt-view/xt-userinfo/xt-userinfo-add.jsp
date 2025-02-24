<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/deng/include/includeboot.jsp"%>
<%@ include file="/deng/include/inplugboot.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="UTF-8">
<title>员工信息表新增页面</title>
<link href="${syspath}/deng/source/css/bootform.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${syspath}/deng/source/plugins/other/bztree/css/bootstrapStyle/bootstrapStyle.css" type="text/css">
<script type="text/javascript" src="${syspath}/deng/source/plugins/other/bztree/js/jquery.ztree.core.js"></script>
<script type="text/javascript" src="${syspath}/deng/source/plugins/other/bztree/js/jquery.ztree.excheck.js"></script>
<script type="text/javascript" src="${syspath}/deng/source/plugins/other/bztree/js/jquery.ztree.exedit.js"></script>
</head>
<body>
	<div class="m-portlet">
		<div class="m-portlet__head">
			<div class="m-portlet__head-caption">
				<div class="m-portlet__head-title">
					<span class="m-portlet__head-icon m--hide">
					<i class="la la-gear"></i>
					</span>
					<h3 class="m-portlet__head-text">
						创建员工
					</h3>
				</div>
			</div>
		</div>
		<!--begin::Form-->
		<form class="m-form" id="defaultForm" method="post">
			<div class="m-portlet__body">		
			<fieldset>
			<legend>组织机构</legend>
				<div class="row">
					<!-- 
			        <div class="col-md-1">
			        	<label class="control-label">隶属公司</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
				        	<input type="hidden" maxlength="32"  name="xt_company_id">
				        	<input class="form-control" type="text" maxlength="32" readonly="readonly" name="" placeholder="所属公司">
			        	</div>
			        </div>
			         -->
			         <div class="col-md-1">
			        	<label class="control-label">隶属部门</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group input-group">
			        		<input type="hidden" maxlength="32"  name="xt_departinfo_id" id="xt_departinfo_id">
							<input class="form-control" type="text" maxlength="32" readonly="readonly" id ="xt_departinfo_name" name="xt_departinfo_name" placeholder="请选择隶属部门">
							<span class="input-group-btn">
								<button class="btn btn-default" type="button" onclick="departSelect()">
									选择
								</button>
							</span>
						</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">隶属岗位</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group input-group">
			        		<input type="hidden" maxlength="32"  name="xt_post_id" id="xt_post_id">
							<input class="form-control" type="text"  maxlength="32" readonly="readonly" id="xt_post_name" name="xt_post_name" placeholder="请选择岗位">
							<span class="input-group-btn" style="margin-left: 0px;">
								<button class="btn btn-default" type="button" onclick="postSelect()">
									选择
								</button>
							</span>
						</div>
			        </div>
			    </div>
			</fieldset>
			<fieldset>
			<legend>基本信息</legend>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">用户名称</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="64" onblur="validateUser(this);" style="width: 150px;" id="xt_userinfo_name" data-bv-notempty data-bv-notempty-message="请输入用户名"  name="xt_userinfo_name" placeholder="请输入用户名">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">性别</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_sex"  style="width: 150px;" name="xt_userinfo_sex" placeholder="请选择"></select>
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">出生年月</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form_datetime form-control" readonly="readonly"  style="width: 150px;" type="text" maxlength="20"  name="xt_userinfo_birthday" placeholder="请输入生日">
			       		</div>
			        </div>
			    </div>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">真实姓名</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" style="width: 150px;" maxlength="30" data-bv-notempty data-bv-notempty-message="请输入真实姓名"   name="xt_userinfo_realName" placeholder="请输入真实姓名">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">是否已婚</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_ismarried"  style="width: 150px;" name="xt_userinfo_ismarried" placeholder="请选择"></select>
			      		</div>
			        </div>	
			        <div class="col-md-1">
			        	<label class="control-label">身份证号</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="20"  name="xt_userinfo_card" placeholder="请输入身份证号码">
			        	</div>
			        </div>
			    </div>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">名族</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_nation" id="xt_userinfo_nation" name="xt_userinfo_nation" placeholder="请选择名族" ></select>
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">籍贯</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="20"  name="xt_userinfo_origo" placeholder="请输入籍贯">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">毕业学校</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="64"  name="xt_userinfo_schoolName" placeholder="请输入毕业学校">
			        	</div>
			        </div>
			    </div>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">联系电话</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="30"  name="xt_userinfo_phone" placeholder="请输入联系电话">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">移动电话</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="20"  name="xt_userinfo_mobile" placeholder="请输入移动电话">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">其他电话</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="20" name="xt_userinfo_ortherTel" placeholder="请输入其他电话">
			        	</div>
			        </div>
			    </div>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">备注</label>
			        </div>
			        <div class="col-md-11">
			        	<div class="form-group">
			        		<textarea class="form-control" cols="100" maxlength="200"  name="xt_userinfo_remark" placeholder="请输入备注"></textarea>
			        	</div>
			        </div>
			    </div>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">地址</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="255" name="xt_userinfo_address" placeholder="请输入地址">
			        	</div>
			        </div>
			    </div>
			    <div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">入职时间</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form_datetime form-control" readonly="readonly"  style="width: 150px;" type="text" maxlength="32"  name="xt_userinfo_intime" placeholder="请输入入职时间">
			       		</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">离职时间</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form_datetime form-control" readonly="readonly"  style="width: 150px;" type="text" maxlength="32"  name="xt_userinfo_outTime" placeholder="请输入离职时间">
			       		</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">到期时间</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form_datetime form-control" readonly="readonly"  style="width: 150px;" type="text" maxlength="32"  name="xt_userinfo_contractTime" placeholder="请输入合同到期时间">
			        	</div>
			        </div>
			    </div>
			    <div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">QQ号码</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="12"  style="width: 150px;" name="xt_userinfo_qq" placeholder="请输入qq号码">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">电子邮件</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="50" style="width: 150px;" name="xt_userinfo_email" placeholder="请输入电子邮件">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">状态</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_state"  style="width: 150px;" name="xt_userinfo_state" placeholder="请选择状态"></select>
			        	</div>
				    </div>
				</div>
			</fieldset>
			<fieldset>
			<legend>其它信息</legend>
				<div class="row">
			        <div class="col-md-1">
			        	<label class="control-label">政治面貌</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<input class="form-control" type="text" maxlength="32"  style="width: 150px;" name="xt_userinfo_politicalStatus" placeholder="请选择政治面貌">
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">文化程度</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_highestDegree"  style="width: 150px;" name="xt_userinfo_highestDegree" placeholder="请选择文化程度"></select>
			        	</div>
			        </div>
			        <div class="col-md-1">
			        	<label class="control-label">工作年限</label>
			        </div>
			        <div class="col-md-3">
			        	<div class="form-group">
			        		<select class="form-control" maxlength="32" id="xt_userinfo_workYear"  style="width: 150px;" name="xt_userinfo_workYear" placeholder="请选择工作年限"></select>
			        	</div>
			        </div>
			    </div>
			</fieldset>
			</div>
			<div class="m-portlet__foot m-portlet__foot--fit">
				<div class="m-form__actions m-form__actions--right">
					<div class="row">
						<div class="col m--align-left">
							<button type="button" class="btn btn-success m-btn m-btn--custom m-btn--icon" onclick="addXtUserinfo()">保存</button>
							<button type="button" class="btn btn-secondary m-btn m-btn--custom m-btn--icon" onclick="goback()">返回</button>
						</div>
						<div class="col m--align-right">
							<a href="javascript:resetAll('defaultForm')" class="btn btn-secondary m-btn m-btn--custom m-btn--icon">
								<span><i class="fa fa-repeat"></i><span>重置</span></span>
							</a>
						</div>
					</div>
				</div>
			</div>
		</form>
		<!--end::Form-->
	</div>
</body>
<!-- 部门选择器模态框（Modal）开始 -->
<div class="modal fade" id="departSelectModal" tabindex="-1" role="dialog" aria-labelledby="departSelectModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="departSelectModalLabel">
					部门选择器
				</h4>
			</div>
			<div class="modal-body">
				<ul id="tree" class="ztree"></ul>
			</div>
			<div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="doDepartSelect()">保存</button>
            </div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>
<!-- 部门选择器模态框（Modal）结束 -->

<!-- 岗位选择器模态框（Modal）开始 -->
<div class="modal fade" id="postSelectModal" tabindex="-1" role="dialog" aria-labelledby="postSelectModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="postSelectModalLabel">
					岗位选择器
				</h4>
			</div>
			<div class="modal-body">
				<ul id="posttree" class="ztree"></ul>
			</div>
			<div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onclick="doPostSelect()">保存</button>
            </div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>
<!-- 岗位选择器模态框（Modal）结束 -->
<script type="text/javascript" src="../view/pc/xt-view/xt-userinfo/xt-userinfo-add.js"></script> 
</html>
