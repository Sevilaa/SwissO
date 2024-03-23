package ch.swisso;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;

public class FriendClubLayout extends ConstraintLayout {

    public FriendClubLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int id, String name, MainActivity act, ProfilFragment.ProfilList list, ProfilFragment fragment) {
        TextView tv = findViewById(R.id.name_friend_club);
        MaterialButton button = findViewById(R.id.delete_friend_club);
        tv.setText(name);
        button.setOnClickListener(v -> {
            act.getDaten().deleteProfilElementById(id, list);
            fragment.showFriendsAndClubs();
        });
    }
}
