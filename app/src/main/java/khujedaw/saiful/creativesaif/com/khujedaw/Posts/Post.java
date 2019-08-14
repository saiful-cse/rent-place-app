package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private String post_id,time, phone, alt_phone, category, place_name, fee, place_address,
            description, place_photo, name, email, photo;


    public Post() {

    }

    public Post(String post_id, String time, String phone, String alt_phone, String category, String place_name, String fee, String place_address, String description, String place_photo, String name, String email, String photo) {
        this.post_id = post_id;
        this.time = time;
        this.phone = phone;
        this.alt_phone = alt_phone;
        this.category = category;
        this.place_name = place_name;
        this.fee = fee;
        this.place_address = place_address;
        this.description = description;
        this.place_photo = place_photo;
        this.name = name;
        this.email = email;
        this.photo = photo;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAlt_phone() {
        return alt_phone;
    }

    public void setAlt_phone(String alt_phone) {
        this.alt_phone = alt_phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace_photo() {
        return place_photo;
    }

    public void setPlace_photo(String place_photo) {
        this.place_photo = place_photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.post_id);
        dest.writeString(this.time);
        dest.writeString(this.phone);
        dest.writeString(this.alt_phone);
        dest.writeString(this.category);
        dest.writeString(this.place_name);
        dest.writeString(this.fee);
        dest.writeString(this.place_address);
        dest.writeString(this.description);
        dest.writeString(this.place_photo);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.photo);
    }

    protected Post(Parcel in) {
        this.post_id = in.readString();
        this.time = in.readString();
        this.phone = in.readString();
        this.alt_phone = in.readString();
        this.category = in.readString();
        this.place_name = in.readString();
        this.fee = in.readString();
        this.place_address = in.readString();
        this.description = in.readString();
        this.place_photo = in.readString();
        this.name = in.readString();
        this.email = in.readString();
        this.photo = in.readString();
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
