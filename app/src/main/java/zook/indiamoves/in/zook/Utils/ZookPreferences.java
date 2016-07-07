package zook.indiamoves.in.zook.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import zook.indiamoves.in.zook.model.User;

/**
 * Created by Admin on 7/7/2016.
 */
public class ZookPreferences {


    private static final String USER_INFO ="user_Info";
    private static final String NAME ="user_name" ;
    private static final String EMAIL = "user_email";
    private static final String PHONE_NO = "user_phoneNo";
    private static final String USER_IMAGE = "user_image";
    private static final String HOME_ADDRESS = "user_home_address";
    private static final String WORK_ADDRESS = "user_work_address";

    public static void saveUser(Context context, User user) {
        try {


            SharedPreferences mPrefs = context.getSharedPreferences(USER_INFO, 0);

            SharedPreferences.Editor editor = mPrefs.edit();

            // conversion gson to json
            Gson gson = new Gson();

            String name = user.getName();
            String email = user.getEmail();
            String phoneno = user.getPhoneNo();
            String homeAddress = user.getHomeAddress();
            String workAddress = user.getWorkAddress();
            String profileImage = user.getUserImage();


            if (name != null) {
                editor.putString(NAME, name);
            }

            if (email != null) {
                editor.putString(EMAIL, email);
            }
            if (phoneno != null) {
                editor.putString(PHONE_NO, phoneno);
            }

            if (homeAddress != null) {
                editor.putString(HOME_ADDRESS, homeAddress);
            }
            if (workAddress != null) {
                editor.putString(WORK_ADDRESS, workAddress);
            }
            if (profileImage != null) {
                editor.putString(USER_IMAGE, profileImage );
            }
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();


        }
    }

    public static User getUser(Context context) {

        try {
            User user = new User();
            SharedPreferences mPref = context.getSharedPreferences(USER_INFO, 0);
            user.setEmail(mPref.getString(EMAIL, null));
            user.setName(mPref.getString(NAME, null));
            user.setPhoneNo(mPref.getString(PHONE_NO, null));
            user.setHomeAddress(mPref.getString(HOME_ADDRESS, null));
            user.setWorkAddress(mPref.getString(WORK_ADDRESS, null));
            user.setUserImage(mPref.getString(USER_IMAGE, null));
            return user;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void deleteUserInfo(Context context) {
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(USER_INFO, 0);
            SharedPreferences.Editor editor = mPrefs.edit();

            editor.remove(NAME);
            editor.remove(EMAIL);
            editor.remove(PHONE_NO);
            editor.remove(HOME_ADDRESS);
            editor.remove(WORK_ADDRESS);
            editor.remove(USER_IMAGE);
            editor.commit();


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
