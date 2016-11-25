package idvital1.idvital1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class splash extends AppCompatActivity {

    /* renamed from: idvital1.idvital1.splash.1 */
    class C02421 implements Runnable {
        C02421() {
        }

        public void run() {
            splash.this.startActivity(new Intent(splash.this, MainActivity.class));
            splash.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0239R.layout.activity_splash);
        new Handler().postDelayed(new C02421(), 3000);
    }
}
