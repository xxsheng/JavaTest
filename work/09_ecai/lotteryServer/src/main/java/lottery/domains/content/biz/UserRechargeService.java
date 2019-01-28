package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.pool.payment.PrepareResult;
import lottery.domains.pool.payment.VerifyResult;
import lottery.web.WebJSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserRechargeService {
	/**
	 * 新增一条未支付的充值记录
	 */
	boolean addRecharge(int userId, double amount, String billno, PaymentChannel channel, Integer requestBankId,String ip);

	/**
	 * 网银转账，返回附言
	 */
	Integer addWangYingTransfersRecharge(int userId, double amount, String billno, String name, int cardId, PaymentChannel channel, Integer requestBankId,String postscript,String ip);

	/**
	 * 检查是否有未支付的网银转账单
	 */
	int checkUnPaidWangYingTransfersRecharge(WebJSON json, int userId, String name);
	
	/**
	 * 封装支付参数或获取支付二维码
	 */
	PrepareResult prepare(PaymentChannel channel, String billno, double amount, String bankco, HttpServletRequest request);

	/**
	 * 支付成功验证
	 */
	VerifyResult verify(PaymentChannel channel, Map<String, String> resMap);

	/**
	 * 获取用户订单详情
	 */
	UserRecharge  getUserRechargeOrderInfo(String billno);

	/**
	 * 充值成功调用
	 */
	boolean doRecharge(PaymentChannel channel, VerifyResult verifyResult);

	UserRecharge getByBillno(String billno);
}