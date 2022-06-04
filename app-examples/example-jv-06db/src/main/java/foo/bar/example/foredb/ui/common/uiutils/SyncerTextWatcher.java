package foo.bar.example.foredb.ui.common.uiutils;

import android.text.Editable;
import android.text.TextWatcher;

import co.early.fore.core.ui.SyncableView;

/**
 *
 */
public class SyncerTextWatcher implements TextWatcher {

    private final SyncableView syncableView;

    public SyncerTextWatcher(SyncableView syncableView) {
        this.syncableView = syncableView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        syncableView.syncView();
    }

}
