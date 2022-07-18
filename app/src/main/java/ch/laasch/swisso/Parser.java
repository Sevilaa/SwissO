package ch.laasch.swisso;

public abstract class Parser {

    public abstract void onResult(MyHttpClient.RequestCodes requestCode, int id, String result);
}
