<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="/deng/include/includeboot.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="UTF-8">
<title>仓库</title>
</head>
<body>
	<div class="panel panel-default">
		<fieldset>
			<legend>查询区域</legend>
			<form method="POST" id="searchForm" class="form-inline">
				<div class="form-group">
					<label>仓库名称</label>
					<input type="text" class="form-control" name="b_warehouse_name" placeholder="请输入仓库名称">
				</div>
				<div class="form-group">
					<label>商户、卖家</label>
					<input type="text" class="form-control" name="b_seller_name" placeholder="请输入商户、卖家">
				</div>
				<div class="form-group">
					<label>仓库类型</label>
					<select class="form-control" name="b_warehouse_type" >
						<option>请选择</option>
						<option value="0">赠品</option>
						<option value="1">疵品</option>
						<option value="2">正品</option>
					</select>
				</div>
					&nbsp;
				<a class="btn btn-primary" title="检索" href="javascript:search('datatables');">
		           <i class="fi-search"></i>检索
		        </a>&nbsp;
		        <a class="btn btn-primary" title="重置" href="javascript:resetAll();;">
		           <i class="icon-trash"></i>重置
		        </a>
			</form>
		</fieldset>
	</div>
	<div class="panel-body">
		<div class="btn-group pull-right" style="margin-right: 20px;">
			<button class="btn btn-default" onclick="toBWarehouseAdd()">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
			</button>
			<button class="btn btn-default" onclick="toBWarehouseUpdate()">
				<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
			</button>
			<button class="btn btn-default" onclick="delBWarehouse()">
				<span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
			</button>
			<button class="btn btn-default" onclick="search('datatables')">
				<span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>刷新
			</button>
		</div>
		<table id="datatables" class="table table-bordered table-striped table-hover">
			<thead>
				<tr>
					<th><label class="mt-checkbox mt-checkbox-single mt-checkbox-outline"><input type="checkbox" class="checkall" /><span></span></label></th>
					<th>序号</th>
					<th>商户、卖家</th>
					<th>仓库名称</th>
					<th>省份</th>
					<th>城市</th>
					<th>区县</th>
					<th>详细地址</th>
					<th>创建时间</th>
					<th>修改时间</th>
					<th>操作者</th>
					<th>仓库类型</th>
					<th>操作</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
<script type="text/javascript" src="../view/pc/b-view/b-warehouse/b-warehouse-list.js"></script> 
</html>
