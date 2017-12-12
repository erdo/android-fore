package foo.bar.example.asafui.ui.login;

import android.widget.CompoundButton;

import co.early.asaf.core.Affirm;
import co.early.asaf.core.ui.SyncableView;

/**
 *
 */
public class SyncerCheckChanged implements CompoundButton.OnCheckedChangeListener {

    private final SyncableView syncableView;

    public SyncerCheckChanged(SyncableView syncableView) {
        this.syncableView = Affirm.notNull(syncableView);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        syncableView.syncView();
    }
}
