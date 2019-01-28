package lottery.domains.jobs;

import javautils.StringUtil;

import java.util.List;

public class AgBean implements java.io.Serializable{

    private static final long serialVersionUID = 160056186994417905L;

    private Integer result;

    private String msg;

    private String data;
    //电子结果
    private String billno;

    private String username;

    private String billtime;

    private String reckontime;

    private String slottype;

    private String gametype;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBilltime() {
        return billtime;
    }

    public void setBilltime(String billtime) {
        this.billtime = billtime;
    }

    public String getReckontime() {
        return reckontime;
    }

    public void setReckontime(String reckontime) {
        this.reckontime = reckontime;
    }

    public String getSlottype() {
        return slottype;
    }

    public void setSlottype(String slottype) {
        this.slottype = slottype;
    }

    public String getGametype() {
        return gametype;
    }

    public void setGametype(String gametype) {
        this.gametype = gametype;
    }
}
