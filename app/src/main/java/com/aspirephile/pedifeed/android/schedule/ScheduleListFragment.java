package com.aspirephile.pedifeed.android.schedule;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspirephile.pedifeed.android.R;
import com.aspirephile.pedifeed.android.db.Db;
import com.aspirephile.pedifeed.android.db.async.OnQueryCompleteListener;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ScheduleListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private final OnListFragmentInteractionListener internalListener;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScheduleListFragment() {
        internalListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Schedule.RowItem item) {
                mListener.onListFragmentInteraction(item);
                onRefresh();
            }
        };
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ScheduleListFragment newInstance(int columnCount) {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        refreshView = (SwipeRefreshLayout) view.findViewById(R.id.srl_record_list);
        refreshView.setOnRefreshListener(this);
        onRefresh();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScheduleListFragment.OnListFragmentInteractionListener) {
            mListener = (ScheduleListFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        Db.getScheduleManager().getListQuery()
                .queryInBackground(new OnQueryCompleteListener() {
                    @Override
                    public void onQueryComplete(Cursor c, SQLException e) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        refreshView.setRefreshing(false);
                        List<Schedule.Content> list = Db.getScheduleManager().getListFromResult(c);
                        recyclerView.setAdapter(new ScheduleRecyclerViewAdapter(
                                list, internalListener));
                    }
                });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Schedule.RowItem item);
    }
}
