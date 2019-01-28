package javautils.image;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lottery.domains.content.payment.RX.utils.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2017/1/10.
 */
public class ImageUtil {

    /**
     * 将信息写到二维码中，并进行base64编码
     */
    public static String encodeQR(String signature, int height, int width) {
        ByteArrayOutputStream outputStream = null;
        try {
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(signature, BarcodeFormat.QR_CODE, height, width, hints);

            outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);

            BASE64Encoder encoder = new BASE64Encoder();
            return "data:image/png;base64," + encoder.encode(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
    
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();

            encodeQR("http://static.hs.com/static/images/m_01.png", 200, 200);

            long spent = System.currentTimeMillis() - start;

            System.out.println("耗时" + spent);
        }

		// try {
		// 	String aa="iVBORw0KGgoAAAANSUhEUgAAAMgAAADIAQAAAACFI5MzAAABZklEQVR42u2YMW4EIQxFTcUxuOnA3JRjUOH4f8Mkq1WaKBIudrQFw5vCsvnfZkV/e+RD/kiG2JO0V+0iZUpp2CgxSLEQ7yGXb4xy2/uIQprYW5estmgZX6VQxKLOSGodJRxBUm1vfRWHqG+rTmy/nYOThCphyDvwF/2cJM8zIBEL/N13jhFEjUjTEDuJdh75C0IQ6YREFAXP3T0mBjFLdudjOrnYGT1OEDIsOev9yCVHIbQTOJ9x97+kQQgy6g2tUs1CjwlBFMHChjkFLI9xbQcgleKoLhdG7dWOQCT35Omkr0weyRBkrLGOOvZ2sTvteQJHSfTmKb6QrZ/zxCK9vMJ0GpQ9hyFr3lxlZ3MrIcj3CIDDmLiQIGRNT8JbDlJ7j13t84RTp6mERfZmW3/Mo2fJntDhLk/TiER8SPfWwdgjER/Sm9+qH22fJn7LmdSxa+WKQlaL4B0CXbfJq34Oks//Vf9LvgD9GHM6hjoaIwAAAABJRU5ErkJggg==";
		// 	System.out.println(Base64.decodeToString(aa));
		//
		// 	BASE64Decoder encoder = new BASE64Decoder();
		// 	byte [] bb=encoder.decodeBuffer(aa);
		// 	System.out.println(new String(bb,"UTF-8"));
		// } catch (Exception e) {
		// 	// TODO 自动生成的 catch 块
		// 	e.printStackTrace();
		// }
		
	}
}
