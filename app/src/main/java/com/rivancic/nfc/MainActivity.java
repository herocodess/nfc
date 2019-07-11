package com.rivancic.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button openReadBtn;
    Button openReadExplicitBtn;
    Button openWriteBtn;
    Button openWriteVCardBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        openReadBtn = (Button) findViewById(R.id.open_read_btn);
        openWriteBtn = (Button) findViewById(R.id.open_write_btn);
        openReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReadActivity.class));
            }
        });
        openWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WriteActivity.class));
            }
        });


        if(!NfcUtils.hasNFCSupport(this)) {
            Toast.makeText(this, "NFC is not available for the device.", Toast.LENGTH_LONG).show();
            openReadBtn.setVisibility(View.GONE);
            openReadExplicitBtn.setVisibility(View.GONE);
            openWriteBtn.setVisibility(View.GONE);
        }
    }
}
