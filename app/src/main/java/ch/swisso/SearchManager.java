package ch.swisso;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.HashMap;

public abstract class SearchManager {

    protected final MyActivity act;
    private final SearchBar seachBar;

    public SearchManager(SearchBar searchBar, @NonNull SearchView searchView, @NonNull ListView searchListView, @NonNull MyActivity act, @NonNull MyActivity.MyViewModel viewModel) {
        this.act = act;
        this.seachBar = searchBar;
        act.setSupportActionBar(searchBar);

        viewModel.getSearchParams().observe(act, s -> {
            searchBar.setText(s.first);
            act.invalidateOptionsMenu();
        });

        searchView.setupWithSearchBar(searchBar);

        EditText editText = searchView.getEditText();
        editText.setOnEditorActionListener((v, actionId, event) -> {
            viewModel.setSearchParams(Pair.create(searchView.getText().toString().trim(), null));
            searchView.hide();
            return false;
        });

        searchListView.setOnItemClickListener((parent, view, position, id) -> {
            //noinspection unchecked
            Pair<String, String> searchParams = (Pair<String, String>) searchListView.getAdapter().getItem(position);
            viewModel.setSearchParams(searchParams);
            searchView.hide();
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                HashMap<String, String> suggestions = search.isEmpty() ? new HashMap<>() : getSeachSuggestions(search);
                searchListView.setAdapter(new SearchListAdapter(act, suggestions));
            }
        });
    }

    public void setHint(@StringRes int hint){
        seachBar.setHint(hint);
    }

    protected abstract HashMap<String, String> getSeachSuggestions(String seachText);

    public static class EventSearchManager extends SearchManager {

        private boolean favList;

        public EventSearchManager(MyActivity act, MyActivity.MyViewModel viewModel) {
            super(act.findViewById(R.id.search_bar_main), act.findViewById(R.id.search_view_main), act.findViewById(R.id.search_list_main), act, viewModel);
        }

        public void setSearchContent(boolean fav) {
            favList = fav;
        }

        protected HashMap<String, String> getSeachSuggestions(String seachText) {
            return act.getDaten().getEventSeachSuggestions(seachText, favList);
        }
    }

    public static class RunnerSearchManager extends SearchManager {

        private List list;

        public RunnerSearchManager(MyActivity act, MyActivity.MyViewModel viewModel) {
            super(act.findViewById(R.id.search_bar_event), act.findViewById(R.id.search_view_event), act.findViewById(R.id.search_list_event), act, viewModel);
        }

        public void setSeachContent(List list) {
            this.list = list;
        }

        protected HashMap<String, String> getSeachSuggestions(String seachText) {
            return list == null ? new HashMap<>() : act.getDaten().getLaeuferSeachSuggestions(seachText, list.getId());
        }
    }
}
