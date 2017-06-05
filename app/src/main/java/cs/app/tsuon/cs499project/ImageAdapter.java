package cs.app.tsuon.cs499project;

import com.example.tsuon.cs499project.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> pictures = new ArrayList<>();

    public ImageAdapter(Context c, YelpBusinesses yb) {
        mContext = c;
        pictures = yb.getPictures();
    }

    public int getCount() {
        return pictures.size();
    }

    @Override
    public String getItem(int position) {
        return pictures.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = getItem(position);
        Picasso
                .with(mContext)
                .load(url)
                .noFade().resize(150, 150)
                .centerCrop()
                .into(imageView);
        return imageView;
    }
}