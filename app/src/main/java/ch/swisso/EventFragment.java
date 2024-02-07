package ch.swisso;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class EventFragment extends Fragment {

    protected EventActivity act;

    public EventActivity getAct(){
        return act;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (EventActivity) getActivity();
    }
}
