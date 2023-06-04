package ch.swisso;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class MyFragment extends Fragment {

    protected MyActivity act;

    public abstract void reloadList();

    public MyActivity getAct() {
        return act;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MyActivity) getActivity();
    }
}
