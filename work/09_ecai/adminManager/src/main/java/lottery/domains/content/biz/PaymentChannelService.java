package lottery.domains.content.biz;

import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.vo.payment.PaymentChannelVO;

import java.util.List;

public interface PaymentChannelService {
	
	List<PaymentChannelVO> listAllVOs();

	List<PaymentChannel> listAllFullProperties();

	List<PaymentChannelVO> listAllMobileScanVOs();

	PaymentChannelVO getVOById(int id);

	PaymentChannel getFullPropertyById(int id);

	boolean add(String name, String mobileName, String frontName, String channelCode, String merCode, double totalCredits, double minTotalRecharge, double maxTotalRecharge, double minUnitRecharge, double maxUnitRecharge, String maxRegisterTime, String qrCodeContent, int fixedQRAmount, int type, int subType, double consumptionPercent, String whiteUsernames, String startTime, String endTime, String fixedAmountQrs, int addMoneyType);
	
	boolean edit(int id, String name, String mobileName, String frontName, double totalCredits, double minTotalRecharge, double maxTotalRecharge, double minUnitRecharge, double maxUnitRecharge, String maxRegisterTime, String qrCodeContent, int fixedQRAmount, double consumptionPercent, String whiteUsernames, String startTime, String endTime, String fixedAmountQrs);
	
	boolean updateStatus(int id, int status);
	
	boolean resetCredits(int id);
	
	boolean delete(int id);

	boolean moveUp(int id);

	boolean moveDown(int id);

}