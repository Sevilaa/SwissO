package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

public class ProfilFragment extends MyFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton addFriend = view.findViewById(R.id.add_friend);
        MaterialButton addClub = view.findViewById(R.id.add_club);
        addFriend.setOnClickListener(v -> new EditTextDialog(false, this).show(getChildFragmentManager(), "friend"));
        addClub.setOnClickListener(v -> new EditTextDialog(true, this).show(getChildFragmentManager(), "club"));

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
        if(getView() != null) {
            ListView freundeList = getView().findViewById(R.id.profil_peoplelist);
            Cursor cursor = act.getDaten().getAllFreunde();
            if (cursor.getCount() > 0) {
                freundeList.setVisibility(View.VISIBLE);
                FriendClubAdapter adapter = new FriendClubAdapter((MainActivity) act, cursor, false, this);
                freundeList.setAdapter(adapter);
            } else {
                freundeList.setVisibility(View.INVISIBLE);
            }
            ListView clubList = getView().findViewById(R.id.profil_clublist);
            cursor = act.getDaten().getAllClubs();
            if (cursor.getCount() > 0) {
                clubList.setVisibility(View.VISIBLE);
                FriendClubAdapter adapter = new FriendClubAdapter((MainActivity) act, cursor, true, this);
                clubList.setAdapter(adapter);
            } else {
                clubList.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void reloadList() {
    }
}
