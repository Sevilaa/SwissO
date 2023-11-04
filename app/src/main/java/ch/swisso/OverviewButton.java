package ch.swisso;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

public class OverviewButton extends MaterialButton {

    public OverviewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(MyActivity act, int text, @NonNull Event e, Event.UriArt uriArt) {
        boolean hasUri = e.getUri(uriArt) != null;
        setText(text);
        setVisibility(hasUri ? VISIBLE : GONE);
        setOnClickListener(v -> act.openEventDetails(e, uriArt));
    }
}
