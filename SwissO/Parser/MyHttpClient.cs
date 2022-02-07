using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO.Parser {
    public abstract class MyHttpClient {

        public enum RequestCodes { Eventliste, SingleEvent, Rangliste, Startliste};

        public abstract void SendStringRequest(Parser parser, string url, RequestCodes requestCode, int id);

    }
}
