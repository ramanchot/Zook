package zook.indiamoves.in.zook.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import zook.indiamoves.in.zook.R;
import zook.indiamoves.in.zook.activities.BookRideActivity;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Admin on 7/6/2016.
 */
public class AlertDialogMessages {

    public static void quickride_options(final Context context) {
        final String[] items = {"Home","Work","Other"};
        AlertDialog myDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        builder.setTitle("Select Your Prefered Destination");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Selected: "+ items[which], LENGTH_SHORT).show();
                Intent quickRide=new Intent(context, BookRideActivity.class);
                context.startActivity(quickRide);

            }
        });

        builder.setCancelable(true);
        myDialog = builder.create();
        myDialog.show();

    }
}

