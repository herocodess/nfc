package com.rivancic.nfc;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class WriteBalance extends AppCompatActivity {

    private TextView bal;
    private Button confirm_transaction;
    private String balance_string, messageToWrite;
    boolean writeMode;
    NfcAdapter nfcAdapter;
    PendingIntent nfcPendingIntent;
    IntentFilter[] writeTagFilters;
    String tagData = "", name = "", amt = "", pin = "", newAmt = "";
    volatile String all = "", all_pref ="", h = "";
    private static final String mimeType = "application";

    SharedPreferences allPref;
    SharedPreferences.Editor allPrefEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_balance);

        allPref = getSharedPreferences("pin_prefs", MODE_PRIVATE);
        allPrefEditor = allPref.edit();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        bal = findViewById(R.id.balance_hint);
        confirm_transaction = findViewById(R.id.write_balance);

        Intent intent = getIntent();
        balance_string = intent.getStringExtra("NonsenseName");

        name = allPref.getString("name", "");
        amt = allPref.getString("amount", "");
        pin = allPref.getString("pin", "");
        newAmt = amt.replace(amt, balance_string);
        all = name+":"+newAmt+":"+pin;
        Log.d("shared_name", name);
        Log.d("shared_amt", amt);
        Log.d("shared_pin", pin);
        Log.d("all_pref", all);

        bal.setText("Balance is: "+balance_string);




        confirm_transaction.setOnClickListener(new ButtonWriteClick());

        nfcPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    class ButtonWriteClick implements View.OnClickListener {


        @Override
        public void onClick(View v) {


            // NFC: Write id to tag
            messageToWrite =all;
            enableTagWriteMode();

            new AlertDialog.Builder(WriteBalance.this).setTitle("Touch tag to write")
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
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//
//        if (rawMsgs != null) {
//            disableTagWriteMode();
//            NdefMessage msg = (NdefMessage) rawMsgs[0];
//            NdefRecord cardRecord = msg.getRecords()[0];
//            try {
//                tagData = readRecord(cardRecord.getPayload());
//
//                allPrefEditor.putString("tagData", tagData);
//                allPrefEditor.apply();
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // Tag writing mode
        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefRecord record = NfcUtils.getMediaRecord(messageToWrite, mimeType);
            if (NfcUtils.writeTag(NfcUtils.getNdefMessage(record), detectedTag)) {
                Toast.makeText(this, "Balance Written", Toast.LENGTH_LONG)
                        .show();
                startActivity(new Intent(WriteBalance.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Write failed", Toast.LENGTH_LONG).show();
            }
        }


    }
//    public String getBal() {
//        h = allPref.getString("tagData", "");
//        Log.d("tagData", h);
//        String [] sep = tagData.split("\\:");
//        Log.d("sepLength", "" + sep.length);
//        name = sep[0];
//        amt = sep[1];
//        pin = sep[2];
//        newAmt = amt.replace(amt, balance_string);
//        all = name+" : "+newAmt+" : "+pin;
//        Log.d("sepData", sep.toString());
//
//        Log.d("nameData", name);
//        Log.d("pinData", pin);
//        Log.d("amount",amt);
//        Log.d("amtData", newAmt);
//        Log.d("all", all);
//        return all;
//    }


    String readRecord(byte[] payload) throws UnsupportedEncodingException {
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 63;

//        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        return new String(payload);
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
