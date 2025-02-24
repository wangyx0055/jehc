package jehc.paymodules.union.apiservice;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import jehc.paymodules.base.util.apiservice.Callback;
import jehc.paymodules.base.util.builder.PayTextOutMessage;
import jehc.paymodules.base.util.exception.PayErrorException;
import jehc.paymodules.base.util.exception.entity.PayException;
import jehc.paymodules.base.util.http.HttpConfigDb;
import jehc.paymodules.base.util.http.UriVariables;
import jehc.paymodules.base.util.sign.SignUtils;
import jehc.paymodules.base.util.sign.encrypt.RSA;
import jehc.paymodules.base.util.sign.encrypt.RSA2;
import jehc.paymodules.union.apidao.UnionPayConfigDaoImpl;
import jehc.paymodules.union.model.SDKConstants;
import jehc.paymodules.union.model.UnionTransactionType;

public class UnionPayServiceImpl extends PayServiceImpl {
    private static final Log log = LogFactory.getLog(UnionPayServiceImpl.class);
    /**
     * 测试域名
     */
    private static final String TEST_BASE_DOMAIN = "test.95516.com";
    /**
     * 正式域名
     */
    private static final String RELEASE_BASE_DOMAIN = "95516.com";
    /**
     * 交易请求地址
     */
    private static final String FRONT_TRANS_URL= "https://gateway.%s/gateway/api/frontTransReq.do";
    private static final String BACK_TRANS_URL= "https://gateway.%s/gateway/api/backTransReq.do";
    private static final String SINGLE_QUERY_URL= "https://gateway.%s/gateway/api/queryTrans.do";
    private static final String BATCH_TRANS_URL= "https://gateway.%s/gateway/api/batchTrans.do";
    private static final String FILE_TRANS_URL= "https://filedownload.%s/";
    private static final String APP_TRANS_URL= "https://gateway.%s/gateway/api/appTransReq.do";
    private static final String CARD_TRANS_URL= "https://gateway.%s/gateway/api/cardTransReq.do";

    /**
     * 构造函数
     * @param payConfigDao 支付配置
     */
    public UnionPayServiceImpl (PayConfigDao payConfigDao) {
        super(payConfigDao);
    }
    public UnionPayServiceImpl (PayConfigDao payConfigDao, HttpConfigDb configDao) {
        super(payConfigDao, configDao);
    }

    /**
     * 根据是否为沙箱环境进行获取请求地址
     * @return 请求地址
     */
    public String getReqUrl() {
        return (payConfigDao.isTest() ? TEST_BASE_DOMAIN : RELEASE_BASE_DOMAIN) ;
    }

    public  String getFrontTransUrl () {
        return  String.format(FRONT_TRANS_URL, getReqUrl());
    }

    public  String getBackTransUrl () {
        return String.format(BACK_TRANS_URL, getReqUrl());
    }

    public  String getSingleQueryUrl () {
        return String.format(SINGLE_QUERY_URL,getReqUrl());
    }


    public  String getFileTransUrl () {
        return String.format(FILE_TRANS_URL, getReqUrl());
    }

    /**
     * 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改
     * @return 返回参数集合
     */
    private Map<String, Object> getCommonParam() {
        Map<String, Object> params = new TreeMap<>();
        UnionPayConfigDaoImpl configDao = (UnionPayConfigDaoImpl) payConfigDao;
        //银联接口版本
        params.put(SDKConstants.param_version, configDao.getVersion());
        //编码方式
        params.put(SDKConstants.param_encoding, payConfigDao.getInputCharset().toUpperCase());
        //商户代码
        params.put(SDKConstants.param_merId, payConfigDao.getPid());

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        //订单发送时间
        params.put(SDKConstants.param_txnTime, df.format(System.currentTimeMillis()));
        //后台通知地址
        params.put(SDKConstants.param_backUrl, payConfigDao.getNotifyUrl());
        //交易币种
        params.put(SDKConstants.param_currencyCode, "156");
        //接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
        params.put(SDKConstants.param_accessType, configDao.getAccessType());
        return params;
    }

    /**
     * 回调校验
     * @param result 回调回来的参数集
     * @return 签名校验 true通过
     */
    public boolean verify(Map<String, Object> result) {
        if (null == result || result.get(SDKConstants.param_signature) == null) {
            log.debug("银联支付验签异常：params：" + result);
            return false;
        }
        return this.signVerify(result, (String) result.get(SDKConstants.param_signature));
    }

    /**
     * 签名校验
     * @param params 参数集
     * @param sign   签名原文
     * @return 签名校验 true通过
     */
    public boolean signVerify (Map<String, Object> params, String sign) {
        SignUtils signUtils = SignUtils.valueOf(payConfigDao.getSignType());
        String data = SignUtils.parameterText(params, "&", "signature");
        switch (signUtils){
            case RSA:
                data = SignUtils.SHA1.createSign(data,"", payConfigDao.getInputCharset());
                return RSA.verify(data, sign, verifyCertificate(genCertificateByStr((String)params.get(SDKConstants.param_signPubKeyCert))).getPublicKey(), payConfigDao.getInputCharset());
            case RSA2:
                data = SignUtils.SHA256.createSign(data,"", payConfigDao.getInputCharset());
                return RSA2.verify(data, sign, verifyCertificate(genCertificateByStr((String)params.get(SDKConstants.param_signPubKeyCert))).getPublicKey(), payConfigDao.getInputCharset());
            case SHA1:
            case SHA256:
            case SM3:
                String before = signUtils.createSign(payConfigDao.getKeyPublic(),"",payConfigDao.getInputCharset());
                return signUtils.verify(data, sign, "&" + before, payConfigDao.getInputCharset());
            default:
                return false;
        }
    }

    /**
     * 支付宝需要,微信是否也需要再次校验来源，进行订单查询
     * 校验数据来源
     * @param id 业务id, 数据的真实性.
     * @return true通过
     */
    public boolean verifySource (String id) {
        return false;
    }
    /**
     * 返回创建的订单信息
     * @param order 支付订单
     * @return 订单信息
     * @see PayOrder 支付订单信息
     */
    public Map<String, Object>  orderInfo (PayOrder order) {
        Map<String, Object> params = this.getCommonParam();
        UnionTransactionType type =  (UnionTransactionType)order.getTransactionType();
        //设置交易类型相关的参数
        type.convertMap(params);
        params.put(SDKConstants.param_orderId, order.getOutTradeNo());
        switch (type){
            case WAP:
            case WEB:
            case B2B:
                params.put(SDKConstants.param_txnAmt, order.getPrice().multiply(new BigDecimal(100)));
                params.put("orderDesc", order.getSubject());
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                // 订单超时时间。
                // 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
                // 此时间建议取支付时的北京时间加15分钟。
                // 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
                params.put(SDKConstants.param_payTimeout, df.format(System.currentTimeMillis() + 30 * 60 * 1000));
                params.put(SDKConstants.param_frontUrl, payConfigDao.getReturnUrl());
                break;
            case CONSUME:
                params.put(SDKConstants.param_txnAmt, order.getPrice().multiply(new BigDecimal(100)));
                params.put(SDKConstants.param_qrNo, order.getAuthCode());
                break;
            default:
        }
        return  setSign(params);
    }
    /**
     * 生成并设置签名
     * @param parameters 请求参数
     * @return 请求参数
     */
    private Map<String, Object> setSign(Map<String, Object> parameters){
        SignUtils signUtils = SignUtils.valueOf(payConfigDao.getSignType());
        String signStr;
        switch (signUtils){
            case RSA:
                parameters.put(SDKConstants.param_signMethod, SDKConstants.SIGNMETHOD_RSA);
                parameters.put(SDKConstants.param_certId, payConfigDao.getCertDescriptor().getSignCertId());
                signStr = SignUtils.SHA1.createSign( SignUtils.parameterText(parameters, "&", "signature"),"", payConfigDao.getInputCharset());
                parameters.put(SDKConstants.param_signature, RSA.sign(signStr, payConfigDao.getCertDescriptor().getSignCertPrivateKey(payConfigDao.getKeyPrivateCertPwd()), payConfigDao.getInputCharset()));
                break;
            case RSA2:
                parameters.put(SDKConstants.param_signMethod, SDKConstants.SIGNMETHOD_RSA);
                parameters.put(SDKConstants.param_certId, payConfigDao.getCertDescriptor().getSignCertId());
                signStr = SignUtils.SHA256.createSign( SignUtils.parameterText(parameters, "&", "signature"),"", payConfigDao.getInputCharset());
                parameters.put(SDKConstants.param_signature, RSA2.sign(signStr, payConfigDao.getCertDescriptor().getSignCertPrivateKey(payConfigDao.getKeyPrivateCertPwd()), payConfigDao.getInputCharset()));
                break;
            case SHA1:
            case SHA256:
            case SM3:
                String key = payConfigDao.getKeyPrivate();
                signStr = SignUtils.parameterText(parameters, "&", "signature");
                 key = signUtils.createSign(key,"",payConfigDao.getInputCharset()) + "&";
                parameters.put(SDKConstants.param_signature, signUtils.createSign(signStr, key, payConfigDao.getInputCharset()));
                break;
            default:
              throw new PayErrorException(new PayException("sign fail", "未找到的签名类型"));
        }
        return parameters;
    }

    /**
     * 验证证书链
     * @param cert 需要验证的证书
     */
    private X509Certificate verifyCertificate (X509Certificate cert) {
        try {
            cert.checkValidity();//验证有效期
            X509Certificate middleCert = payConfigDao.getCertDescriptor().getPublicCert();
            X509Certificate rootCert = payConfigDao.getCertDescriptor().getRootCert();

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(cert);

            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
            trustAnchors.add(new TrustAnchor(rootCert, null));
            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters( trustAnchors, selector);

            Set<X509Certificate> intermediateCerts = new HashSet<X509Certificate>();
            intermediateCerts.add(rootCert);
            intermediateCerts.add(middleCert);
            intermediateCerts.add(cert);

            pkixParams.setRevocationEnabled(false);

            CertStore intermediateCertStore = CertStore.getInstance("Collection",new CollectionCertStoreParameters(intermediateCerts));
            pkixParams.addCertStore(intermediateCertStore);

            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");

            @SuppressWarnings("unused")
            PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder
                    .build(pkixParams);
            return cert;
        } catch (java.security.cert.CertPathBuilderException e) {
            log.error("verify certificate chain fail.", e);
        } catch (CertificateExpiredException e) {
            log.error(e);
        } catch (CertificateNotYetValidException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
    /**
     * 获取输出二维码，用户返回给支付端,
     * @param order 发起支付的订单信息
     * @return 返回图片信息，支付时需要的
     */
    public BufferedImage genQrPay (PayOrder order)  {
        Map<String ,Object> params = orderInfo(order);
        String responseStr =  getHttpRequestTemplate().postForObject(this.getBackTransUrl(),params,String.class);
        Map<String ,Object> response = UriVariables.getParametersToMap(responseStr);
        if(response.isEmpty()){
            throw new PayErrorException(new PayException("failure", "响应内容有误!",responseStr));
        }
        if(this.verify(response)){
            if(SDKConstants.OK_RESP_CODE.equals(response.get(SDKConstants.param_respCode))){
                //成功,获取tn号
                return MatrixToImageWriter.writeInfoToJpgBuff((String)response.get(SDKConstants.param_qrCode));
            }
            throw new PayErrorException(new PayException((String)response.get(SDKConstants.param_respCode), (String)response.get(SDKConstants.param_respMsg), responseStr));
        }
        throw new PayErrorException(new PayException("failure", "验证签名失败", responseStr));
    }

    /**
     * 刷卡付,pos主动扫码付款(条码付)
     * @param order 发起支付的订单信息
     * @return 返回支付结果
     */
    public Map<String, Object> microPay (PayOrder order) {
        Map<String ,Object > params = orderInfo(order);
        String responseStr =  getHttpRequestTemplate().postForObject(this.getBackTransUrl(),params,String.class);
        return UriVariables.getParametersToMap(responseStr);
    }

    /**
     * 将字符串转换为X509Certificate对象.
     * @param x509CertString 证书串
     * @return X509Certificate
     */
    public static X509Certificate genCertificateByStr(String x509CertString) {
        X509Certificate x509Cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream tIn = new ByteArrayInputStream(x509CertString.getBytes("ISO-8859-1"));
            x509Cert = (X509Certificate) cf.generateCertificate(tIn);
        } catch (Exception e) {
            log.error("gen certificate error", e);
        }
        return x509Cert;
    }

    /**
     * 获取输出消息，用户返回给支付端
     * @param code    状态
     * @param message 消息
     * @return 返回输出消息
     */
    public PayOutMessage getPayOutMessage (String code, String message) {
        return PayTextOutMessage.TEXT().content(code.toLowerCase()).build();
    }

    /**
     * 获取成功输出消息，用户返回给支付端
     * 主要用于拦截器中返回
     * @param payMessage 支付回调消息
     * @return 返回输出消息
     */
    public PayOutMessage successPayOutMessage (PayMessage payMessage) {
        return getPayOutMessage("ok", null);
    }

    /**
     * 获取输出消息，用户返回给支付端, 针对于web端
     * @param orderInfo   发起支付的订单信息
     * @param method 请求方式  "post" "get",
     * @return 获取输出消息，用户返回给支付端, 针对于web端
     * @see MethodType 请求类型
     */
    public String buildRequest(Map<String, Object> orderInfo, MethodType method) {
        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + payConfigDao.getInputCharset() + "\"/></head><body>");
        sf.append("<form id = \"pay_form\" action=\"" + getFrontTransUrl()  + "\" method=\"post\">");
        if (null != orderInfo && 0 != orderInfo.size()) {
            Set<Map.Entry<String, Object>> set = orderInfo.entrySet();
            Iterator<Map.Entry<String, Object>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> ey = it.next();
                String key = ey.getKey();
                Object value = ey.getValue();
                sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + value + "\"/>");
            }
        }
        sf.append("</form>");
        sf.append("</body>");
        sf.append("<script type=\"text/javascript\">");
        sf.append("document.all.pay_form.submit();");
        sf.append("</script>");
        sf.append("</html>");
        return sf.toString();
    }


    /**
     * 交易查询接口，带处理器
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @param callback   处理器
     * @return 返回查询回来的结果集
     */
    public <T> T query(String tradeNo, String outTradeNo, Callback<T> callback) {
        Map<String ,Object > params = this.getCommonParam();
        UnionTransactionType.QUERY.convertMap(params);
        params.put(SDKConstants.param_orderId,outTradeNo);
        this.setSign(params);
        String responseStr = getHttpRequestTemplate().postForObject(this.getSingleQueryUrl(), params, String.class);
        JSONObject response = UriVariables.getParametersToMap(responseStr);
        if (this.verify(response)) {
            if (SDKConstants.OK_RESP_CODE.equals(response.getString(SDKConstants.param_respCode))) {
                String origRespCode = response.getString(SDKConstants.param_origRespCode);
                if ((SDKConstants.OK_RESP_CODE).equals(origRespCode)) {
                    //交易成功，更新商户订单状态
                    return callback.perform(response);
                }
            }
            throw new PayErrorException(new PayException(response.getString(SDKConstants.param_respCode), response.getString(SDKConstants.param_respMsg), response.toJSONString()));
        }
        throw new PayErrorException(new PayException("failure", "验证签名失败", response.toJSONString()));
    }

    /**
     * 交易查询接口
     * @param tradeNo支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回查询回来的结果集，支付方原值返回
     */
    public Map<String, Object> query(String tradeNo, String outTradeNo) {
    	return query(tradeNo, outTradeNo, new Callback<Map<String, Object>>() {
	        public Map<String, Object> perform(Map<String, Object> map) {
	            return map;
		    }
	    });
    }


    /**
     * 消费撤销/退货接口
     * @param origQryId 原交易查询流水号.
     * @param orderId 退款单号
     * @param refundAmount 退款金额
     * @param type UnionTransactionType.REFUND  或者UnionTransactionType.CONSUME_UNDO
     * @return 返回支付方申请退款后的结果
     */
    public Map<String, Object> unionRefundOrConsumeUndo (String origQryId, String orderId, BigDecimal refundAmount,UnionTransactionType type) {
        return unionRefundOrConsumeUndo(new RefundOrder(orderId, origQryId,refundAmount ), type);
    }

    /**
     * 消费撤销/退货接口
     * @param refundOrder   退款订单信息
     * @param type UnionTransactionType.REFUND  或者UnionTransactionType.CONSUME_UNDO
     * @return 返回支付方申请退款后的结果
     */
    public Map<String, Object> unionRefundOrConsumeUndo (RefundOrder refundOrder,UnionTransactionType type) {
        Map<String ,Object> params = this.getCommonParam();
        type.convertMap(params);
        params.put(SDKConstants.param_orderId, refundOrder.getRefundNo());
        params.put(SDKConstants.param_txnAmt, refundOrder.getRefundAmount());
        params.put(SDKConstants.param_origQryId, refundOrder.getTradeNo());
        this.setSign(params);
        String responseStr =  getHttpRequestTemplate().postForObject(this.getBackTransUrl(),params,String.class);
        JSONObject response =  UriVariables.getParametersToMap(responseStr);
        if(this.verify(response)){
            if(SDKConstants.OK_RESP_CODE.equals(response.getString(SDKConstants.param_respCode))){
                String origRespCode = response.getString(SDKConstants.param_origRespCode);
                //交易成功，更新商户订单状态
                return response;
            }
            throw new PayErrorException(new PayException(response.getString(SDKConstants.param_respCode), response.getString(SDKConstants.param_respMsg), response.toJSONString()));
        }
        throw new PayErrorException(new PayException("failure", "验证签名失败", response.toJSONString()));
    }
    /**
     * 交易关闭接口
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回支付方交易关闭后的结果
     */
    public Map<String, Object> close (String tradeNo, String outTradeNo) {
        return null;
    }

    /**
     * 交易关闭接口
     * @param tradeNo支付平台订单号
     * @param outTradeNo 商户单号
     * @param callback   处理器
     * @return 返回支付方交易关闭后的结果
     */
    public <T> T close (String tradeNo, String outTradeNo, Callback<T> callback) {
        return null;
    }
    
    /**
     * 申请退款接口
     * @param tradeNo支付平台订单号
     * @param outTradeNo   商户单号
     * @param refundAmount 退款金额
     * @param totalAmount  总金额
     * @return 返回支付方申请退款后的结果
     * @see #refund(RefundOrder)
     */
    @Deprecated
    public Map<String, Object> refund (String tradeNo, String outTradeNo, BigDecimal refundAmount, BigDecimal totalAmount) {
        return refund(tradeNo, outTradeNo, refundAmount, totalAmount, new Callback<Map<String, Object>>() {
            @Override
            public Map<String, Object> perform(Map<String, Object> map) {
                return map;
            }
        });
    }

    /**
     * 申请退款接口
     * @param tradeNo支付平台订单号
     * @param outTradeNo商户单号
     * @param refundAmount 退款金额
     * @param totalAmount  总金额
     * @param callback处理器
     * @return 返回支付方申请退款后的结果
     * @see #refund(RefundOrder, Callback)
     */
    @Deprecated
    public <T> T refund (String tradeNo, String outTradeNo, BigDecimal refundAmount, BigDecimal totalAmount, Callback<T> callback) {
        return refund(new RefundOrder(tradeNo, outTradeNo, refundAmount, totalAmount), callback);
    }

    public Map<String, Object> refund(RefundOrder refundOrder) {
        return refund(refundOrder, new Callback<Map<String, Object>>() {
            public Map<String, Object> perform(Map<String, Object> map) {
                return map;
            }
        });
    }

    public <T> T refund(RefundOrder refundOrder, Callback<T> callback) {
        return callback.perform(unionRefundOrConsumeUndo(refundOrder, UnionTransactionType.REFUND));
    }
    /**
     * 查询退款
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @return 返回支付方查询退款后的结果
     */
    public Map<String, Object> refundquery (String tradeNo, String outTradeNo) {
        return null;
    }

    /**
     * 查询退款
     * @param tradeNo    支付平台订单号
     * @param outTradeNo 商户单号
     * @param callback   处理器
     * @return 返回支付方查询退款后的结果
     */
    public <T> T refundquery (String tradeNo, String outTradeNo, Callback<T> callback) {
        return null;
    }

    /**
     * 下载对账单
     * @param billDate 账单时间
     * @param billType 账单类型
     * @return 返回fileContent 请自行将数据落地
     */
    public Map<String, Object> downloadbill (Date billDate, String billType) {
        Map<String ,Object > params = this.getCommonParam();
        UnionTransactionType.FILE_TRANSFER.convertMap(params);
        DateFormat df = new SimpleDateFormat("MMdd");
        params.put(SDKConstants.param_settleDate,df.format(billDate));
        params.put(SDKConstants.param_fileType,billType);
        params.remove(SDKConstants.param_backUrl);
        params.remove(SDKConstants.param_currencyCode);
        this.setSign(params);
        String responseStr =  getHttpRequestTemplate().postForObject(this.getFileTransUrl(),params,String.class);
        JSONObject response =  UriVariables.getParametersToMap(responseStr);
        if(this.verify(response)){
            if(SDKConstants.OK_RESP_CODE.equals(response.get(SDKConstants.param_respCode))){
                return response;
            }
            throw new PayErrorException(new PayException(response.get(SDKConstants.param_respCode).toString(), response.get(SDKConstants.param_respMsg).toString(), response.toString()));
        }
        throw new PayErrorException(new PayException("failure", "验证签名失败", response.toString()));
    }

    /**
     * @param tradeNoOrBillDate  支付平台订单号或者账单类型， 具体请 类型为{@link String }或者 {@link Date }，类型须强制限制，类型不对应则抛出异常{@link PayErrorException}
     * @param outTradeNoBillType 商户单号或者 账单类型
     * @param transactionType    交易类型
     * @return 返回支付方对应接口的结果
     */
    public Map<String, Object> secondaryInterface(Object tradeNoOrBillDate, String outTradeNoBillType, TransactionType transactionType) {
        return null;
    }
}
