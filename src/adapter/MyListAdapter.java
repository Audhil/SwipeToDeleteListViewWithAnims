package adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myswipelistview.R;

public class MyListAdapter extends ArrayAdapter<String> {
	
	public static final String RIGHT = "rightSwipe";
	public static final String LEFT = "leftSwipe";

	private LayoutInflater inflater;
	private ArrayList<String> items;
	private ListView listView;
	private Context context;

	public MyListAdapter(Context context, int resource, ArrayList<String> items) {
		super(context, resource);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.context = context;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View workingView = null;
		
		if(convertView == null){
			workingView = inflater.inflate(R.layout.list_item, parent,false);
			setViewHolder(workingView);
		}
		else if(((ViewHolder)convertView.getTag()).needInflate){
			workingView = inflater.inflate(R.layout.list_item, parent,false);
			setViewHolder(workingView);
		}
		else{
			workingView = convertView;
		}
		
		ViewHolder viewHolder = (ViewHolder)workingView.getTag();		
		viewHolder.tView.setText(items.get(position));
		
		//	applying swipe detector
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.mainView.getLayoutParams();
		params.rightMargin = 0;
		params.leftMargin = 0;
		
		viewHolder.mainView.setLayoutParams(params);
		workingView.setOnTouchListener(new SwipeDetector(viewHolder, position));
		
		return workingView;
	}

	private void setViewHolder(View workingView) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mainView = (LinearLayout) workingView.findViewById(R.id.audio_object_mainview);
		viewHolder.deleteView = (RelativeLayout) workingView.findViewById(R.id.audio_object_deleteview);
		viewHolder.shareView = (RelativeLayout) workingView.findViewById(R.id.audio_object_shareview);
		viewHolder.tView = (TextView)workingView.findViewById(R.id.itemName);
		
		viewHolder.needInflate = false;
		workingView.setTag(viewHolder);
	}

	public void setListView(ListView view) {
		listView = view;
	}

	public static class ViewHolder {
		public TextView tView;
		public LinearLayout mainView;
		public RelativeLayout deleteView;
		public RelativeLayout shareView;
		
		//	needInflate or not
		public boolean needInflate;
	}

	// swipe detector
	public class SwipeDetector implements View.OnTouchListener {

		private static final int MIN_DISTANCE = 300;
		private static final int MIN_LOCK_DISTANCE = 30;
		private boolean motionInterceptDisallowed = false;
		private float downX, upX;
		private ViewHolder viewHolder;
		private int position;

		public SwipeDetector(ViewHolder h, int pos) {
			viewHolder = h;
			position = pos;
		}

		@SuppressLint("ClickableViewAccessibility") 
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction()) {
			
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				
				// allow other events like Click to be processed
				return true;
			}

			case MotionEvent.ACTION_MOVE: {
				upX = event.getX();
				
				float deltaX = downX - upX;

				if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && listView != null && !motionInterceptDisallowed) {
					listView.requestDisallowInterceptTouchEvent(true);
					motionInterceptDisallowed = true;
				}

				if (deltaX > 0) {
					viewHolder.deleteView.setVisibility(View.GONE);
				} else {
					// if first swiped left and then swiped right
					viewHolder.deleteView.setVisibility(View.VISIBLE);
				}

				swipe(-(int) deltaX);
				return true;
			}

			case MotionEvent.ACTION_UP:				
				upX = event.getX();
				
				float deltaX = upX - downX;
				
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
//					working fine without animation
//					swipeRemove();
					
					if(deltaX < 0){
						swipeToRemove(v,RIGHT);
					}
					else{
						swipeToRemove(v,LEFT);
					}
					
				} else {
					swipe(0);
				}

				if (listView != null) {
					listView.requestDisallowInterceptTouchEvent(false);
					motionInterceptDisallowed = false;
				}

				viewHolder.deleteView.setVisibility(View.VISIBLE);
				return true;

			case MotionEvent.ACTION_CANCEL:
				viewHolder.deleteView.setVisibility(View.VISIBLE);
				return false;
			}

			return true;
		}

		private void swipe(int distance) {
			View animationView = viewHolder.mainView;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
			params.rightMargin = -distance;
			params.leftMargin = distance;			
			animationView.setLayoutParams(params);
		}

//		working fine without animation
//		private void swipeRemove() {			
//			Toast.makeText(context, items.get(position) + " : posi : "+position, Toast.LENGTH_SHORT).show();
//			items.remove(position);			
//			notifyDataSetChanged();			
//		}
		
//		addingWith animation
		public void swipeToRemove(final View view,final String whichDirection) {
//									
////			for vertical animation
//			final int initialHeight = view.getMeasuredHeight();
//			
//			final Animation vertiAnim = new Animation() {
//				
//				@Override
//				protected void applyTransformation(float interpolatedTime, Transformation t) {
//					
//					if (interpolatedTime == 1) {
//						view.setVisibility(View.GONE);
//					}
//					else {
//						view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
//						view.requestLayout();
//					}
//				}
//
//				@Override
//				public boolean willChangeBounds() {
//					return true;
//				}
//			};
//			
//			vertiAnim.setAnimationListener(new AnimationListener() {
//				
//				@Override
//				public void onAnimationStart(Animation animation) {}
//				
//				@Override
//				public void onAnimationRepeat(Animation animation) {}
//				
//				@Override
//				public void onAnimationEnd(Animation arg0) {
//					
//					items.remove(position);
//
//					ViewHolder viewHolder = (ViewHolder)view.getTag();
//					viewHolder.needInflate = true;
//
//					notifyDataSetChanged();
//				}
//			});		
//			
//			vertiAnim.setDuration(1000);			
////			view.startAnimation(vertiAnim);
//			
////			for horizontal animation
//			final int viewWidth = view.getMeasuredWidth();
//					
//			Animation horiAnim = new Animation() {
//				
//				@Override
//				protected void applyTransformation(float interpolatedTime,Transformation t) {
//							
//					if (interpolatedTime == 1) {
//						view.setVisibility(View.GONE);
//					}
//					else {
//						//	right swipe
//						if(whichDirection.equals(RIGHT)){
//							view.getLayoutParams().width = viewWidth - (int)(viewWidth * interpolatedTime);
//						}
//						//	left swipe
//						else if(whichDirection.equals(LEFT)){
//							view.getLayoutParams().width = viewWidth + (int)(viewWidth * interpolatedTime);
//						}		
//						
//						view.requestLayout();
//					}
//				}
//			};
//			
//			horiAnim.setAnimationListener(new AnimationListener() {
//						
//				@Override
//				public void onAnimationStart(Animation animation) {}
//						
//				@Override
//				public void onAnimationRepeat(Animation animation) {}
//						
//				@Override
//				public void onAnimationEnd(Animation animation) {
//					view.startAnimation(vertiAnim);
//				}
//			});
//					
//			horiAnim.setDuration(500);
//			view.startAnimation(horiAnim);
			
			final int initialHeight = view.getMeasuredHeight();
			
			final Animation vertiAnim = new Animation() {
				
				@Override
				protected void applyTransformation(float interpolatedTime, Transformation t) {
					
					if (interpolatedTime == 1) {
						view.setVisibility(View.GONE);
					}
					else {
						view.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
						view.requestLayout();
					}
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};
			
			vertiAnim.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation arg0) {
					
					items.remove(position);

					ViewHolder viewHolder = (ViewHolder)view.getTag();
					viewHolder.needInflate = true;

					notifyDataSetChanged();
				}
			});		
			
			vertiAnim.setDuration(1000);			
			
			Animation animation = null;
			
			if(whichDirection.equals(RIGHT)){
				animation = AnimationUtils.loadAnimation(context,R.anim.slide_in_right_to_left_out);				
			}
			else if(whichDirection.equals(LEFT)){
				animation = AnimationUtils.loadAnimation(context,R.anim.slide_in_left_to_right_out);
			}
			
			animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					view.startAnimation(vertiAnim);
				}
			});
			
			view.startAnimation(animation);
		}
	}
}