package lottery.domains.content;

import admin.web.WebJSONObject;
import lottery.domains.content.entity.PaymentChannel;
import lottery.domains.content.entity.PaymentChannelBank;
import lottery.domains.content.entity.UserCard;
import lottery.domains.content.entity.UserWithdraw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Nick on 2017/12/6.
 */
public abstract class AbstractPayment {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 代付接口，返回第三方的注单ID
     */
    public abstract String daifu(WebJSONObject json, UserWithdraw order, UserCard card, PaymentChannelBank bank, PaymentChannel channel);

    /**
     * 打印一条开始代付的日志
     */
    protected void logStart(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel) {
        String bankCode = bank == null ? "" : bank.getCode();
        log.info("开始[{}]代付，注单ID：{}，银行名称：{}，银行代码: {}, 商户号：{}", channel.getName(), order.getBillno(), order.getBankName(),bankCode, channel.getMerCode());
    }

    /**
     * 打印一条开始代付的日志
     */
    protected void logSuccess(UserWithdraw order, String payOrderId, PaymentChannel channel) {
        log.info("[{}]代付请求成功，我方注单ID:{}，第三方返回注单ID：{}，商户号：{}", channel.getName(), order.getBillno(), payOrderId, channel.getMerCode());
    }

    /**
     * 打印一条代付异常的日志
     */
    protected void logException(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg, Exception e) {
        String bankCode = bank == null ? "" : bank.getCode();
        log.error("[{}]代付发生异常：{}，注单ID:{}，商户号：{}", channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode(), e);
    }

    /**
     * 打印一条代付异常的日志
     */
    protected void logException(PaymentChannel channel, String msg, Exception e) {
        log.error("[{}]发生异常：{}，商户号：{}", channel.getName(), msg, channel.getMerCode(), e);
    }

    /**
     * 打印一条代付日志
     */
    protected void logInfo(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg) {
        String bankCode = bank == null ? "" : bank.getCode();
        log.info("[{}]代付{}，注单ID:{}，商户号：{}", channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode());
    }

    /**
     * 打印一条代付日志
     */
    protected void logInfo(PaymentChannel channel, String msg) {
        log.info("[{}]{}，商户号：{}", channel.getName(), msg, channel.getMerCode());
    }

    /**
     * 打印一条代付日志
     */
    protected void logWarn(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg) {
        String bankCode = bank == null ? "" : bank.getCode();
        log.warn("[{}]代付{}，注单ID:{}，商户号：{}", channel.getName(), msg, order.getBillno(), order.getBankName(), bankCode, channel.getMerCode());
    }

    /**
     * 打印一条代付日志
     */
    protected void logWarn(PaymentChannel channel, String msg) {
        log.warn("[{}]{}，商户号：{}", channel.getName(), msg, channel.getMerCode());
    }

    /**
     * 打印一条代付日志
     */
    protected void logError(UserWithdraw order, PaymentChannelBank bank, PaymentChannel channel, String msg) {
        log.error("[{}]代付{}，注单ID:{}，商户号：{}", channel.getName(), msg, order.getBillno(), channel.getMerCode());
    }

    /**
     * 打印一条代付日志
     */
    protected void logError(PaymentChannel channel, String msg) {
        log.error("[{}]{}，商户号：{}", channel.getName(), msg, channel.getMerCode());
    }
}
