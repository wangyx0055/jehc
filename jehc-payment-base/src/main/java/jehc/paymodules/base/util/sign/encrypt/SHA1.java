package jehc.paymodules.base.util.sign.encrypt;

import org.apache.commons.codec.digest.DigestUtils;

import jehc.paymodules.base.util.str.StringUtils;


/**
 * SHA1签名工具
 */
public class SHA1 {
    /**
     * 签名字符串
     * @param text需要签名的字符串
     * @param key密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String key, String input_charset) {
        //拼接key
        text = text + key;
        return DigestUtils.sha1Hex(  StringUtils.getContentBytes(text, input_charset));
    }

    /**
     * 签名字符串
     * @param text需要签名的字符串
     * @param sign签名结果
     * @param key密钥
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String key, String input_charset) {
        //判断是否一样
        return StringUtils.equals(sign(text, key, input_charset).toUpperCase(), sign.toUpperCase());
    }
}
