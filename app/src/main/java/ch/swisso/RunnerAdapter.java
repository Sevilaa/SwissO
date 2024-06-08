package ch.swisso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class RunnerAdapter extends ArrayAdapter<Runner> {

    private final SingleListFragment.Config config;
    
    public RunnerAdapter(Context context, @NonNull SingleListFragment.Config config, ArrayList<Runner> runner) {
        super(context, getLayout(config), runner);
        this.config = config;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Runner runner = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(getLayout(config), parent, false);
        }

        assert convertView instanceof SingleRunnerLayout;
        ((SingleRunnerLayout)convertView).init(config, runner);

        // Return the completed view to render on screen
        return convertView;
    }

    private static int getLayout(@NonNull SingleListFragment.Config config){
        return Helper.isStartliste(config.listType) ? R.layout.listitem_startliste : R.layout.listitem_rangliste;
    }
}