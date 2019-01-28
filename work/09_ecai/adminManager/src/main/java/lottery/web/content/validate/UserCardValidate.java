package lottery.web.content.validate;

import javautils.StringUtil;

import org.springframework.stereotype.Component;

import admin.web.WebJSONObject;

@Component
public class UserCardValidate {
	
	public boolean required(WebJSONObject json, Integer bankId, String cardName, String cardId) {
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
    		if(cardId.length() < 10) return false;
    		String nonCardId = cardId.substring(0, cardId.length() - 1);
    		char code = cardId.charAt(cardId.length() - 1);
    		char[] chs = nonCardId.trim().toCharArray();
            int luhmSum = 0;
            for(int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
                int k = chs[i] - '0';
                if(j % 2 == 0) {
                    k *= 2;
                    k = k / 10 + k % 10;
                }
                luhmSum += k;           
            }
            char bit = (luhmSum % 10 == 0) ? '0' : (char)((10 - luhmSum % 10) + '0');
            return bit == code;
    	}
    	return false;
    }
	
}