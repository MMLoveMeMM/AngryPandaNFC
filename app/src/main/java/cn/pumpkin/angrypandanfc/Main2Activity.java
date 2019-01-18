package cn.pumpkin.angrypandanfc;

import android.nfc.Tag;
import android.nfc.tech.NfcB;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.nfc.NfcAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.provider.Settings;

import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.keshengxuanyi.mobilereader.NFCReaderHelper;
import cn.com.keshengxuanyi.mobilereader.UserInfo;
import cn.pumpkin.angrypandanfc.utils.SpUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Main2Activity extends AppCompatActivity {

    private static ImageView iv_zhaopian;
    static Handler uiHandler = null;

    /**
     * demo使用
     */
    private static String appKey = "941c9b37d4dd4e569ff0320b21d9071c";

    private static String appSecret = "8eb5c020856040f7be7e52cff4ce3a77";

    // 身份证头像解析服务器

    static String headip = "card.jsske.com";

    static String headipbak = "ds.jsske.com";

    static int headport = 9098;

    NfcAdapter mNfcAdapter;
    TextView uuIdText;
    private NFCReaderHelper mNFCReaderHelper;
    PendingIntent mNfcPendingIntent;
    TextView tvname;
    TextView tvsex;
    TextView tvnation;
    TextView tvbirthday;
    TextView tvcode;
    TextView tvaddress;
    TextView tvdate;
    TextView tvdepar;
    TextView readerstatText;

    TextView tvshijiancontent;

    Button buttonset;

    private Context context = null;

    /**
     * 是否本地解析头像
     */
    private Boolean isLocalParsingImage = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        uuIdText = ((TextView) findViewById(R.id.tvuuid));
        iv_zhaopian = (ImageView) findViewById(R.id.ivHead);
        tvname = (TextView) findViewById(R.id.tvname2);
        tvsex = (TextView) findViewById(R.id.tvsex2);
        tvnation = (TextView) findViewById(R.id.tvnation2);
        tvbirthday = (TextView) findViewById(R.id.tvbirthday2);
        tvcode = (TextView) findViewById(R.id.tvcode2);
        tvaddress = (TextView) findViewById(R.id.tvaddress2);
        tvdate = (TextView) findViewById(R.id.tvdate2);
        tvdepar = (TextView) findViewById(R.id.tvdepart2);
        readerstatText = (TextView) findViewById(R.id.readerstatText);
        tvshijiancontent = (TextView) findViewById(R.id.tvshijiancontent);

        buttonset = (Button) findViewById(R.id.buttonset);
        buttonset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConfigActivity.class);
                startActivity(intent);
            }
        });

        context = this;
        uiHandler = new MyHandler(this);

        // 设备注册
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // 判断设备是否可用
        if (mNfcAdapter == null) {
            toast("该设备不支持nfc!");

            return;
        }

        if ((null != mNfcAdapter) && !mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            finish();

            return;
        }

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mNFCReaderHelper = new NFCReaderHelper(this, uiHandler, appKey,
                appSecret, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (null != mNfcPendingIntent) {
                mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
                        null, null);
                resolvIntent(getIntent());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 0表示本地解析，1-表示网络解析
            isLocalParsingImage = !"1".equals(SpUtil.getString(context,
                    "touxiang_set", "0"));

        } catch (Exception ex) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//		try {
//			if (mNfcAdapter != null) {
//				mNfcAdapter.disableForegroundDispatch(this);
//
//				mNfcAdapter.disableForegroundNdefPush(this);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            setIntent(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    synchronized void resolvIntent(Intent intent) {
        try {
            String action = intent.getAction();

            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                new NFCReadTask(intent, context).execute();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // AA0512A010AAAAAA96690508000090010004001852D75FDD4F20002000200020002000200020002000200020002000200031003000310031003900380035003000320030003200566E5753017761883396025EE07356663A531C4E3396216EB065578831003700F75320002000200020002000200020002000200020002000200020002000200020002000200034003300300034003100310031003900380035003000320030003200300030003100380061883396025E6C51895B405CE07356660652405C200020002000200020003200300031003700300037003200370032003000330037003000370032003700200020002000200020002000200020002000200020002000200020002000200020002000574C66007E00320000FF85015151513E710DD564F371E1CD039CB35CAD7816D4EDC9801D66EA2E9C9FF2C7D01E09FB97D7B32A60AE51C32363B027C00BB7CE3C985324FE561B63B0320531E6A58ABD378ED9AA0087AF971BEE9D98C8CA91AED25251515A3E84B7C80A847D98C013F49DC9DBEE89E66DEE3FBC916A54BAFB6B1CCFA9E7D584A9FCB57937AE513560EDD74E589F194250092EEB69BE72547BC91BA1193CC1261F08EEDCAFB6F418F94CC909E99B8CB2644222E5546DD2292DE5559F3FC3BCA32ED57B6BCECBC865A24EE93A9622AA99153CAA4868478CC1AD1E80FEB40DFC3045F9EDB61A5C35041A7A781B09FC7B908736FC05D298AFDF1DDE297B771C54D425FF83C4F4C8A2F085DF3344DAF3DEAC3CF30FDC0E261069DFD9B08F6D0B6876BDD1B923499F7D98C0D087FED88F2509C5BDF7EC1CCADA9E4943DF61B12E8CFD3ED5233E88918B52BAC3BA56E926B3B5FA90D280B6E8507F75B1F3ED53C240680244E7DED25DA66DAB1057F0BEE8065327C12F65A40920BB984BAC6E4893CFF51E6F62F3CFEFCC84C27B0F3CC7A3C96A084C7990B25EBC08055B53AD339AF7F1E7D09FA7C952479CF47CFAD721C5A52FF1F692142727E8E7E2C415711F783FAE510397D9C47D3CD497E1DE8C556C5152A89B28F5489668593947E50FFD183EA59F405360383E42370E3AC99327034CC13157960305EE0F6511214FF3F4491D4F60EA5A154042B6B92A7C9710D53FD6DEE3E7BBCEFFDC71724A975A0D1DBB30311B2BF13D5AA367111B9E33677940A94DDA4BD298CBAF0D5AF5B127F2704035DD8F73919026873CC5544A0587D61FB6BE3C0E829F5F13006EEE4FAFD136C9F712F7E134DEE08EB8D9AA3C96DD1384B87E4BA40A59270A7F9A8DD60E0257A61D43ACAD8FBC3DE796A833FD8B9B8134D18951B36C5137E001E7F4BFDC67BBF577E49B4DC2569979E852579277774F5A43E3327ED1E315E5B69FAE51EE37D83F70BFBC3F8B1156554CEE00D217AA2FAAC66659B2B468F7AF4C5ADC73CD187E477778941823EFE66937450DDA7109CCDAE5AFF2D9BE85E4D7A58C60AB3FA456DBBCA9A0BDA37623D1F45D463E422CA4AFEC8B3046526AB06C9892E197F3DB8BB125D67BB005C88D0482E6951C065DCC24790DDF97D011842B5E402DEE5E25969548031357C49B2E90519E43CFA343079ED1996152D4A26C0CC34832891310C65E42F55A3EB745DBA20A7B09020EA302397672B9000BD8851BA320E76B274A658E5EFD0B49AB84F8621049B93312CD7BB7A6B2B24806BD720B745BF91350AD5E2515845938FB14B9461DC59AFEF6A78F88DE73A95A3E383F8E745065E45F4B36A178BDED834B8CA9DB7BE7745BC04D92E612E4E17659E459172DBA80C6A6BA10EA24D07F13BEC1CC0E81C7E5A23AE1D8CBD370E72CBCF05AC2263B3CB87A34F6
    private class NFCReadTask extends AsyncTask<Void, Void, String> {
        private Intent mIntent = null;
        private Context context = null;
        private long beginTime;

        public NFCReadTask(Intent i, Context contextTemp) {
            mIntent = i;
            context = contextTemp;
        }

        @Override
        protected String doInBackground(Void... params) {

            beginTime = System.currentTimeMillis();

            String strCardInfo = mNFCReaderHelper.readCardWithIntent(mIntent);

            // 获取uuid
            String uuid = mNFCReaderHelper.readCardUUId(mIntent);

            return uuid + "," + strCardInfo;
        }

        @Override
        protected void onPostExecute(String strCardInfo) {
            super.onPostExecute(strCardInfo);

            String uuid = "";
            try {
                uuid = strCardInfo.split(",")[0];
                strCardInfo = strCardInfo.split(",")[1];
            } catch (Exception ex) {

            }

            uuIdText.setText(uuid);
            tvshijiancontent.setText((System.currentTimeMillis() - beginTime)
                    + "毫秒");

            if ((null != strCardInfo) && (strCardInfo.length() > 1600)) {
                UserInfo userInfo = mNFCReaderHelper
                        .parsePersonInfoNew(strCardInfo);
                tvname.setText(userInfo.name);
                tvsex.setText(userInfo.sex);
                tvnation.setText(userInfo.nation);
                tvbirthday.setText(userInfo.brithday);
                tvcode.setText(userInfo.id);
                tvaddress.setText(userInfo.address);
                tvdate.setText(userInfo.exper + "-" + userInfo.exper2);
                tvdepar.setText(userInfo.issue);

                // TODO:
                if (isLocalParsingImage) {
                    // 本地动态库解析
                    Bitmap bm = mNFCReaderHelper.decodeImagexxx(strCardInfo);
                    iv_zhaopian.setImageBitmap(bm);
                } else {
                    // 网络解析头像
                    ShowHeadThread showThread = new ShowHeadThread();
                    showThread.img = mNFCReaderHelper
                            .decodeImageByte(strCardInfo);
                    showThread.start();
                }

            }
        }
    }

    static class ShowHeadThread extends Thread {
        static Handler handler = new Handler();
        byte[] img; // 记录用户头像

        private byte[] regulardata(byte[] sourbyte) {
            int len = sourbyte.length;
            byte[] desbyte = new byte[len + 8];
            String strbin = Integer.toBinaryString(len);

            int tmplen = strbin.length();

            for (int i = 0; i < (16 - tmplen); i++) {
                strbin = "0" + strbin;
            }

            String tmp1 = strbin.substring(0, 8);
            desbyte[4] = (byte) ((int) Integer.valueOf(tmp1, 2));
            tmp1 = strbin.substring(8, 16);
            desbyte[5] = (byte) ((int) Integer.valueOf(tmp1, 2));
            desbyte[0] = (byte) 0xff;
            desbyte[1] = 3;
            desbyte[2] = 5;
            desbyte[3] = 0;
            desbyte[len - 2] = 0;
            desbyte[len - 1] = (byte) 0xaa;
            System.arraycopy(sourbyte, 0, desbyte, 6, sourbyte.length);

            return desbyte;
        }

        @Override
        public synchronized void run() {
            DatagramPacket packet = null;
            DatagramSocket udpSocket = null;
            DatagramPacket packet2 = null;

            byte[] tmp = new byte[5000];
            for (int i = 0; i < 100; i++) {
                tmp[i] = 0x00;
            }
            byte[] lastimg = null;

            try {
                byte[] img2 = regulardata(img);

                packet = new DatagramPacket(img2, img2.length,
                        InetAddress.getByName(headip), headport);
                packet.setData(img2, 0, img2.length);

                udpSocket = new DatagramSocket();
                udpSocket.send(packet);

                try {
                    packet2 = new DatagramPacket(tmp, tmp.length);
                    udpSocket.setSoTimeout(1500);
                    udpSocket.receive(packet2);
                    lastimg = new byte[packet2.getLength() - 8];
                    System.arraycopy(tmp, 6, lastimg, 0, lastimg.length);
                    data2view(lastimg);
                } catch (SocketTimeoutException ee) {
                    ee.printStackTrace();

                    // 如果接收超时，则再发
                    try {
                        packet = new DatagramPacket(img2, img2.length,
                                InetAddress.getByName(headipbak), headport);
                        packet.setData(img2, 0, img2.length);

                        udpSocket = new DatagramSocket();
                        udpSocket.send(packet);
                        packet2 = new DatagramPacket(tmp, tmp.length);
                        udpSocket.setSoTimeout(1500);
                        udpSocket.receive(packet2);
                        lastimg = new byte[packet2.getLength() - 8];
                        System.arraycopy(tmp, 6, lastimg, 0, lastimg.length);
                        data2view(lastimg);
                    } catch (SocketTimeoutException see) {
                        see.printStackTrace();
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        void data2view(final byte[] imgByte) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte,
                                0, imgByte.length);
                        iv_zhaopian.setImageBitmap(bitmap);
                    } catch (Exception ee) {
                    }
                }
            });
        }
    }

    class MyHandler extends Handler {
        private Main2Activity activity;

        MyHandler(Main2Activity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:

                    String msgTemp = (String) msg.obj;
                    readerstatText.setText(msgTemp);

                    break;
            }
        }
    }
}
