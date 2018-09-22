package foo.bar.example.forethreading.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foo.bar.example.forethreading.R;


public class CounterFragment extends Fragment {

    public static CounterFragment newInstance() {
        CounterFragment fragment = new CounterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_counter, null);
    }

}
