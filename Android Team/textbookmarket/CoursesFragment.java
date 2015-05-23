package com.lakehead.textbookmarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The fragment used in the "Books" tab of MainActivity
 */
public class CoursesFragment extends Fragment implements OnTaskCompleted, ExpandableListView.OnChildClickListener {
    JSONArray jArray;
    ListView courseListView;
    View rootView;

    CourseExpandableListAdapter courseAdapter;
    ExpandableListView expListView;
    ArrayList<String> deptHeaders;
    HashMap<String, ArrayList<Course>> departmentCourseHash;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_courses_expand, container, false);
        courseListView = (ListView) rootView.findViewById(R.id.course_list_view);
        expListView = (ExpandableListView) rootView.findViewById(R.id.courses_expand_lv);

        if (savedInstanceState != null) {
            Log.d("CoursesFragment", "onCreateView() -> " + "Found saved instance state. Loading Course list from it...");
            deptHeaders = savedInstanceState.getStringArrayList("deptHeaders");
            departmentCourseHash = new HashMap<String, ArrayList<Course>>();
            ArrayList<Course> temporary;
            for (String department : deptHeaders) {
                temporary = savedInstanceState.getParcelableArrayList(department);
                departmentCourseHash.put(department, temporary);
            }

            courseAdapter = new CourseExpandableListAdapter(this.getActivity(), deptHeaders, departmentCourseHash);
            expListView.setAdapter(courseAdapter);

            if (savedInstanceState.getInt("numDepts") == 1) {
                expListView.expandGroup(0);
            }
        } else {
            Log.d("CoursesFragment", "onCreateView() -> " + "No Saved Instance state. Loading Course list from API...");

            JSONObject request = new JSONObject();

            ArrayList<Integer> testList = new ArrayList<Integer>();
            testList.add(Integer.valueOf(1));
            testList.add(Integer.valueOf(7));

            JSONArray ids = new JSONArray();
            try {
                ids.put(new JSONObject().put("dept_id", 1));
                ids.put(new JSONObject().put("dept_id", 7));
                request.put("id", ids);
                request.put("count", testList.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            NameValuePair json = new BasicNameValuePair("json", request.toString());
            new GetJSONArrayTask(this, "/api/departmentdetail").execute(json);


        }
        expListView.setOnChildClickListener(this);

        return rootView;
    }

    @Override
    public void onPause() {
        Log.d("CoursesFragment", "onPause() -> " + "paused fragment.");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("CoursesFragment", "onResume() -> " + "resumed fragment.");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("CoursesFragment", "onSaveInstanceState() -> " + "state saved for fragment.");
        outState.putInt("numDepts", deptHeaders.size());
        outState.putStringArrayList("deptHeaders", deptHeaders);
        for (String key : departmentCourseHash.keySet()) {
            outState.putParcelableArrayList(key, departmentCourseHash.get(key));
        }
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onTaskCompleted(Object obj) {
        jArray = (JSONArray) obj;
        int id;
        int numDepts = 0;
        String deptTitle;
        String title;
        String code;
        String section;
        int department_id;
        String instructor;
        String term;
        JSONObject courseDataNode;
        JSONObject deptDataNode;
        ArrayList<Course> courseList = new ArrayList<Course>();

        deptHeaders = new ArrayList<String>();
        departmentCourseHash = new HashMap<String, ArrayList<Course>>();

        //The outer loop loops through the departments
        try {
            for (int i = 0; i < jArray.length(); i++) {

                courseList = new ArrayList<Course>();
                deptDataNode = jArray.getJSONObject(i).getJSONObject("data");

                try {
                    deptTitle = deptDataNode.getString("name");
                    deptHeaders.add(deptTitle);
                } catch (Exception e) {
                    deptTitle = new String();
                    e.printStackTrace();
                }

                for (int j = 0; j < deptDataNode.getJSONArray("courses").length(); j++) {
                    courseDataNode = deptDataNode.getJSONArray("courses").getJSONObject(j);

                    try {
                        id = courseDataNode.getInt("id");
                    } catch (Exception e) {
                        id = 0;
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for id: " + e.toString());
                        continue;
                    }
                    try {
                        title = courseDataNode.getString("title");
                    } catch (Exception e) {
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for title: " + e.toString());
                        continue;
                    }
                    try {
                        code = courseDataNode.getString("code");
                    } catch (Exception e) {
                        code = "N/A";
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for code: " + e.toString());
                    }
                    try {
                        section = courseDataNode.getString("section");
                    } catch (Exception e) {
                        section = "N/A";
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for section: " + e.toString());
                    }
                    try {
                        department_id = courseDataNode.getInt("department_id");
                    } catch (Exception e) {
                        //maybe we should hold some uncategorized classes?
                        department_id = -1;
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for department_id: " + e.toString());
                    }
                    try {
                        instructor = courseDataNode.getString("instructor");
                    } catch (Exception e) {
                        //maybe we should hold some uncategorized classes?
                        instructor = "N/A";
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for instructor: " + e.toString());
                    }
                    try {
                        term = courseDataNode.getString("term");
                    } catch (Exception e) {
                        term = "N/A";
                        Log.e("CoursesFragment", "OnTaskCompleted() -> Couldn't parse JSON for term: " + e.toString());
                    }

                    courseList.add(new Course(id, title, code, section, department_id, instructor, term));
                }
                departmentCourseHash.put(deptTitle, courseList);
                numDepts++;
            }
        } catch (Exception e) {
            Log.e("CoursesFragment", "onTaskCompleted() -> " + e.toString());
            e.printStackTrace();
        }

        final CourseExpandableListAdapter courseAdapter = new CourseExpandableListAdapter(this.getActivity(), deptHeaders, departmentCourseHash);
        expListView.setAdapter(courseAdapter);
        if (numDepts == 1) {
            expListView.expandGroup(0);
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        Intent intent = new Intent(v.getContext(), Course_Info.class);
        Bundle extras = new Bundle();

        String currentHeader = deptHeaders.get(groupPosition);
        Course selectedCourse = departmentCourseHash.get(currentHeader).get(childPosition);
        extras.putParcelable("course", selectedCourse);
        intent.putExtras(extras);
        startActivity(intent);
        return true;
    }
}

