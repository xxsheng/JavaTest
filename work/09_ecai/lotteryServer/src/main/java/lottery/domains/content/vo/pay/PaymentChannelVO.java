package lottery.domains.content.vo.pay;

import java.util.ArrayList;
import java.util.List;

import lottery.domains.content.entity.PaymentCard;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.PaymentChannelQrCode;
import lottery.domains.pool.DataFactory;

public class PaymentChannelVO {

	private int id;
	private String channelCode;
	private double minUnitRecharge;
	private double maxUnitRecharge;
	private String mobileName;
	private String frontName;
	private int fixedQRAmount;//是否是固定金额二维码
	private int type;
	private int subType;
	private List<PaymentChannelBank> banklist;
	private List<PaymentChannelQrCode> qrCodes = new ArrayList<>();
    private List<PaymentCardVO> pbankList ;
	public PaymentChannelVO(PaymentChannel bean, DataFactory dataFactory) {
		this.id = bean.getId();
		this.channelCode = bean.getChannelCode();
		this.frontName = bean.getFrontName();
		this.mobileName = bean.getMobileName();
		this.minUnitRecharge = bean.getMinUnitRecharge();
		this.maxUnitRecharge = bean.getMaxUnitRecharge();
		this.banklist = dataFactory.listPaymentChannelBank(bean.getChannelCode());
		
		this.fixedQRAmount = bean.getFixedQRAmount();
		this.type = bean.getType();
		this.subType = bean.getSubType();
	}
	

	public List<PaymentCardVO> getPbankList() {
		return pbankList;
	}


	public void setPbankList(List<PaymentCardVO> pbankList) {
		this.pbankList = pbankList;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public double getMinUnitRecharge() {
		return minUnitRecharge;
	}

	public void setMinUnitRecharge(double minUnitRecharge) {
		this.minUnitRecharge = minUnitRecharge;
	}

	public double getMaxUnitRecharge() {
		return maxUnitRecharge;
	}

	public void setMaxUnitRecharge(double maxUnitRecharge) {
		this.maxUnitRecharge = maxUnitRecharge;
	}

	public String getMobileName() {
		return mobileName;
	}

	public void setMobileName(String mobileName) {
		this.mobileName = mobileName;
	}

	public String getFrontName() {
		return frontName;
	}

	public void setFrontName(String frontName) {
		this.frontName = frontName;
	}

	public int getFixedQRAmount() {
		return fixedQRAmount;
	}

	public void setFixedQRAmount(int fixedQRAmount) {
		this.fixedQRAmount = fixedQRAmount;
	}

	public List<PaymentChannelBank> getBanklist() {
		return banklist;
	}

	public void setBanklist(List<PaymentChannelBank> banklist) {
		this.banklist = banklist;
	}

	public List<PaymentChannelQrCode> getQrCodes() {
		return qrCodes;
	}

	public void setQrCodes(List<PaymentChannelQrCode> qrCodes) {
		this.qrCodes = qrCodes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSubType() {
		return subType;
	}

	public void setSubType(int subType) {
		this.subType = subType;
	}
}