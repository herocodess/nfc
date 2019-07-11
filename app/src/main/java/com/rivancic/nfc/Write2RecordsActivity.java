package com.rivancic.nfc;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Write2RecordsActivity extends AppCompatActivity {

    private static final String url = "https://www.useit.at/";
    Button writeBtn;
    EditText nfcMessageEt;
    String messageToWrite;
    boolean writeMode;
    NfcAdapter nfcAdapter;
    PendingIntent nfcPendingIntent;
    IntentFilter[] writeTagFilters;
    private static final String mimeType = "application/com.rivancic.nfc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_activity);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcMessageEt = (EditText) findViewById(R.id.nfc_message_et);
        writeBtn = (Button) findViewById(R.id.write_btn);
        writeBtn.setOnClickListener(new ButtonWriteClick());
        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    class ButtonWriteClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {


            // NFC: Write id to tag
            messageToWrite = nfcMessageEt.getText().toString();
            enableTagWriteMode();

            new AlertDialog.Builder(Write2RecordsActivity.this).setTitle("Touch tag to write")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            disableTagWriteMode();
                        }
                    }).create().show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Tag writing mode
        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefRecord record = NfcUtils.getMediaRecord(messageToWrite, mimeType);
            if (NfcUtils.writeTag(NfcUtils.getNdefMessage(record), detectedTag)) {
                Toast.makeText(this, "Success: Wrote custom media to nfc tag", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this, "Write failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void disableTagWriteMode() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        writeMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        writeTagFilters = new IntentFilter[]{tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }
}
