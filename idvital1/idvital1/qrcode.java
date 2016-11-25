package idvital1.idvital1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.integration.android.IntentIntegrator;
import me.dm7.barcodescanner.zxing.BuildConfig;

public class qrcode extends Activity implements OnClickListener {
    private ImageButton mReadBtn;
    private TextView mResult;
    private TextView mResult1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0239R.layout.activity_qrcode);
        this.mReadBtn = (ImageButton) findViewById(C0239R.id.capture);
        this.mResult = (TextView) findViewById(C0239R.id.result);
        this.mResult1 = (TextView) findViewById(C0239R.id.result1);
        this.mReadBtn.setOnClickListener(this);
    }

    public void onClick(View view) {
        this.mResult.setText(BuildConfig.FLAVOR);
        this.mResult1.setText(BuildConfig.FLAVOR);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientation(90);
        integrator.addExtra(Scan.HEIGHT, Integer.valueOf(300));
        integrator.addExtra(Scan.WIDTH, Integer.valueOf(300));
        integrator.initiateScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE /*49374*/:
                if (resultCode == -1) {
                    String tmp = IntentIntegrator.parseActivityResult(requestCode, resultCode, data).getContents();
                    Log.d("TAG", "OK");
                    Log.d("TAG", "RESULT" + tmp);
                    Log.d("TAG", "----------");
                    String contents = data.getStringExtra(Scan.RESULT);
                    Log.d("TAG", "OK");
                    Log.d("TAG", "RESULT CONTENT : " + contents);
                    Log.d("TAG", "-----------");
                    this.mResult.setText(contents);
                    this.mResult1.setText(contents);
                    return;
                }
                Log.d("TAG", "NOT OK");
            default:
                Log.d("TAG", "NOT RESULT CODE");
        }
    }
}
