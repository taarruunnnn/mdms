package com.arun.newtru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;

public class LocationChecker extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent){
        if(!checkLocationConnection(context)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(Html.fromHtml("<font color='#FF0000'>No GPS!</font>"));
            dialog.setMessage("This app requires GPS to work. Please enable location services and press continue.");
            dialog.setPositiveButton(Html.fromHtml("<font color='#009688'>Continue</font>"), null); //Set to null. We override the onclick
            dialog.setNegativeButton("Exit", null);
            dialog.setCancelable(false);
            final AlertDialog d = dialog.create();
            d.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button button = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if(checkLocationConnection(context)){
                                d.dismiss();
                            }

                        }
                    });
                    Button button2 = ((AlertDialog) d).getButton(AlertDialog.BUTTON_NEGATIVE);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                }
            });
            d.show();
        }
    }

    boolean checkLocationConnection(Context context){
        serviceManager serviceManagerObj = new serviceManager(context);
        return serviceManagerObj.isLocationAvailable(context);
    }
}
