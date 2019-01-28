package lottery.web.content;

import admin.domains.content.entity.AdminUser;
import admin.web.WUC;
import admin.web.WebJSONObject;
import admin.web.helper.AbstractActionController;
import javautils.StringUtil;
import javautils.excel.ExcelUtil;
import javautils.http.HttpUtil;
import lottery.domains.content.biz.UserRechargeService;
import lottery.domains.content.dao.UserBillDao;
import lottery.domains.content.dao.UserWithdrawDao;
import lottery.domains.content.entity.UserBill;
import lottery.domains.content.entity.UserRecharge;
import lottery.domains.content.entity.UserWithdraw;
import lottery.domains.content.global.Global;
import lottery.domains.content.global.PaymentConstant;
import lottery.domains.content.vo.payment.PaymentChannelVO;
import lottery.domains.content.vo.user.UserVO;
import lottery.domains.pool.LotteryDataFactory;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

@Controller
public class LotteryPlatformBillController extends AbstractActionController {
	
	@Autowired
	private UserBillDao uBillDao;
	
	@Autowired
	private UserRechargeService uRechargeService;
	
	@Autowired
	private UserWithdrawDao uWithdrawDao;
	
	@Autowired
	private LotteryDataFactory lotteryDataFactory;
	
	// 第三方
	private final String thridTemplate = "classpath:config/template/recharge-thrid.xls";
	// 转账
	private final String transferTemplate = "classpath:config/template/recharge-transfer.xls";
	// 充值未到账
	private final String systemTemplate = "classpath:config/template/recharge-system.xls";
	// 取款
	private final String withdrawTemplate = "classpath:config/template/withdraw.xls";
	// 充值
	private final String rechargeTemplate = "classpath:config/template/recharge.xls";

	/**
	 * 第三方账单
	 * @param list
	 * @return
	 */
	private HSSFWorkbook getChannelExcel(List<UserRecharge> list) {
		try {
			File file = ResourceUtils.getFile(thridTemplate);
			HSSFWorkbook workbook = ExcelUtil.getInstance().read(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserRecharge tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + 1);
				if(row == null) {
					row = sheet.createRow(i + 1);
				}
				// 账户
				if(tmpUser != null) {
					ExcelUtil.getCell(row, "A").setCellValue(tmpUser.getUsername());
				}
				// 类型
				ExcelUtil.getCell(row, "B").setCellValue("在线存款");
				// 金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getMoney());
				// 时间
				ExcelUtil.getCell(row, "D").setCellValue(tmpBean.getPayTime());
				// 后台单号
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getBillno());
				// 第三方单号
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getPayBillno());
				// 备注信息
				ExcelUtil.getCell(row, "G").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 转账汇款账单
	 * @param list
	 * @return
	 */
	private HSSFWorkbook getTransferExcel(List<UserRecharge> list) {
		try {
			File file = ResourceUtils.getFile(transferTemplate);
			HSSFWorkbook workbook = ExcelUtil.getInstance().read(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserRecharge tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + 1);
				if(row == null) {
					row = sheet.createRow(i + 1);
				}
				// 账户
				if(tmpUser != null) {
					ExcelUtil.getCell(row, "A").setCellValue(tmpUser.getUsername());
				}
				// 类型
				ExcelUtil.getCell(row, "B").setCellValue("转账存款");
				// 金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getMoney());
				// 手续费
				ExcelUtil.getCell(row, "D").setCellValue(tmpBean.getFeeMoney());
				// 到账金额
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getRecMoney());
				// 时间
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getPayTime());
				// 后台单号
				ExcelUtil.getCell(row, "G").setCellValue(tmpBean.getBillno());
				// 银行单号
				ExcelUtil.getCell(row, "H").setCellValue(tmpBean.getPayBillno());
				// 备注信息
				ExcelUtil.getCell(row, "I").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 充值未到账账单
	 * @param list
	 * @return
	 */
	private HSSFWorkbook getSystemExcel(List<UserRecharge> list) {
		try {
			File file = ResourceUtils.getFile(systemTemplate);
			HSSFWorkbook workbook = ExcelUtil.getInstance().read(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserRecharge tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + 1);
				if(row == null) {
					row = sheet.createRow(i + 1);
				}
				// 账户
				if(tmpUser != null) {
					ExcelUtil.getCell(row, "A").setCellValue(tmpUser.getUsername());
				}
				// 类型
				ExcelUtil.getCell(row, "B").setCellValue("充值未到账");
				// 金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getMoney());
				// 时间
				ExcelUtil.getCell(row, "D").setCellValue(tmpBean.getPayTime());
				// 后台单号
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getBillno());
				// 备注信息
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private HSSFWorkbook addOtherRecharge(HSSFWorkbook workbook,List<UserBill> list) {
		try {
			HSSFSheet sheet = workbook.getSheetAt(0);
			int lastRow = ExcelUtil.getRowNum(sheet);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserBill tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + lastRow);
				if(row == null) {
					row = sheet.createRow(i + lastRow);
				}
				// 账户
				if(tmpUser != null) {
					ExcelUtil.getCell(row, "A").setCellValue(tmpUser.getUsername());
				}
				// 类型
				if(tmpBean.getType() == Global.BILL_TYPE_ACTIVITY) {
					ExcelUtil.getCell(row, "B").setCellValue("活动补贴");
				}
				if(tmpBean.getType() == Global.BILL_TYPE_ADMIN_ADD) {
					ExcelUtil.getCell(row, "B").setCellValue("管理员增");
				}
				if(tmpBean.getType() == Global.BILL_TYPE_ADMIN_MINUS) {
					ExcelUtil.getCell(row, "B").setCellValue("管理员减");
				}
				// 金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getMoney());
				// 时间
				ExcelUtil.getCell(row, "D").setCellValue(tmpBean.getTime());
				// 后台单号
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getBillno());
				// 备注信息
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 提现账单
	 * @param list
	 * @return
	 */
	private HSSFWorkbook getWithdrawExcel(List<UserWithdraw> list) {
		try {
			File file = ResourceUtils.getFile(withdrawTemplate);
			HSSFWorkbook workbook = ExcelUtil.getInstance().read(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserWithdraw tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + 1);
				if(row == null) {
					row = sheet.createRow(i + 1);
				}
				// 用户名
				ExcelUtil.getCell(row, "A").setCellValue(tmpBean.getId());
				if(tmpUser != null) {
					ExcelUtil.getCell(row, "B").setCellValue(tmpUser.getUsername());
				}
				// 之前金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getBeforeMoney());
				// 提现金额
				ExcelUtil.getCell(row, "D").setCellValue(tmpBean.getMoney());
				// 到账金额
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getRecMoney());
				// 之后金额
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getAfterMoney());
				// 手续费
				ExcelUtil.getCell(row, "G").setCellValue(tmpBean.getFeeMoney());
				// 订单流水号
				ExcelUtil.getCell(row, "H").setCellValue(tmpBean.getBillno());
				// 支付流水号
				ExcelUtil.getCell(row, "I").setCellValue(tmpBean.getPayBillno());
				// 申请时间
				ExcelUtil.getCell(row, "J").setCellValue(tmpBean.getTime());
				// 支付时间
				ExcelUtil.getCell(row, "K").setCellValue(tmpBean.getOperatorTime());
				// 操作人
				ExcelUtil.getCell(row, "L").setCellValue(tmpBean.getOperatorUser());
				// 订单状态
				ExcelUtil.getCell(row, "M").setCellValue("已完成");
				// 备注信息
				ExcelUtil.getCell(row, "N").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 充值账单
	 * @return
	 */
	private HSSFWorkbook getRechargeExcel(List<UserRecharge> list) {
		try {
			File file = ResourceUtils.getFile(rechargeTemplate);
			HSSFWorkbook workbook = ExcelUtil.getInstance().read(file);
			HSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 0, j = list.size(); i < j; i++) {
				UserRecharge tmpBean = list.get(i);
				UserVO tmpUser = lotteryDataFactory.getUser(tmpBean.getUserId());
				HSSFRow row = sheet.getRow(i + 1);
				if(row == null) {
					row = sheet.createRow(i + 1);
				}
				// ID
				ExcelUtil.getCell(row, "A").setCellValue(tmpBean.getId());
				if(tmpUser != null) {
					// 用户名
					ExcelUtil.getCell(row, "B").setCellValue(tmpUser.getUsername());
				}
				// 金额
				ExcelUtil.getCell(row, "C").setCellValue(tmpBean.getMoney());
				// 支付类型
				String channelName = "";
				if (tmpBean.getChannelId() != null) {
					PaymentChannelVO paymentChannel = lotteryDataFactory.getPaymentChannelVO(tmpBean.getChannelId());
					if (paymentChannel != null) {
						channelName = paymentChannel.getName();
					}
				}
				else {
					channelName = PaymentConstant.formatPaymentChannelType(tmpBean.getType(), tmpBean.getSubtype());
				}

				ExcelUtil.getCell(row, "D").setCellValue(channelName);
				// 下单时间
				ExcelUtil.getCell(row, "E").setCellValue(tmpBean.getTime());
				// 到账时间
				ExcelUtil.getCell(row, "F").setCellValue(tmpBean.getPayTime());
				// 状态
				ExcelUtil.getCell(row, "G").setCellValue("已完成");
				// 订单流水号
				ExcelUtil.getCell(row, "H").setCellValue(tmpBean.getBillno());
				// 第三方订单号
				ExcelUtil.getCell(row, "I").setCellValue(tmpBean.getPayBillno());
				// 订单说明
				ExcelUtil.getCell(row, "J").setCellValue(tmpBean.getRemarks());
			}
			return workbook;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void out(HttpServletResponse response, HSSFWorkbook workbook, String filename) {
		OutputStream os = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
			os = response.getOutputStream();
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(os != null) {
					os.flush();
					os.close();
				}
				response.flushBuffer();
			} catch (Exception e) {
			}
		}
	}
	
	@RequestMapping(value = WUC.LOTTERY_PLATFORM_BILL_DOWNLOAD, method = { RequestMethod.GET })
	@ResponseBody
	public void LOTTERY_PLATFORM_BILL_DOWNLOAD(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		WebJSONObject json = new WebJSONObject(super.getAdminDataFactory());
		AdminUser uEntity = super.getCurrUser(session, request, response);
		if (uEntity != null) {
			String action = HttpUtil.getStringParameterTrim(request, "action");
			String sDate = HttpUtil.getStringParameterTrim(request, "sDate");
			String eDate = HttpUtil.getStringParameterTrim(request, "eDate");
			if (!StringUtil.isNotNull(sDate) || !StringUtil.isNotNull(eDate)) return;
			if (!StringUtil.isNotNull(action)) return;

			// if("thrid".equals(action)) {
			// 	int type = Global.USER_RECHARGE_TYPE_ONLINE;
			// 	List<UserRecharge> list = uRechargeDao.listByTypeAndDate(type, date);
			// 	HSSFWorkbook workbook = getChannelExcel(list);
			// 	out(response, workbook, "recharge-thrid-" + date + ".xls");
			// }
			// if("transfer".equals(action)) {
			// 	int type = Global.USER_RECHARGE_TYPE_TRANSFER;
			// 	List<UserRecharge> list = uRechargeDao.listByTypeAndDate(type, date);
			// 	HSSFWorkbook workbook = getTransferExcel(list);
			// 	out(response, workbook, "recharge-transfer-" + date + ".xls");
			// }
			// if("system".equals(action)) {
			// 	HSSFWorkbook workbook = null;
			// 	try {
			// 		int type = Global.USER_RECHARGE_TYPE_SYSTEM;
			// 		List<UserRecharge> list = uRechargeDao.listByTypeAndDate(type, date);
			// 		workbook = getSystemExcel(list);
			// 	} catch (Exception e) {
			// 		e.printStackTrace();
			// 	}
			// 	try {
			// 		int type = Global.BILL_TYPE_ACTIVITY;
			// 		int[] refType = { 0 };
			// 		List<UserBill> list = uBillDao.listByDateAndType(date, type, refType);
			// 		workbook = addOtherRecharge(workbook, list);
			// 	} catch (Exception e) {
			// 		e.printStackTrace();
			// 	}
			// 	try {
			// 		int type = Global.BILL_TYPE_ADMIN_ADD;
			// 		int[] refType = { };
			// 		List<UserBill> list = uBillDao.listByDateAndType(date, type, refType);
			// 		workbook = addOtherRecharge(workbook, list);
			// 	} catch (Exception e) {
			// 		e.printStackTrace();
			// 	}
			// 	try {
			// 		int type = Global.BILL_TYPE_ADMIN_MINUS;
			// 		int[] refType = { };
			// 		List<UserBill> list = uBillDao.listByDateAndType(date, type, refType);
			// 		workbook = addOtherRecharge(workbook, list);
			// 	} catch (Exception e) {
			// 		e.printStackTrace();
			// 	}
			// 	out(response, workbook, "recharge-system-" + date + ".xls");
			// }
			if("recharge".equals(action)) {
				List<UserRecharge> userRecharges = uRechargeService.listByPayTimeAndStatus(sDate, eDate, 1);
				HSSFWorkbook workbook = getRechargeExcel(userRecharges);
				out(response, workbook, "recharge-" + sDate + ".xls");
			}
			if("withdraw".equals(action)) {
				List<UserWithdraw> list = uWithdrawDao.listByOperatorTime(sDate, eDate);
				HSSFWorkbook workbook = getWithdrawExcel(list);
				out(response, workbook, "withdraw-" + sDate + ".xls");
			}
		} else {
			json.set(2, "2-6");
		}
	}
	
}