package lottery.domains.content.api.ag;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Nick on 2016/12/30.
 */
@XmlRootElement(name = "result")
public class AGResult {
    @XmlAttribute(name = "info")
    private String info;
    @XmlAttribute(name = "msg")
    private String msg;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
