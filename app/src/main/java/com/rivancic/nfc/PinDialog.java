package com.rivancic.nfc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PinDialog extends AppCompatDialogFragment {

    public EditText pinEt, amant;
    private Context context;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View view;
    String pinS = "", pin_stud, amountS = "", amount_stud;
    SharedPreferences getPinPreferences, floatPref;
    SharedPreferences.Editor getPinPrefsEditor, floatPrefEditor;
    double balance = 0.0;
    float bal = 0;

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.layout_dialog, null);
        pinEt = view.findViewById(R.id.edit_text_phone);
        amant = view.findViewById(R.id.edit_text_amount);

        getPinPreferences = getActivity().getSharedPreferences("pin_prefs", Context.MODE_PRIVATE);
        getPinPrefsEditor = getPinPreferences.edit();

//        floatPref = getActivity().getSharedPreferences("bal_float", Context.MODE_PRIVATE);
//        floatPrefEditor = floatPref.edit();

        builder.setView(view)
                .setTitle("Enter Student Pin")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pinS = pinEt.getText().toString();
                        amountS = amant.getText().toString();
                        pin_stud = getPinPreferences.getString("pin", "");
                        amount_stud = getPinPreferences.getString("amount", "");
                        Double aS = Double.parseDouble(amount_stud);
                        Double ac = Double.parseDouble(amountS);
                        Log.d("pinStud", pin_stud);
                        Log.d("amtStud", amount_stud);
                        Log.d("pinssssss", pinS);

                        if ((pinS.equals(pin_stud)) && (ac<=aS)) {
                            balance = aS-ac;
                            bal = (float) balance;
                            getPinPrefsEditor.putFloat("balance", bal);
                            getActivity().getSharedPreferences("",Context.MODE_PRIVATE);

                            Log.d("new_balance", String.valueOf(bal));
                            Toast.makeText(getContext(), "Balance is :"+bal, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity().getApplicationContext(), WriteBalance.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("NonsenseName",String.valueOf(bal));
                            getActivity().getApplicationContext().startActivity(intent);
                        }else if ((pinS.equals(pin_stud)) && (ac>aS)) {
                            Toast.makeText(getContext(), "Insufficient Funds", Toast.LENGTH_SHORT).show();
                        }else if (!(pinS.equals(pin_stud))){
                            Toast.makeText(getContext(), "Incorrect Pinnnnnn", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }
//                        floatPrefEditor.apply();
                    }
                });
        return builder.create();
    }

}

