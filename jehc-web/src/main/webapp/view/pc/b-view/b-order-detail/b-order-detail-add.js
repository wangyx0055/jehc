var bOrderDetailWinAdd;
var bOrderDetailFormAdd;
function addBOrderDetail(){
	initBOrderDetailFormAdd();
	bOrderDetailWinAdd = Ext.create('Ext.Window',{
		layout:'fit',
		width:800,
		height:400,
		maximizable:true,
		minimizable:true,
		animateTarget:document.body,
		plain:true,
		modal:true,
		title:'添加信息',
		listeners:{
			minimize:function(win,opts){
				win.collapse();
			}
		},
		items:bOrderDetailFormAdd,
		buttons:[{
			text:'保存',
			itemId:'save',
			handler:function(button){
				submitForm(bOrderDetailFormAdd,'../bOrderDetailController/addBOrderDetail',grid,bOrderDetailWinAdd,false,true);
			}
		},{
			text:'关闭',
			itemId:'close',
			handler:function(button){
				button.up('window').close();
			}
		}]
	});
	bOrderDetailWinAdd.show();
}
function initBOrderDetailFormAdd(){
	bOrderDetailFormAdd = Ext.create('Ext.FormPanel',{
		xtype:'form',
		waitMsgTarget:true,
		defaultType:'textfield',
		autoScroll:true,
		fieldDefaults:{
			labelWidth:70,
			labelAlign:'left',
			flex:1,
			margin:'2 5 4 5'
		},
		items:[
		{
			fieldLabel:'商品编号',
			xtype:'textfield',
			name:'b_product_id',
			maxLength:32,
			anchor:'100%'
		},
		{
			fieldLabel:'卖家编号',
			xtype:'textfield',
			name:'b_seller_id',
			maxLength:32,
			anchor:'100%'
		},
		{
			fieldLabel:'购物数量',
			xtype:'numberfield',
			value:'0',
			name:'b_order_detail_number',
			anchor:'100%'
		},
		{
			fieldLabel:'购买单价',
			xtype:'numberfield',
			value:'0',
			name:'b_order_detail_price',
			anchor:'100%'
		},
		{
			fieldLabel:'折扣',
			xtype:'textfield',
			name:'b_order_detail_discount',
			anchor:'100%'
		},
		{
			fieldLabel:'创建时间',
			xtype:'datefield',
			format:'Y-m-d H:i:s',
			name:'b_order_detail_ctime',
			anchor:'100%'
		},
		{
			fieldLabel:'修改时间',
			xtype:'datefield',
			format:'Y-m-d H:i:s',
			name:'b_order_detail_mtime',
			anchor:'100%'
		}
		]
	});
}
