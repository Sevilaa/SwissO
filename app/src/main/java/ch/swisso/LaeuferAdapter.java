package ch.swisso;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LaeuferAdapter extends ArrayAdapter<Laeufer> {

    private final int listType;
    
    public LaeuferAdapter(Context context, int listType, ArrayList<Laeufer> laeufer) {
        super(context, getLayout(listType), laeufer);
        this.listType = listType;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Laeufer laeufer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(getLayout(listType), parent, false);
        }
        TextView nummer, name, kat, zeit;
        if (Helper.isStartliste(listType)) {
            nummer = convertView.findViewById(R.id.sl_startnummer);
            name = convertView.findViewById(R.id.sl_name);
            kat = convertView.findViewById(R.id.sl_kat);
            zeit = convertView.findViewById(R.id.sl_starttime);
        }
        else {
            nummer = convertView.findViewById(R.id.rl_rang);
            name = convertView.findViewById(R.id.rl_name);
            kat = convertView.findViewById(R.id.rl_kat);
            zeit = convertView.findViewById(R.id.rl_zielzeit);
        }
        // Populate the data from the data object into the template view
        name.setText(laeufer.getName());
        kat.setText(laeufer.getCategory());
        if (Helper.isStartliste(listType)) {
            nummer.setText(laeufer.getStartnummer() != Helper.intnull ? "" + laeufer.getStartnummer() : "");
            if(laeufer.getStartZeit() != Helper.intnull) {
                String hhmmss = DateUtils.formatElapsedTime(laeufer.getStartZeit() / 1000);
                zeit.setText(hhmmss.substring(0, hhmmss.length() - 3));
            }
        }
        else {
            nummer.setText(laeufer.getRangString(convertView.getResources()));
            zeit.setText(laeufer.getZielzeitString(convertView.getResources()));
        }
        // Return the completed view to render on screen
        return convertView;
    }

    private static int getLayout(int listType){
        return Helper.isStartliste(listType) ? R.layout.listitem_startliste : R.layout.listitem_rangliste;
    }
}