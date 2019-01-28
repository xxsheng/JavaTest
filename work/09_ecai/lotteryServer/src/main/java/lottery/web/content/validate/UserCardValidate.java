package lottery.web.content.validate;

import javautils.StringUtil;
import lottery.domains.content.biz.UserBlacklistService;
import lottery.domains.content.biz.UserService;
import lottery.domains.content.biz.UserWhitelistService;
import lottery.domains.content.entity.User;
import lottery.domains.content.entity.UserBlacklist;
import lottery.domains.content.entity.UserWhitelist;
import lottery.web.WebJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCardValidate {
	@Autowired
	private UserService uService;

	@Autowired
	private UserBlacklistService uBlacklistService;

	@Autowired
	private UserWhitelistService uWhitelistService;
	
	public boolean required(WebJSON json, Integer bankId, String cardName, String cardId) {
		if(bankId == null) {
			json.set(2, "2-1011");
			return false;
		}
		if(!StringUtil.isNotNull(cardName)) {
			json.set(2, "2-1012");
			return false;
		}
		if(!StringUtil.isNotNull(cardId)) {
			json.set(2, "2-1013");
			return false;
		}
		return true;
	}
	
	/**
	 * 银行卡LUHM验证
	 */
	public boolean checkCardId(final String cardId){
    	if(StringUtil.isNotNull(cardId) && cardId.matches("\\d+")) {
			int len = cardId.length();
    		if(len < 10 || len > 28) return false;

			return true;
    	}
    	return false;
    	// if(StringUtil.isNotNull(cardId) && cardId.matches("\\d+")) {
    	// 	if(cardId.length() < 10) return false;
    	// 	String nonCardId = cardId.substring(0, cardId.length() - 1);
         //    char code = cardId.charAt(cardId.length() - 1);
         //    char[] chs = nonCardId.trim().toCharArray();
         //    int luhmSum = 0;
         //    for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
         //        int k = chs[i] - '0';
         //        if(j % 2 == 0) {
         //            k *= 2;
         //            k = k / 10 + k % 10;
         //        }
         //        luhmSum += k;
         //    }
         //    char bit = (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
         //    return bit == code;
    	// }
    	// return false;
    }
	
	/**
	 * 白名单黑名单验证
	 */
	public boolean testBlackWhiteList(WebJSON json, User uBean, int bankId, String cardName, String cardId, String ip) {
		// 验证白名单
		List<UserWhitelist> wlist = uWhitelistService.getByUsername(uBean.getUsername());
		if(wlist != null && wlist.size() > 0) {
			for (UserWhitelist wBean : wlist) {
				if(StringUtil.isNotNull(wBean.getCardName()) && StringUtil.isNotNull(wBean.getCardId()) && wBean.getBankId() != null) {
					if(wBean.getCardName().equals(cardName) && wBean.getCardId().equals(cardId) && wBean.getBankId().intValue() == bankId) {
						return true;
					} else {
						json.set(2, "2-1031");
						return false;
					}
				}
			}
		}
		// 验证黑名单
		boolean isPassBlacklist = true;
		List<UserBlacklist> nlist = uBlacklistService.getByCard(cardName, cardId);
		if(nlist != null && nlist.size() > 0) {
			isPassBlacklist = false;
		}
		List<UserBlacklist> blist = uBlacklistService.getByUsername(uBean.getUsername());
		if(blist != null && blist.size() > 0) {
			if(blist.size() >= 3) {
				if(uBean.getAStatus() >= 0) { // 必须是正常状态
					uService.updateAStatus(uBean.getId(), -1, "多次尝试绑定资料失败!");
				}
			} else if(blist.size() >= 2) {
				// 限制下注
				if(uBean.getBStatus() >= 0) { // 必须是正常状态
					uService.updateBStatus(uBean.getId(), -1, "多次尝试绑定资料失败!");
				}
			}
			isPassBlacklist = false;
		}
		if(!isPassBlacklist) {
			uBlacklistService.add(uBean.getUsername(), bankId, cardName, cardId, ip);
			json.set(2, "2-1016");
			return false;
		}
		return true;
	}
	
}