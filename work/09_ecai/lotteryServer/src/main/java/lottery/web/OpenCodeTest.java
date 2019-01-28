package lottery.web;

import java.util.List;

/**
 * Created by Nick on 2017-09-02.
 */
public class OpenCodeTest {
    private String rows;
    private String code;
    private String info;
    private List<Data> data;

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        private String expect;
        private String opencode;
        private String opentime;
        private String opentimestamp;

        public String getExpect() {
            return expect;
        }

        public void setExpect(String expect) {
            this.expect = expect;
        }

        public String getOpencode() {
            return opencode;
        }

        public void setOpencode(String opencode) {
            this.opencode = opencode;
        }

        public String getOpentime() {
            return opentime;
        }

        public void setOpentime(String opentime) {
            this.opentime = opentime;
        }

        public String getOpentimestamp() {
            return opentimestamp;
        }

        public void setOpentimestamp(String opentimestamp) {
            this.opentimestamp = opentimestamp;
        }
    }
}
