package foo.bar.example.asafui.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;

import foo.bar.example.asafui.R;
import foo.bar.example.asafui.ui.counter.CounterActivity;
import foo.bar.example.asafui.ui.fruitcollector.FruitCollectorActivity;
import foo.bar.example.asafui.ui.wallet.WalletActivity;

/**
 *
 */
public class GlobalBottomNavigationView extends BottomNavigationView {

    private int selectedId = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FruitCollectorActivity.start((AppCompatActivity) GlobalBottomNavigationView.this.getContext());
                    return true;
                case R.id.navigation_dashboard:
                    CounterActivity.start((AppCompatActivity) GlobalBottomNavigationView.this.getContext());
                    return true;
                case R.id.navigation_notifications:
                    WalletActivity.start((AppCompatActivity) GlobalBottomNavigationView.this.getContext());
                    return true;
            }
            return false;
        }
    };



    public GlobalBottomNavigationView(Context context) {
        super(context);
        init(null);
    }

    public GlobalBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GlobalBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {

        if (attrs == null) {
            selectedId = 0;
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GlobalBottomNavigationView);
            selectedId = a.getInt(R.styleable.GlobalBottomNavigationView_selectedId, R.id.navigation_home);
        }
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setSelectedItemId(selectedId);
        setOnNavigationItemSelectedListener(itemSelectedListener);

    }
}
