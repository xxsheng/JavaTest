package lottery.domains.content.biz.impl;

import javautils.StringUtil;
import javautils.date.Moment;
import javautils.encrypt.PaymentChannelEncrypt;
import javautils.math.MathUtil;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.dao.PaymentChannelDao;
import lottery.domains.content.dao.PaymentChannelQrCodeDao;
import lottery.domains.content.dao.read.UserMainReportReadDao;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelQrCode;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.pay.PaymentChannelVO;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.helper.session.SessionUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentChannelServiceImpl implements PaymentChannelService {
	
	/**
	 * DAO
	 */
	@Autowired
	private PaymentChannelDao paymentChannelDao;

	@Autowired
	private UserMainReportReadDao uMainReportReadDao;
	@Autowired
	private PaymentChannelQrCodeDao paymentChannelQrCodeDao;
	

	/**
	 * DataFactory
	 */
	@Autowired
	private DataFactory dataFactory;
	
	@Override
	@Transactional(readOnly = true)
	public List<PaymentChannelVO> getAvailableByUserId(SessionUser user) {
		if(dataFactory.getRechargeConfig().isEnable() == false) {
			return new ArrayList<>();
		}

		double totalRechargeMoney = uMainReportReadDao.getTotalRecharge(user.getId());
		List<Criterion> criterions = new ArrayList<>();
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("sequence"));
		criterions.add(Restrictions.and(
				Restrictions.le("minTotalRecharge", totalRechargeMoney),
				Restrictions.ge("maxTotalRecharge", totalRechargeMoney))
		);

		// 只取出可用额度的账号
		criterions.add(Restrictions.ltProperty("usedCredits", "totalCredits"));

		criterions.add(Restrictions.eq("status", 0));

		Moment userRegisterTime = new Moment().fromTime(user.getRegistTime());
		List<PaymentChannel> clist = paymentChannelDao.find(criterions, orders);
		List<PaymentChannelVO> list = new ArrayList<>();

		for (PaymentChannel tmpBean : clist) {
			if (StringUtils.isNotEmpty(tmpBean.getMaxRegisterTime())) {
				Moment maxRegisterTime = new Moment().fromTime(tmpBean.getMaxRegisterTime());
				if (userRegisterTime.gt(maxRegisterTime)) {
					continue;
				}
			}

			if (StringUtils.isNotEmpty(tmpBean.getStartTime()) && StringUtils.isNotEmpty(tmpBean.getEndTime())) {
				String serviceTime = tmpBean.getStartTime() + "~" + tmpBean.getEndTime();
				if (!StringUtil.isServiceTime(new Moment(), serviceTime)) {
					continue;
				}
			}

			PaymentChannelVO paymentChannelVO = new PaymentChannelVO(tmpBean, dataFactory);
			
			
			if (tmpBean.getType() == Global.PAYMENT_CHANNEL_TYPE_MOBILE && tmpBean.getFixedQRAmount() == 1) {
				List<PaymentChannelQrCode> qrCodeList= paymentChannelQrCodeDao.getByList(tmpBean.getId());
				if (qrCodeList !=null && !qrCodeList.isEmpty()) {

					for (PaymentChannelQrCode paymentChannelQrCode : qrCodeList) {
						paymentChannelQrCode.setQrUrlCode(null);
						//
						// decryptSensitiveProperties(paymentChannelQrCode);
					}

					paymentChannelVO.setQrCodes(qrCodeList);
				}
				//平台充充
			}
			
			list.add(paymentChannelVO);
		
//			}
		}
		return list;
	}

	@Override
	public List<PaymentChannel> listAll() {
		List<PaymentChannel> paymentChannels = paymentChannelDao.listAll();
		if (CollectionUtils.isNotEmpty(paymentChannels)) {
			for (PaymentChannel paymentChannel : paymentChannels) {
				int id = paymentChannel.getId();
				decryptSensitiveProperties(paymentChannel);
			}
		}
		return paymentChannels;
	}

	@Override
	public boolean isAvailable(WebJSON json, SessionUser user, double rechargeAmount, PaymentChannel channel) {
		if(dataFactory.getRechargeConfig().isEnable() == false) {
			json.set(2, "2-8");
			return false;
		}

		if (channel == null) {
			json.set(2, "2-7");
			return false;
		}

		if (rechargeAmount <= 0) {
			json.set(2, "2-7");
			return false;
		}

		if (channel.getStatus() != 0) {
			json.set(2, "2-1136");
			return false;
		}

		// 检查最大注册时间
		if (StringUtils.isNotEmpty(channel.getMaxRegisterTime())) {
			Moment userRegisterTime = new Moment().fromTime(user.getRegistTime());
			Moment maxRegisterTime = new Moment().fromTime(channel.getMaxRegisterTime());
			if (userRegisterTime.gt(maxRegisterTime)) {
				json.set(2, "2-1078");
				return false;
			}
		}

		// 检查服务时间
		if (StringUtils.isNotEmpty(channel.getStartTime()) && StringUtils.isNotEmpty(channel.getEndTime())) {
			String serviceTime = channel.getStartTime() + "~" + channel.getEndTime();
			if (!StringUtil.isServiceTime(new Moment(), serviceTime)) {
				json.set(2, "2-1135");
				return false;
			}
		}

		// 检查单次充值范围
		if (channel.getMinUnitRecharge() > 0) {
			if (rechargeAmount < channel.getMinUnitRecharge()) {
				json.set(2, "2-1050", MathUtil.doubleUpTOString(channel.getMinUnitRecharge()));
				return false;
			}
			if (rechargeAmount > channel.getMaxUnitRecharge()) {
				json.set(2, "2-1051", MathUtil.doubleUpTOString(channel.getMaxUnitRecharge()));
				return false;
			}
		}

		// 检查累计充值可见
		if (channel.getMinTotalRecharge() > 0) {
			double totalRechargeMoney = uMainReportReadDao.getTotalRecharge(user.getId());
			if (totalRechargeMoney < channel.getMinTotalRecharge() || totalRechargeMoney > channel.getMaxTotalRecharge()) {
				json.set(2, "2-1078");
				return false;
			}
		}

		// 检查额度
		if (channel.getAddMoneyType() == Global.ADD_MONEY_TYPE_MANUAL) {
			PaymentChannel newestChannel = paymentChannelDao.getAvailableById(channel.getId());
			if (newestChannel == null) {
				json.set(2, "2-1136");
				return false;
			}
			if (rechargeAmount + newestChannel.getUsedCredits() >= newestChannel.getTotalCredits()) {
				json.set(2, "2-1078");
				return false;
			}
		}

		return true;
	}

	private void decryptSensitiveProperties(PaymentChannel paymentChannel) {
		String payUrl = paymentChannel.getPayUrl();
		if (StringUtils.isNotEmpty(payUrl)) {
		    payUrl = PaymentChannelEncrypt.decrypt(payUrl);
		    paymentChannel.setPayUrl(payUrl);
		}
		String armourUrl = paymentChannel.getArmourUrl();
		if (StringUtils.isNotEmpty(armourUrl)) {
		    armourUrl = PaymentChannelEncrypt.decrypt(armourUrl);
		    paymentChannel.setArmourUrl(armourUrl);
		}
		String qrUrlCode = paymentChannel.getQrUrlCode();
		if (StringUtils.isNotEmpty(qrUrlCode)) {
		    qrUrlCode = PaymentChannelEncrypt.decrypt(qrUrlCode);
		    paymentChannel.setQrUrlCode(qrUrlCode);
		}
		String md5Key = paymentChannel.getMd5Key();
		if (StringUtils.isNotEmpty(md5Key)) {
		    md5Key = PaymentChannelEncrypt.decrypt(md5Key);
		    paymentChannel.setMd5Key(md5Key);
		}
		String rsaPublicKey = paymentChannel.getRsaPublicKey();
		if (StringUtils.isNotEmpty(rsaPublicKey)) {
		    rsaPublicKey = PaymentChannelEncrypt.decrypt(rsaPublicKey);
		    paymentChannel.setRsaPublicKey(rsaPublicKey);
		}
		String rsaPrivateKey = paymentChannel.getRsaPrivateKey();
		if (StringUtils.isNotEmpty(rsaPrivateKey)) {
		    rsaPrivateKey = PaymentChannelEncrypt.decrypt(rsaPrivateKey);
		    paymentChannel.setRsaPrivateKey(rsaPrivateKey);
		}
		String rsaPlatformPublicKey = paymentChannel.getRsaPlatformPublicKey();
		if (StringUtils.isNotEmpty(rsaPlatformPublicKey)) {
		    rsaPlatformPublicKey = PaymentChannelEncrypt.decrypt(rsaPlatformPublicKey);
		    paymentChannel.setRsaPlatformPublicKey(rsaPlatformPublicKey);
		}
		String ext1 = paymentChannel.getExt1();
		if (StringUtils.isNotEmpty(ext1)) {
		    ext1 = PaymentChannelEncrypt.decrypt(ext1);
		    paymentChannel.setExt1(ext1);
		}
		String ext2 = paymentChannel.getExt2();
		if (StringUtils.isNotEmpty(ext2)) {
		    ext2 = PaymentChannelEncrypt.decrypt(ext2);
		    paymentChannel.setExt2(ext2);
		}
		String ext3 = paymentChannel.getExt3();
		if (StringUtils.isNotEmpty(ext3)) {
		    ext3 = PaymentChannelEncrypt.decrypt(ext3);
		    paymentChannel.setExt3(ext3);
		}
	}

	private void decryptSensitiveProperties(PaymentChannelQrCode qrCode) {
		if (StringUtils.isNotEmpty(qrCode.getQrUrlCode())) {
			String qrUrlCode = PaymentChannelEncrypt.decrypt(qrCode.getQrUrlCode()); // 二维码解密
			qrCode.setQrUrlCode(qrUrlCode);
		}
	}
}