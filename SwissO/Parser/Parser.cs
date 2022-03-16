using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO.Parser {
    public abstract class Parser {

        public enum RequestCodes { SOLVEventlist, PicoEventlist, SOLVRangliste, SOLVStartliste, PicoStartliste}

        protected readonly MyHttpClient httpClient;
        protected readonly PageManager manager;
        protected readonly RequestCodes requestCode;

        public Parser(MyHttpClient client, PageManager manager, RequestCodes requestCode) {
            httpClient = client;
            this.manager = manager;
            this.requestCode = requestCode;
        }

        public abstract void onResult(MyHttpClient.RequestCodes requestCode, int id, string html);
    }
}
