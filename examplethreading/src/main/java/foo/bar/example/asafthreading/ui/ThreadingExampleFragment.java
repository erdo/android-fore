package foo.bar.example.asafthreading.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foo.bar.example.asafthreading.R;


public class ThreadingExampleFragment extends Fragment {

    public static ThreadingExampleFragment newInstance() {
        ThreadingExampleFragment fragment = new ThreadingExampleFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_threadingexample, null);
    }

}
