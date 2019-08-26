package org.ministryofhealth.healthtalkkit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ministryofhealth.healthtalkkit.adapter.KitAdapter;
import org.ministryofhealth.healthtalkkit.database.Database;
import org.ministryofhealth.healthtalkkit.model.HealthTalkKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements KitAdapter.ItemClick{

    RecyclerView recyclerView;
    ExpandableListView expandableListView;
    ParentListAdapter listAdapter;
    KitAdapter adapter;
    Database db;
    List<HealthTalkKit> kits = new ArrayList<HealthTalkKit>();

    private int lastExpandedPosition = -1;

    List<String> groups = new ArrayList<String>();
    HashMap<String, List<HealthTalkKit>> subcontentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);
        kits = db.getHealthTalkKits();

        for (HealthTalkKit kit:
             kits) {
            List<HealthTalkKit> subcontent = db.getHealthTalkKitSubcontent(kit.getId());
            groups.add(kit.getTitle());
            subcontentMap.put(kit.getTitle(), subcontent);
        }

        recyclerView = findViewById(R.id.health_talk_kit);
        expandableListView = findViewById(R.id.expandableList);

        listAdapter = new ParentListAdapter();
        expandableListView.setAdapter(listAdapter);

        expandableListView.expandGroup(0);
        lastExpandedPosition = 0;

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                HealthTalkKit kit = subcontentMap.get(groups.get(groupPosition)).get(childPosition);
                Intent intent = new Intent(MainActivity.this, GrandchildrenActivity.class);
                intent.putExtra("subcontent_id", kit.getId());
                startActivity(intent);
                return true;
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

//        LinearLayoutManager layoutManager
//                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(true);
//        adapter = new KitAdapter(this);
//        adapter.setKits(kits);
//        recyclerView.setAdapter(adapter);
    }

    protected void showContentDialog(String content){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.layout_content);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        WebView webview = dialog.findViewById(R.id.contentWebview);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadDataWithBaseURL("", content, "text/html", "UTF-8", "");
        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Refresh Content?");
                builder.setMessage("Are you sure you want to refresh content?");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.clearTables();
                        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(HealthTalkKit kit) {
        Intent intent = new Intent(this, SubcontentActivity.class);
        intent.putExtra("kit_id", kit.getId());
        startActivity(intent);
    }

    public class ParentListAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return subcontentMap.get(groups.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return subcontentMap.get(groups.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
            final String groupTitle = (String) getGroup(groupPosition);
            final HealthTalkKit kit = kits.get(groupPosition);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.parent_layout, null);
            }

            TextView txtListHeader = convertView.findViewById(R.id.title);
            final ImageView btnDropdown = convertView.findViewById(R.id.bt_expand);

            txtListHeader.setText(groupTitle);

            btnDropdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isExpanded) ((ExpandableListView) parent).collapseGroup(groupPosition);
                    else ((ExpandableListView) parent).expandGroup(groupPosition, true);
                }
            });

            txtListHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Kit content======" + kit.getContent());
                    if (kit.getContent() != null && !kit.getContent().equals("<p><br></p>")) {
                        showContentDialog(kit.getContent());
                    }else{
                        btnDropdown.callOnClick();
                    }
                }
            });

            if (isExpanded){
                btnDropdown.setImageResource(R.drawable.ic_remove_circle_outline);
            }else{
                btnDropdown.setImageResource(R.drawable.ic_add_circle_outline);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            HealthTalkKit kit = (HealthTalkKit) getChild(groupPosition, childPosition);
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.title_item, null);
            }

            TextView txtChildView = convertView.findViewById(R.id.title);
            txtChildView.setText(kit.getTitle());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
