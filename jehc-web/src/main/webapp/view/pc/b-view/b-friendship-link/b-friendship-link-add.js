//返回r
function goback(){
	tlocation("../bFriendshipLinkController/loadBFriendshipLink");
}
$('#defaultForm').bootstrapValidator({
	message:'此值不是有效的'
});
//保存
function addBFriendshipLink(){
	submitBForm('defaultForm','../bFriendshipLinkController/addBFriendshipLink','../bFriendshipLinkController/loadBFriendshipLink');
}
//初始化日期选择器
$(document).ready(function(){
	datetimeInit();
});

