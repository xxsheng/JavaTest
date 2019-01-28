// package lottery.domains.pool.payment.huanst.pay;
//
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
// import java.util.Map;
//
// import org.apache.http.HttpResponse;
// import org.apache.http.client.entity.UrlEncodedFormEntity;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.impl.client.DefaultHttpClient;
// import org.apache.http.message.BasicNameValuePair;
// import org.apache.http.util.EntityUtils;
// import lottery.domains.content.entity.PaymentThrid;
// import lottery.domains.content.entity.UserRecharge;
// import lottery.domains.pool.payment.cfg.MoneyFormat;
// import lottery.domains.pool.payment.cfg.PayConfig;
// import lottery.domains.pool.payment.cfg.RechargePay;
// import lottery.domains.pool.payment.cfg.RechargeResult;
// import lottery.domains.pool.payment.dinpay.WechatResultEntity;
// import lottery.domains.pool.payment.huanst.util.Base64;
// import lottery.domains.pool.payment.huanst.util.RSAUtil;
// import lottery.domains.pool.payment.huanst.util.SSLClient;
// import lottery.domains.pool.payment.huanst.util.SignUtils;
// import lottery.domains.pool.payment.pay41.utils.AppConstants;
// import lottery.domains.pool.payment.pay41.utils.KeyValue;
// import lottery.domains.pool.payment.pay41.utils.KeyValues;
//
// /**
//  *环商通支付工具类
//  */
// public class HuanstPayment {
// 	/**
// 	 * 封装请求数据
// 	 * @param payType 第三方支付
// 	 * @param billno 订单号
// 	 * @param Amount 金额
// 	 * @param bankco 银行编码
// 	 * @param thridBean 第三方支付配置信息
// 	 * @param notifyUrl 异步通知地址
// 	 * @param resultUrl 同步通知地址
// 	 * @param host 端口
// 	 * @param ip
// 	 * @throws Exception
// 	 */
// 	public static void prepareWeiXin(RechargeResult result,String payType, String billno,
// 			String Amount, String bankco, PaymentThrid thridBean, String notifyUrl, String resultUrl, String host,
// 			String ip){
// 		try {
// 			// 将金额换成分模式(环商通的支付金额为分模式)
// 			String act = String.valueOf(MoneyFormat.yuanToFenMoney(Amount));
// 			DefaultHttpClient httpClient = new SSLClient();
// 	        HttpPost postMethod = new HttpPost(PayConfig.HUANSTPAY.trans_url);//支付请求地址
// 	        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
// 	        nvps.add(new BasicNameValuePair("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())));//请求交易流水号
// 	        nvps.add(new BasicNameValuePair("version", "V1.0"));//接口版本
// 	        nvps.add(new BasicNameValuePair("productId", "0108"));//产品类型
// 	        nvps.add(new BasicNameValuePair("transId", "10"));//交易类型
// 	        nvps.add(new BasicNameValuePair("clientIp", ip));//客户端IP
// 	        nvps.add(new BasicNameValuePair("merNo", thridBean.getMerCode()));//商户编号
// 	        nvps.add(new BasicNameValuePair("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date())));//请求交易时间
// 	        nvps.add(new BasicNameValuePair("orderNo", billno));//订单号
// 	        nvps.add(new BasicNameValuePair("returnUrl", resultUrl));//同步通知地址
// 	        nvps.add(new BasicNameValuePair("notifyUrl", notifyUrl));//异步通知地址
// 	        nvps.add(new BasicNameValuePair("transAmt", act));//金额
// 	        nvps.add(new BasicNameValuePair("commodityName", "用户充值"));
// 	        nvps.add(new BasicNameValuePair("signature", SignUtils.signData(nvps)));//加密
//             postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
//             HttpResponse resp = httpClient.execute(postMethod);
//             String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
//             int statusCode = resp.getStatusLine().getStatusCode();
//             if (200 == statusCode) {
//             	if(!str.contains("signature")){
//             		result.setReturnCode(PayConfig.wxPayReturnCode.sginError);//验签失败
//             		return;
//             	}
//
//                 boolean signFlag = SignUtils.verferSignData(str);
//                 if (!signFlag) {
//                 	result.setReturnCode(PayConfig.wxPayReturnCode.sginError);//验签失败
//                     return;
//                 }
//                 result.setReturnCode(PayConfig.wxPayReturnCode.reqSuccess);//验证成功
//                 result.setReturnValue(str);//返回值
//                 return;
//             }
//             result.setReturnCode(PayConfig.wxPayReturnCode.reqError);//请求失败
//             result.setErrorMsg(String.valueOf(statusCode));//请求错误码
//             return;
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
// 		result.setReturnCode(PayConfig.wxPayReturnCode.payException);//支付异常
// 		result.setErrorMsg("数据处理异常");//
// 		return;
// 	}
//
//
// 	/**
// 	 * 解析微信支付请求返回数据
// 	 * @param resMap
// 	 * @return
// 	 * @throws Exception
// 	 */
// 	public static WechatResultEntity weChatRequestResult(Map<String,String> resMap,UserRecharge recharge) throws Exception{
// 		if(resMap != null && resMap.size() >0){
// 			String orderNo = resMap.get("orderNo");
// 			String transAmt = resMap.get("transAmt");
// 			String respCode = resMap.get("respCode");
// 			String respDesc = resMap.get("respDesc");
// 			String codeUrl = resMap.get("codeUrl");
// 			String imgUrl = resMap.get("imgUrl");
//
// 			WechatResultEntity result = new WechatResultEntity();
// 			if(respCode!=null && !respCode.equals("") && codeUrl != null && !codeUrl.equals("")){
// 				long amt = MoneyFormat.yuanToFenMoney(String.valueOf(recharge.getMoney()));
// 				if(respCode.equals("0000")){
// 					if(amt != Long.parseLong(transAmt) && !orderNo.equals(recharge.getBillno())){
// 						result.setResp_desc("订单信息验证错误");
// 						result.setPayResult("error");
// 						return result;
// 					}
// 					String wxUrl = Base64.getFromBase64(codeUrl);
// 					String imgPath = imgUrl.substring(0, imgUrl.indexOf("?")+1);
// 					result.setQrcode(imgPath + wxUrl);
// 					result.setResp_desc(respDesc);
// 					result.setPayResult("success");
// 					return result;
// 				}else{
// 					result.setResp_desc(respDesc);
// 					result.setPayResult("error");
// 					return result;
// 				}
// 			}
// 		}
// 		return null;
// 	}
//
// 	/**
// 	 * 支付成功通知
// 	 * @param thridBean
// 	 * @param resMap
// 	 */
// 	public static RechargePay notify(PaymentThrid thridBean,Map<String, String> resMap) throws Exception{
// 		try {
// 	        if(!resMap.containsKey("signature")){
//         		return null;
//         	}
// 			String productId = resMap.get("productId");
// 	        String transId = resMap.get("transId");
// 	        String merNo = resMap.get("merNo");
// 	        String orderNo = resMap.get("orderNo");
// 	        String transAmt = resMap.get("transAmt");
// 	        String orderDate = resMap.get("orderDate");
// 	        String respCode = resMap.get("respCode");
// 	        String respDesc = resMap.get("respDesc");
// 	        String signature = resMap.get("signature");
//
// 	        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
// 	        nvps.add(new BasicNameValuePair("productId",productId));
// 	        nvps.add(new BasicNameValuePair("transId", transId));
// 	        nvps.add(new BasicNameValuePair("merNo", merNo));
// 	        nvps.add(new BasicNameValuePair("orderNo", orderNo));
// 	        nvps.add(new BasicNameValuePair("transAmt", transAmt));
// 	        nvps.add(new BasicNameValuePair("orderDate", orderDate));
// 	        nvps.add(new BasicNameValuePair("respCode", respCode));
// 	        nvps.add(new BasicNameValuePair("respDesc",respDesc));
//
// 	        String sgin = SignUtils.signData(nvps);
//             if (!sgin.equals(signature)) {
//                 return null;
//             }
// 	        if (respCode.equals("0000")){
// 	        	RechargePay pay = new RechargePay();
// 	        	pay.setRecvCardNo(merNo);//商户号
// 	        	pay.setAmount(Double.parseDouble(transAmt)/100);//金额
// 	        	pay.setBillno(orderNo);//订单编号
// 	        	pay.setTradeNo("");//支付订单
// 	        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
// 				pay.setPayTime(sdf.parse(sdf.format(new Date())));//订单通知时间
// 				pay.setTradeStatus(respCode);//支付状态(0000 交易成功,failed 交易失败,paying 交易中)
// 	        	return pay;
// 	        }else {
// 				return null;
// 			}
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return null;
// 	}
//
//
// 	public static void main(String[] args) {
// 		try {
// 			drawingsPay();
// 		} catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 	}
//
// 	//代付(提款)
// 	public static void drawingsPay() throws Exception{
// 		 	DefaultHttpClient httpClient = new SSLClient();
// 	        HttpPost postMethod = new HttpPost(PayConfig.HUANSTPAY.trans_url);
// 	        List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
// 	        nvps.add(new BasicNameValuePair("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())));
// 	        nvps.add(new BasicNameValuePair("version", "V1.0"));//接口版本号
// 	        nvps.add(new BasicNameValuePair("productId", "0201"));// 0201-非垫支,0203-垫支(T0)
// 	        nvps.add(new BasicNameValuePair("transId", "07"));
// 	        nvps.add(new BasicNameValuePair("merNo", "800440048161604"));//商户号
// 	        nvps.add(new BasicNameValuePair("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date())));
// 	        nvps.add(new BasicNameValuePair("orderNo", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));
// 	        nvps.add(new BasicNameValuePair("notifyUrl", "http://www.yx888.info/Rechargenotify/13"));
// 	        // nvps.add(new BasicNameValuePair("transAmt", "5000000"));//金额
// 	        nvps.add(new BasicNameValuePair("transAmt", "1126400"));//金额
// 	        nvps.add(new BasicNameValuePair("isCompay", "0"));// 0-对私,1-对公
// 	        nvps.add(new BasicNameValuePair("phoneNo", "13178597867"));//电话
// 	        nvps.add(new BasicNameValuePair("customerName", "谭超爱"));//开户人
// 	        nvps.add(new BasicNameValuePair("cerdType", "01"));//证件类型  01  身份证
// 	        nvps.add(new BasicNameValuePair("cerdId", "452226199006060970"));//身份证号码
// 	        nvps.add(new BasicNameValuePair("accBankNo", "105615506759"));//支行代码
// 	        nvps.add(new BasicNameValuePair("accBankName", "建设银行"));
// 	        nvps.add(new BasicNameValuePair("acctNo", "6217003380002588249"));//银行卡号
// 	        nvps.add(new BasicNameValuePair("note", "提款"));
// 	        nvps.add(new BasicNameValuePair("signature", SignUtils.signData(nvps)));
// 	        try {
// 	            postMethod.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
// 	            HttpResponse resp = httpClient.execute(postMethod);
// 	            String str = EntityUtils.toString(resp.getEntity(), "UTF-8");
// 	            int statusCode = resp.getStatusLine().getStatusCode();
// 	            if (200 == statusCode) {
// 	            	System.out.println(str);
// 	                boolean signFlag = SignUtils.verferSignData(str);
// 	                if (!signFlag) {
// 	                    System.out.println("验签失败");
// 	                    return;
// 	                }
// 	                System.out.println("验签成功");
// 	                return;
// 	            }
// 	            System.out.println("返回错误码:" + statusCode);
// 	        } catch (Exception e) {
// 	            e.printStackTrace();
// 	        }
// 	}
// }
