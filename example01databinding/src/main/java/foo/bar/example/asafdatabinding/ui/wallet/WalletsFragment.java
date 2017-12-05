package foo.bar.example.asafdatabinding.ui.wallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foo.bar.example.asafdatabinding.R;


public class WalletsFragment extends Fragment {

    public static WalletsFragment newInstance() {
        WalletsFragment fragment = new WalletsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, null);
    }

}
