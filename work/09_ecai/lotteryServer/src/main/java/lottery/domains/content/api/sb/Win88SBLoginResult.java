package lottery.domains.content.api.sb;

/**
 * Created by Nick on 2017-05-28.
 */
public class Win88SBLoginResult extends Win88SBResult {
    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
