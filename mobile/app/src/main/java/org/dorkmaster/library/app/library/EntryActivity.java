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
import org.dorkmaster.library.app.library.event.AddBookEvent;
import org.dorkmaster.library.app.library.service.LibraryService;

public class EntryActivity extends AppCompatActivity {
    LibraryService service = new LibraryService();
    private int scanned = -1;
    private final static int BOOK = 1;
    private final static int LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void clearBook() {
        ((EditText) findViewById(R.id.isbnField)).setText("");
    }

    public void addScanBook(View view) {
        scanned = BOOK;
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.addExtra("TRY_HARDER", "ON");
        ii.addExtra("SCAN_MODE", "UPC_EAN_EXTENSION");
        ii.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

//    private void showDialog(int title, CharSequence message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.setPositiveButton(R.string.ok_button, null);
//        builder.show();
//    }

    // networkTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query, isbn);

    public void addScanLocation(View view) {
        scanned = LOCATION;
        IntentIntegrator ii = new IntentIntegrator(this);
        ii.initiateScan();
    }

    public void addBook(View view) {
        String url = ((EditText) findViewById(R.id.urlField)).getText().toString();
        url = url + (url.endsWith("/") ? "cmd" : "/cmd");

        String json = makeAddBookEvent(
                ((EditText) findViewById(R.id.isbnField)).getText().toString(),
                ((EditText) findViewById(R.id.locationField)).getText().toString()
        );
        if (null != json) {
            new AsyncAddBook().doInBackground(url, json);
        }
    }

    private String makeAddBookEvent(String bookSellerId, String locationId) {
        AddBookEvent event = new AddBookEvent();
        event.bookSellerId = bookSellerId;
        event.locationId = locationId;
        event = service.lookupDetails(((EditText) findViewById(R.id.urlField)).getText().toString(), event);
        return null != event ? event.toJson() : null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            EditText et;

            String format = scanResult.getFormatName();
            switch (scanned) {
                case BOOK :
                    et = (EditText) findViewById(R.id.isbnField);
                    et.setText(scanResult.getContents());
                    new AsyncLookupBook().doInBackground(((EditText) findViewById(R.id.urlField)).getText().toString(), scanResult.getContents());
                    break;
                case LOCATION:
                    et = (EditText) findViewById(R.id.locationField);
                    et.setText(scanResult.getContents());
                    break;
            }
        }
        // else continue with any other code you need in the method
        //    ...
    }

    class AsyncLookupBook extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String requestUrl = params[0];
            String isbn = params[1];

            AddBookEvent event = new AddBookEvent();
            event.bookSellerId = params[1];
            service.lookupDetails(requestUrl, event);

            EditText title = (EditText) findViewById(R.id.titleField);
            title.setText(event.title);
            EditText author = (EditText) findViewById(R.id.authorField);
            author.setText(event.author);

            return null;
        }
    }

    class AsyncAddBook extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String requestUrl = params[0];
            String json = params[1];

//            service.submitEvents(requestUrl, json);
//            clearBook();

            return null;
        }
    }
}