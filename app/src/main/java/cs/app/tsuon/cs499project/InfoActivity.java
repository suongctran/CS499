package cs.app.tsuon.cs499project;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tsuon.cs499project.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class InfoActivity extends AppCompatActivity implements Serializable {
    private Button backButton;
    private TextView nameText;
    private GridView imgGridView;
    private TextView ratingsInfoText;
    YelpBusinesses yb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        nameText = (TextView)findViewById(R.id.name);
        ratingsInfoText = (TextView) findViewById(R.id.ratingsInfoText);
        backButton = (Button) findViewById(R.id.backButton);
        imgGridView = (GridView) findViewById(R.id.imgGridView);
        Log.v("new3", "info");
        Bundle b = getIntent().getExtras();
        yb = (YelpBusinesses) b.getSerializable("YBClass");
        //yb = (YelpBusinesses) getIntent().getSerializableExtra("YBClass");
        nameText.setText(yb.getName());
        ratingsInfoText.setText(String.valueOf(yb.getRating()) + " Stars; " + String.valueOf(yb.getReviewCount()) + " Reviews");
        Log.v("new3", "info3");
        if(yb.getPictures() != null) {
            imgGridView.setAdapter(new ImageAdapter(InfoActivity.this, yb));
            Log.v("new3", "works22");
            imgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(InfoActivity.this, FullImageActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("YBClass", (Serializable) yb);
                    startActivity(intent);
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
