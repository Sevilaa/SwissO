package ch.swisso;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Event> events = new ArrayList<>();
    private MyFragment fragment;
    private Daten daten;
    private SwissOParser parser;
    private Event selectedEvent;
    private FragmentType fragmentType;
    private BottomNavigationView navigation;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daten = new Daten(this);
        parser = new SwissOParser(this);

        navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnItemSelectedListener(item -> setFragment(item.getItemId()));

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> onOptionItemClicked(item.getItemId()));

        initEvents();

        setFragment(R.id.navigation_overview);

        parser.sendMessageRequest();
    }

    private boolean onOptionItemClicked(int itemId) {
        return fragment.onOptionsItemClicked(itemId);
    }

    public void editOptionMenuItem(int itemId, boolean visible) {
        toolbar.getMenu().findItem(itemId).setVisible(visible);
    }

    private boolean setFragment(int itemId) {
        if (selectedEvent == null && itemId != R.id.navigation_overview) {
            return false;
        }
        toolbar.getMenu().findItem(R.id.menu_refresh).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_search).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_browser).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_sorting).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_club_add).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_friend_add).setVisible(false);
        if (itemId == R.id.navigation_overview) {
            fragment = new OverviewFragment();
            fragmentType = FragmentType.Overview;
            toolbar.getMenu().findItem(R.id.menu_refresh).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_search).setVisible(true);
            toolbar.setTitle(R.string.overview);
        } else if (itemId == R.id.navigation_startliste) {
            fragment = new ListFragment();
            fragmentType = FragmentType.Startliste;
            toolbar.getMenu().findItem(R.id.menu_refresh).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_search).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_browser).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_sorting).setVisible(true);
            setToolbarTitle(R.string.startlist);
        } else if (itemId == R.id.navigation_rangliste) {
            fragment = new ListFragment();
            fragmentType = FragmentType.Rangliste;
            toolbar.getMenu().findItem(R.id.menu_refresh).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_search).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_browser).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_sorting).setVisible(true);
            setToolbarTitle(R.string.rangliste);
        } else if (itemId == R.id.navigation_profil) {
            fragment = new ProfilFragment();
            fragmentType = FragmentType.Profil;
            toolbar.getMenu().findItem(R.id.menu_club_add).setVisible(true);
            toolbar.getMenu().findItem(R.id.menu_friend_add).setVisible(true);
            setToolbarTitle(R.string.profil);
   /*     } else if (itemId == R.id.navigation_details) {
            fragment = new DetailsFragment();
            fragmentType = FragmentType.Details;*/
        } else {
            return false;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.host_fragment_activity_main, fragment).commit();
        return true;
    }

    private void setToolbarTitle(@StringRes int resId) {
        String title = getString(resId);
        title += ": " + selectedEvent.getName();
        toolbar.setTitle(title);
    }

    public final void initEvents() {
        Cursor cursor = daten.getEvents();
        events.clear();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event e = new Event(cursor);
            events.add(e);
            cursor.moveToNext();
        }
        cursor.close();

        int index = events.indexOf(selectedEvent);
        if (index != -1) {
            selectedEvent = events.get(index);
        } else { // Get the next Event that happens
            for (int i = 0; i < events.size(); i++) {
                if (selectedEvent == null && events.get(i).getBeginDate().getTime() >= Helper.getToday().getTimeInMillis()) {
                    selectedEvent = events.get(i);
                    i = events.size();
                }
            }
        }
        if (selectedEvent == null && events.size() > 0) {
            selectedEvent = events.get(events.size() - 1);
        }

        if (fragment != null) {
            fragment.reloadEvents();
        }
    }

    public void reloadList() {
        fragment.reloadList();
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public Daten getDaten() {
        return daten;
    }

    public SwissOParser getParser() {
        return parser;
    }

    @Override
    protected void onDestroy() {
        daten.Close();
        super.onDestroy();
    }

    public final void openEventDetails(Event e, @NonNull Event.UriArt uriArt) {
        selectedEvent = e;
        switch (uriArt) {
            case Rangliste:
                navigation.setSelectedItemId(R.id.navigation_rangliste);
                break;
            case Startliste:
                navigation.setSelectedItemId(R.id.navigation_startliste);
                break;
            default:
                openWebBrowser(e.getUri(uriArt));
                break;
        }
    }

    public void openWebBrowser(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        boolean available = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        findViewById(R.id.tv_internet_connection).setVisibility(available ? View.GONE : View.VISIBLE);
        return available;
    }

    public void showMessages() {
        Cursor c = getDaten().getUnreadMessages();
        if (c.getCount() > 0) {
            String title = getResources().getString(R.string.newmessages);
            ArrayList<String> messages = new ArrayList<>();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                messages.add(Helper.getString(c, SQLiteHelper.COLUMN_MESSAGE));
                c.moveToNext();
            }
            c.close();
            String content = String.join("\n\n", messages);

            MessageDialog dialog = new MessageDialog();
            dialog.init(daten, content, title);
            dialog.show(getSupportFragmentManager(), null);
        }
    }

    public FragmentType getFragmentType() {
        return fragmentType;
    }

    public enum FragmentType {
        Overview, Startliste, Rangliste, Profil, Details
    }
}