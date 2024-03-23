package ch.swisso;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

public class ProfilFragment extends MainFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton addFriend = view.findViewById(R.id.add_friend);
        MaterialButton addClub = view.findViewById(R.id.add_club);
        MaterialButton addKat = view.findViewById(R.id.add_kat);
        addFriend.setOnClickListener(v -> new EditTextDialog(ProfilList.Freund, this).show(getChildFragmentManager(), "friend"));
        addClub.setOnClickListener(v -> new EditTextDialog(ProfilList.Club, this).show(getChildFragmentManager(), "club"));
        addKat.setOnClickListener(v -> new EditTextDialog(ProfilList.Kat, this).show(getChildFragmentManager(), "kat"));

        showFriendsAndClubs();
    }

    public void editTextDialogResult(ProfilList list, String name) {
        act.getDaten().insertProfilElement(name, list);
        showFriendsAndClubs();
    }

    public void showFriendsAndClubs() {
        if (getView() == null) return;
        int[] listViewIds = new int[]{R.id.profil_peoplelist, R.id.profil_clublist, R.id.profil_katlist};
        ProfilList[] types = new ProfilList[]{ProfilList.Freund, ProfilList.Club, ProfilList.Kat};
        for (int i = 0; i < types.length; i++) {
            ListView listView = getView().findViewById(listViewIds[i]);
            Cursor cursor = act.getDaten().getAllProfilElements(types[i]);
            if (cursor.getCount() > 0) {
                listView.setVisibility(View.VISIBLE);
                FriendClubAdapter adapter = new FriendClubAdapter(act, cursor, types[i], this);
                listView.setAdapter(adapter);
            } else {
                listView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public enum ProfilList {Freund, Club, Kat}
}
