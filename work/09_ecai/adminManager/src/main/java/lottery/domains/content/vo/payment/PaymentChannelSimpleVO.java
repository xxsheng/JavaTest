package lottery.domains.content.vo.payment;

import lottery.domains.content.entity.PaymentChannel;

/**
 * Created by Nick on 2017/12/6.
 */
public class PaymentChannelSimpleVO {
    private int id;
    private String name; // 后台名称
    private String channelCode; // 通道编码
    private int status; // 状态：0：启用；-1：禁用
    private int type; // 支付类型；1：网银充值；2：手机充值；3：管理员增；4：上下级转账；
    private int subType; // 支付子类型；1：网银充值(1：网银在线；2：网银转账；3：快捷支付；4：银联扫码；5：网银扫码转账；)；2：手机充值（1：微信在线；2：微信扫码转账；3：支付宝在线；4：支付宝扫码转账；5：QQ在线、6：QQ扫码转账；7：京东钱包）；3：系统充值（1：充值未到账；2：优惠活动；3：管理员增；4：管理员减)；4：上下级转账(1：转入、2：转出)
    private int apiPayStatus; // 是否开启API代付，0：启用：-1：禁用；

    public PaymentChannelSimpleVO(PaymentChannel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.channelCode = channel.getChannelCode();
        this.status = channel.getStatus();
        this.type = channel.getType();
        this.subType = channel.getSubType();
        this.apiPayStatus = channel.getApiPayStatus();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getApiPayStatus() {
        return apiPayStatus;
    }

    public void setApiPayStatus(int apiPayStatus) {
        this.apiPayStatus = apiPayStatus;
    }
}
