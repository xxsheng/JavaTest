package lottery.web.helper.speed;

import java.io.Serializable;

/**
 * Created by Nick on 2017-09-16.
 */
public class AccessHistory implements Serializable{
    private static final long serialVersionUID = -7287663103348713039L;

    private String sessionID = null;
    private AccessTime accessTimeQueue;

    public AccessHistory(int inMilliSeconds, int times) {
        accessTimeQueue = new AccessTime(inMilliSeconds, times);
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public AccessTime getAccessTimeQueue() {
        return accessTimeQueue;
    }
}
