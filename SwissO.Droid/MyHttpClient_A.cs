using Android.App;
using SwissO.Parser;
using Volley;
using Volley.Toolbox;

namespace SwissO.Droid {

    class MyResponseListener : Java.Lang.Object, Response.IListener, Response.IErrorListener {

        private MyHttpClient.RequestCodes requestCode;
        private Parser.Parser parser;
        private int id;

        public MyResponseListener(Parser.Parser parser, MyHttpClient.RequestCodes code, int id) {
            this.parser = parser;
            requestCode = code;
            this.id = id;
        }

        public void OnErrorResponse(VolleyError p0) {
        }

        public void OnResponse(Java.Lang.Object p0) {
            Java.Lang.String s = (Java.Lang.String)p0;
            parser.onResult(requestCode, id, (string)s);
            
        }
    }
    public class MyHttpClient_A : MyHttpClient {

        private RequestQueue queue;

        public MyHttpClient_A(Activity act) {
            DiskBasedCache cache = new DiskBasedCache(act.CacheDir, 1024 * 1024); // 1MB cap
            BasicNetwork network = new BasicNetwork(new HurlStack());
            queue = new RequestQueue(cache, network);
            queue.Start();
        }

        public override void SendStringRequest(Parser.Parser parser, string url, RequestCodes requestCode, int id) {
            MyResponseListener listener = new MyResponseListener(parser, requestCode, id);
            StringRequest stringRequest = new StringRequest(Request.Method.Get, url, listener, listener);
            queue.Add(stringRequest);
        }
    }
}