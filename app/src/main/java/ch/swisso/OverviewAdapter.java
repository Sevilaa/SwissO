package ch.swisso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class OverviewAdapter extends BaseAdapter {

    private final MainActivity act;
    private final ArrayList<Event> events;

    public OverviewAdapter(MainActivity act, ArrayList<Event> events){
        this.act = act;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(act).inflate(R.layout.listitem_overview, parent, false);
        }
        ((OverviewLayout)convertView).init(events.get(position), act);
        return convertView;
    }
}
