package ch.swisso;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class MainFragment extends MyFragment {

    protected MainActivity act;

    public MainActivity getAct() {
        return act;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MainActivity) getActivity();
    }

}
