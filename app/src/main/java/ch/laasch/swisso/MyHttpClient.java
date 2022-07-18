package ch.laasch.swisso;

import android.app.Activity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

public class MyHttpClient {

    private final RequestQueue queue;

    public MyHttpClient(Activity act) {

        // Instantiate the cache
        Cache cache = new DiskBasedCache(act.getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

        // Start the queue
        queue.start();
    }

    public void sendStringRequest(Parser parser, String url, RequestCodes requestCode, int id) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parser.onResult(requestCode, id, response);
                },
                error -> {
                });
        queue.add(stringRequest);
    }

    public enum RequestCodes {Eventliste, SingleEvent, Rangliste, Startliste}
}
