package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;

public class ProfilFragment extends MyFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        act = (MainActivity) getActivity();

        ListView freundeList = view.findViewById(R.id.profil_peoplelist);
        ListView clubList = view.findViewById(R.id.profil_clublist);
        freundeList.setOnItemLongClickListener((parent, view1, position, id) -> {
            int itemId = Helper.getInt((Cursor) freundeList.getItemAtPosition(position), SQLiteHelper.COLUMN_AUTO_ID);
            act.getDaten().deleteFreundById(itemId);
            showFriendsAndClubs();
            return true;
        });
        clubList.setOnItemLongClickListener((parent, view1, position, id) -> {
            int itemId = Helper.getInt((Cursor) clubList.getItemAtPosition(position), SQLiteHelper.COLUMN_AUTO_ID);
            act.getDaten().deleteClubById(itemId);
            showFriendsAndClubs();
            return true;
        });
        showFriendsAndClubs();
    }

    public void editTextDialogResult(boolean club, String name) {
        if (club) {
            act.getDaten().insertClub(name);
        } else {
            act.getDaten().insertFreund(name);
        }
        showFriendsAndClubs();
    }

    public void showFriendsAndClubs() {
        ListView freundeList = getView().findViewById(R.id.profil_peoplelist);
        Cursor cursor = act.getDaten().getAllFreunde();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            freundeList.setVisibility(View.VISIBLE);
            String[] anzeigeSpalten = new String[]{SQLiteHelper.COLUMN_NAME};
            int[] anzeigeViews = new int[]{R.id.listitem_name};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(act, R.layout.listitem_friendclub, cursor, anzeigeSpalten, anzeigeViews, SimpleCursorAdapter.NO_SELECTION);
            freundeList.setAdapter(adapter);
        } else {
            freundeList.setVisibility(View.INVISIBLE);
        }
        ListView clubList = getView().findViewById(R.id.profil_clublist);
        cursor = act.getDaten().getAllClubs();
        if (cursor.getCount() > 0) {
            clubList.setVisibility(View.VISIBLE);
            String[] anzeigeSpalten = new String[]{SQLiteHelper.COLUMN_NAME};
            int[] anzeigeViews = new int[]{R.id.listitem_name};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(act, R.layout.listitem_friendclub, cursor, anzeigeSpalten, anzeigeViews, SimpleCursorAdapter.NO_SELECTION);
            clubList.setAdapter(adapter);
        } else {
            clubList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void reloadEvents() {
    }

    @Override
    public void reloadList() {
    }

    @Override
    public boolean onOptionsItemClicked(int itemId) {
        if (itemId == R.id.menu_friend_add) {
            new EditTextDialog(false, this).show(getChildFragmentManager(), "friend");
            return true;
        } else if (itemId == R.id.menu_club_add) {
            new EditTextDialog(true, this).show(getChildFragmentManager(), "club");
            return true;
        }
        return false;
    }
}
