//返回r
function goback(){
	tlocation("../oaTaskController/loadOaTask");
}
$('#defaultForm').bootstrapValidator({
	message:'此值不是有效的'
});
//保存
function updateOaTask(){
	submitBForm('defaultForm','../oaTaskController/updateOaTask','../oaTaskController/loadOaTask');
}
//初始化日期选择器
$(document).ready(function(){
	datetimeInit();
});

