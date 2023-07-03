package ch.swisso;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Objects;

public class SearchListAdapter extends BaseAdapter {

    private final MyActivity act;
    private final HashMap<String, String> searchSuggestions;

    public SearchListAdapter(@NonNull MyActivity act, HashMap<String, String> searchSuggestions) {
        this.act = act;
        this.searchSuggestions = searchSuggestions;
    }

    @Override
    public int getCount() {
        return searchSuggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return searchSuggestions.keySet().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(act).inflate(R.layout.listitem_search, parent, false);
        }
        String key = (String) searchSuggestions.keySet().toArray()[position];
        ((TextView) convertView.findViewById(R.id.search_result_text)).setText(key);
        ((TextView) convertView.findViewById(R.id.search_result_type)).setText(getStringFromColumn(searchSuggestions.get(key)));
        return convertView;
    }

    private static int getStringFromColumn(String column) {
        if(column == null){
            return R.string.empty;
        }
        if (column.equals(SQLiteHelper.COLUMN_NAME)) {
            return R.string.event;
        }
        if (column.equals(SQLiteHelper.COLUMN_MAP)) {
            return R.string.karte;
        }
        if (column.equals(SQLiteHelper.COLUMN_CLUB)) {
            return R.string.club;
        }
        if (column.equals(SQLiteHelper.COLUMN_REGION)) {
            return R.string.region;
        }
        return R.string.empty;
    }
}
