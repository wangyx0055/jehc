//返回r
function goback(){
	tlocation("../paymentAccountController/loadPaymentAccount");
}
$('#defaultForm').bootstrapValidator({
	message:'此值不是有效的'
});
//保存
function updatePaymentAccount(){
	submitBForm('defaultForm','../paymentAccountController/updatePaymentAccount','../paymentAccountController/loadPaymentAccount');
}
//初始化日期选择器
$(document).ready(function(){
	datetimeInit();
});

