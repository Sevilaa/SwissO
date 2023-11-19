package ch.swisso;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OverviewLayout extends FrameLayout {

    public OverviewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("SetTextI18n")
    public void init(@NonNull Event event, MainActivity act) {

        TextView tvtitle = findViewById(R.id.overview_item_title);
        TextView tvdate = findViewById(R.id.overview_item_date);
        TextView tvmap = findViewById(R.id.overview_item_map);
        TextView tvclub = findViewById(R.id.overview_item_club);
        TextView tvdeadline = findViewById(R.id.overview_item_deadline);
        TextView[] textViews = new TextView[]{tvtitle, tvdate, tvmap, tvclub, tvdeadline};

        tvtitle.setText(event.getName());
        tvtitle.setOnClickListener(v -> tvtitle.setSelected(!tvtitle.isSelected()));
        String date = " " + getDateString(event.getBeginDate());
        if (event.getEndDate() != null) {
            date += " - " + getDateString(event.getEndDate());
        }
        tvdate.setText(date);
        tvmap.setText(" " + event.getMap());
        tvclub.setText(" " + event.getClub());
        tvdeadline.setText(" " + getDateString(event.getDeadline()));
        for (TextView tv : textViews) {
            setTextViewVisible(tv);
        }

        boolean over = Helper.getToday().after(event.getEndDate() != null ? event.getEndDate() : event.getBeginDate()) || event.getUri(Event.UriArt.Rangliste) != null;
        boolean deadlinePassed = Helper.getToday().after(event.getDeadline());
        Event.UriArt[] uris;
        int[] resources;
        Event.UriArt startliste = event.getUri(Event.UriArt.Startliste) != null ? Event.UriArt.Startliste : Event.UriArt.Teilnehmerliste;
        int startlistString = event.getUri(Event.UriArt.Startliste) != null ? R.string.startlist : R.string.teilnehmer;
        if (over) {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, startliste, Event.UriArt.Rangliste, Event.UriArt.WKZ};
            resources = new int[]{R.string.ausschreibung, startlistString, R.string.rangliste, R.string.wkz};
        } else if (deadlinePassed) {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, startliste, Event.UriArt.Liveresultate, Event.UriArt.WKZ, Event.UriArt.Kalender};
            resources = new int[]{R.string.ausschreibung, startlistString, R.string.liveresult, R.string.wkz, R.string.kalender};
        } else {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, startliste, Event.UriArt.Liveresultate, Event.UriArt.WKZ, Event.UriArt.Kalender};
            resources = new int[]{R.string.ausschreibung, startlistString, R.string.liveresult, R.string.wkz, R.string.kalender};
        }

        OverviewButton[] buttons = new OverviewButton[8];
        buttons[0] = findViewById(R.id.overview_item_button1);
        buttons[1] = findViewById(R.id.overview_item_button2);
        buttons[2] = findViewById(R.id.overview_item_button3);
        buttons[3] = findViewById(R.id.overview_item_button4);
        buttons[4] = findViewById(R.id.overview_item_button5);
        buttons[5] = findViewById(R.id.overview_item_button6);
        buttons[6] = findViewById(R.id.overview_item_button7);
        buttons[7] = findViewById(R.id.overview_item_button8);

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
            buttons[btni].setVisibility(GONE);
            btni++;
        }

        OnClickListener favOnClickListener = v -> {
            event.toggleFavorit();
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.COLUMN_FAVORIT, event.isFavorit() ? 1 : 0);
            act.getDaten().updateEvent(values, event.getId());
            findViewById(R.id.overview_item_fav_checkbox_enabled).setVisibility(event.isFavorit() ? VISIBLE : INVISIBLE);
            findViewById(R.id.overview_item_fav_checkbox_disabled).setVisibility(event.isFavorit() ? INVISIBLE : VISIBLE);
        };

        findViewById(R.id.overview_item_fav_checkbox_enabled).setOnClickListener(favOnClickListener);
        findViewById(R.id.overview_item_fav_checkbox_disabled).setOnClickListener(favOnClickListener);
        findViewById(R.id.overview_item_fav_checkbox_enabled).setVisibility(event.isFavorit() ? VISIBLE : INVISIBLE);
        findViewById(R.id.overview_item_fav_checkbox_disabled).setVisibility(event.isFavorit() ? INVISIBLE : VISIBLE);

        setOnClickListener(v -> act.openEventDetails(event, Event.UriArt.Details));
    }

    @NonNull
    private String getDateString(Date date) {
        return date == null ? "" : SimpleDateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    private void setTextViewVisible(@NonNull TextView tv) {
        String s = tv.getText().toString().trim();
        tv.setVisibility(s.isEmpty() || s.equals("null") ? GONE : VISIBLE);
    }
}
