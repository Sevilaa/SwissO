package ch.swisso;

import android.annotation.SuppressLint;
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


    private Event event;

    private OverviewAdapter adapter;

    private OverviewButton[] buttons;
    private TextView[] textViews;

    public OverviewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("SetTextI18n")
    public void Init(Event e, MainActivity act, OverviewAdapter adapter) {
        event = e;
        this.adapter = adapter;

        TextView tvtitle = findViewById(R.id.overview_item_title);
        TextView tvdate = findViewById(R.id.overview_item_date);
        TextView tvmap = findViewById(R.id.overview_item_map);
        TextView tvclub = findViewById(R.id.overview_item_club);
        TextView tvdeadline = findViewById(R.id.overview_item_deadline);
        TextView tvregion = findViewById(R.id.overview_item_region);
        textViews = new TextView[]{tvtitle, tvdate, tvmap, tvclub, tvdeadline, tvregion};

        tvtitle.setText(event.getName());
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

        boolean over = Helper.getToday().after(event.getEndDate()) || event.getUri(Event.UriArt.Rangliste) != null;
        boolean deadlinePassed = Helper.getToday().after(event.getDeadline());
        Event.UriArt[] uris;
        int[] resources;
        Event.UriArt startliste = event.getUri(Event.UriArt.Startliste) != null ? Event.UriArt.Startliste : Event.UriArt.Teilnehmerliste;
        int startlistString = event.getUri(Event.UriArt.Startliste) != null ? R.string.startlist : R.string.teilnehmer;
        if (over) {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, Event.UriArt.Weisungen, startliste, Event.UriArt.Rangliste, Event.UriArt.WKZ};
            resources = new int[]{R.string.ausschreibung, R.string.weisungen, startlistString, R.string.rangliste, R.string.wkz};
        } else if (deadlinePassed) {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, Event.UriArt.Weisungen, startliste, Event.UriArt.Liveresultate, Event.UriArt.WKZ, Event.UriArt.Mutation};
            resources = new int[]{R.string.ausschreibung, R.string.weisungen, startlistString, R.string.liveresult, R.string.wkz, R.string.mutation};
        } else {
            uris = new Event.UriArt[]{Event.UriArt.Ausschreibung, Event.UriArt.Weisungen, Event.UriArt.Anmeldung, startliste, Event.UriArt.Liveresultate, Event.UriArt.WKZ, Event.UriArt.Mutation};
            resources = new int[]{R.string.ausschreibung, R.string.weisungen, R.string.anmeldung, startlistString, R.string.liveresult, R.string.wkz, R.string.mutation};
        }

        buttons = new OverviewButton[8];
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
            if (e.getUri(uris[conti]) != null) {
                buttons[btni].Init(act, resources[conti], e, uris[conti]);
                btni++;
            }
            conti++;
        }
        while (btni < buttons.length) {
            //buttons[btni].Init(act, R.string.wkz, null, Event.UriArt.WKZ);
            buttons[btni].setVisibility(GONE);
            btni++;
        }
    }

    /*internal void SetExpandViewClick() {
        expandView = (ImageView)findViewById(R.id.overview_item_expand);
        expandView.Click += (e, sender) => {
            adapter.ExpandViewClick(this);
        };
    }

    public void Expand() {
        expandView.SetImageResource(Resource.Drawable.ic_arrow_collapse);
        foreach (OverviewButton btn in btnExpand) {
            btn.SetVisible();
        }
        foreach (TextView tv in tvExpand) {
            SetTextViewVisible(tv);
        }
    }

    public void Collapse() {
        expandView.SetImageResource(Resource.Drawable.ic_arrow_expand);
        foreach (OverviewButton btn in btnExpand) {
            btn.Visibility = ViewStates.Gone;
        }
        foreach (TextView tv in tvExpand) {
            tv.Visibility = ViewStates.Gone;
        }
    }*/

    private String getDateString(Date date) {
        return date == null ? "" : SimpleDateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    private void setTextViewVisible(TextView tv) {
        tv.setVisibility(tv.getText().toString().trim().isEmpty() ? GONE : VISIBLE);
    }
}
