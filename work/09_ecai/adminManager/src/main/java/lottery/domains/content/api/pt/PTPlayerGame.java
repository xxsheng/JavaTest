package lottery.domains.content.api.pt;

import java.util.List;

/**
 * Created by Nick on 2016/12/27.
 */
public class PTPlayerGame {
    private List<PTPlayerGameResult> result;
    private PTPlayerGamePagination pagination;

    public List<PTPlayerGameResult> getResult() {
        return result;
    }

    public void setResult(List<PTPlayerGameResult> result) {
        this.result = result;
    }

    public PTPlayerGamePagination getPagination() {
        return pagination;
    }

    public void setPagination(PTPlayerGamePagination pagination) {
        this.pagination = pagination;
    }
}
