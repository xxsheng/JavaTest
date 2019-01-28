package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by Nick on 2016/12/24.
 */
@Entity
@Table(name = "game", catalog = Database.name)
public class Game implements Serializable{
    private int id; // ID
    private String gameName; // 游戏名称
    private String gameCode; // 游戏编码，对应平台编码
    private int typeId; // 类型ID
    private int platformId; // 平台ID
    private String imgUrl; // 图片URL
    private int sequence; // 排序号
    private int display; // 显示，0：不显示。1：显示
    private int flashSupport; // 支持flash，0：不支持。1：支持
    private int h5Support; // 支持html5，0：不支持。1：支持
    private int progressiveSupport; // 支持奖池，0：不支持。1：支持
    private String progressiveCode; // 奖池代码

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "game_name", nullable = false, length = 128)
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Column(name = "game_code", nullable = false, length = 128)
    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    @Column(name = "type_id", nullable = false)
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Column(name = "platform_id", nullable = false)
    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    @Column(name = "img_url", nullable = false, length = 128)
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Column(name = "sequence", nullable = false)
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Column(name = "display", nullable = false)
    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    @Column(name = "flash_support", nullable = false)
    public int getFlashSupport() {
        return flashSupport;
    }

    public void setFlashSupport(int flashSupport) {
        this.flashSupport = flashSupport;
    }

    @Column(name = "h5_support", nullable = false)
    public int getH5Support() {
        return h5Support;
    }

    public void setH5Support(int h5Support) {
        this.h5Support = h5Support;
    }

    @Column(name = "progressive_support", nullable = false)
    public int getProgressiveSupport() {
        return progressiveSupport;
    }

    public void setProgressiveSupport(int progressiveSupport) {
        this.progressiveSupport = progressiveSupport;
    }

    @Column(name = "progressive_code", length = 128)
    public String getProgressiveCode() {
        return progressiveCode;
    }

    public void setProgressiveCode(String progressiveCode) {
        this.progressiveCode = progressiveCode;
    }
}
