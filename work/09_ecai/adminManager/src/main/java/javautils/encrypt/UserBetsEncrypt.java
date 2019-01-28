package javautils.encrypt;

import lottery.domains.content.entity.UserBetsOriginal;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by Nick on 2017/1/15.
 */
public class UserBetsEncrypt {
    private static final DESUtil DES = DESUtil.getInstance();
    private static final String DES_KEY = "#$ddw4FFWfg#GR0(DSFW@#?!@#!@#$C$$3GhyUhb";

    public static String encryptCertification(String certification) {
        return DES.encryptStr(certification, DES_KEY);
    }

    public static String decryptCertification(String certification) {
        return DES.decryptStr(certification, DES_KEY);
    }

    public static String getRandomCertification() {
        return RandomStringUtils.random(10, true, true);
    }

    public static String getIdentification(UserBetsOriginal original, String certification) {
        StringBuffer sb = new StringBuffer();

        int point = Double.valueOf(original.getPoint()).intValue();
        int money = Double.valueOf(original.getMoney()).intValue();

        sb.append(original.getId())
                .append(original.getBillno())
                .append(original.getUserId())
                .append(original.getType())
                .append(original.getLotteryId())
                .append(original.getExpect())
                .append(original.getRuleId())
                .append(DigestUtils.md5Hex(original.getCodes()))
                .append(original.getNums())
                .append(original.getModel())
                .append(original.getMultiple())
                .append(original.getCode())
                .append(point)
                .append(money)
                .append(original.getTime())
                .append(original.getStopTime())
                .append(original.getOpenTime())
                .append(original.getStatus())
                .append(original.getOpenCode())
                .append(original.getPrizeMoney())
                .append(original.getPrizeTime())
                .append(original.getChaseBillno())
                .append(original.getChaseBillno())
                .append(original.getPlanBillno())
                .append(original.getRewardMoney())
                .append(certification);

        String thisData = sb.toString();
        return DigestUtils.md5Hex(thisData + certification);
    }
}
