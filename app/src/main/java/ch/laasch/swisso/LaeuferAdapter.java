package ch.laasch.swisso;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LaeuferAdapter extends ArrayAdapter<Laeufer> {

    private final MainActivity.FragmentType listType;
    
    public LaeuferAdapter(Context context, MainActivity.FragmentType listType, ArrayList<Laeufer> laeufer) {
        super(context, getLayout(listType), laeufer);
        this.listType = listType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Laeufer laeufer = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(getLayout(listType), parent, false);
        }
        TextView nummer, name, kat, zeit;
        if (listType == MainActivity.FragmentType.Startliste) {
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
        if (listType == MainActivity.FragmentType.Startliste) {
            nummer.setText(laeufer.getStartnummer() != Helper.intnull ? "" + laeufer.getStartnummer() : "");
            String hhmmss = DateUtils.formatElapsedTime(laeufer.getStartZeit() / 1000);
            zeit.setText(hhmmss.substring(0, hhmmss.length() - 3));
        }
        else {
            nummer.setText(Helper.getRang(laeufer.getRang(),  convertView.getResources()));
            zeit.setText(Helper.getZielzeit(laeufer.getZielzeit(), convertView.getResources()));
        }
        // Return the completed view to render on screen
        return convertView;
    }

    private static int getLayout(MainActivity.FragmentType listType){
        return listType == MainActivity.FragmentType.Startliste ? R.layout.listitem_startliste : R.layout.listitem_rangliste;
    }
}