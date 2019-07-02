package com.onetwothree.addressbook;

/**
 * Created by Jiang Zixiao on 2019/7/2.
 */
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.util.HashMap;

public class QRcodeActivity extends AppCompatActivity {

    private TextView mTv_Result;
    private ImageView mImg;
    private String data;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode);
        mTv_Result= (TextView) findViewById(R.id.tv_result);
        mImg= (ImageView) findViewById(R.id.img);
        Intent  intent = getIntent ();
        data = intent.getStringExtra("data" );
        actionBar = getSupportActionBar();
        actionBar.setTitle("二维码");
        actionBar.setDisplayHomeAsUpEnabled(true);
        make();
    }

    /**
     *生成二维码
     */
    public void make(){
        //生成二维码，然后为二维码增加logo
        Bitmap bitmap= EncodingUtils.createQRCode(data,500,500,
                null
        );
        mImg.setImageBitmap(bitmap);
    }

    /**
     *  扫描二维码
     */
    public void scan(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请CAMERA权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        mImg.setDrawingCacheEnabled(true);
        Bitmap bMap = Bitmap.createBitmap(mImg.getDrawingCache());
        mImg.setDrawingCacheEnabled(false);

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(),intArray);

        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader  reader = new MultiFormatReader();
        HashMap hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        Result result;
        try {
            result = reader.decode(bitmap, hints);
        }
        catch (NotFoundException E) {
            mTv_Result.setText("扫描出错");
            return ;
        }
        mTv_Result.setText(result.toString());
        //startActivityForResult(new Intent(QRcodeActivity.this, CaptureActivity.class),0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i("TAG","resultCode: "+resultCode+" result_ok: "+RESULT_OK);
        if (resultCode==RESULT_OK){
            Bundle bundle=data.getExtras();
            String result= bundle.getString("result");
            mTv_Result.setText(result);
        } if(resultCode == RESULT_CANCELED) {
            mTv_Result.setText("扫描出错");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}