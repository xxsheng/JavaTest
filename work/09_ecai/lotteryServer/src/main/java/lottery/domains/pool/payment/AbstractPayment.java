package lottery.domains.pool.payment;

import javautils.image.ImageUtil;

import java.util.Map;

/**
 * Created by Nick on 2017/12/6.
 */
public abstract class AbstractPayment {
    public static PrepareResult retPrepareFailed(String errorMsg) {
        PrepareResult prepareResult = new PrepareResult();
        prepareResult.setSuccess(false);
        prepareResult.setErrorMsg(errorMsg);
        return prepareResult;
    }

    public static PrepareResult retPrepareWangYing(String formUrl, Map<String, String> params) {
        PrepareResult prepareResult = new PrepareResult();

        prepareResult.setJumpType(PrepareResult.JUMP_TYPE_FORM);
        prepareResult.setFormParams(params);
        prepareResult.setFormUrl(formUrl);
        prepareResult.setSuccess(true);
        return prepareResult;
    }
    
    public static PrepareResult retPrepareQuick(String formUrl,String qrCode) {
        PrepareResult prepareResult = new PrepareResult();
        prepareResult.setJumpType(PrepareResult.QUICK_TYPE_FORM);
        prepareResult.setFormUrl(formUrl);
        prepareResult.setQrCode(qrCode);
        prepareResult.setSuccess(true);
        return prepareResult;
    }

    public static PrepareResult retPrepareQRCode(String url) {
        PrepareResult prepareResult = new PrepareResult();

      String qrCode = ImageUtil.encodeQR(url, 200, 200);
        prepareResult.setJumpType(PrepareResult.JUMP_TYPE_QR);
        prepareResult.setSuccess(true);
        prepareResult.setQrCode(qrCode);
        return prepareResult;
    }

    public static VerifyResult retVerifyFailed(String output) {
        VerifyResult verifyResult = new VerifyResult();
        verifyResult.setSuccess(false);
        verifyResult.setOutput(output);
        return verifyResult;
    }
}
