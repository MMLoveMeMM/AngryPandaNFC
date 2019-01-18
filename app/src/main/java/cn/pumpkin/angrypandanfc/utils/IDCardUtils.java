package cn.pumpkin.angrypandanfc.utils;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.sabirjan.reader.tools.Tool;
import com.synjones.bluetooth.DecodeWlt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cn.pumpkin.angrypandanfc.data.IDCardBean;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/18 09:33
 * @des:
 * @see {@link }
 */

public class IDCardUtils {
    private static final String TAG = IDCardUtils.class.getName();

    public static final String ID = "AAAAAA96690508000090010004001852D75FDD4F20002000200020002000200020002000200020002000200031003000310031003900380035003000320030003200566E5753017761883396025EE07356663A531C4E3396216EB065578831003700F75320002000200020002000200020002000200020002000200020002000200020002000200034003300300034003100310031003900380035003000320030003200300030003100380061883396025E6C51895B405CE07356660652405C200020002000200020003200300031003700300037003200370032003000330037003000370032003700200020002000200020002000200020002000200020002000200020002000200020002000574C66007E00320000FF85015151513E710DD564F371E1CD039CB35CAD7816D4EDC9801D66EA2E9C9FF2C7D01E09FB97D7B32A60AE51C32363B027C00BB7CE3C985324FE561B63B0320531E6A58ABD378ED9AA0087AF971BEE9D98C8CA91AED25251515A3E84B7C80A847D98C013F49DC9DBEE89E66DEE3FBC916A54BAFB6B1CCFA9E7D584A9FCB57937AE513560EDD74E589F194250092EEB69BE72547BC91BA1193CC1261F08EEDCAFB6F418F94CC909E99B8CB2644222E5546DD2292DE5559F3FC3BCA32ED57B6BCECBC865A24EE93A9622AA99153CAA4868478CC1AD1E80FEB40DFC3045F9EDB61A5C35041A7A781B09FC7B908736FC05D298AFDF1DDE297B771C54D425FF83C4F4C8A2F085DF3344DAF3DEAC3CF30FDC0E261069DFD9B08F6D0B6876BDD1B923499F7D98C0D087FED88F2509C5BDF7EC1CCADA9E4943DF61B12E8CFD3ED5233E88918B52BAC3BA56E926B3B5FA90D280B6E8507F75B1F3ED53C240680244E7DED25DA66DAB1057F0BEE8065327C12F65A40920BB984BAC6E4893CFF51E6F62F3CFEFCC84C27B0F3CC7A3C96A084C7990B25EBC08055B53AD339AF7F1E7D09FA7C952479CF47CFAD721C5A52FF1F692142727E8E7E2C415711F783FAE510397D9C47D3CD497E1DE8C556C5152A89B28F5489668593947E50FFD183EA59F405360383E42370E3AC99327034CC13157960305EE0F6511214FF3F4491D4F60EA5A154042B6B92A7C9710D53FD6DEE3E7BBCEFFDC71724A975A0D1DBB30311B2BF13D5AA367111B9E33677940A94DDA4BD298CBAF0D5AF5B127F2704035DD8F73919026873CC5544A0587D61FB6BE3C0E829F5F13006EEE4FAFD136C9F712F7E134DEE08EB8D9AA3C96DD1384B87E4BA40A59270A7F9A8DD60E0257A61D43ACAD8FBC3DE796A833FD8B9B8134D18951B36C5137E001E7F4BFDC67BBF577E49B4DC2569979E852579277774F5A43E3327ED1E315E5B69FAE51EE37D83F70BFBC3F8B1156554CEE00D217AA2FAAC66659B2B468F7AF4C5ADC73CD187E477778941823EFE66937450DDA7109CCDAE5AFF2D9BE85E4D7A58C60AB3FA456DBBCA9A0BDA37623D1F45D463E422CA4AFEC8B3046526AB06C9892E197F3DB8BB125D67BB005C88D0482E6951C065DCC24790DDF97D011842B5E402DEE5E25969548031357C49B2E90519E43CFA343079ED1996152D4A26C0CC34832891310C65E42F55A3EB745DBA20A7B09020EA302397672B9000BD8851BA320E76B274A658E5EFD0B49AB84F8621049B93312CD7BB7A6B2B24806BD720B745BF91350AD5E2515845938FB14B9461DC59AFEF6A78F88DE73A95A3E383F8E745065E45F4B36A178BDED834B8CA9DB7BE7745BC04D92E612E4E17659E459172DBA80C6A6BA10EA24D07F13BEC1CC0E81C7E5A23AE1D8CBD370E72CBCF05AC2263B3CB87A34F6";

    public static String bmpPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/photo.bmp";
    public static String wltPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/photo.wlt";

    /**
     * 身份证信息处理
     * 身份证信息(文字+照片)结构：
     * AA AA AA 96 69 05 08 00 00 90 01 00 04 00 +（256  字节文字信息 ）+（1024 字节  照片信息）+（1  字节 CRC）
     *
     * @param dataStr
     */
    public static IDCardBean getIdCardDataBean(String dataStr) {
        Log.i("idcard_str", dataStr);
        IDCardBean idCard = new IDCardBean();
        byte[] data = StringUtils.hexStringToBytes(dataStr);
        Log.i(TAG, dataStr.length() + "");
        if (data.length >= 1295) {
            //1.文字信息处理
            byte[] idWordbytes = Arrays.copyOfRange(data, 14, 270);
            //2.头像处理
            String headStr = dataStr.substring(540, 2588);
            idCard.headImage = StringUtils.hex2byte(headStr);//Arrays.copyOfRange(data,270, 1294);
            try {
                idCard.word.name = new String(Arrays.copyOfRange(idWordbytes, 0, 30), "UTF-16LE").trim().trim();
                idCard.word.gender = new String(Arrays.copyOfRange(idWordbytes, 30, 32), "UTF-16LE").trim();
                idCard.word.nation = new String(Arrays.copyOfRange(idWordbytes, 32, 36), "UTF-16LE").trim();
                idCard.word.birthday = new String(Arrays.copyOfRange(idWordbytes, 36, 52), "UTF-16LE").trim();
                idCard.word.address = new String(Arrays.copyOfRange(idWordbytes, 52, 122), "UTF-16LE").trim();
                idCard.word.idCard = new String(Arrays.copyOfRange(idWordbytes, 122, 158), "UTF-16LE").trim();
                idCard.word.issuingAuthority = new String(Arrays.copyOfRange(idWordbytes, 158, 188), "UTF-16LE").trim();
                idCard.word.startTime = new String(Arrays.copyOfRange(idWordbytes, 188, 204), "UTF-16LE").trim();
                idCard.word.startopTime = new String(Arrays.copyOfRange(idWordbytes, 204, 220), "UTF-16LE").trim();
                //名族的特殊处理
                idCard.word.nation = NationUtils.getNationNameById(idCard.word.nation);

            } catch (UnsupportedEncodingException e) {
            }
        }
        if(data.length>=1294){
            //照片信息处理
            idCard.headImage = Arrays.copyOfRange(data,270, 1295);
            final byte[] imagebytes = Arrays.copyOfRange(data,270, 1295);
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    decodeImagexxx(imagebytes);
                }
            }).start();*/
            // decodeImagexxx(idCard.headImage);
            Tool.getBitmap(idCard.headImage);

        }
        Log.e(TAG, "身份信息 : " + idCard.toString());
        return idCard;
    }

    public static void decodeImagexxx(byte[] wlt) {
        if (wlt == null) {
            return;
        }
        try {
            File wltFile = new File(wltPath);
            FileOutputStream fos = new FileOutputStream(wltFile);
            fos.write(wlt);
            fos.close();

            DecodeWlt dw = new DecodeWlt();
            int result = dw.Wlt2Bmp(wltPath, bmpPath);

            if (result == 1) {
                File f = new File(bmpPath);
                if (f.exists()) {
                    /*headIv.setImageBitmap(BitmapFactory
                            .decodeFile(bmpPath));*/
                }else {
//                    imageViewPhoto.setImageResource(R.drawable.photo);
                }
            } else {
//                imageViewPhoto.setImageResource(R.drawable.photo);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
