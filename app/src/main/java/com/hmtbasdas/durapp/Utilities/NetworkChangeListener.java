package com.hmtbasdas.durapp.Utilities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.hmtbasdas.durapp.R;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Common.isConnectedToInternet(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.FullScreen);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.no_connection, null);
            builder.setView(layout_dialog);

            Button retryButton = layout_dialog.findViewById(R.id.tryButton);
            Button exit = layout_dialog.findViewById(R.id.exit);

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);

            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);
                }
            });
        }
    }
}
