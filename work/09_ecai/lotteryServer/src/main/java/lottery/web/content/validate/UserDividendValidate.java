package lottery.web.content.validate;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javautils.StringUtil;
import lottery.domains.content.biz.UserDailySettleService;
import lottery.domains.content.biz.UserDividendBillService;
import lottery.domains.content.biz.UserDividendService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserDividend;
import lottery.domains.content.entity.UserDividendBill;
import lottery.domains.content.global.Global;
import lottery.domains.pool.DataFactory;
import lottery.web.WebJSON;
import lottery.web.content.utils.UserCodePointUtil;

/**
 * Created by Nick on 2016/11/1.
 */
@Component
public class UserDividendValidate {
    @Autowired
    private UserDividendService uDividendService;

    @Autowired
    private UserDividendBillService uDividendBillService;

    @Autowired
    private UserService uService;

    @Autowired
    private UserDailySettleService uDailySettleService;

    @Autowired
    private UserCodePointUtil uCodePointUtil;

    @Autowired
    private DataFactory dataFactory;

    /**
     * 发起人的账号是否可以发起
     */
    public boolean allowRequestByUserId(int userId) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            return false;
        }

        if (userId == Global.USER_TOP_ID) {
            return false;
        }

        User user = uService.getById(userId);
        if (user == null) {
            return false;
        }

        return allowRequestByUser(user);
    }

    /**
     * 发起人的账号是否可以发起
     */
    public boolean allowRequestByUser(User user) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            return false;
        }

        if (user.getId() == Global.USER_TOP_ID || user.getCode() < 1800) {
            return false;
        }

        // 1997以上或1800以下都是不可以发起的
        if (uCodePointUtil.isLevel1Proxy(user) || user.getCode() < 1800) {
            return false;
        }

//        if (user.getCode() == dataFactory.getCodeConfig().getSysCode()) {
//            // 内部招商不可以发起
//            if (uCodePointUtil.isLevel1Proxy(user)) {
//                return false;
//            }
//        }

        return true;
    }

    /**
     * 是否允许接受契约分红
     * @param requestUser 发起人
     * @param acceptUser 接受人
     */
    public boolean allowAccept(User requestUser, User acceptUser) {
        if (!dataFactory.getDividendConfig().isEnable()) return false;

        // 发起人必须是接受人的直属上级
        if (acceptUser.getUpid() != requestUser.getId()) return false;

        // 总账不可以发起或接受
        if (requestUser.getId() == Global.USER_TOP_ID || acceptUser.getId() == Global.USER_TOP_ID) return false;

        // 1997以上不可以发起或接受
        if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser)) return false;

        // 1800以下不可以发起或接受
        if (requestUser.getCode() < 1800 || acceptUser.getCode() < 1800) return false;

//        if (uCodePointUtil.isLevel2Proxy(requestUser)) {
//            if (uCodePointUtil.isLevel1Proxy(requestUser) || uCodePointUtil.isLevel1Proxy(acceptUser)) {
//                return false;
//            }
//            if (uCodePointUtil.isLevel2Proxy(acceptUser)) {
//                return false;
//            }
//            else if (uCodePointUtil.isLevel2Proxy(requestUser)) {
//                // 招商只允许对直属发起
//                if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
//                    return true;
//                }
//                return false;
//            }
//        }
        
        if (uCodePointUtil.isLevel2Proxy(requestUser)) {
          // 招商只允许对直属发起
          if (uCodePointUtil.isLevel3Proxy(acceptUser)) {
              return true;
          }
          return false;
      }

        // TODO bojin  其它账号只能对自己的平级或直属发起，但账号等级差异不允许超过2级
//        int diffCode = requestUser.getCode() - acceptUser.getCode();
//        if (diffCode > 2 || diffCode < 0) return false;

        return true;
    }

    /**
     * 检查发起契约请求是否合法，并返回相应错误码
     */
    public boolean isRequestLegal(WebJSON json, int requestUserId, int acceptUserId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser,String userLevel) {
    	boolean check = checkForRequest(json, requestUserId, acceptUserId, scaleLevel, lossLevel, salesLevel, minValidUser, userLevel);
        if (!check) {
            return check;
        }

        return true;
    }

    /**
     * 检查接受契约请求是否合法，并返回相应错误码
     */
    public boolean isAgreeLegal(WebJSON json, int userId, int id) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            json.set(2, "2-8"); // 该功能目前没有启用！
            return false;
        }

        UserDividend upuserDividend = uDividendService.getById(id);
        if (upuserDividend == null) {
            json.set(2, "2-6004"); // 您当前未拥有任何契约分红设置，无法发起契约分红！
            return false;
        }

        if (upuserDividend.getStatus() != Global.DIVIDEND_REQUESTED) {
            json.set(2, "2-6014"); // 您的契约分红数据处于非正常状态，请刷新后重试！
            return false;
        }

        if (upuserDividend.getUserId() != userId) {
            json.set(2, "2-6015"); // 这不是您的契约分红，请勿操作！
            return false;
        }

        return true;
    }

    /**
     * 检查拒绝契约请求是否合法，并返回相应错误码
     */
    public boolean isDenyLegal(WebJSON json, int userId, int id) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            json.set(2, "2-8"); // 该功能目前没有启用！
            return false;
        }

        UserDividend upUserDividend = uDividendService.getById(id);
        if (upUserDividend == null) {
            json.set(2, "2-6004"); // 您当前未拥有任何契约分红设置，无法发起契约分红！
            return false;
        }

        if (upUserDividend.getStatus() != Global.DIVIDEND_REQUESTED) {
            json.set(2, "2-6014"); // 您的契约分红数据处于非正常状态，请刷新后重试！
            return false;
        }

        if (upUserDividend.getUserId() != userId) {
            json.set(2, "2-6015"); // 这不是您的契约分红，请勿操作！
            return false;
        }

        return true;
    }

    /**
     * 检查领取契约分红请求是否合法，并返回相应错误码
     */
    public boolean isCollectLegal(WebJSON json, int userId, UserDividendBill userDividendBill) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            json.set(2, "2-8"); // 该功能目前没有启用！
            return false;
        }

        if (userDividendBill == null) {
            json.set(2, "2-6"); // 请求数据错误！
            return false;
        }

        if (userDividendBill.getStatus() != Global.DIVIDEND_BILL_UNCOLLECT && userDividendBill.getStatus() != Global.DIVIDEND_BILL_PART_COLLECTED) {
            json.set(2, "2-6017"); // 该状态不可领取，请联系客服处理！
            return false;
        }

        if (userDividendBill.getUserId() != userId) {
            json.set(2, "2-6015"); // 这不是您的契约分红，请勿操作！
            return false;
        }

        if (userDividendBill.getAvailableAmount() <= 0) {
            json.set(2, "2-6022"); // 没有可以领取的分红金额！
            return false;
        }

        return true;
    }

    private boolean checkForRequest(WebJSON json, int requestUserId, int acceptUserId, String scaleLevel, String lossLevel, String salesLevel, int minValidUser, String userLevel) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            json.set(2, "2-8"); // 该功能目前没有启用！
            return false;
        }
        
        // 检查数据库数据，分别检查发起人和接受人的数据库是否已经有分红，状态，等等,如果这里超过,表示发起人和接受人是合法的,接下来只需要判断比例即可,人数也不用判断,因为只有60招商才需要判断
        boolean checkDBDataForRequest = checkDBDataForRequest(json, requestUserId, acceptUserId);
        if (!checkDBDataForRequest) {
            return false;
        }

        User requestUser = uService.getById(requestUserId); // 发起人
        if (!allowRequestByUser(requestUser)) {
            json.set(2, "2-13"); // 您的账号类型无法使用该功能！
            return false;
        }

        User acceptUser = uService.getById(acceptUserId); // 接受人
        if (!allowAccept(requestUser, acceptUser)) {
            json.set(2, "2-6026"); // 不允许对该账号发起契约分红！
            return false;
        }

        UserDividend requestUserDividend = uDividendService.getByUserId(requestUser.getId());
        if (!checkValidLevel(scaleLevel, salesLevel, lossLevel, requestUserDividend, userLevel)) { 
            json.set(2, "2-6028"); // 契约分红比例不在范围值内！
            return false;
        }

        return true;
    }

    private boolean checkDBDataForRequest(WebJSON json, int requestUserId, int acceptUserId) {
        // 首先查找自己的契约分红设置状态
        UserDividend requestUserDividend = uDividendService.getByUserId(requestUserId);
        if (requestUserDividend == null) {
            json.set(2, "2-6004"); // 您当前未拥有任何契约分红设置，无法发起契约分红！
            return false;
        }
        if (requestUserDividend.getStatus() == Global.DIVIDEND_REQUESTED) {
            json.set(2, "2-6005"); // 请先同意您的契约！
            return false;
        }
        if (requestUserDividend.getStatus() == Global.DIVIDEND_INVALID) {
            json.set(2, "2-6006"); // 您当前拥有的契约分红设置无效，无法发起契约分红！
            return false;
        }
        if (requestUserDividend.getStatus() == Global.DIVIDEND_EXPIRED) {
            json.set(2, "2-6007"); // 您当前拥有的契约分红设置已过期，无法发起契约分红！
            return false;
        }
        if (requestUserDividend.getStatus() != Global.DIVIDEND_VALID) {
            json.set(2, "2-6008"); // 您当前拥有的契约分红设置未生效，无法发起契约分红！
            return false;
        }

        // 再
        UserDividend acceptDividend = uDividendService.getByUserId(acceptUserId);
        if (acceptDividend != null && acceptDividend.getStatus() == Global.DIVIDEND_REQUESTED) {
            json.set(2, "2-6010"); // 您已经对该用户发起过签约了，正在等待对方同意！
            return false;
        }
        if (acceptDividend != null && acceptDividend.getStatus() == Global.DIVIDEND_VALID) {
            json.set(2, "2-6011"); // 您已经和该用户签订了契约分红，无法再次发起！
            return false;
        }

        return true;
    }

    /**
     * 验证是否有未发放分红金额
     */
    public boolean validateUnIssue(WebJSON json, int userId) {
        if (!dataFactory.getDividendConfig().isEnable()) {
            return true;
        }

        double unIssue = uDividendBillService.getTotalUnIssue(userId);

        if(unIssue > 0) {
            BigDecimal bigDecimal = BigDecimal.valueOf(unIssue).abs();
            String amount = bigDecimal.setScale(4, BigDecimal.ROUND_HALF_UP).toString();
            json.set(2, "2-6021", amount);
            return false;
        }
        return true;
    }
    
    /**
     * 验证条款
     * @param scaleLevel
     * @param salesLevel
     * @param lossLevel
     * @return
     */
    public boolean checkValidLevel(String scaleLevel, String salesLevel, String lossLevel, UserDividend upDividend, String userLevel) {
		if (!StringUtil.isNotNull(scaleLevel) || !StringUtil.isNotNull(salesLevel) || !StringUtil.isNotNull(lossLevel) || !StringUtil.isNotNull(userLevel)) {
			return false;
		}
		
		String[] scaleArrs = scaleLevel.split(",");
		String[] upScaleArrs = upDividend.getScaleLevel().split(",");

		String[] salesArrs = salesLevel.split(",");
		String[] upSalesArrs = upDividend.getSalesLevel().split(",");

		String[] lossArrs = lossLevel.split(",");
		String[] upLossArrs = upDividend.getLossLevel().split(",");
		
		String[] userArrs = userLevel.split(",");
		String[] upUserArrs = upDividend.getUserLevel().split(",");

		int maxLength = dataFactory.getDividendConfig().getMaxSignLevel();
		if (scaleArrs.length > maxLength || salesArrs.length > maxLength || lossArrs.length > maxLength || userArrs.length > maxLength) {
			return false;
		}

		if (scaleArrs.length != salesArrs.length || scaleArrs.length != lossArrs.length
				|| salesArrs.length != lossArrs.length || userArrs.length != scaleArrs.length) {
			return false;
		}

		double[] scaleConfig = dataFactory.getDividendConfig().getLevelsScale();
		for (int i = 0; i < scaleArrs.length; i++) {
			double val = Double.valueOf(scaleArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {
				if (val < scaleConfig[0] || val > Double.valueOf(upScaleArrs[upScaleArrs.length - 1])) {
					return false;
				}
			} else {
				if (val < scaleConfig[0] || val > scaleConfig[1]) {
					return false;
				}
			}

			// 必须是递增条款
			if (i > 0 && (Double.valueOf(scaleArrs[i - 1]) >= val)) {
				return false;
			}

			/*
			 *签署规则：销量、亏损大于等于上级某条款，并且分红比例小于等于上级某条款。
			 */
			double tmSales = Double.valueOf(salesArrs[i]);
			double tmLoss = Double.valueOf(lossArrs[i]);

			// 获取需要匹配的上级条款索引
			int upIndex = -1;
			for (int j = 0; j < upScaleArrs.length; j++) {
				// 找到最配的上级条款
				double tmUpSales = Double.valueOf(upSalesArrs[j]);
				double tmUpLoss = Double.valueOf(upLossArrs[j]);
				if(tmSales >= tmUpSales && tmLoss >= tmUpLoss){
					upIndex = j;
				}
			}
			if (upIndex == -1) {
				return false;
			}

			//比对与上级的比例
			double tmUpScale = Double.valueOf(upScaleArrs[upIndex]);
			if (val > tmUpScale) {
				//再次比对与上级下一个比例
				upIndex++;
				if(upIndex >= upScaleArrs.length){
					return false;
				}else{
					double tmUpSales = Double.valueOf(upSalesArrs[upIndex]);
					double tmUpLoss = Double.valueOf(upLossArrs[upIndex]);
					if(tmSales >= tmUpSales && tmLoss >= tmUpLoss){
						tmUpScale = Double.valueOf(upScaleArrs[upIndex]);
						if (val > tmUpScale) {
							return false;
						}
					}else{
						return false;
					}
				}
			}
		}

		double[] salesConfig = dataFactory.getDividendConfig().getLevelsSales();
		for (int i = 0; i < salesArrs.length; i++) {
			double val = Double.valueOf(salesArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {

				double minSales = Double.valueOf(upSalesArrs[0]);
				for (String l : upLossArrs) {
					double ll = Double.valueOf(l);
					if (ll < minSales) {
						minSales = ll;
					}
				}

				if (val < minSales || val > salesConfig[1]) {
					return false;
				}
			} else {
				if (val < salesConfig[0] || val > salesConfig[1]) {
					return false;
				}
			}

			// 必须是递增条款
			// if (i>0 && (Double.valueOf(salesArrs[i-1]) >= val)) {
			// return false;
			// }
		}

		double[] lossConfig = dataFactory.getDividendConfig().getLevelsLoss();
		for (int i = 0; i < lossArrs.length; i++) {
			double val = Double.valueOf(lossArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {

				double minLoss = Double.valueOf(upLossArrs[0]);
				for (String l : upLossArrs) {
					double ll = Double.valueOf(l);
					if (ll < minLoss) {
						minLoss = ll;
					}
				}
				if (val < minLoss || val > lossConfig[1]) {
					return false;
				}
			} else {
				if (val < lossConfig[0] || val > lossConfig[1]) {
					return false;
				}
			}

			// 亏损不能大于销量
			if (Double.valueOf(lossArrs[i]) > Double.valueOf(salesArrs[i])) {
				return false;
			}

			// 必须是递增条款
			// if (i>0 && (Double.valueOf(lossArrs[i-1]) >= val)) {
			// return false;
			// }
		}
		
		int[] userConfig = {dataFactory.getDividendConfig().getMinValidUserl(), 1000};
		for (int i = 0; i < userArrs.length; i++) {
			int val = Integer.valueOf(userArrs[i]);
			// 满足系统配置条件
			if (null != upDividend) {
				int minUser = Integer.valueOf(upUserArrs[0]);
				for (String l : upUserArrs) {
					int ll = Integer.valueOf(l);
					if (ll < minUser) {
						minUser = ll;
					}
				}
				if (val < minUser || val > userConfig[1]) {
					return false;
				}
			} else {
				if (val < userConfig[0] || val > userConfig[1]) {
					return false;
				}
			}

			// 必须是同样人数或者递增人数
			 if (i >0 && (Integer.valueOf(userArrs[i-1]) > val)) {
				 return false;
			 }
		}
		return true;
	}
}
