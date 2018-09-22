package foo.bar.example.foreretrofit.ui.fruit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foo.bar.example.foreretrofit.R;


public class FruitFragment extends Fragment {

    public static FruitFragment newInstance() {
        FruitFragment fragment = new FruitFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fruit, null);
    }

}
