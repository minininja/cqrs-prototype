package org.dorkmaster.library.app.library;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

public class EntryActivity extends AppCompatActivity {
    private final static String EAN_13 = "EAN_13";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void clearBook() {
        ((EditText) findViewById(R.id.editText)).setText("");
    }

    public void addScanBook(View view) {
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.initiateScan();
    }

    public void addScanLocation(View view) {
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.initiateScan();
    }

    public void addBook(View view) {
        String url = ((EditText) findViewById(R.id.editText3)).getText().toString();
        url = url + (url.endsWith("/") ? "cmd" : "/cmd");

        String json = makeAddBookEvent(
                ((EditText) findViewById(R.id.editText)).getText().toString(),
                ((EditText) findViewById(R.id.editText2)).getText().toString()
        );

        new AsyncAddBook().doInBackground(url, json);
    }

    private String makeAddBookEvent(String bookSellerId, String locationId) {
        StringBuilder sb = new StringBuilder();
        sb.append("[{")
                    .append("\"bookSellerId\":\"").append(bookSellerId).append("\",")
                    .append("\"locationId\":\"").append(locationId).append("\",")
                    .append("\"@class\":\"org.dorkmaster.library.event.AddBook\",")
                    .append("\"createdBy\":\"mjackson\",")
                    .append("\"created\":").append(System.currentTimeMillis()).append(",")
                    .append("\"id\":\"").append(UUID.randomUUID().toString()).append("\"")
            .append("}]");
        return sb.toString();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            EditText et;

            String format = scanResult.getFormatName();
            if (EAN_13.equals(format)) {
                et = (EditText) findViewById(R.id.editText);
                et.setText(scanResult.getContents());
            } else {
                // must be the location
                et = (EditText) findViewById(R.id.editText2);
                et.setText(scanResult.getContents());
            }
        }
        // else continue with any other code you need in the method
        //    ...
    }

    class AsyncAddBook extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String requestUrl = params[0];
            byte[] json = params[1].getBytes();

            try {
                URL url = new URL(requestUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setRequestProperty("Content-type","application/json");
                    OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                    out.write(json);
                    out.flush();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    byte[] buf = new byte[1024 * 1024];
                    int size = in.read(buf);
                    String result = new String(buf);
                    result = "result: " + result;
                    clearBook();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}