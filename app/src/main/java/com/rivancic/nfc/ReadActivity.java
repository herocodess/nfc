package com.rivancic.nfc;

import android.app.PendingIntent;
import android.content.Intent;
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

import java.io.UnsupportedEncodingException;

/**
 * This activity will be invoked with the Intent dispatch system. If no other activity has higher
 * priority of getting the NFC Intent. The filter is more specific.
 */
public class ReadActivity extends AppCompatActivity {

    TextView noTagV;
    TextView tagValueTv;
    View readV;
    Button enterPin;
    String name = "", amt = "", pin = "";

    SharedPreferences pinPreference;
    SharedPreferences.Editor pinPrefsEditor;

    PinDialog pinDialog = new PinDialog();

    private PendingIntent pendingIntent = null;
    private NfcAdapter adapter = null;

    String [] sep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);
        tagValueTv = findViewById(R.id.nfc_value);
        noTagV = findViewById(R.id.no_nfc_tv);
        readV = findViewById(R.id.nfc_read_v);
        enterPin = findViewById(R.id.enter_pin);
        enterPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPin();
            }
        });

        adapter = NfcAdapter.getDefaultAdapter(this);

        pinPreference = getSharedPreferences("pin_prefs", MODE_PRIVATE);
        pinPrefsEditor = pinPreference.edit();
//        readV = findViewById(R.id.nfc_read_v);
    }

    /**
     * The value of the intent is set by the dispatch system. It should carry the NDEF message
     * because the intent filter definition form the Android Manifest file.
     */
    @Override
    protected void onResume() {
        super.onResume();
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
//            NdefMessage[] messages = NfcUtils.getNdefMessages(getIntent());
//            byte[] payload = messages[0].getRecords()[0].getPayload();
//            String id = new String(payload);
//            noTagV.setVisibility(View.GONE);
//            readV.setVisibility(View.VISIBLE);
//            tagValueTv.setText(id);
//            sep = id.split(":");
//            name = sep[0];
//            amt = sep[1];
//            pin = sep[2];
//
//            Log.d("card-amount", amt);
//            Log.d("card-pin", pin);
//
//            pinPrefsEditor.putString("amount", amt.trim());
//            pinPrefsEditor.putString("pin", pin.trim());
//            pinPrefsEditor.apply();

//        }

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        }

        adapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    String tagData = "";

    @Override
    protected void onNewIntent(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (rawMsgs != null) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            NdefRecord cardRecord = msg.getRecords()[0];
            try {
                tagData = readRecord(cardRecord.getPayload());
                String [] sep = tagData.split("\\:");
                name = sep[0];
                amt = sep[1];
                pin = sep[2];
                noTagV.setText(tagData);
//                Log.d("tagData", tagData);
//                Log.d("nameData", name);
//                Log.d("pinData", pin);
//                Log.d("amtData", amt);
                pinPrefsEditor.putString("name", name.trim());
                pinPrefsEditor.putString("amount", amt.trim());
                pinPrefsEditor.putString("pin", pin.trim());
                pinPrefsEditor.apply();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String readRecord(byte[] payload) throws UnsupportedEncodingException {
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 63;

//        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        return new String(payload);
    }
    public void enterPin() {
        pinDialog.show(getSupportFragmentManager(), "Enter " + name + " Pin " );
    }

}
