package com.newsblur.fragment;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.newsblur.R;
import com.newsblur.activity.ItemsList;
import com.newsblur.database.StoryItemsAdapter;
import com.newsblur.util.StoryOrder;

public abstract class ItemListFragment extends Fragment implements OnScrollListener {

    protected int currentPage = 0;
    protected boolean requestedPage;
	protected StoryItemsAdapter adapter;
	
	public abstract void hasUpdated();
	public abstract void changeState(final int state);
	public abstract void setStoryOrder(StoryOrder storyOrder);


    public void resetPagination() {
        this.currentPage = 0;
    }

    public void setEmptyListView(int rid) {
        View v = this.getView();
        if (v == null) return; // we might have beat construction?

        ListView itemList = (ListView) v.findViewById(R.id.itemlistfragment_list);
        if (itemList == null) {
            Log.w(this.getClass().getName(), "ItemListFragment does not have the expected ListView.");
            return;
        }

        TextView emptyView = (TextView) itemList.getEmptyView();
        emptyView.setText(rid);
    }

	@Override
	public synchronized void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		if (totalCount != 0 && (firstVisible + visibleCount == totalCount) && !requestedPage) {
			currentPage += 1;
			requestedPage = true;
			triggerRefresh(currentPage);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { }

	protected void triggerRefresh(int page) {
        ((ItemsList) getActivity()).triggerRefresh(page);
    }

    // all child classes need this callback, so implement it here
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null) {
            if (cursor.getCount() == 0) {
                currentPage += 1;
                requestedPage = true;
                triggerRefresh(currentPage);
            }
			adapter.swapCursor(cursor);
		}
	}

}
