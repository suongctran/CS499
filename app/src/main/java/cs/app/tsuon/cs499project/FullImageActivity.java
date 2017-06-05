package cs.app.tsuon.cs499project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tsuon.cs499project.R;

import java.io.InputStream;

public class FullImageActivity extends Activity {
    YelpBusinesses yb;
    ProgressDialog pDialog;
    ImageView img;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        yb = (YelpBusinesses) getIntent().getSerializableExtra("YBClass");
        int position = getIntent().getIntExtra("position", 0);
        ImageAdapter imageAdapter = new ImageAdapter(FullImageActivity.this, yb);
        img = (ImageView) findViewById(R.id.image);
        String url = imageAdapter.getItem(position);

        new DownloadImage().execute(url);

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        linLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            img.setImageBitmap(result);
        }
    }

}
