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
import android.widget.Toast;

import com.example.myswipelistview.R;

public class SwipeDismissAdaper extends ArrayAdapter<String> {

	private LayoutInflater inflater;
	private ArrayList<String> items;
	private ListView listView;

	public SwipeDismissAdaper(Context context, int resource,ArrayList<String> items) {
		super(context, resource);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View workingView = null;

		if (convertView == null) {
			workingView = inflater.inflate(R.layout.list_item, parent, false);
			setViewHolder(workingView);
		} else if (((ViewHolder) convertView.getTag()).needInflate) {
			workingView = inflater.inflate(R.layout.list_item, parent, false);
			setViewHolder(workingView);
		} else {
			workingView = convertView;
		}

		ViewHolder viewHolder = (ViewHolder) workingView.getTag();
		viewHolder.tView.setText(items.get(position));

		// applying swipe detector
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
		viewHolder.tView = (TextView) workingView.findViewById(R.id.itemName);

		viewHolder.needInflate = false;
		workingView.setTag(viewHolder);
	}

	public class SwipeDetector implements View.OnTouchListener {

		private static final int MIN_DISTANCE = 300;
		private static final int MIN_LOCK_DISTANCE = 30;
		private boolean motionInterceptDisallowed = false;
		private float downX, upX;
		private ViewHolder holder;
		private int position;

		public SwipeDetector(ViewHolder h, int pos) {
			holder = h;
			position = pos;
		}

		@SuppressLint("ClickableViewAccessibility") 
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				return true; // allow other events like Click to be processed
			}

			case MotionEvent.ACTION_MOVE: {
				upX = event.getX();
				float deltaX = downX - upX;

				if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && listView != null && !motionInterceptDisallowed) {
					listView.requestDisallowInterceptTouchEvent(true);
					motionInterceptDisallowed = true;
				}

				if (deltaX > 0) {
					holder.deleteView.setVisibility(View.GONE);
				} else {
					holder.deleteView.setVisibility(View.VISIBLE);	//	if first swiped left and then swiped right
				}
				swipe(-(int) deltaX);
				return true;
			}

			case MotionEvent.ACTION_UP:
				upX = event.getX();
				float deltaX = upX - downX;
				
				if (Math.abs(deltaX) > MIN_DISTANCE) {
//					swipeRemove();					
//					swipeRemoveWithFadeOutAnim(v);					
					swipeRemoveWithSlideOutAnims(v,deltaX);
				} else {
					swipe(0);
				}

				if (listView != null) {
					listView.requestDisallowInterceptTouchEvent(false);
					motionInterceptDisallowed = false;
				}
//				holder.deleteView.setVisibility(View.VISIBLE);	//	makes trouble
				return true;

			case MotionEvent.ACTION_CANCEL:
				holder.deleteView.setVisibility(View.VISIBLE);
				return false;
			}

			return true;
		}

		//	swiping and deletions
		private void swipeRemoveWithSlideOutAnims(final View view, float deltaX) {			
			
			Animation animation;
			
			//	its right			
			if(deltaX < 0){
				animation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_right_to_left_out);
			}
			//	its left
			else{
				animation = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_left_to_right_out);
			}
			
			animation.setDuration(500);
			
			animation.setAnimationListener(new AnimationListener() {
					
				@Override
				public void onAnimationStart(Animation animation) {}
					
				@Override
				public void onAnimationRepeat(Animation animation) {}
					
				@Override
				public void onAnimationEnd(Animation animation) {
					items.remove(position);
			        notifyDataSetChanged();
			        
			        Toast.makeText(getContext(), "item deleted : "+position, Toast.LENGTH_SHORT).show();
			        
			        Animation anim = new Animation() {
			        	
			        	final int initialHeight = view.getMeasuredHeight();

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
					anim.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {}
						
						@Override
						public void onAnimationRepeat(Animation animation) {}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							
//							items.remove(position);							
//							ViewHolder viewHolder = (ViewHolder)view.getTag();
//							viewHolder.needInflate = true;							
//					        notifyDataSetChanged();
					        
							ViewHolder viewHolder = (ViewHolder)view.getTag();
							viewHolder.needInflate = true;
							notifyDataSetChanged();
						}
					});
					
					anim.setDuration(250);
					view.startAnimation(anim);
				}
			});
			
			holder.mainView.startAnimation(animation);
		}

//		private void swipeRemoveWithFadeOutAnim(View v) {
//			final Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.list_item_removal_anim);
//		    v.startAnimation(animation);
//		    Handler handle = new Handler();
//		    handle.postDelayed(new Runnable() {
//
//		        @Override
//		        public void run() {
//		            items.remove(position);
//		            notifyDataSetChanged();
//		            animation.cancel();
//		        }
//		    }, 500);
//		}
		
//		private void swipeRemove() {
//			items.remove(position);
//			notifyDataSetChanged();
//		}

		private void swipe(int distance) {
			View animationView = holder.mainView;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
			params.rightMargin = -distance;
			params.leftMargin = distance;
			animationView.setLayoutParams(params);
		}		
	}

	// setting listview
	public void setListView(ListView listView) {
		this.listView = listView;
	}
	
	public static class ViewHolder {
		public TextView tView;
		public LinearLayout mainView;
		public RelativeLayout deleteView;
		public RelativeLayout shareView;
		
		//	needInflate or not
		public boolean needInflate;
	}
}