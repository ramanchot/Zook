package zook.indiamoves.in.zook.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import zook.indiamoves.in.zook.R;
import zook.indiamoves.in.zook.Utils.ZookPreferences;
import zook.indiamoves.in.zook.model.User;

public class SplashScreenActivity extends AppCompatActivity implements View.OnClickListener  {
    private CoordinatorLayout coordinateLayout;
    private LocationManager manager;
    private Snackbar snackbar;
    private Button register_button,logIn_button;
    private int secondsDelayed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        register_button = (Button) findViewById(R.id.register_button);
        logIn_button = (Button) findViewById(R.id.logIn_button);
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            snackbar = Snackbar.make(coordinateLayout, "Your GPS seems to be disabled", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        } else {onResume();}
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = ZookPreferences.getUser(this);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && user.getPhoneNo() == null) {
            register_button.setVisibility(View.VISIBLE);
            logIn_button.setVisibility(View.VISIBLE);
            register_button.setOnClickListener(this);
            logIn_button.setOnClickListener(this);
        }
//        else{
//            int secondsDelayed = 1;
//            new Handler().postDelayed(new Runnable() {
//                public void run() {
//                    startActivity(new Intent(SplashScreenActivity.this, MainMapActivity.class));
//                    finish();
//                }
//            }, secondsDelayed * 500);

//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.register_button :
                Intent registerIntent = new Intent(SplashScreenActivity.this,UserRegisterActivity.class);
                startActivity(registerIntent);
                break;

            case R.id.logIn_button:
                Intent logInIntent = new Intent (SplashScreenActivity.this,UserLogInActivity.class);
                startActivity(logInIntent);
                break;
        }
    }
}


