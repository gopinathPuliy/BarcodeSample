package com.honeywell.barcodeexample;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.pm.ActivityInfo;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;

public class MainActivity extends Activity {

    private static BarcodeReader barcodeReader;
    private AidcManager manager;

    private Button btnAutomaticBarcode;
    private Button btnClientBarcode;
    private Button btnScannerSelectBarcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 33) { // Android 15
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            final View root = findViewById(android.R.id.content);
            if (root != null) {
                ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                    final androidx.core.graphics.Insets sysBars = insets.getInsets(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
                    v.setPadding(v.getPaddingLeft(),sysBars.top+30,v.getPaddingRight(),sysBars.bottom);
                    return insets;});
                root.requestApplyInsets();
            }
        }
        Log.d("MainActivity","Entering into OnCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.MODEL.startsWith("VM1A")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // create the AidcManager providing a Context and a
        // CreatedCallback implementation.
        AidcManager.create(this, new CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                try{
                    barcodeReader = manager.createBarcodeReader();
                }
                catch (InvalidScannerNameException e){
                    Toast.makeText(MainActivity.this, "Invalid Scanner Name Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ActivitySetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity","Entering into onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    static BarcodeReader getBarcodeObject() {
        return barcodeReader;
    }

    /**
     * Create buttons to launch demo activities.
     */
    public void ActivitySetting() {
        Log.d("MainActivity","Entering into ActivitySetting()");
        btnAutomaticBarcode = (Button) findViewById(R.id.buttonAutomaticBarcode);
        btnAutomaticBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.AUTOMATICBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        btnClientBarcode = (Button) findViewById(R.id.buttonClientBarcode);
        btnClientBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent("android.intent.action.CLIENTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });

        btnScannerSelectBarcode = (Button) findViewById(R.id.buttonScannerSelectBarcode);
        btnScannerSelectBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the intent action string from AndroidManifest.xml
                Intent barcodeIntent = new Intent(
                        "android.intent.action.SCANNERSELECTBARCODEACTIVITY");
                startActivity(barcodeIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d("MainActivity","Entering into onDestroy()");
        super.onDestroy();

        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager.close();
        }
    }

}
