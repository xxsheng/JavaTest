package lottery.domains.content.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javautils.encrypt.PaymentChannelEncrypt;
import javautils.image.ImageUtil;
import lottery.domains.content.biz.PaymentChannelService;
import lottery.domains.content.dao.DbServerSyncDao;
import lottery.domains.content.dao.PaymentChannelDao;
import lottery.domains.content.dao.PaymentChannelQrCodeDao;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelQrCode;
import lottery.domains.content.global.DbServerSyncEnum;
import lottery.domains.content.global.Global;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentChannelServiceImpl implements PaymentChannelService {
	
	@Autowired
	private PaymentChannelDao paymentChannelDao;
	@Autowired
	private PaymentChannelQrCodeDao paymentChannelQrCodeDao;

	@Autowired
	private DbServerSyncDao dbServerSyncDao;

	@Override
	public List<PaymentChannelVO> listAllVOs() {
		List<PaymentChannel> paymentChannels = paymentChannelDao.listAll();
		if (CollectionUtils.isNotEmpty(paymentChannels)) {

			List<PaymentChannelVO> paymentChannelVOs = new ArrayList<>();

			for (PaymentChannel paymentChannel : paymentChannels) {
				PaymentChannelVO vo = new PaymentChannelVO(paymentChannel);

				decryptSensitiveProperties(vo);

				paymentChannelVOs.add(vo);
			}

			return paymentChannelVOs;
		}
		return new ArrayList<>();
	}

	@Override
	public List<PaymentChannel> listAllFullProperties() {
		List<PaymentChannel> paymentChannels = paymentChannelDao.listAll();
		if (CollectionUtils.isNotEmpty(paymentChannels)) {
			for (PaymentChannel paymentChannel : paymentChannels) {
				decryptSensitiveProperties(paymentChannel);
			}
		}
		return paymentChannels;
	}

	@Override
	public List<PaymentChannelVO> listAllMobileScanVOs() {
		int type = Global.PAYMENT_CHANNEL_TYPE_MOBILE;
		Integer [] subType ={
				Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_WECHAT_SCAN,
				Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_ALIPAY_SCAN,
				Global.PAYMENT_CHANNEL_SUB_TYPE_MOBILE_QQ_SCAN };
		List<Criterion> criterions = new ArrayList<>();
		criterions.add(Restrictions.eq("type", type));
		criterions.add(Restrictions.in("subType", subType));

		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("sequence"));
		List<PaymentChannel> paymentChannels = paymentChannelDao.listAll(criterions,orders);

		if (CollectionUtils.isNotEmpty(paymentChannels)) {

			List<PaymentChannelVO> paymentChannelVOs = new ArrayList<>();

			for (PaymentChannel paymentChannel : paymentChannels) {
				PaymentChannelVO vo = new PaymentChannelVO(paymentChannel);

				decryptSensitiveProperties(vo);

				paymentChannelVOs.add(vo);
			}

			return paymentChannelVOs;
		}
		return new ArrayList<>();
	}

	@Override
	public PaymentChannelVO getVOById(int id) {
		PaymentChannel paymentChannel = paymentChannelDao.getById(id);
		if (paymentChannel != null) {
			PaymentChannelVO vo = new PaymentChannelVO(paymentChannel);
			decryptSensitiveProperties(vo);
			return vo;
		}
		return null;
	}

	@Override
	public PaymentChannel getFullPropertyById(int id) {
		PaymentChannel paymentChannel = paymentChannelDao.getById(id);
		if (paymentChannel != null) {
			decryptSensitiveProperties(paymentChannel);
			return paymentChannel;
		}
		return null;
	}

	@Override
	public boolean add(String name, String mobileName, String frontName, String channelCode, String merCode, double totalCredits, double minTotalRecharge, double maxTotalRecharge, double minUnitRecharge, double maxUnitRecharge, String maxRegisterTime, String qrCodeContent, int fixedQRAmount, int type, int subType, double consumptionPercent, String whiteUsernames, String startTime, String endTime, String fixedAmountQrs, int addMoneyType) {

		PaymentChannel entity = new PaymentChannel();
		entity.setName(name);
		entity.setMobileName(mobileName);
		entity.setFrontName(frontName);
		entity.setChannelCode(channelCode);
		entity.setMerCode(merCode);
		entity.setTotalCredits(totalCredits);
		entity.setMinTotalRecharge(minTotalRecharge);
		entity.setMaxTotalRecharge(maxTotalRecharge);
		entity.setMinUnitRecharge(minUnitRecharge);
		entity.setMaxUnitRecharge(maxUnitRecharge);
		entity.setMaxRegisterTime(maxRegisterTime);
		entity.setFixedQRAmount(fixedQRAmount);
		entity.setType(type);
		entity.setSubType(subType);
		entity.setConsumptionPercent(consumptionPercent);
		entity.setStartTime(startTime);
		entity.setEndTime(endTime);
		entity.setMaxRegisterTime(maxRegisterTime);
		String md5Key = PaymentChannelEncrypt.encrypt(RandomStringUtils.random(20, true, true));
		entity.setMd5Key(md5Key); // 随机生成一个加密密钥
		entity.setPayUrl(null);
		entity.setArmourUrl(null);
		entity.setStatus(-1); // 默认禁用
		entity.setThirdFee(0);
		entity.setThirdFeeFixed(0);
		entity.setUsedCredits(0);
		entity.setWhiteUsernames(whiteUsernames);
		entity.setAddMoneyType(addMoneyType);

		int maxSequence = paymentChannelDao.getMaxSequence();
		entity.setSequence(maxSequence+1);
		if (StringUtils.isNotEmpty(qrCodeContent)) {
			String qrUrlCode = ImageUtil.encodeQR(qrCodeContent, 200, 200);
			qrUrlCode = PaymentChannelEncrypt.encrypt(qrUrlCode); // 二维码加密
			entity.setQrUrlCode(qrUrlCode);
		}

		boolean added = paymentChannelDao.add(entity);
		
		if (added && fixedQRAmount == 1) {
			JSONArray jsonArray = JSONArray.parseArray(fixedAmountQrs);
			for (Object object : jsonArray) {
				JSONObject jsonObject = JSONObject.parseObject(object.toString());
				
				PaymentChannelQrCode paymentChannelQrCode = new PaymentChannelQrCode();
				Double amount = Double.valueOf(jsonObject.getString("amount"));
				String fuxQRCodeContent = jsonObject.getString("qrCodeContent");
				if (StringUtils.isNotEmpty(fuxQRCodeContent)) {
					String base64QR = ImageUtil.encodeQR(fuxQRCodeContent, 200, 200);
					base64QR = PaymentChannelEncrypt.encrypt(base64QR); // 二维码加密
					paymentChannelQrCode.setQrUrlCode(base64QR);
				}
				paymentChannelQrCode.setChannelId(entity.getId());
				paymentChannelQrCode.setMoney(amount);
				paymentChannelQrCodeDao.add(paymentChannelQrCode);
			}
		}
		
		if (added) {
			dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CHANNEL);
		}
		return added;
	}

	@Override
	public boolean edit(int id, String name, String mobileName, String frontName, double totalCredits, double minTotalRecharge, double maxTotalRecharge, double minUnitRecharge, double maxUnitRecharge, String maxRegisterTime, String qrCodeContent, int fixedQRAmount, double consumptionPercent, String whiteUsernames, String startTime, String endTime, String fixedAmountQrs) {

		PaymentChannel entity = paymentChannelDao.getById(id);
		if (entity == null) {
			return false;
		}

		entity.setName(name);
		entity.setMobileName(mobileName);
		entity.setFrontName(frontName);
		// entity.setChannelCode(channelCode);
		// entity.setMerCode(merCode);
		entity.setTotalCredits(totalCredits);
		entity.setMinTotalRecharge(minTotalRecharge);
		entity.setMaxTotalRecharge(maxTotalRecharge);
		entity.setMinUnitRecharge(minUnitRecharge);
		entity.setMaxUnitRecharge(maxUnitRecharge);
		entity.setMaxRegisterTime(maxRegisterTime);
		entity.setFixedQRAmount(fixedQRAmount);
		// entity.setType(type);
		// entity.setSubType(subType);
		entity.setConsumptionPercent(consumptionPercent);
		entity.setStartTime(startTime);
		entity.setEndTime(endTime);
		entity.setMaxRegisterTime(maxRegisterTime);
		entity.setWhiteUsernames(whiteUsernames);

		if (StringUtils.isNotEmpty(qrCodeContent)) {
			String base64QR = ImageUtil.encodeQR(qrCodeContent, 200, 200);
			base64QR = PaymentChannelEncrypt.encrypt(base64QR); // 二维码加密
			entity.setQrUrlCode(base64QR);
		}

		boolean updated = paymentChannelDao.update(entity);
		if (updated) {
			if (updated && fixedQRAmount == 1) {
				JSONArray jsonArray = JSONArray.parseArray(fixedAmountQrs);
				for (Object object : jsonArray) {
					JSONObject jsonObject = JSONObject.parseObject(object.toString());
					String amount = jsonObject.getString("amount");
					String fixQRCodeContent = jsonObject.getString("qrCodeContent");
					String paymentChannelQrCodeId = jsonObject.getString("id");

					PaymentChannelQrCode paymentChannelQrCode = new PaymentChannelQrCode();
					paymentChannelQrCode.setMoney(Double.valueOf(amount));
					paymentChannelQrCode.setChannelId(id);
					if (StringUtils.isNotEmpty(fixQRCodeContent)) {
						String base64QR = ImageUtil.encodeQR(fixQRCodeContent, 200, 200);
						base64QR = PaymentChannelEncrypt.encrypt(base64QR); // 二维码加密
						paymentChannelQrCode.setQrUrlCode(base64QR);
					}

					if (StringUtils.isEmpty(paymentChannelQrCodeId)) {
						paymentChannelQrCodeDao.add(paymentChannelQrCode);
					}else{
						PaymentChannelQrCode qrCode = paymentChannelQrCodeDao.getById(Integer.valueOf(paymentChannelQrCodeId));
						qrCode.setMoney(Double.valueOf(amount));
						qrCode.setId(Integer.valueOf(paymentChannelQrCodeId));
						if (StringUtils.isNotEmpty(fixQRCodeContent)) {
							String base64QR = ImageUtil.encodeQR(fixQRCodeContent, 200, 200);
							base64QR = PaymentChannelEncrypt.encrypt(base64QR); // 二维码加密
							qrCode.setQrUrlCode(base64QR);
						}
						paymentChannelQrCodeDao.update(qrCode);
					}

				}
			}

			dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CHANNEL);
		}
		return updated;
	}

	@Override
	public boolean updateStatus(int id, int status) {
		PaymentChannel entity = paymentChannelDao.getById(id);
		if(entity != null) {
			entity.setStatus(status);
			boolean updated = paymentChannelDao.update(entity);
			if (updated) {
				dbServerSyncDao.update(DbServerSyncEnum.PAYMENT_CHANNEL);
			}
			return updated;
		}
		return false;
	}
	
	
	@Override
	public boolean resetCredits(int id) {
		PaymentChannel entity = paymentChannelDao.getById(id);
		if(entity != null) {
			entity.setUsedCredits(0);
			return paymentChannelDao.update(entity);
		}
		return false;
	}
	
	@Override
	public boolean delete(int id) {
		PaymentChannelVO paymentChannel = getVOById(id);
		if (paymentChannel == null) {
			return false;
		}

		// 删除数据
		boolean deleted = paymentChannelDao.delete(id);

		// 把所有后面的排序号减1
		if (deleted) {
			paymentChannelDao.batchModSequence(paymentChannel.getSequence());
		}

		return deleted;
	}

	@Override
	public boolean moveUp(int id) {
		PaymentChannel entity = paymentChannelDao.getById(id);
		if(entity != null && entity.getSequence() != 1) {
			// 获取上一位 为了防止出现多位list接受
			List<PaymentChannel> prev = paymentChannelDao.getBySequenceUp(entity.getSequence());
			if (prev !=null && prev.size()>0) {
				PaymentChannel paymentChannel=prev.get(0);//上一个
				int adminUserMenuSort=entity.getSequence()-paymentChannel.getSequence();
				if (adminUserMenuSort>1) {
					// 上移
					paymentChannelDao.modSequence(entity.getId(), -1);
				}else{
					paymentChannelDao.updateSequence(entity.getId(), prev.get(0).getSequence());
					// 上一位下移
					paymentChannelDao.updateSequence(prev.get(0).getId(), entity.getSequence());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean moveDown(int id) {
		PaymentChannel entity = paymentChannelDao.getById(id);

		int total = paymentChannelDao.getMaxSequence();
		
		if(entity != null && entity.getSequence() != total) {//看是否是已经是最后一个了
			// 获取下一位
			List<PaymentChannel> nexts = paymentChannelDao.getBySequenceDown(entity.getSequence());
			if (nexts !=null && nexts.size()>0) {
				PaymentChannel nextPaymentChannel=nexts.get(0);
				// 下移
				int nextPaymentChannelSequence=nextPaymentChannel.getSequence()-entity.getSequence();
				if (nextPaymentChannelSequence>1) {
					paymentChannelDao.modSequence(entity.getId(), +1);
				}else{
					paymentChannelDao.updateSequence(entity.getId(), nextPaymentChannel.getSequence());
					// 下一位上移
					paymentChannelDao.updateSequence(nextPaymentChannel.getId(), entity.getSequence());
				}
				return true;
			}
		}
		return false;
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

	private void decryptSensitiveProperties(PaymentChannelVO paymentChannelVO) {
		String qrUrlCode = paymentChannelVO.getQrUrlCode();
		if (StringUtils.isNotEmpty(qrUrlCode)) {
			qrUrlCode = PaymentChannelEncrypt.decrypt(qrUrlCode);
			paymentChannelVO.setQrUrlCode(qrUrlCode);
		}
	}

}