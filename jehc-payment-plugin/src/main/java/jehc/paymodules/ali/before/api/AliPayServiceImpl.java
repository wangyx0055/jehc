package jehc.paymodules.ali.before.api;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import jehc.paymodules.ali.model.AliTransactionType;
import jehc.paymodules.base.dao.PayConfigDao;
import jehc.paymodules.base.model.MethodType;
import jehc.paymodules.base.model.PayMessage;
import jehc.paymodules.base.model.PayOrder;
import jehc.paymodules.base.model.PayOutMessage;
import jehc.paymodules.base.model.RefundOrder;
import jehc.paymodules.base.model.TransactionType;
import jehc.paymodules.base.model.TransferOrder;
import jehc.paymodules.base.service.impl.PayServiceImpl;
import jehc.paymodules.base.util.apiservice.Callback;
import jehc.paymodules.base.util.exception.PayErrorException;
import jehc.paymodules.base.util.exception.entity.PayException;
import jehc.paymodules.base.util.http.HttpConfigDb;
import jehc.paymodules.base.util.http.UriVariables;
import jehc.paymodules.base.util.sign.SignUtils;
import jehc.paymodules.base.util.str.StringUtils;

/**
 * 支付宝支付通知
 * @author Administrator
 *
 */
public class AliPayServiceImpl extends PayServiceImpl {
    protected final Log LOG = LogFactory.getLog(AliPayServiceImpl.class);
    private static final String HTTPS_REQ_URL = "https://mapi.alipay.com/gateway.do";
    private static final String QUERY_REQ_URL = "https://openapi.alipay.com/gateway.do";
    public AliPayServiceImpl(PayConfigDao payConfigDao) {
        super(payConfigDao);
    }
    public AliPayServiceImpl(PayConfigDao payConfigDao, HttpConfigDb configDao) {
        super(payConfigDao, configDao);
    }
    public String getHttpsVerifyUrl() {
        return HTTPS_REQ_URL + "?service=notify_verify";
    }
    /**
     * 回调校验
     * @param params 回调回来的参数集
     * @return 签名校验 true通过
     */
    public boolean verify(Map<String, Object> params) {
        if (params.get("sign") == null || params.get("notify_id") == null) {
            LOG.debug("支付宝支付异常：params：" + params);
            return false;
        }
        try {
            return signVerify(params, (String) params.get("sign")) && verifySource((String) params.get("notify_id"));
        } catch (PayErrorException e) {
            LOG.error(e);
        }
        return false;
    }
    /**
     * 校验数据来源
     * @param id 业务id, 数据的真实性.
     * @return true通过
     */
    public boolean verifySource(String id) {
        return "true".equals(requestTemplate.getForObject( getHttpsVerifyUrl() + "&partner=" + payConfigDao.getPid() + "&notify_id=" + id, String.class));
    }
    /**
     * 根据反馈回来的信息，生成签名结果
     * @param params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    public boolean signVerify(Map<String, Object> params, String sign) {
        return SignUtils.valueOf(payConfigDao.getSignType()).verify(params,  sign,  payConfigDao.getKeyPublic(), payConfigDao.getInputCharset());
    }
    /**
     *  生成并设置签名
     * @param parameters 请求参数
     * @return 订单信息
     */
    private Map<String, Object> setSign(Map<String, Object> parameters){
        parameters.put("sign_type", payConfigDao.getSignType());
        String sign = createSign(SignUtils.parameterText(parameters, "&",  "sign", "appId"), payConfigDao.getInputCharset());
        parameters.put("sign", sign);
        return parameters;
    }

    /**
     * 获取公共请求参数
     * @param transactionType 交易类型
     * @return 公共请求参数
     */
    private Map<String, Object> getPublicParameters(TransactionType transactionType ){
        Map<String, Object> orderInfo = new TreeMap<>();
        orderInfo.put("app_id", payConfigDao.getAppid());
        orderInfo.put("method", transactionType.getMethod());
        orderInfo.put("format", "json");
        orderInfo.put("charset", payConfigDao.getInputCharset());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        orderInfo.put("timestamp", df.format(new Date()));
        orderInfo.put("version", "1.0");
        return  orderInfo;
    }

    /**
     * 返回创建的订单信息
     * @param order 支付订单
     * @return 订单信息
     * @see PayOrder 支付订单信息
     */
    public Map<String, Object> orderInfo(PayOrder order) {
        Map<String, Object> orderInfo = getOrder(order);
        String sign = null;
        if (AliTransactionType.APP == order.getTransactionType() ){
            sign = createSign(getOrderInfo(order), payConfigDao.getInputCharset());
        }else {
            sign = createSign(orderInfo, payConfigDao.getInputCharset());
        }
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        orderInfo.put("sign", sign);
        orderInfo.put("sign_type", payConfigDao.getSignType());
        return orderInfo;
    }

    private String getOrderInfo(PayOrder order) {
        String orderInfo = "partner=\"" + this.payConfigDao.getPid() + "\"";
        orderInfo = orderInfo + "&seller_id=\"" + this.payConfigDao.getSeller() + "\"";
        orderInfo = orderInfo + "&out_trade_no=\"" + order.getOutTradeNo() + "\"";
        orderInfo = orderInfo + "&subject=\"" + order.getSubject() + "\"";
        orderInfo = orderInfo + "&body=\"" + order.getBody() + "\"";
        orderInfo = orderInfo + "&total_fee=\"" + order.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString()  + "\"";
        orderInfo = orderInfo + "&notify_url=\"" + this.payConfigDao.getNotifyUrl() + "\"";
        orderInfo = orderInfo + "&service=\"mobile.securitypay.pay\"";
        orderInfo = orderInfo + "&payment_type=\"1\"";
        orderInfo = orderInfo + "&_input_charset=\""+ payConfigDao.getInputCharset()+"\"";
        orderInfo = orderInfo + "&it_b_pay=\"30m\"";
        orderInfo = orderInfo + "&return_url=\""+payConfigDao.getReturnUrl()+"\"";
        return orderInfo;
    }

    /**
     * 支付宝创建订单信息
     * create the order info
     * @param order 支付订单
     * @return 订单信息
     * @see PayOrder
     */
    private Map<String, Object> getOrder(PayOrder order) {
        Map<String, Object> orderInfo = new TreeMap<>();
        // 签约合作者身份ID
        orderInfo.put("partner", payConfigDao.getPid());
        // 签约卖家支付宝账号
        orderInfo.put("seller_id", payConfigDao.getSeller());
        // 商户网站唯一订单号
        orderInfo.put("out_trade_no", order.getOutTradeNo());
        // 商品名称
        orderInfo.put("subject", order.getSubject());
        // 商品详情
        orderInfo.put("body", order.getBody());
        // 商品金额
        orderInfo.put("total_fee", order.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString() );
        // 服务器异步通知页面路径
        orderInfo.put("notify_url", payConfigDao.getNotifyUrl() );
        // 服务接口名称， 固定值
        orderInfo.put("service",  order.getTransactionType().getMethod()  );
        // 支付类型， 固定值
        orderInfo.put("payment_type",  "1" );
        // 参数编码， 固定值
        orderInfo.put("_input_charset",  payConfigDao.getInputCharset());
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        // TODO 2017/2/6 11:05 author: egan  目前写死，这一块建议配置
        orderInfo.put("it_b_pay",  "30m");
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo.put("return_url", payConfigDao.getReturnUrl());
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
//        if (order.getTransactionType().getType())
//          orderInfo.put("paymethod","expressGateway");

        return orderInfo;
    }

    /**
     * 获取输出消息，用户返回给支付端
     * @param code 状态
     * @param message 消息
     * @return 返回输出消息
     */
    public PayOutMessage getPayOutMessage(String code, String message) {
        return PayOutMessage.TEXT().content(code.toLowerCase()).build();
    }

    /**
     * 获取成功输出消息，用户返回给支付端
     * 主要用于拦截器中返回
     * @param payMessage 支付回调消息
     * @return 返回输出消息
     */
    public PayOutMessage successPayOutMessage(PayMessage payMessage) {
        return PayOutMessage.TEXT().content("success").build();
    }

    /**
     * 获取输出消息，用户返回给支付端, 针对于web端
     * @param orderInfo 发起支付的订单信息
     * @param method    请求方式  "post" "get",
     * @return 获取输出消息，用户返回给支付端, 针对于web端
     * @see MethodType 请求类型
     */
    public String buildRequest(Map<String, Object> orderInfo, MethodType method) {
        StringBuffer formHtml = new StringBuffer();
        formHtml.append("<form id=\"_alipaysubmit_\" name=\"alipaysubmit\" action=\"" )
                .append( HTTPS_REQ_URL)
                .append(  "?_input_charset=" )
                .append( payConfigDao.getInputCharset())
                .append( "\" method=\"")
                .append( method.name().toLowerCase()) .append( "\">");
        for (String key: orderInfo.keySet()) {
            Object o = orderInfo.get(key);
            if (null == o ||"null".equals(o) || "".equals(o) ){
                continue;
            }
            formHtml.append("<input type=\"hidden\" name=\"" + key + "\" value=\"" + orderInfo.get(key) + "\"/>");
        }
        //submit按钮控件请不要含有name属性
        formHtml.append("</form>");
        formHtml.append("<script>document.forms['_alipaysubmit_'].submit();</script>");
        return formHtml.toString();
    }

    /**
     * 生成二维码支付
     * 暂未实现或无此功能
     * @param orderInfo 发起支付的订单信息
     * @return 返回图片信息，支付时需要的
     */
    public BufferedImage genQrPay(PayOrder orderInfo) {
        throw new UnsupportedOperationException();
    }

    /**
     * 刷卡付,pos主动扫码付款(条码付)
     * @param order 发起支付的订单信息
     * @return 支付结果
     */
    public Map<String, Object> microPay(PayOrder order) {
        throw new UnsupportedOperationException();
    }

    /**
     * 交易查询接口
     * @param tradeNo 支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回查询回来的结果集，支付方原值返回
     */
    public Map<String, Object> query(String tradeNo, String outTradeNo) {
        return secondaryInterface(tradeNo, outTradeNo, AliTransactionType.QUERY);
    }
    
    /**
     * 交易关闭接口
     * @param tradeNo 支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回支付方交易关闭后的结果
     */
    public Map<String, Object> close(String tradeNo, String outTradeNo) {
        return  secondaryInterface(tradeNo, outTradeNo, AliTransactionType.CLOSE);
    }

    /**
     * 申请退款接口
     * 废弃
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @param refundAmount 退款金额
     * @param totalAmount 总金额
     * @return 返回支付方申请退款后的结果
     * @see #refund(RefundOrder, Callback)
     */
    @Deprecated
    public Map<String, Object> refund(String tradeNo, String outTradeNo, BigDecimal refundAmount, BigDecimal totalAmount) {
        return refund(new RefundOrder(tradeNo, outTradeNo, refundAmount, totalAmount));
    }

    /**
     * 申请退款接口
     * @param refundOrder   退款订单信息
     * @return 返回支付方申请退款后的结果
     */
    public Map<String, Object> refund(RefundOrder refundOrder) {
        Map<String, Object> parameters = getPublicParameters(AliTransactionType.REFUND);
        Map<String, Object> bizContent = getBizContent(refundOrder.getTradeNo(), refundOrder.getOutTradeNo(), null);
        if (!StringUtils.isEmpty(refundOrder.getRefundNo())){
            bizContent.put("out_request_no", refundOrder.getRefundNo());
        }
        bizContent.put("refund_amount", refundOrder.getRefundAmount());
        //设置请求参数的集合
        parameters.put("biz_content", JSON.toJSONString(bizContent));
        //设置签名
        setSign(parameters);
        return  requestTemplate.getForObject(QUERY_REQ_URL + "?" + UriVariables.getMapToParameters(parameters), JSONObject.class);
    }

    /**
     * 查询退款
     * @param tradeNo 支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回支付方查询退款后的结果
     */
    public Map<String, Object> refundquery(String tradeNo, String outTradeNo) {
        return secondaryInterface(tradeNo, outTradeNo, AliTransactionType.REFUNDQUERY);
    }

    /**
     * 目前只支持日账单
     * @param billDate 账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型：trade、signcustomer；trade指商户基于支付宝交易收单的业务账单；signcustomer是指基于商户支付宝余额收入及支出等资金变动的帐务账单；
     * @param billType 账单时间：日账单格式为yyyy-MM-dd，月账单格式为yyyy-MM。
     * @return 返回支付方下载对账单的结果
     */
    public Map<String, Object> downloadbill(Date billDate, String billType) {
        //获取公共参数
        Map<String, Object> parameters = getPublicParameters(AliTransactionType.DOWNLOADBILL);
        Map<String, Object> bizContent = new TreeMap<>();
        bizContent.put("bill_type", billType);
        //目前只支持日账单
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        bizContent.put("bill_date", df.format(billDate));
        //设置请求参数的集合
        parameters.put("biz_content", JSON.toJSONString(bizContent));
        //设置签名
        setSign(parameters);
        return  requestTemplate.getForObject(QUERY_REQ_URL + "?" + UriVariables.getMapToParameters(parameters), JSONObject.class);
    }
    /**
     * @param tradeNoOrBillDate 支付平台订单号或者账单类型， 具体请 类型为{@link String }或者 {@link Date }，类型须强制限制，类型不对应则抛出异常{@link PayErrorException}
     * @param outTradeNoBillType  商户单号或者 账单类型
     * @param transactionType 交易类型
     * @return 返回支付方对应接口的结果
     */
    public Map<String, Object> secondaryInterface(Object tradeNoOrBillDate, String outTradeNoBillType, TransactionType transactionType) {
        if (transactionType == AliTransactionType.DOWNLOADBILL){
            if (tradeNoOrBillDate instanceof  Date){
                return downloadbill((Date) tradeNoOrBillDate, outTradeNoBillType);
            }
            throw new PayErrorException(new PayException("failure", "非法类型异常:" + tradeNoOrBillDate.getClass()));
        }
        if (!(tradeNoOrBillDate instanceof  String)){
            throw new PayErrorException(new PayException("failure", "非法类型异常:" + tradeNoOrBillDate.getClass()));
        }
        //获取公共参数
        Map<String, Object> parameters = getPublicParameters(transactionType);
        //设置请求参数的集合
        parameters.put("biz_content", getContentToJson(tradeNoOrBillDate.toString(), outTradeNoBillType));
        //设置签名
        setSign(parameters);
        return requestTemplate.getForObject(QUERY_REQ_URL + "?" + UriVariables.getMapToParameters(parameters), JSONObject.class);
    }

    /**
     * 转账
     * @param order 转账订单
     * @return 对应的转账结果
     */
    public Map<String, Object> transfer(TransferOrder order) {
        return null;
    }
    /**
     * 获取biz_content。请求参数的集合 不包含下载账单
     * @param tradeNo 支付平台订单号
     * @param outTradeNo 商户单号
     * @param bizContent  请求参数的集合
     * @return 请求参数的集合 不包含下载账单
     */
    private  Map<String, Object> getBizContent(String tradeNo, String outTradeNo,  Map<String, Object> bizContent){
        if (null == bizContent){
            bizContent = new TreeMap<>();
        }
        if (null != outTradeNo){
            bizContent.put("out_trade_no", outTradeNo);
        }
        if (null != tradeNo){
            bizContent.put("trade_no", tradeNo);
        }
        return bizContent;
    }

    /**
     * 获取biz_content。不包含下载账单
     * @param tradeNo 支付平台订单号
     * @param outTradeNo 商户单号
     * @return 请求参数的集合 不包含下载账单
     */
    private String getContentToJson(String tradeNo, String outTradeNo){
        return JSON.toJSONString(getBizContent(tradeNo, outTradeNo, null));
    }
}
