package lottery.domains.content.vo.lottery;

import lottery.domains.content.entity.Lottery;

/**
 * Created by Nick on 2017-09-08.
 */
public class LotteryVO {
    private int id;
    private String showName;
    private String shortName;
    private int type;
    private int status;
    private int display;

    public LotteryVO(Lottery lottery) {
        this.id = lottery.getId();
        this.showName = lottery.getShowName();
        this.shortName = lottery.getShortName();
        this.type = lottery.getType();
        this.status = lottery.getStatus();
        this.display = lottery.getDisplay();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }
}
