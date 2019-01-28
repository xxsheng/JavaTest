package lottery.domains.content.vo.user;

import lottery.domains.content.entity.Game;
import lottery.domains.pool.LotteryDataFactory;

/**
 * Created by Nick on 2016/12/25.
 */
public class GameVO {
    private String platformName;
    private String typeName;
    private Game bean;

    public GameVO(Game bean, LotteryDataFactory dataFactory) {
        this.bean = bean;

        this.platformName = dataFactory.getSysPlatform(bean.getPlatformId()).getName();
        this.typeName = dataFactory.getGameType(bean.getTypeId()).getTypeName();
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Game getBean() {
        return bean;
    }

    public void setBean(Game bean) {
        this.bean = bean;
    }
}
