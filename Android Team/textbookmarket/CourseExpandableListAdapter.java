package com.lakehead.textbookmarket;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
/**
 * The adapter used to populate the list view for the courses fragment
 */
public class CourseExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> departmentHeaders; // header titles
    private HashMap<String, ArrayList<Course>> courses; //courses nested within departments


    /**
     * @param context Used to get the inflater for the object.
     * @param departmentHeaders An array of department names
     * @param courses A HashMap where a department name maps to an array of courses
     */
    public CourseExpandableListAdapter(Context context, ArrayList<String> departmentHeaders,
                                       HashMap<String, ArrayList<Course>> courses) {
        this.context = context;
        this.departmentHeaders = departmentHeaders;
        this.courses = courses;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.courses.get(this.departmentHeaders.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     *
     * @param groupPosition The current header position
     * @param childPosition The current child element position
     * @param isLastChild Determines whether the current child is that last
     * @param convertView unused
     * @param parent The parent of the current View.
     * @return Returns the now-formulated row of data.
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String deptTitle = departmentHeaders.get(groupPosition);
        Course course = courses.get(deptTitle).get(childPosition);
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.courses_item, parent, false);
        TextView titleTextView = (TextView)rowView.findViewById(R.id.courseTitle);
        titleTextView.setText(course.get_title());

        //commented out as we currently have no need for an Icon for the class?
        //ImageView iconImageView = (ImageView)rowView.findViewById(R.id.icon);
        //this is temp shit until we decide if we want an icon for courselist.
        //iconImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));

        TextView sectionTextView = (TextView)rowView.findViewById(R.id.courseInfo);
        sectionTextView.setText(course.get_code()+ " " + course.get_section());
        TextView instructorTextView = (TextView)rowView.findViewById(R.id.courseInstructor);
        instructorTextView.setText(course.get_instructor());
        return rowView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.courses.get(this.departmentHeaders.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.departmentHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.departmentHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.courses_item_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.courses_department_textview);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}