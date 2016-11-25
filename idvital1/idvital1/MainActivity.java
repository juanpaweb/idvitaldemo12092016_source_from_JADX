package idvital1.idvital1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends ActionBarActivity {
    ImageButton carrito;
    ImageButton idalerta;
    ImageButton nosotros;
    ImageButton qcode;
    ImageButton qrcode1;

    /* renamed from: idvital1.idvital1.MainActivity.1 */
    class C02351 implements OnClickListener {
        C02351() {
        }

        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, nosotros.class));
        }
    }

    /* renamed from: idvital1.idvital1.MainActivity.2 */
    class C02362 implements OnClickListener {
        C02362() {
        }

        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, qrcode.class));
        }
    }

    /* renamed from: idvital1.idvital1.MainActivity.3 */
    class C02373 implements OnClickListener {
        C02373() {
        }

        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, qrcode1.class));
        }
    }

    /* renamed from: idvital1.idvital1.MainActivity.4 */
    class C02384 implements OnClickListener {
        C02384() {
        }

        public void onClick(View v) {
            MainActivity.this.startActivity(new Intent(MainActivity.this, alertas.class));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0239R.layout.activity_inicio);
        setSupportActionBar((Toolbar) findViewById(C0239R.id.my_toolbar));
        getSupportActionBar().setIcon((int) C0239R.drawable.log_toolbar1);
        this.nosotros = (ImageButton) findViewById(C0239R.id.nosotros);
        this.nosotros.setOnClickListener(new C02351());
        this.qcode = (ImageButton) findViewById(C0239R.id.qcode);
        this.qcode.setOnClickListener(new C02362());
        this.qrcode1 = (ImageButton) findViewById(C0239R.id.qrcode1);
        this.qrcode1.setOnClickListener(new C02373());
        this.idalerta = (ImageButton) findViewById(C0239R.id.idalerta);
        this.idalerta.setOnClickListener(new C02384());
    }
}
