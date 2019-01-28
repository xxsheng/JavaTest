package lottery.domains.pool.play;

/**
 * Created by Nick on 2017-07-27.
 */
public class MultipleResult {
    private String code; // 中奖号码
    private int oddsIndex; // 赔率下标
    private int nums; // 中奖注数

    public MultipleResult() {
    }

    public MultipleResult(String code, int oddsIndex) {
        this.code = code;
        this.oddsIndex = oddsIndex;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getOddsIndex() {
        return oddsIndex;
    }

    public void setOddsIndex(int oddsIndex) {
        this.oddsIndex = oddsIndex;
    }

    public int getNums() {
        return nums;
    }

    public void setNums(int nums) {
        this.nums = nums;
    }

    public void increseNum() {
        this.nums++;
    }
}
