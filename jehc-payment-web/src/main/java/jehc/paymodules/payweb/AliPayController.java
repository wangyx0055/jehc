package jehc.paymodules.payweb;
import jehc.paymodules.ali.apidao.AliPayConfigDaoImpl;
import jehc.paymodules.ali.apiservice.AliPayServiceImpl;
import jehc.paymodules.ali.model.AliTransactionType;
import jehc.paymodules.base.model.*;
import jehc.paymodules.base.model.query.QueryOrder;
import jehc.paymodules.base.service.PayService;
import jehc.paymodules.base.util.http.HttpConfigDb;
import jehc.paymodules.base.util.http.UriVariables;
import jehc.paymodules.base.util.sign.SignUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * 发起支付入口
 */
@RestController
@RequestMapping("ali")
public class AliPayController {
    private PayService service = null;
    @PostConstruct
    public void init() {
        AliPayConfigDaoImpl aliPayConfigDaoImpl = new AliPayConfigDaoImpl();
        aliPayConfigDaoImpl.setPid("2088102169916436");
        aliPayConfigDaoImpl.setAppId("2016080400165436");
        aliPayConfigDaoImpl.setKeyPublic("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB");
        aliPayConfigDaoImpl.setKeyPrivate("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKroe/8h5vC4L6T+B2WdXiVwGsMvUKgb2XsKix6VY3m2wcf6tyzpNRDCNykbIwGtaeo7FshN+qZxdXHLiIam9goYncBit/8ojfLGy2gLxO/PXfzGxYGs0KsDZ+ryVPPmE34ZZ8jiJpR0ygzCFl8pN3QJPJRGTJn5+FTT9EF/9zyZAgMBAAECgYAktngcYC35u7cQXDk+jMVyiVhWYU2ULxdSpPspgLGzrZyG1saOcTIi/XVX8Spd6+B6nmLQeF/FbU3rOeuD8U2clzul2Z2YMbJ0FYay9oVZFfp5gTEFpFRTVfzqUaZQBIjJe/xHL9kQVqc5xHlE/LVA27/Kx3dbC35Y7B4EVBDYAQJBAOhsX8ZreWLKPhXiXHTyLmNKhOHJc+0tFH7Ktise/0rNspojU7o9prOatKpNylp9v6kux7migcMRdVUWWiVe+4ECQQC8PqsuEz7B0yqirQchRg1DbHjh64bw9Kj82EN1/NzOUd53tP9tg+SO97EzsibK1F7tOcuwqsa7n2aY48mQ+y0ZAkBndA2xcRcnvOOjtAz5VO8G7R12rse181HjGfG6AeMadbKg30aeaGCyIxN1loiSfNR5xsPJwibGIBg81mUrqzqBAkB+K6rkaPXJR9XtzvdWb/N3235yPkDlw7Z4MiOVM3RzvR/VMDV7m8lXoeDde2zQyeMOMYy6ztwA6WgE1bhGOnQRAkEAouUBv1sVdSBlsexX15qphOmAevzYrpufKgJIRLFWQxroXMS7FTesj+f+FmGrpPCxIde1dqJ8lqYLTyJmbzMPYw==");
        aliPayConfigDaoImpl.setNotifyUrl("http://pay.egzosn.com/payBack.json");
        aliPayConfigDaoImpl.setReturnUrl("http://pay.egzosn.com/payBack.html");
        aliPayConfigDaoImpl.setSignType(SignUtils.RSA.name());
        aliPayConfigDaoImpl.setSeller("2088102169916436");
        aliPayConfigDaoImpl.setInputCharset("utf-8");
        //是否为测试账号，沙箱环境
        aliPayConfigDaoImpl.setTest(true);
        //请求连接池配置
        HttpConfigDb httpConfigDb = new HttpConfigDb();
        //最大连接数
        httpConfigDb.setMaxTotal(20);
        //默认的每个路由的最大连接数
        httpConfigDb.setDefaultMaxPerRoute(10);
        service =  new AliPayServiceImpl(aliPayConfigDaoImpl, httpConfigDb);
    }
    /**
     * 跳到支付页面
     * 针对实时支付,即时付款
     * @param price       金额
     * @return 跳到支付页面
     */
    @RequestMapping(value = "toPay.html", produces = "text/html;charset=UTF-8")
    public String toPay( BigDecimal price) {
        //及时收款
        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), AliTransactionType.DIRECT);
        //WAP
//        PayOrder order = new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), AliTransactionType.WAP);

        Map orderInfo = service.orderInfo(order);
        return service.buildRequest(orderInfo, MethodType.POST);
    }
    /**
     * 获取支付预订单信息
     * @return 支付预订单信息
     */
    @RequestMapping("app")
    public Map<String, Object> app() {
        Map<String, Object> data = new HashMap<>();
        data.put("code", 0);
        PayOrder order = new PayOrder("订单title", "摘要", new BigDecimal(0.01), UUID.randomUUID().toString().replace("-", ""));
        //App支付
        order.setTransactionType(AliTransactionType.APP);
        data.put("orderInfo", UriVariables.getMapToParameters(service.orderInfo(order)));
        return data;
    }
    /**
     * 获取二维码图像
     * 二维码支付
     * @param price金额
     * @return 二维码图像
     */
    @RequestMapping(value = "toQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
    public byte[] toWxQrPay( BigDecimal price) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(service.genQrPay( new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, System.currentTimeMillis()+"", AliTransactionType.SWEEPPAY)), "JPEG", baos);
        return baos.toByteArray();
    }

    /**
     * 刷卡付,pos主动扫码付款(条码付)
     * @param authCode授权码，条码等
     * @param price金额
     * @return 支付结果
     */
    @RequestMapping(value = "microPay")
    public Map<String, Object> microPay(BigDecimal price, String authCode) throws IOException {
        //获取对应的支付账户操作工具（可根据账户id）
        //条码付
        PayOrder order = new PayOrder("huodull order", "huodull order", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), AliTransactionType.BAR_CODE);
           //声波付
//        PayOrder order = new PayOrder("huodull order", "huodull order", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), AliTransactionType.WAVE_CODE);
        //设置授权码，条码等
        order.setAuthCode(authCode);
        //支付结果
        Map<String, Object> params = service.microPay(order);
        //校验
        if (service.verify(params)) {
            //支付校验通过后的处理
            //......业务逻辑处理块........
        }
        //这里开发者自行处理
        return params;
    }
    /**
     * 支付回调地址
     * @param request
     * @return
     */
    @RequestMapping(value = "payBack.json")
    public String payBack(HttpServletRequest request) throws IOException {
        //获取支付方返回的对应参数
        Map<String, Object> params = service.getParameter2Map(request.getParameterMap(), request.getInputStream());
        if (null == params) {
            return service.getPayOutMessage("fail", "失败").toMessage();
        }
        //校验
        if (service.verify(params)) {
            //这里处理业务逻辑
            //......业务逻辑处理块........
            return service.getPayOutMessage("success", "成功").toMessage();
        }
        return service.getPayOutMessage("fail", "失败").toMessage();
    }
    /**
     * 查询
     * @param order 订单的请求体
     * @return 返回查询回来的结果集，支付方原值返回
     */
    @RequestMapping("query")
    public Map<String, Object> query(QueryOrder order) {
        return service.query(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 交易关闭接口
     * @param order 订单的请求体
     * @return 返回支付方交易关闭后的结果
     */
    @RequestMapping("close")
    public Map<String, Object> close(QueryOrder order) {
        return service.close(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 申请退款接口
     * @param order 订单的请求体
     * @return 返回支付方申请退款后的结果
     */
    @RequestMapping("refund")
    public Map<String, Object> refund(RefundOrder order) {
        return service.refund(order);
    }
    /**
     * 查询退款
     * @param order 订单的请求体
     * @return 返回支付方查询退款后的结果
     */
    @RequestMapping("refundquery")
    public Map<String, Object> refundquery(QueryOrder order) {
        return service.refundquery(order.getTradeNo(), order.getOutTradeNo());
    }
    /**
     * 下载对账单
     * @param order 订单的请求体
     * @return 返回支付方下载对账单的结果
     */
    @RequestMapping("downloadbill")
    public Object downloadbill(QueryOrder order) {
        return service.downloadbill(order.getBillDate(), order.getBillType());
    }
    /**
     * 通用查询接口，根据 AliTransactionType 类型进行实现,此接口不包括退款
     * @param order 订单的请求体
     * @return 返回支付方对应接口的结果
     */
    @RequestMapping("secondaryInterface")
    public Map<String, Object> secondaryInterface(QueryOrder order) {
        TransactionType type = AliTransactionType.valueOf(order.getTransactionType());
        return service.secondaryInterface(order.getTradeNoOrBillDate(), order.getOutTradeNoBillType(), type);
    }
    /**
     * 转账
     * @param order 转账订单
     * @return 对应的转账结果
     */
    @RequestMapping("transfer")
    public Map<String, Object> transfer(TransferOrder order) {
//        order.setOutNo("转账单号");
//        order.setPayeeAccount("收款方账户,支付宝登录号，支持邮箱和手机号格式");
//        order.setAmount(new BigDecimal(10));
//        order.setPayerName("付款方姓名, 非必填");
//        order.setPayeeName("收款方真实姓名, 非必填");
//        order.setRemark("转账备注, 非必填");
        return service.transfer(order);
    }
    /**
     * 转账查询
     * @param outNo   商户转账订单号
     * @param tradeNo 支付平台转账订单号
     * @return 对应的转账订单
     */
    @RequestMapping("transferQuery")
    public Map<String, Object> transferQuery(String outNo, String tradeNo) {
        return service.transferQuery(outNo, tradeNo);
    }
}
