package cn.pumpkin.angrypandanfc;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sabirjan.reader.tools.Tool;

import cn.pumpkin.angrypandanfc.data.IDCardBean;
import cn.pumpkin.angrypandanfc.utils.IDCardUtils;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;

    private TextView mNFCText;
    private Button mBtn;
    private Button mParseBtn;

    private ImageView imageView;
    private TextView textView;

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

        checkPermission();
        // verifyStoragePermissions(this);

        imageView=(ImageView)findViewById(R.id.image);
        textView=(TextView)findViewById(R.id.nfctext);


        mBtn = (Button) findViewById(R.id.switchpage);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        mParseBtn = (Button)findViewById(R.id.parse);
        mParseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IDCardBean idCardBean = IDCardUtils.getIdCardDataBean(IDCardUtils.ID);
                imageView.setImageBitmap(Tool.getBitmap(idCardBean.headImage));
                textView.setText(idCardBean.toString());
            }
        });
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE/*new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}*/, REQUEST_EXTERNAL_STORAGE);
        }
            else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "checkPermission: 已经授权！");
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
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

    private static final int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A
            | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_NFC_F
            | NfcAdapter.FLAG_READER_NFC_V | NfcAdapter.FLAG_READER_NFC_BARCODE;

    private void enableReaderMode() {
        // int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
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
                    Log.i(TAG, "update ui");
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
