using SwissO.Parser;
using System;
using System.Collections.Generic;
using System.Text;

namespace SwissO {
    public abstract class PageManager {

        protected readonly MyHttpClient httpClient;
        protected readonly AppManager appManager;
        //protected readonly Daten daten;

        public PageManager(AppManager appManager) {
            this.appManager = appManager;
            httpClient = appManager.GetHttpClient();
        }

        public AppManager GetAppManager() {
            return appManager;
        }
    }
}
