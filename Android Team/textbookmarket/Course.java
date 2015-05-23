package com.lakehead.textbookmarket;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Master on 2/17/14.
 */
public class Course implements Parcelable{
    private final int _id;
    private final String _title;
    private final String _code;
    private final String _section;
    private final int _department_id;
    private final String _instructor;
    private final String _term;

    /**
     *
     * @param id course ID.
     * @param title Title of the course.
     * @param code Code, example: BUSI-4423.
     * @param section WA/FA/FY/etc..
     * @param department_id //The department that the course belongs to.
     * @param instructor The person who will be teaching the course
     * @param term YYYYT -> 2014F. The year and term.
     */
    public Course(int id, String title, String code, String section, int department_id, String instructor, String term){
        _id = id;
        _title = title;
        _code = code;
        _section = section;
        _department_id = department_id;
        _instructor = instructor;
        _term = term;
    }

    public static final Parcelable.Creator<Course> CREATOR
            = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    private Course(Parcel in) {
        this._id = in.readInt();
        this._title = in.readString();
        this._code = in.readString();
        this._section = in.readString();
        this._department_id = in.readInt();
        this._instructor = in.readString();
        this._term = in.readString();
    }


    public int get_id() {return _id; }

    public String get_title() {
        return _title;
    }

    public String get_section() {
        return _section;
    }

    public String get_instructor() {
        return _instructor;
    }

    public String get_code() {return _code; }

    public int get_department_id() { return _department_id; }

    public String get_term() { return _term; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //public Course(int id, String title, String code, String section, int department_id, String instructor, String term){
        dest.writeInt(this._id);
        dest.writeString(this._title);
        dest.writeString(this._code);
        dest.writeString(this._section);
        dest.writeInt(this._department_id);
        dest.writeString(this._instructor);
        dest.writeString(this._term);
    }
}
