package lottery.domains.content.entity;

import lottery.domains.content.global.Database;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by Nick on 2016/12/24.
 */
@Entity
@Table(name = "user_game_account", catalog = Database.name, uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "platform_id", "model"}),
        @UniqueConstraint(columnNames = {"username", "platform_id", "model"})})
public class UserGameAccount implements Serializable{
    private int id;
    private int userId; // 用户ID
    private int platformId; // 平台ID
    private String username; // 第三方用户名
    private String password; // 第三方密码
    private int model; // 1：真钱；0：试玩
    private String ext1; // 定制属性1
    private String ext2; // 定制属性2
    private String ext3; // 定制属性3
    private String time; // 创建时间

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "user_id", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "platform_id", nullable = false)
    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    @Column(name = "username", nullable = false, length = 50)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "model", nullable = false)
    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    @Column(name = "ext1", length = 128)
    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    @Column(name = "ext2", length = 128)
    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    @Column(name = "ext3", length = 128)
    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    @Column(name = "time", nullable = false, length = 50)
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
