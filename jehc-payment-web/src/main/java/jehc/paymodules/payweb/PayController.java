package jehc.paymodules.payweb;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import jehc.paymentmodules.paymentmodel.PaymentAccount;
import jehc.paymodules.ali.apiservice.AliPayServiceImpl;
import jehc.paymodules.ali.model.AliTransactionType;
import jehc.paymodules.base.dao.PayConfigDao;
import jehc.paymodules.base.model.MethodType;
import jehc.paymodules.base.model.PayMessage;
import jehc.paymodules.base.model.PayOrder;
import jehc.paymodules.base.model.PayOutMessage;
import jehc.paymodules.base.model.RefundOrder;
import jehc.paymodules.base.model.TransactionType;
import jehc.paymodules.base.model.TransferOrder;
import jehc.paymodules.base.model.query.QueryOrder;
import jehc.paymodules.base.service.PayService;
import jehc.paymodules.base.util.MatrixToImageWriter;
import jehc.paymodules.base.util.http.UriVariables;
import jehc.paymodules.base.util.str.StringUtils;
import jehc.paymodules.payenum.PayType;
import jehc.paymodules.payservice.ApyAccountService;
import jehc.paymodules.payservice.PayResponse;
import jehc.paymodules.wx.model.WxTransactionType;

/**
 * 发起支付入口
 */
@RestController
@RequestMapping
public class PayController {
    @Resource
    private ApyAccountService service;
    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("/index.html");
    }
    /**
     * 跳到支付页面
     * 针对实时支付,即时付款
     * @param payId 账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param bankType 针对刷卡支付，卡的类型，类型值
     * @param price金额
     * @return 跳到支付页面
     */
    @RequestMapping(value = "toPay.html", produces = "text/html;charset=UTF-8")
    public String toPay(HttpServletRequest request,String payId, String transactionType, String bankType, BigDecimal price) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType(transactionType));
        // ------  微信H5使用----
        order.setSpbillCreateIp(request.getHeader("X-Real-IP"));
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        order.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ? requestURL.indexOf("/") : requestURL.length() ));
        //设置网页名称
        order.setWapName("在线充值");
        // ------  微信H5使用----
        //此处只有刷卡支付(银行卡支付)时需要
        if (StringUtils.isNotEmpty(bankType)) {
            order.setBankType(bankType);
        }
        Map orderInfo = payResponse.getPayService().orderInfo(order);
        return payResponse.getPayService().buildRequest(orderInfo, MethodType.POST);
    }

    /**
     * 跳到支付页面
     * 针对实时支付,即时付款
     * @return 跳到支付页面
     */
    @RequestMapping(value = "toWxPay.html", produces = "text/html;charset=UTF-8")
    public String toWxPay(HttpServletRequest request,String id) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(id);

        PayOrder order = new PayOrder("订单title", "摘要",  new BigDecimal(0.01) , UUID.randomUUID().toString().replace("-", ""),  WxTransactionType.MWEB);
        order.setSpbillCreateIp(request.getHeader("X-Real-IP"));
        StringBuffer requestURL = request.getRequestURL();
        //设置网页地址
        order.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ? requestURL.indexOf("/") : requestURL.length() ));
        //设置网页名称
        order.setWapName("在线充值");
        Map orderInfo = payResponse.getPayService().orderInfo(order);
        return payResponse.getPayService().buildRequest(orderInfo, MethodType.POST);
    }


    /**
     * 公众号支付
     * @param payId  账户id
     * @param openid openid
     * @param price 金额
     * @return 返回jsapi所需参数
     */
    @RequestMapping(value = "jsapi" )
    public Map toPay(String payId, String openid, BigDecimal price) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType("JSAPI"));
        order.setOpenid(openid);
        Map orderInfo = payResponse.getPayService().orderInfo(order);
        orderInfo.put("code", 0);
       return orderInfo;
    }
    /**
     * 刷卡付,pos主动扫码付款(条码付)
     * @param payId账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param authCode授权码，条码等
     * @param price金额
     * @return 支付结果
     */
    @RequestMapping(value = "microPay")
    public Map<String, Object> microPay(String payId, String transactionType, BigDecimal price, String authCode) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        PayOrder order = new PayOrder("huodull order", "huodull order", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType(transactionType));
        //设置授权码，条码等
        order.setAuthCode(authCode);
        //支付结果
        Map<String, Object> params = payResponse.getPayService().microPay(order);
        PayConfigDao storage = payResponse.getPayService().getPayConfigDao();
        //校验
        if (payResponse.getPayService().verify(params)) {
            PayMessage message = new PayMessage(params, storage.getPayType(), storage.getMsgType().name());
            //支付校验通过后的处理
            payResponse.getPayMessageRouter().route(message);
        }
        //这里开发者自行处理
        return params;
    }

    /**
     * 获取二维码图像
     * 二维码支付
     * @param payId账户id
     * @param transactionType 交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
     * @param price金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
    public byte[] toWxQrPay(String payId, String transactionType, BigDecimal price) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(payResponse.getPayService().genQrPay(new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, System.currentTimeMillis()+"", PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType(transactionType))), "JPEG", baos);
        return baos.toByteArray();
    }

    /**
     * 获取一码付二维码图像
     * 二维码支付
     * @param wxPayId 微信账户id
     * @param aliPayId支付宝id
     * @param price金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toWxAliQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
    public byte[] toWxAliQrPay(Integer wxPayId,Integer aliPayId, BigDecimal price, HttpServletRequest request) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里为需要生成二维码的地址
        StringBuffer url = request.getRequestURL();
        url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
         url .append("/toWxAliPay.html?");
        if (null != wxPayId){
            url.append("wxPayId=").append(wxPayId).append("&");
        }
        if (null != aliPayId){
            url.append("aliPayId=").append(aliPayId).append("&");
        }
        url.append("price=").append(price);
        ImageIO.write(MatrixToImageWriter.writeInfoToJpgBuff(url.toString()), "JPEG", baos);
        return baos.toByteArray();
    }

    /**
     * 支付宝与微信平台的判断 并进行支付的转跳
     * @param wxPayId 微信账户id
     * @param aliPayId支付宝id
     * @param price金额
     * @return 支付宝与微信平台的判断
     */
    @RequestMapping(value = "toWxAliPay.html", produces = "text/html;charset=UTF-8")
    public String toWxAliPay(String wxPayId,String aliPayId, BigDecimal price, HttpServletRequest request) throws IOException {
        StringBuilder html = new StringBuilder();
        //订单
        PayOrder payOrder = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, System.currentTimeMillis() + "");
        String ua = request.getHeader("user-agent");
        if(ua.contains("MicroMessenger")){
            payOrder.setTransactionType(WxTransactionType.NATIVE);
            PayService service = this.service.getPayResponse(wxPayId).getPayService();
            return String.format("<script type=\"text/javascript\">location.href=\"%s\"</script>",(String) service.orderInfo(payOrder).get("code_url"));
        }
        if(ua.contains("AlipayClient")){
            payOrder.setTransactionType(AliTransactionType.SWEEPPAY);
            AliPayServiceImpl service = (AliPayServiceImpl)this.service.getPayResponse(aliPayId).getPayService();
            JSONObject result = service.getHttpRequestTemplate().postForObject(service.getReqUrl() + "?" + UriVariables.getMapToParameters(service.orderInfo(payOrder)), null, JSONObject.class);
             result = result.getJSONObject("alipay_trade_precreate_response");
            return String.format("<script type=\"text/javascript\">location.href=\"%s\"</script>", result.getString("qr_code"));
        }
        return  String.format("<script type=\"text/javascript\">alert(\"请使用微信或者支付宝App扫码%s\");window.close();</script>", ua);
    }
    /**
     * 获取支付预订单信息
     * @param payId支付账户id
     * @param transactionType 交易类型
     * @return 支付预订单信息
     */
    @RequestMapping("getOrderInfo")
    public Map<String, Object> getOrderInfo(String payId, String transactionType, BigDecimal price) {
        //获取对应的支付账户操作工具（可根据账户id）
        PayResponse payResponse = service.getPayResponse(payId);
        Map<String, Object> data = new HashMap<>();
        data.put("code", 0);
        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType(transactionType));
        data.put("orderInfo", payResponse.getPayService().orderInfo(order));
        return data;
    }

    /**
     * 支付回调地址
     * @param request
     * @return 支付是否成功
     */
    @RequestMapping(value = "payBack{payId}.json")
    public String payBack(HttpServletRequest request, @PathVariable String payId) throws IOException {
        //根据账户id，获取对应的支付账户操作工具
        PayResponse payResponse = service.getPayResponse(payId);
        PayConfigDao storage = payResponse.getPayConfigDao();
        //获取支付方返回的对应参数
        Map<String, Object> params = payResponse.getPayService().getParameter2Map(request.getParameterMap(), request.getInputStream());
//        Map<String, Object> params = JSONObject.parseObject("{\"bizType\":\"000201\",\"signPubKeyCert\":\"-----BEGIN CERTIFICATE-----\\r\\nMIIEQzCCAyugAwIBAgIFEBJJZVgwDQYJKoZIhvcNAQEFBQAwWDELMAkGA1UEBhMC\\r\\nQ04xMDAuBgNVBAoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhv\\r\\ncml0eTEXMBUGA1UEAxMOQ0ZDQSBURVNUIE9DQTEwHhcNMTcxMTAxMDcyNDA4WhcN\\r\\nMjAxMTAxMDcyNDA4WjB3MQswCQYDVQQGEwJjbjESMBAGA1UEChMJQ0ZDQSBPQ0Ex\\r\\nMQ4wDAYDVQQLEwVDVVBSQTEUMBIGA1UECxMLRW50ZXJwcmlzZXMxLjAsBgNVBAMU\\r\\nJTA0MUBaMjAxNy0xMS0xQDAwMDQwMDAwOlNJR05AMDAwMDAwMDEwggEiMA0GCSqG\\r\\nSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDDIWO6AESrg+34HgbU9mSpgef0sl6avr1d\\r\\nbD/IjjZYM63SoQi3CZHZUyoyzBKodRzowJrwXmd+hCmdcIfavdvfwi6x+ptJNp9d\\r\\nEtpfEAnJk+4quriQFj1dNiv6uP8ARgn07UMhgdYB7D8aA1j77Yk1ROx7+LFeo7rZ\\r\\nDdde2U1opPxjIqOPqiPno78JMXpFn7LiGPXu75bwY2rYIGEEImnypgiYuW1vo9UO\\r\\nG47NMWTnsIdy68FquPSw5FKp5foL825GNX3oJSZui8d2UDkMLBasf06Jz0JKz5AV\\r\\nblaI+s24/iCfo8r+6WaCs8e6BDkaijJkR/bvRCQeQpbX3V8WoTLVAgMBAAGjgfQw\\r\\ngfEwHwYDVR0jBBgwFoAUz3CdYeudfC6498sCQPcJnf4zdIAwSAYDVR0gBEEwPzA9\\r\\nBghggRyG7yoBATAxMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNmY2EuY29tLmNu\\r\\nL3VzL3VzLTE0Lmh0bTA5BgNVHR8EMjAwMC6gLKAqhihodHRwOi8vdWNybC5jZmNh\\r\\nLmNvbS5jbi9SU0EvY3JsMjQ4NzIuY3JsMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQU\\r\\nmQQLyuqYjES7qKO+zOkzEbvdFwgwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUF\\r\\nBwMEMA0GCSqGSIb3DQEBBQUAA4IBAQAujhBuOcuxA+VzoUH84uoFt5aaBM3vGlpW\\r\\nKVMz6BUsLbIpp1ho5h+LaMnxMs6jdXXDh/du8X5SKMaIddiLw7ujZy1LibKy2jYi\\r\\nYYfs3tbZ0ffCKQtv78vCgC+IxUUurALY4w58fRLLdu8u8p9jyRFHsQEwSq+W5+bP\\r\\nMTh2w7cDd9h+6KoCN6AMI1Ly7MxRIhCbNBL9bzaxF9B5GK86ARY7ixkuDCEl4XCF\\r\\nJGxeoye9R46NqZ6AA/k97mJun//gmUjStmb9PUXA59fR5suAB5o/5lBySZ8UXkrI\\r\\npp/iLT8vIl1hNgLh0Ghs7DBSx99I+S3VuUzjHNxL6fGRhlix7Rb8\\r\\n-----END CERTIFICATE-----\",\"orderId\":\"20171213224128\",\"signature\":\"l8xBYSoMNzt01DDa9/JYcrQKWxN5tasUgSxf6NNsQK5t+DqMr2G9qhHXnDg5bEzeRyTFP4bM3htX9RTRhXYDy7EEsL46ZD4ib5I6mp2wXx+26zscUcLdJUiddkY5eFvQK4tPC8blw7Y6p858yiVJpHgbOK3cONhS7vwPJtK2jMbkY+GATu3aZ4iygkQc75cG+EW8nJQVwLNh7q9A6A6II18EFxR7XubdlIHXv/InVaS6ux8Wh2nmQlhRRnLtHq1ri7v1QPlu2FzM+kaf7/fn61iGr8zEPj62NzWDXue62LUfb4kTRgdkcJnfJBJl8vjZ/w93UtsnK3zjzJC/Nu+wCw==\",\"txnSubType\":\"01\",\"traceNo\":\"492156\",\"accNo\":\"6221********0000\",\"settleAmt\":\"1000\",\"settleCurrencyCode\":\"156\",\"settleDate\":\"1213\",\"txnType\":\"01\",\"encoding\":\"UTF-8\",\"version\":\"5.1.0\",\"queryId\":\"511712132241284921568\",\"accessType\":\"0\",\"exchangeRate\":\"0\",\"respMsg\":\"success\",\"traceTime\":\"1213224128\",\"txnTime\":\"20171213224128\",\"merId\":\"777290058154626\",\"currencyCode\":\"156\",\"respCode\":\"00\",\"signMethod\":\"01\",\"txnAmt\":\"1000\"}");
        if (null == params) {
            return payResponse.getPayService().getPayOutMessage("fail", "失败").toMessage();
        }
        //校验
        if (payResponse.getPayService().verify(params)) {
            PayMessage message = new PayMessage(params, storage.getPayType(), storage.getMsgType().name());
            PayOutMessage outMessage = payResponse.getPayMessageRouter().route(message);
            return outMessage.toMessage();
        }
        return payResponse.getPayService().getPayOutMessage("fail", "失败").toMessage();
    }

    /**
     * 查询
     * @param order 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
    @RequestMapping("query")
    public Map<String, Object> query(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getPayService().query(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 查询
     * @param order 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
/*    @RequestMapping("unionRefundOrConsumeUndo")
    public Map<String, Object> unionQuery(UnionQueryOrder order,String transactionType) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        UnionPayService service =  (UnionPayService)payResponse.getPayService();

        return service.unionRefundOrConsumeUndo(order,UnionTransactionType.valueOf(transactionType));
    }*/

    /**
     * 交易关闭接口
     * @param order 订单的请求体
     * @return 返回支付方交易关闭后的结果
     */
    @RequestMapping("close")
    public Map<String, Object> close(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getPayService().close(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 申请退款接口
     * @param order 订单的请求体
     * @return 返回支付方申请退款后的结果
     */
    @RequestMapping("refund")
    public Map<String, Object> refund(String payId ,RefundOrder order) {
        PayResponse payResponse = service.getPayResponse(payId);
//        return payResponse.getPayService().refund(order.getTradeNo(), order.getOutTradeNo(), order.getRefundAmount(), order.getTotalAmount());
        return payResponse.getPayService().refund(order);
    }

    /**
     * 查询退款
     * @param order 订单的请求体
     * @return 返回支付方查询退款后的结果
     */
    @RequestMapping("refundquery")
    public Map<String, Object> refundquery(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getPayService().refundquery(order.getTradeNo(), order.getOutTradeNo());
    }

    /**
     * 下载对账单
     * @param order 订单的请求体
     * @return 返回支付方下载对账单的结果
     */
    @RequestMapping("downloadbill")
    public Object downloadbill(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        return payResponse.getPayService().downloadbill(order.getBillDate(), order.getBillType());
    }
    /**
     * 通用查询接口，根据 TransactionType 类型进行实现,此接口不包括退款
     * @param order 订单的请求体
     * @return 返回支付方对应接口的结果
     */
    @RequestMapping("secondaryInterface")
    public Map<String, Object> secondaryInterface(QueryOrder order) {
        PayResponse payResponse = service.getPayResponse(order.getPayId());
        TransactionType type = PayType.valueOf(payResponse.getPayConfigDao().getPayType()).getTransactionType(order.getTransactionType());
        return payResponse.getPayService().secondaryInterface(order.getTradeNoOrBillDate(), order.getOutTradeNoBillType(), type);
    }

    /**
     * 转账
     * @param order 转账订单
     * @return 对应的转账结果
     */
    @RequestMapping("transfer")
    public Map<String, Object> transfer(String payId, TransferOrder order) {
        PayService service = this.service.getPayResponse(payId).getPayService();
        return service.transfer(order);
    }
    /**
     * 转账查询
     * @param outNo   商户转账订单号
     * @param tradeNo 支付平台转账订单号
     * @return 对应的转账订单
     */
    @RequestMapping("transferQuery")
    public Map<String, Object> transferQuery(String payId, String outNo, String tradeNo) {
        PayService service = this.service.getPayResponse(payId).getPayService();
        return service.transferQuery(outNo, tradeNo);
    }
}
