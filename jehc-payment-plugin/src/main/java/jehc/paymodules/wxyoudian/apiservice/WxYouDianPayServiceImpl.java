package jehc.paymodules.wxyoudian.apiservice;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import jehc.paymodules.base.dao.PayConfigDao;
import jehc.paymodules.base.model.MethodType;
import jehc.paymodules.base.model.PayMessage;
import jehc.paymodules.base.model.PayOrder;
import jehc.paymodules.base.model.PayOutMessage;
import jehc.paymodules.base.model.RefundOrder;
import jehc.paymodules.base.model.TransactionType;
import jehc.paymodules.base.service.impl.PayServiceImpl;
import jehc.paymodules.base.util.MatrixToImageWriter;
import jehc.paymodules.base.util.exception.PayErrorException;
import jehc.paymodules.base.util.exception.entity.PayError;
import jehc.paymodules.base.util.http.HttpConfigDb;
import jehc.paymodules.base.util.sign.SignUtils;
import jehc.paymodules.base.util.str.StringUtils;
import jehc.paymodules.wxyoudian.model.YdPayError;
import jehc.paymodules.wxyoudian.model.YoudianTransactionType;
/**
 * 友店支付服务
 * @author Administrator
 *
 */
public class WxYouDianPayServiceImpl extends PayServiceImpl {
    protected static final Log LOG = LogFactory.getLog(WxYouDianPayServiceImpl.class);
    private final static String URL = "http://life.51youdian.com/Api/CheckoutCounter/";
    /**
     * 获取请求token
     * @return 授权令牌
     */
    public String getAccessToken()  {
        try {
            return getAccessToken(false);
        } catch (PayErrorException e) {
            throw e;
        }
    }
    /**
     * 获取授权令牌
     * @param forceRefresh 是否重新获取， true重新获取
     * @return 新的授权令牌
     * @throws PayErrorException 支付异常
     */
    public String getAccessToken(boolean forceRefresh) throws PayErrorException {
        Lock lock = payConfigDao.getAccessTokenLock();
        try {
            lock.lock();
            if (forceRefresh) {
                payConfigDao.expireAccessToken();
            }
            if (payConfigDao.isAccessTokenExpired()) {
                if (null == payConfigDao.getAccessToken()){
                    login();
                    return payConfigDao.getAccessToken();
                }
                String apbNonce = SignUtils.randomStr();
                StringBuilder param = new StringBuilder().append("access_token=").append(payConfigDao.getAccessToken());
                String sign = createSign(param.toString() + apbNonce, payConfigDao.getInputCharset());
                param.append("&apb_nonce=").append(apbNonce).append("&sign=").append(sign);
                JSONObject json =  execute(getUrl(YoudianTransactionType.RESET_LOGIN) + "?" +  param.toString(), MethodType.GET, null );
                int errorcode = json.getIntValue("errorcode");
                if (0 == errorcode){
                    payConfigDao.updateAccessToken(payConfigDao.getAccessToken(), 7200);
                }else {
                    throw  new PayErrorException(new YdPayError(errorcode, json.getString("msg"), json.toJSONString()));
                }
            }
        } finally {
            lock.unlock();
        }
        return payConfigDao.getAccessToken();
    }


    /**
     * 登录 并获取登陆信息(授权码)
     * @return  登陆信息
     * @throws PayErrorException 支付异常
     */
     public JSONObject login() throws PayErrorException {
         TreeMap<String, String> data = new TreeMap<>();
         data.put("username",  payConfigDao.getSeller());
         data.put("password", payConfigDao.getKeyPrivate());
         String apbNonce = SignUtils.randomStr();
//         1、确定请求主体为用户登录，即需要传登录的用户名username和密码password并且要生成唯一的随机数命名为apb_nonce，长度为32位
//         2、将所有的参数集进行key排序
//         3、将排序后的数组从起始位置拼接成字符串如：password=XXXXXXXusername=XXXXX
//         4、将拼接出来的字符串连接上apb_nonce的值即AAAAAAAAAA。再连接  password=XXXXXXXusername=XXXXXAAAAAAAAAA
         String sign = createSign(SignUtils.parameterText(data, "") + apbNonce, payConfigDao.getInputCharset());
         String queryParam =  SignUtils.parameterText(data) +  "&apb_nonce=" + apbNonce + "&sign=" + sign;

         JSONObject json = execute(getUrl(YoudianTransactionType.LOGIN) + "?" + queryParam, MethodType.GET, null);
         payConfigDao.updateAccessToken(json.getString("access_token"), json.getLongValue("viptime"));
         return json;
    }

    /**
     * 回调校验
     * @param params 回调回来的参数集
     * @return 签名校验 true通过
     */
    public boolean verify(Map<String, Object> params) {
        if (!"SUCCESS".equals(params.get("return_code"))){
            LOG.debug(String.format("友店微信支付异常：return_code=%s,参数集=%s", params.get("return_code"), params));
            return false;
        }
        if(params.get("sign") == null) {LOG.debug("友店微信支付异常：签名为空！out_trade_no=" + params.get("out_trade_no"));}

        try {
            return signVerify(params, (String) params.get("sign")) && verifySource((String)params.get("out_trade_no"));
        } catch (PayErrorException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }
    /**
     * 根据反馈回来的信息，生成签名结果
     * @param params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    public boolean signVerify(Map<String, Object> params, String sign) {
        return SignUtils.valueOf(payConfigDao.getSignType()).verify(params, sign, "&key=" + payConfigDao.getKeyPublic(), payConfigDao.getInputCharset());
    }

    /**
     * 验证链接来源是否有效
     * 校验数据来源
     * @param id  id 商户订单号（扫码收款返回的order_sn）
     * @return true通过
     */
    public boolean verifySource(String id) {
        try {
            JSONObject jsonObject = (JSONObject)query(id, null);
            return 0 == jsonObject.getIntValue("errorcode");
        }catch (PayErrorException e){
            if (Integer.parseInt(e.getPayError().getErrorCode()) >= 400){
                throw e;
            }
            return false;
        }
    }

    /**
     * 向友店端发送请求，在这里执行的策略是当发生access_token过期时才去刷新，然后重新执行请求，而不是全局定时请求
     * @param uri 请求地址
     * @param method 请求方式 @see MethodType#GET @see MethodType#POST
     * @param request 请求内容，GET无需
     * @return 请求成功后的结果
     * @throws PayErrorException 支付异常
     */
    public JSONObject execute(String uri,  MethodType method, Object request) throws PayErrorException {
        int retryTimes = 0;
        do {
	        try {
	            JSONObject result = requestTemplate.doExecute(uri, request, JSONObject.class, method);
	            if ( 0 != result.getIntValue("errorcode")){
	              throw new PayErrorException(new YdPayError(result.getIntValue("errorcode"), result.getString("msg"), result.toJSONString()));
	            }
	            return  result;
	        }catch (PayErrorException e){
	            PayError error = e.getPayError();
	            if ("401".equals(error.getErrorCode()) ||  "500".equals(error.getErrorCode())) {
	                try {
	                    int sleepMillis = retrySleepMillis * (1 << retryTimes);
	                    LOG.debug(String.format("友店微信系统繁忙，(%s)ms 后重试(第%s次)", sleepMillis, retryTimes + 1));
	                    Thread.sleep(sleepMillis);
	                } catch (InterruptedException e1) {
	                    throw new PayErrorException(new YdPayError(-1, "友店支付服务端重试失败", e1.getMessage()));
	                }
	                // 强制设置wxMpConfigDao它的access token过期了，这样在下一次请求里就会刷新access token
	                payConfigDao.expireAccessToken();
	                //进行重新登陆授权
	                login();
	            }else {
	                throw e;
	            }
	        }
        } while (++retryTimes < maxRetryTimes);
        throw new PayErrorException(new YdPayError(-1, "友店微信服务端异常，超出重试次数"));
    }

    /**
     * 获取支付平台所需的订单信息
     * @param order 支付订单
     * @return 订单信息
     * @see PayOrder 支付订单信息
     */
    public JSONObject orderInfo(PayOrder order) {
        TreeMap<String, String> data = new TreeMap<>();
        data.put("access_token",  getAccessToken());
        data.put("paymoney", order.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        String apbNonce = SignUtils.randomStr();
        String sign = createSign(SignUtils.parameterText(data, "") + apbNonce, payConfigDao.getInputCharset());
        data.put("PayMoney", data.remove("paymoney"));
        String params =  SignUtils.parameterText(data) +  "&apb_nonce=" + apbNonce + "&sign=" + sign;
        try {
            JSONObject json = execute(getUrl(order.getTransactionType())+ "?" +  params, MethodType.GET, null);
            //友店比较特殊，需要在下完预订单后，自己存储 order_sn 对应 微信官方文档 out_trade_no
            order.setOutTradeNo(json.getString("order_sn"));
            return json;
        } catch (PayErrorException e) {
            throw  e;
        }
    }

    /**
     * 签名
     * @param content 需要签名的内容
     * @param characterEncoding 字符编码
     * 1、确定请求主体为用户登录，即需要传登录的用户名username和密码password并且要生成唯一的随机数命名为apb_nonce，长度为32位
     * 2、将所有的参数集进行key排序
 	 * 3、将排序后的数组从起始位置拼接成字符串如：password=XXXXXXXusername=XXXXX
 	 * 4、将拼接出来的字符串连接上apb_nonce的值即AAAAAAAAAA。再连接  password=XXXXXXXusername=XXXXXAAAAAAAAAA
     * @return 签名结果
     */
    public String createSign(String content, String characterEncoding) {
        return  SignUtils.valueOf(payConfigDao.getSignType().toUpperCase()).createSign(content, "&source=http://life.51youdian.com", characterEncoding);
    }

    /**
     * 将请求参数或者请求流转化为 Map
     * @param parameterMap 请求参数
     * @param is 请求流
     * @return 获得回调的请求参数
     */
    public Map<String, Object> getParameter2Map(Map<String, String[]> parameterMap, InputStream is) {
        Map<String, Object> params = new TreeMap<String, Object>();
        for (Iterator iter = parameterMap.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr.trim());
        }
        return params;
    }

    /**
     * 具体需要返回的数据为
     * return_code 返回码只有SUCCESS和FAIL
     * return_msg 返回具体信息
     * nonce_str 您的服务器新生成随机生成32位字符串
     * sign 为签名，签名规则是您需要发送的所有数据(除了sign)按照字典升序排列后加上&amp;key=xxxxxxxx您的密钥后md5加密，最后转成小写
     * 最后把得到的所有需要返回的数据用json格式化成json对象格式如下
     * {&quot;return_code&quot;:&quot;SUCCESS&quot;,&quot;return_msg&quot;:&quot;ok&quot;,&quot;nonce_str&quot;:&quot;dddddddddddddddddddd’,’sign’:’sdddddddddddddddddd&quot;}
     *
     * @param code    return_code
     * @param message return_msg
     *
     * @return 返回输出消息
     */
    public PayOutMessage getPayOutMessage(String code, String message) {
        Map<String, Object> builder = new TreeMap<>();
        builder.put("return_code", code.toUpperCase());
        builder.put("return_msg", message);
        builder.put("nonce_str", SignUtils.randomStr());
        String sgin = SignUtils.valueOf(payConfigDao.getSignType()).sign(builder, "&key=" + payConfigDao.getKeyPrivate(), payConfigDao.getInputCharset());
        return PayOutMessage.TEXT().content("{\"return_code\":\""+builder.get("return_code")+"\",\"return_msg\":\""+builder.get("return_msg")+"\",\"nonce_str\":\""+builder.get("nonce_str")+"\",\"sign\":\""+ sgin +"\"}").build();
    }
    /**
     * 获取成功输出消息，用户返回给支付端
     * 主要用于拦截器中返回
     * @param payMessage 支付回调消息
     * @return 返回输出消息
     */
    public PayOutMessage successPayOutMessage(PayMessage payMessage) {
          return  PayOutMessage.TEXT().content(JSON.toJSONString(payMessage.getPayMessage())).build();
    }

    /**
     * 针对web端的即时付款
     * 暂未实现或无此功能
     * @param orderInfo 发起支付的订单信息
     * @param method 请求方式  &quot;post&quot; &quot;get&quot;,
     * @return 获取输出消息，用户返回给支付端, 针对于web端
     * @see MethodType 请求类型
     */
    public String buildRequest(Map<String, Object> orderInfo, MethodType method) {
        throw new UnsupportedOperationException();
    }

    public BufferedImage genQrPay(PayOrder order) {
        JSONObject orderInfo = orderInfo(order);
        return  MatrixToImageWriter.writeInfoToJpgBuff((String) orderInfo.get("code_url"));
    }

    /**
     * 暂未实现或无此功能
     * @param order 发起支付的订单信息
     * @return 返回支付结果
     */
    public Map<String, Object> microPay(PayOrder order) {
        throw new UnsupportedOperationException();
    }

    /**
     * 交易查询接口
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回查询回来的结果集，支付方原值返回
     */
    public Map<String, Object> query(String tradeNo, String outTradeNo) {
        String apbNonce = SignUtils.randomStr();
        TreeMap<String, String> data = new TreeMap<>();
        data.put("access_token",  payConfigDao.getAccessToken());
        if (StringUtils.isEmpty(tradeNo)){
            data.put("order_sn", outTradeNo);
        }else {
            data.put("order_sn", tradeNo);
        }
        String sign = createSign(SignUtils.parameterText(data, "") + apbNonce, payConfigDao.getInputCharset());
        String queryParam =  SignUtils.parameterText(data) +  "&apb_nonce=" + apbNonce + "&sign=" + sign;
        JSONObject jsonObject = execute(getUrl(YoudianTransactionType.NATIVE_STATUS) + "?"  +  queryParam, MethodType.GET, null);
        return jsonObject;
    }

    public Map<String, Object> close(String tradeNo, String outTradeNo) {
        return new HashMap<>(0);
    }
    public Map<String, Object> refund(String tradeNo, String outTradeNo, BigDecimal refundAmount, BigDecimal totalAmount) {
        return refund(new RefundOrder(tradeNo, outTradeNo,refundAmount, totalAmount));
    }
    public Map<String, Object> refund(RefundOrder refundOrder) {
        String apbNonce = SignUtils.randomStr();
        TreeMap<String, String> data = new TreeMap<>();
        data.put("access_token",  payConfigDao.getAccessToken());

        if (StringUtils.isEmpty(refundOrder.getOutTradeNo())){
            data.put("order_sn", refundOrder.getOutTradeNo());
        }else {
            data.put("order_sn", refundOrder.getTradeNo());
        }
        //支付类型刷卡为3扫码为4
        data.put("type", "4");
        data.put("refund_fee", refundOrder.getRefundAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        String sign = createSign(SignUtils.parameterText(data, "") + apbNonce, payConfigDao.getInputCharset());
        String queryParam =  SignUtils.parameterText(data) +  "&apb_nonce=" + apbNonce + "&sign=" + sign;
        JSONObject jsonObject = execute(getUrl(YoudianTransactionType.NATIVE_STATUS) + "?"  +  queryParam, MethodType.GET, null);
        return jsonObject;
    }
    public Map<String, Object> refundquery(String tradeNo, String outTradeNo) {
        return new HashMap<>(0);
    }
    public Map<String, Object>  downloadbill(Date billDate, String billType) {
        return new HashMap<>(0);
    }
    /**
     * @param tradeNoOrBillDate  支付平台订单号或者账单类型， 具体请
     *                           类型为{@link String }或者 {@link Date }，类型须强制限制，类型不对应则抛出异常{@link PayErrorException}
     * @param outTradeNoBillType 商户单号或者 账单类型
     * @param transactionType    交易类型
     * @return 返回支付方对应接口的结果
     */
    public Map<String, Object> secondaryInterface(Object tradeNoOrBillDate, String outTradeNoBillType, TransactionType transactionType) {
        throw new UnsupportedOperationException();
    }
    public WxYouDianPayServiceImpl(PayConfigDao payConfigDao) {
        super(payConfigDao);
    }
    public WxYouDianPayServiceImpl(PayConfigDao payConfigDao, HttpConfigDb configDao) {
        super(payConfigDao, configDao);
    }
    /**
     * 根据交易类型获取请求地址
     * @param type 交易类型
     * @return 请求地址
     */
    private String getUrl(TransactionType type){
        return URL + type.getMethod();
    }
}
