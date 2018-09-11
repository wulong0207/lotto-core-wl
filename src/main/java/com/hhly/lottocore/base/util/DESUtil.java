package com.hhly.lottocore.base.util;


import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.PropertyUtil;



/**
 * @desc
 * @author cheng chen
 * @date 2017年6月15日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class DESUtil {
	/**
	 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
	 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
	 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
	 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
	 */

	private static String PASSWORD = PropertyUtil.getPropertyValue("sys.properties", "des_password");
	
	public DESUtil() {
		
	}

	
	

	private static Cipher GetCipher(int type,String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象 type Cipher.ENCRYPT_MODE 1 :加密类型, 2:解密类型
		cipher.init(type, securekey, random);
		return cipher;
	}


	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static String encrypt(String encryptStr,String password) {
		try {
			// 正式执行加密操作
			return new String(Base64.encodeBase64(GetCipher(Cipher.ENCRYPT_MODE,password)
					.doFinal(encryptStr.getBytes())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * des加密
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年4月13日 下午12:12:33
	 * @param encryptStr
	 * @return
	 */
	public static String encrypt(String encryptStr){
		return encrypt(encryptStr, PASSWORD);
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static String decrypt(String decryptStr,String password){
		// 正式执行解密操作
		try {
			return new String(GetCipher(Cipher.DECRYPT_MODE,password).doFinal(Base64.decodeBase64(decryptStr.getBytes())));
		} catch (Exception e) {
		    throw new ServiceRuntimeException("解密错误",e);
		}
	}
	public static String decrypt(String decryptStr){
		return decrypt(decryptStr, PASSWORD);
	}
	// 根据指定长度生成字母和数字的随机数
	// 0~9的ASCII为48~57
	// A~Z的ASCII为65~90
	// a~z的ASCII为97~122
	public static String createRandomCharData(int length) {
		StringBuilder sb = new StringBuilder();
		Random randdata = new Random();
		int data = 0;
		for (int i = 0; i < length; i++) {
				data = randdata.nextInt(26) + 65;// 保证只会产生65~90之间的整数
				sb.append((char) data);
		}
		return sb.toString();
	}
	
	// 测试
		public static void main(String args[]) throws Exception {
			// 待加密内容
			String str = "e10adc3949ba59abbe56e057f20f883e";

			String result = DESUtil.encrypt(str);
			System.out.println("64 加密后" + result);

			// 直接将如上内容解密
			try {
				String decryResult = DESUtil.decrypt(result);
				System.out.println("解密后：" + decryResult);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
}
