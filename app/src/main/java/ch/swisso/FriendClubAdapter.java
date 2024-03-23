package ch.swisso;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

public class FriendClubAdapter extends BaseAdapter {

    private final MainActivity act;
    private final String[] names;
    private final int[] ids;
    private final ProfilFragment.ProfilList list;
    private final ProfilFragment fragment;

    public FriendClubAdapter(MainActivity act, @NonNull Cursor c, ProfilFragment.ProfilList list, ProfilFragment fragment){
        this.list = list;
        this.act = act;
        this.fragment = fragment;
        names = new String[c.getCount()];
        ids = new int[c.getCount()];
        c.moveToFirst();
        for (int i = 0; !c.isAfterLast(); i++){
            ids[i] = Helper.getInt(c, SQLiteHelper.COLUMN_AUTO_ID);
            names[i] = Helper.getString(c, SQLiteHelper.COLUMN_NAME);
            c.moveToNext();
        }
        c.close();
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return names[position];
    }

    @Override
    public long getItemId(int position) {
        return ids[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(act).inflate(R.layout.listitem_friendclub, parent, false);
        }
        ((FriendClubLayout)convertView).init(ids[position], names[position], act, list, fragment);
        return convertView;
    }
}
