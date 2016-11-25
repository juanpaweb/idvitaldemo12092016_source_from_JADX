package idvital1.idvital1;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler;

public class qrcode1 extends AppCompatActivity implements ResultHandler {
    private ZXingScannerView mScannerView;

    /* renamed from: idvital1.idvital1.qrcode1.1 */
    class C02401 implements OnClickListener {
        final /* synthetic */ Dialog val$dialog;

        C02401(Dialog dialog) {
            this.val$dialog = dialog;
        }

        public void onClick(View v) {
            qrcode1.this.mScannerView.resumeCameraPreview(qrcode1.this);
            this.val$dialog.dismiss();
        }
    }

    /* renamed from: idvital1.idvital1.qrcode1.2 */
    class C02412 implements OnClickListener {
        final /* synthetic */ String val$shareResult;

        C02412(String str) {
            this.val$shareResult = str;
        }

        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.TEXT", this.val$shareResult);
            sendIntent.setType("text/plain");
            qrcode1.this.getBaseContext().startActivity(sendIntent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0239R.layout.activity_qrcode1);
        this.mScannerView = new ZXingScannerView(this);
        setContentView(this.mScannerView);
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera();
    }

    public void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
    }

    public void onResume() {
        super.onResume();
        this.mScannerView.resumeCameraPreview(this);
    }

    public void handleResult(Result rawResult) {
        Log.e("handler", rawResult.getText());
        Log.e("handler", rawResult.getBarcodeFormat().toString());
        String shareResult = rawResult.getText();
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-1));
        dialog.setContentView(C0239R.layout.activity_qrcode1);
        ImageView click_ok = (ImageView) dialog.findViewById(C0239R.id.click_ok);
        ImageView click_share = (ImageView) dialog.findViewById(C0239R.id.click_share);
        ((TextView) dialog.findViewById(C0239R.id.qrResult)).setText(shareResult);
        click_ok.setOnClickListener(new C02401(dialog));
        click_share.setOnClickListener(new C02412(shareResult));
        dialog.show();
    }
}
