package lottery.domains.content.api.sb;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Nick on 2017-05-28.
 */
public class Win88SBFundTransferResult extends Win88SBResult {
    private Data Data;
    private String opTransId;

    public Win88SBFundTransferResult.Data getData() {
        return Data;
    }

    public void setData(Win88SBFundTransferResult.Data data) {
        Data = data;
    }

    public String getOpTransId() {
        return opTransId;
    }

    public void setOpTransId(String opTransId) {
        this.opTransId = opTransId;
    }

    public class Data {
        @JSONField(name = "trans_id")
        private String transId;

        @JSONField(name = "before_amount")
        private Double beforeAmount;

        @JSONField(name = "after_amount")
        private Double afterAmount;

        private Integer status;

        public String getTransId() {
            return transId;
        }

        public void setTransId(String transId) {
            this.transId = transId;
        }

        public Double getBeforeAmount() {
            return beforeAmount;
        }

        public void setBeforeAmount(Double beforeAmount) {
            this.beforeAmount = beforeAmount;
        }

        public Double getAfterAmount() {
            return afterAmount;
        }

        public void setAfterAmount(Double afterAmount) {
            this.afterAmount = afterAmount;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
