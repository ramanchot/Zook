package zook.indiamoves.in.zook.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zook.indiamoves.in.zook.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAboutFragment extends Fragment{


    public UserAboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_about, container, false);
    }

}
