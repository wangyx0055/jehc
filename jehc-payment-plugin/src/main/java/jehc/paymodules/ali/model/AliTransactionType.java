package jehc.paymodules.ali.model;

import jehc.paymodules.base.model.TransactionType;

/**
 * 阿里交易类型
 * 说明交易类型主要用于支付接口调用参数所需
 * {@link #APP 新版app支付}
 */
public enum AliTransactionType implements TransactionType {
    /**
     * 即时到帐
     */
    DIRECT("alipay.trade.page.pay"),
    /**
     * APP支付
     */
    APP("alipay.trade.app.pay"),
    /**
     * 手机网站支付
     */
    WAP("alipay.trade.wap.pay"),
    /**
     *  扫码付
     */
    SWEEPPAY("alipay.trade.precreate"),
    /**
     * 条码付
     */
    BAR_CODE("alipay.trade.pay"),
    /**
     * 声波付
     */
    WAVE_CODE("alipay.trade.pay"),
    //交易辅助接口
    /**
     * 交易订单查询
     */
    QUERY("alipay.trade.query"),
    /**
     * 交易订单关闭
     */
    CLOSE("alipay.trade.close"),
    /**
     * 退款
     */
    REFUND("alipay.trade.refund"),
    /**
     * 退款查询
     */
    REFUNDQUERY("alipay.trade.fastpay.refund.query"),
    /**
     * 下载对账单
     */
    DOWNLOADBILL("alipay.data.dataservice.bill.downloadurl.query"),
    /**
     * 转账到支付宝
     */
    TRANS("alipay.fund.trans.toaccount.transfer"),
    /**
     * 转账查询
     */
    TRANS_QUERY("alipay.fund.trans.order.query")
    ;
    private String method;
    private AliTransactionType(String method) {
        this.method = method;
    }

    public String getType() {
        return this.name();
    }
    /**
     * 获取接口名称
     * @return 接口名称
     */
    public String getMethod() {
        return this.method;
    }
}
