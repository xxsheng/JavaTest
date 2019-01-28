package lottery.domains.content.api.ag;

import lottery.domains.content.entity.User;

/**
 * Created by Nick on 2017/1/24.
 */
public class AGValidationResult {
    public boolean success;
    public String xml;
    private User user;

    public AGValidationResult(boolean success, String xml, User user) {
        this.success = success;
        this.xml = xml;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
