package foo.bar.example.asafui.ui.fruitcollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import co.early.asaf.ui.LifecycleSyncer;
import co.early.asaf.ui.activity.SyncableAppCompatActivity;
import foo.bar.example.asafui.CustomApp;
import foo.bar.example.asafui.R;
import foo.bar.example.asafui.feature.fruitcollector.FruitCollectorModel;

public class FruitCollectorActivity extends SyncableAppCompatActivity {

    public static void start(AppCompatActivity activity) {
        Intent intent = build(activity);
        activity.startActivity(intent);
    }

    public static Intent build(AppCompatActivity activity) {
        Intent intent = new Intent(activity, FruitCollectorActivity.class);
        return intent;
    }


    @Override
    public int getResourceIdForSyncableView() {
        return R.layout.activity_fruitcollector;
    }

    @Override
    public LifecycleSyncer.Observables getThingsToObserve() {
        return new LifecycleSyncer.Observables(CustomApp.get(FruitCollectorModel.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
