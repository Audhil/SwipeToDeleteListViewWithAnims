package activity;

import java.util.ArrayList;

//import adapter.MyListAdapter;
import adapter.SwipeDismissAdaper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myswipelistview.R;

public class MainActivity extends Activity {

	private ListView listView;
//	private MyListAdapter listAdapter;
	
	private SwipeDismissAdaper listAdapter;
	private ArrayList<String> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}

	private void initViews() {

		items = new ArrayList<String>();
		
		for (int i = 0; i < 15; i++) {
			items.add("item no : "+i);
		}

		listView = (ListView) findViewById(R.id.listView);
//		listAdapter = new MyListAdapter(getApplicationContext(),0, items);
//		listView.setAdapter(listAdapter);
//		listAdapter.setListView(listView);
		
		listAdapter = new SwipeDismissAdaper(getApplicationContext(),0, items);
		listView.setAdapter(listAdapter);
		listAdapter.setListView(listView);
	}
}