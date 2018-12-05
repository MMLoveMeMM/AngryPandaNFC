package cn.pumpkin.angrypandanfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;

    private TextView mNFCText;

    @Override
    protected void onStart() {
        super.onStart();
        initNfc();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNFCText = (TextView) findViewById(R.id.nfctext);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
            disableReaderMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    CardReader.FILTERS, CardReader.TECHLISTS);
            enableReaderMode();
        }
        Log.e(TAG, IsoDep.class.getName());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, intent.getAction());
        mNFCText.setText((p != null) ? CardReader.load(p) : null);
    }

    private void enableReaderMode() {
        int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        if (mNfcAdapter != null) {
            mNfcAdapter.enableReaderMode(this, new FindReaderCallback(), READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        if (mNfcAdapter != null) {
            mNfcAdapter.disableReaderMode(this);
        }
    }

    private void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()/* 获取数据需要拉起的Activity */).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
    }

    /**
     * 这个发现设备刷卡时被扫描到就会被调用,并且返回卡类型和相关卡相关信息.
     * 但是不能够获取卡读写数据,要读写数据需要在onNewIntent中返回.
     * 只做身份识别,获取基本信息即可以了.
     * 如果是交通卡,那么肯定需要在onNewIntent()中获取其他信息.
     */
    public class FindReaderCallback implements NfcAdapter.ReaderCallback {
        @Override
        public void onTagDiscovered(final Tag arg0) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"update ui");
                    mNFCText.setText(CardReader.tagDiscovered(arg0));
                }
            });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mNfcAdapter == null)
            System.out.println("没有找到NFC卡");
        else if (mNfcAdapter.isEnabled())
            System.out.println("NFC卡可以使用了");
        else
            System.out.println("NFC卡禁用了");
    }


}
