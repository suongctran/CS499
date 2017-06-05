package cs.app.tsuon.cs499project;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tsuon on 5/29/2017.
 */

public class YelpBusinesses implements Parcelable, Serializable {
    private String name;
    private String busType;
    private String mainUrl;
    private String picUrl;
    private List<String> pictures;
    private double rating;
    private int reviewCount;
    private double longitude;
    private double latitude;

    public YelpBusinesses(String name, String busType, String mainUrl) {
        this.name = name;
        this.busType = busType;
        setMainUrl(mainUrl);
    }

    protected YelpBusinesses(Parcel in) {
        name = in.readString();
        busType = in.readString();
        mainUrl = in.readString();
        picUrl = in.readString();
        pictures = in.createStringArrayList();
        rating = in.readDouble();
        reviewCount = in.readInt();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<YelpBusinesses> CREATOR = new Creator<YelpBusinesses>() {
        @Override
        public YelpBusinesses createFromParcel(Parcel in) {
            return new YelpBusinesses(in);
        }

        @Override
        public YelpBusinesses[] newArray(int size) {
            return new YelpBusinesses[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) { this.busType = busType; }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
        this.picUrl = mainUrl.replace("/biz", "/biz_photos");
        this.picUrl = this.picUrl.substring(0, this.picUrl.indexOf('?'));
        if (busType.equals("food")) {
            this.picUrl += "?tab=food";
        }
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(busType);
        dest.writeString(mainUrl);
        dest.writeString(picUrl);
        dest.writeStringList(pictures);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }
}
