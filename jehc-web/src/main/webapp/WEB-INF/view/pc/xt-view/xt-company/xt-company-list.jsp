<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/deng/include/includeboot.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>  
<head>  
<meta charset="UTF-8">  
<title>公司信息</title>  
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
						公司信息
					</h3>
				</div>
			</div>
		</div>
		<!--begin::Form-->
		<form class="m-form m-form--fit m-form--label-align-left m-form--group-seperator-dashed" id="defaultForm" method="post">
			<div class="m-portlet__body">
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">公司名称</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="hidden" maxlength="32" name="xt_company_id" value="${xt_Company.xt_company_id }">
						<input class="form-control" type="text" maxlength="50"  data-bv-notempty data-bv-notempty-message="请输入公司名称"  value="${xt_Company.xt_company_name }" name="xt_company_name" placeholder="请输入公司名称">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">公司电话</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="text" maxlength="20"  name="xt_company_tel" placeholder="请输入公司电话" value="${xt_Company.xt_company_tel }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">联&nbsp;系&nbsp;人</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="text" maxlength="20"  name="xt_company_userName" placeholder="请输入联系人" value="${xt_Company.xt_company_userName }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">公司性质</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="text" maxlength="20"  name="xt_company_type" placeholder="请输入公司性质" value="${xt_Company.xt_company_type }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">立成时间</label>
					</div>
					<div class="col-md-3">
						<input class="form_datetime form-control" readonly="readonly" style="width: 150px;" type="text" maxlength="20"  name="xt_company_upTime" placeholder="请输入立成时间" value="${xt_Company.xt_company_upTime }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">总负责人</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="text" maxlength="20"  name="xt_company_ceo" placeholder="请输入总负责人" value="${xt_Company.xt_company_ceo }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">省市区县</label>
					</div>
					<div class="col-md-3">
						<input class="form-control" type="hidden" id="xt_provinceID_" value="${xt_Company.xt_provinceID }">
						<select class="form-control" id="xt_province_id_0" name="xt_provinceID">
							<option value=''>请选择</option>
						</select>
					</div>
					<div class="col-md-2">
						<input class="form-control" type="hidden" id="xt_cityID_" value="${xt_Company.xt_cityID }">
						<select class="form-control" id="xt_city_id_0" name="xt_cityID">
							<option value=''>请选择</option>
						</select>
					</div>
					<div class="col-md-2">
						<input class="form-control" type="hidden" id="xt_districtID_" value="${xt_Company.xt_districtID }">
						<select class="form-control" id="xt_district_id_0" name="xt_districtID">
							<option value=''>请选择</option>
						</select>
					</div>
					<div class="col-md-2">
						<input class="form-control" type="text" maxlength="20"  name="xt_company_address" placeholder="请输入详细地址" value="${xt_Company.xt_company_address }">
					</div>
				</div>
				<div class="form-group m-form__group row">
					<div class="col-md-1">
						<label class="control-label">公司简介</label>
					</div>
					<div class="col-md-3">
						<textarea class="form-control" maxlength="500"  name="xt_company_remark" placeholder="请输入公司简介">${xt_Company.xt_company_remark }</textarea>
					</div>
				</div>
			</div>
			<div class="m-portlet__foot m-portlet__no-border m-portlet__foot--fit">
				<div class="m-form__actions m-form__actions--solid">
					<div class="row">
						<div class="col m--align-left">
							<button type="button" class="btn btn-secondary m-btn m-btn--custom m-btn--icon" onclick="addOrUpdateXtCompany()">保存</button>
						</div>
						<div class="col m--align-right">
						</div>
					</div>
				</div>
			</div>
		</form>
		<!--end::Form-->
	</div>
</body>  
<script type="text/javascript" src="../view/pc/xt-view/xt-company/xt-company-list.js"></script>
</html> 