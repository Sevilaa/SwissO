using Android.Content;
using Android.Util;
using Android.Views;
using Google.Android.Material.Button;
using static SwissO.MyResources;

namespace SwissO.Droid {
    class OverviewButton : MaterialButton {

        private Event.UriArt uriArt;
        private AppManager appManager;
        private Event e;
        private StringResource text;

        public OverviewButton(Context context, IAttributeSet attrs) : base(context, attrs) {
            Click += (sender, e) => {
                appManager.OpenEventDetails(this.e, uriArt);
            };
        }

        public void Init(AppManager appManager, StringResource text, Event e, Event.UriArt uriArt) {
            this.uriArt = uriArt;
            this.appManager = appManager;
            this.e = e;
            this.text = text;
            if (e != null && e.GetUri(uriArt) != null) {
                MyResources_A res = new MyResources_A(Resources);
                SetText(res.GetStringId(this.text));
            }
            SetVisible();
        }

        public void SetVisible() {
            Visibility = e != null && e.GetUri(uriArt) != null ? ViewStates.Visible : ViewStates.Gone;
        }

    }
}