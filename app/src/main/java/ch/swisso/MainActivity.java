package ch.swisso;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<Event> events = new ArrayList<>();
    private MyFragment fragment;
    private Daten daten;
    private SwissOParser parser;
    private Event selectedEvent;
    private NavController navController;
    private BottomNavigationView navigation;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        daten = new Daten(this);
        parser = new SwissOParser(this);

        navigation = findViewById(R.id.bottom_navigation);


        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        initEvents();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigation, navController);

        /*navigation.setOnItemSelectedListener(item -> {
            navController.popBackStack();
            return NavigationUI.onNavDestinationSelected(item, navController);
        });*/

        // Add your own reselected listener
        navigation.setOnItemReselectedListener(item -> navController.popBackStack(item.getItemId(), false));

        parser.sendMessageRequest();
    }

    public void setToolbarTitle(String title) {
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
                navigation.setSelectedItemId(R.id.ranglistFragment);
                //navController.navigate(R.id.ranglistFragment);
                break;
            case Startliste:
                navigation.setSelectedItemId(R.id.startlistFragment);
                //navController.navigate(R.id.startlistFragment);
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
}