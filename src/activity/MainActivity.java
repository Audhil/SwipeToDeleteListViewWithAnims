package activity;

import java.util.ArrayList;

//import adapter.MyListAdapter;
import adapter.SwipeToDismissListViewAdaper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.myswipelistview.R;

//wonderful tutorial @ http://www.jayrambhia.com/blog/swipe-listview/
//there is another tutorial for smooth item deletion @ https://github.com/paraches/ListViewCellDeleteAnimation/blob/master/src/com/example/myanimtest/MainActivity.java

public class MainActivity extends Activity {

	private ListView listView;
//	private MyListAdapter listAdapter;
	
	private SwipeToDismissListViewAdaper listAdapter;
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
		
		listAdapter = new SwipeToDismissListViewAdaper(getApplicationContext(),0, items);
		listView.setAdapter(listAdapter);
		listAdapter.setListView(listView);
	}
}