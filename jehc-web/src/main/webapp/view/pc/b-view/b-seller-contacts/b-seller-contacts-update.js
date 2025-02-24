var bSellerContactsWinEdit;
var bSellerContactsFormEdit;
function updateBSellerContacts(){
	var record = grid.getSelectionModel().selected;
	if(record.length == 0){
		msgTishi('请选择要修改的一项！');
		return;
	}
	initBSellerContactsFormEdit();
	bSellerContactsWinEdit = Ext.create('Ext.Window',{
		layout:'fit',
		width:800,
		height:400,
		maximizable:true,
		minimizable:true,
		animateTarget:document.body,
		plain:true,
		modal:true,
		title:'编辑信息',
		headerPosition:'left',
		listeners:{
			minimize:function(win,opts){
				win.collapse();
			}
		},
		items:bSellerContactsFormEdit,
		buttons:[{
			text:'保存',
			itemId:'save',
			handler:function(button){
				submitForm(bSellerContactsFormEdit,'../bSellerContactsController/updateBSellerContacts',grid,bSellerContactsWinEdit,false,true);
			}
		},{
			text:'关闭',
			itemId:'close',
			handler:function(button){
				button.up('window').close();
			}
		}]
	});
	bSellerContactsWinEdit.show();
	loadFormData(bSellerContactsFormEdit,'../bSellerContactsController/getBSellerContactsById?b_seller_contacts_id='+ record.items[0].data.b_seller_contacts_id);
}
function initBSellerContactsFormEdit(){
	bSellerContactsFormEdit = Ext.create('Ext.FormPanel',{
		xtype:'form',
		waitMsgTarget:true,
		defaultType:'textfield',
		fieldDefaults:{
			labelWidth:70,
			labelAlign:'left',
			flex:1,
			margin:'2 5 4 5'
		},
		items:[
		{
			fieldLabel:'卖家明细编号',
			xtype:'textfield',
			hidden:true,
			name:'b_seller_contacts_id',
			allowBlank:false,
			maxLength:32,
			anchor:'100%'
		},
		{
			fieldLabel:'卖家编号',
			xtype:'textfield',
			name:'b_seller_id',
			id:'b_seller_id_',
			maxLength:32,
			hidden:true,
			anchor:'100%'
		},
		{
			fieldLabel:'姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名',
			xtype:'textfield',
			name:'b_seller_contacts_uname',
			maxLength:50,
			anchor:'100%'
		},
		{
			fieldLabel:'联系电话',
			xtype:'textfield',
			name:'b_seller_contacts_tel',
			maxLength:30,
			anchor:'100%'
		},
		{
			fieldLabel:'性&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别',
			name:'b_seller_contacts_sex',
			xtype:"combo",
            store:[["0","男"],["1","女"]],
            emptyText:"请选择",
            mode:"local",
            value:'0',
            triggerAction:"all",
            editable:false,
			hiddenName:'b_seller_contacts_sex',
			allowBlank:false,
			anchor:'25%'
		},
		{
			fieldLabel:'职&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;位',
			name:'b_seller_contacts_type',
			xtype:"combo",
            store:[["0","普通员工"],["1","法人"],['2','经理'],['3','主管']],
            emptyText:"请选择",
            mode:"local",
            value:'0',
            triggerAction:"all",
            editable:false,
			hiddenName:'b_seller_contacts_type',
			allowBlank:false,
			anchor:'25%'
		},
		{
			fieldLabel:'是否离职',
			xtype:'textfield',
			hidden:true,
			name:'b_seller_contacts_turnover',
			maxLength:2,
			anchor:'100%'
		}
		]
	});
}
