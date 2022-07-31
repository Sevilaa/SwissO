package ch.swisso;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.button.MaterialButton;

public class OverviewButton extends MaterialButton {

    private boolean hasUri = false;

    public OverviewButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void Init(MainActivity act, int text, Event e, Event.UriArt uriArt) {
        hasUri = e != null && e.getUri(uriArt) != null;
        setText(text);
        setVisibility(hasUri ? VISIBLE : GONE);
        setOnClickListener(v -> act.openEventDetails(e, uriArt));
    }
}
