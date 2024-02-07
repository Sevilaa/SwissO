package ch.swisso;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {

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
