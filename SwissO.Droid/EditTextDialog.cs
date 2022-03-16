using Android.Content;
using Android.OS;
using Android.Views;
using Android.Widget;
using System;
using Android.App;

namespace SwissO.Droid {
    class EditTextDialog : AndroidX.Fragment.App.DialogFragment {

        private bool club;
        private ProfilManager manager;

        private View v;

        public EditTextDialog(bool club, ProfilManager manager) {
            this.club = club;
            this.manager = manager;
        }

        public override Dialog OnCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity);
            LayoutInflater inflater = Activity.LayoutInflater;
            v = inflater.Inflate(Resource.Layout.dialog_edittext, null);
            builder.SetView(v);

            builder.SetTitle(club ? Resource.String.club_add : Resource.String.friend_add);
            builder.SetPositiveButton("OK", new EventHandler<DialogClickEventArgs>(OkClick));
            builder.SetNegativeButton(Resource.String.cancel, new EventHandler<DialogClickEventArgs>(CancelClick));
            Cancelable = true;
            return builder.Create();
        }


        private void OkClick(object sender, DialogClickEventArgs e) {
            string vorname = ((EditText)v.FindViewById(Resource.Id.dialog_name)).Text;
            manager.NamenCallback(club, vorname);
            Dismiss();
        }

        private void CancelClick(object sender, DialogClickEventArgs e) {
            Dismiss();
        }
    }
}