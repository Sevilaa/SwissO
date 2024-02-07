package ch.swisso;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsFragment extends EventFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Event event = act.getEvent();
        TextView tvdate = view.findViewById(R.id.details_item_date);
        TextView tvmap = view.findViewById(R.id.details_item_map);
        TextView tvclub = view.findViewById(R.id.details_item_club);
        TextView tvdeadline = view.findViewById(R.id.details_item_deadline);
        TextView tvregion = view.findViewById(R.id.details_item_region);
        TextView[] textViews = new TextView[]{tvdate, tvmap, tvclub, tvdeadline, tvregion};

        String date = " " + getDateString(event.getBeginDate());
        if (event.getEndDate() != null) {
            date += " - " + getDateString(event.getEndDate());
        }
        tvdate.setText(date);
        tvmap.setText(" " + event.getMap());
        tvclub.setText(" " + event.getClub());
        tvdeadline.setText(" " + getDateString(event.getDeadline()));
        tvregion.setText(" " + event.getRegion());
        for (TextView tv : textViews) {
            setTextViewVisible(tv);
        }

        Event.UriArt[] uris;
        int[] resources;
        Event.UriArt startliste = event.getUri(Event.UriArt.Startliste) != null ? Event.UriArt.Startliste : Event.UriArt.Teilnehmerliste;
        int startlistString = event.getUri(Event.UriArt.Startliste) != null ? R.string.startlist : R.string.teilnehmer;

        uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, Event.UriArt.Weisungen, Event.UriArt.Anmeldung, startliste, Event.UriArt.Rangliste, Event.UriArt.Liveresultate, Event.UriArt.WKZ, Event.UriArt.Mutation, Event.UriArt.Kalender};
        resources = new int[]{R.string.ausschreibung, R.string.weisungen, R.string.anmeldung, startlistString, R.string.rangliste, R.string.liveresult, R.string.wkz, R.string.mutation, R.string.kalender};


        OverviewButton[] buttons = new OverviewButton[8];
        buttons[0] = view.findViewById(R.id.details_item_button1);
        buttons[1] = view.findViewById(R.id.details_item_button2);
        buttons[2] = view.findViewById(R.id.details_item_button3);
        buttons[3] = view.findViewById(R.id.details_item_button4);
        buttons[4] = view.findViewById(R.id.details_item_button5);
        buttons[5] = view.findViewById(R.id.details_item_button6);
        buttons[6] = view.findViewById(R.id.details_item_button7);
        buttons[7] = view.findViewById(R.id.details_item_button8);

        int btni = 0;
        int conti = 0;
        while (conti < uris.length) {
            if (event.getUri(uris[conti]) != null) {
                buttons[btni].init(act, resources[conti], event, uris[conti]);
                btni++;
            }
            conti++;
        }
        while (btni < buttons.length) {
            buttons[btni].setVisibility(View.GONE);
            btni++;
        }

    }

    @NonNull
    private String getDateString(Date date) {
        return date == null ? "" : SimpleDateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    private void setTextViewVisible(@NonNull TextView tv) {
        String s = tv.getText().toString().trim();
        tv.setVisibility(s.isEmpty() || s.equals("null") ? View.GONE : View.VISIBLE);
    }
}
