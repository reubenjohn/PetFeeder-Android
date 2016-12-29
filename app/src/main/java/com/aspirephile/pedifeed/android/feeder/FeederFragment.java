package com.aspirephile.pedifeed.android.feeder;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspirephile.pedifeed.android.R;
import com.aspirephile.pedifeed.android.connection.BluetoothSerialService;
import com.aspirephile.pedifeed.android.connection.DeviceListActivity;
import com.aspirephile.pedifeed.android.db.Db;
import com.aspirephile.pedifeed.android.db.async.OnQueryCompleteListener;
import com.aspirephile.pedifeed.android.record.Record.SyncManager;
import com.aspirephile.pedifeed.android.schedule.Schedule;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeederFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeederFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeederFragment extends Fragment {
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private OnFragmentInteractionListener mListener;
    private TextView deviceStatusView, deviceNameView;
    private Button connectB;
    private TextView logView;
    private BluetoothSerialService mSerialService;
    private boolean mEnablingBT;
    private BluetoothAdapter mBluetoothAdapter;
    private Button feedNowB;
    private SeekBar quantitySeekBar;
    private TextView quantityView;
    private Button syncB;
    private Editable editableLog;
    private ScrollView logScrollerView;
    private SyncManager syncManager;
    private String deviceName;
    private int id;
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:
                            if (isVisible()) {
                                deviceStatusView.setText(R.string.connected);
                                deviceStatusView.setBackgroundColor(Color.GREEN);
                                connectB.setTextColor(Color.RED);
                                connectB.setText(R.string.disconnect);
                                feedNowB.setEnabled(true);
                                syncB.setEnabled(true);
                            }

                            deviceNameView.setText(R.string.title_connected_to);
                            deviceNameView.append(" " + deviceName);
                            break;

                        case BluetoothSerialService.STATE_CONNECTING:
                            deviceNameView.setText(R.string.title_connecting);
                            break;
                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:
                            if (isVisible()) {
                                deviceStatusView.setText(R.string.disconnected);
                                deviceStatusView.setBackgroundColor(Color.RED);
                                connectB.setTextColor(Color.GREEN);
                                connectB.setText(R.string.connect);
                                feedNowB.setEnabled(false);
                                syncB.setEnabled(false);
                                deviceNameView.setText(R.string.na);
                            }

                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buf;
                    String string;

                    buf = (byte[]) msg.obj;
                    string = new String(buf, 0, msg.arg1);
                    editableLog.append(string);
                    if (editableLog.length() > 4000) {
                        editableLog.delete(0, editableLog.length() - 4000);
                    }
                    logScrollerView.fullScroll(View.FOCUS_DOWN);
                    break;

                case MESSAGE_READ:
                    if (id == -1) {
                        id = msg.arg2;
                    } else if (id + 1 == msg.arg2) {
                        id++;
                        Log.d("ID", String.valueOf(id));
                    } else {
                        Log.d("ID", "Out of sequence!");
                        Log.d("ID", String.valueOf(msg.arg2));
                    }
                    buf = (byte[]) msg.obj;
                    syncManager.onRead(buf);
                    string = new String(buf, 0, msg.arg1);
                    logView.append(string);
                    if (editableLog.length() > 4000) {
                        editableLog.delete(0, editableLog.length() - 4000);
                    }
                    logScrollerView.fullScroll(View.FOCUS_DOWN);
                    break;

                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    deviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_connected_to) + " "
                            + deviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    if (isVisible())
                        Toast.makeText(getActivity().getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public FeederFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FeederFragment.
     */
    public static FeederFragment newInstance() {
        FeederFragment fragment = new FeederFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mSerialService != null)
            mSerialService.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        quantitySeekBar.setMax(PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("pref_max_servings", 4));

        if (!mEnablingBT) { // If we are turning on the BT we cannot check if it's enable
            if ((mBluetoothAdapter != null) && (!mBluetoothAdapter.isEnabled())) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.alert_dialog_turn_on_bt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_warning_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mEnablingBT = true;
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishDialogNoBluetooth();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            if (mSerialService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mSerialService.start();
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mEnablingBT = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feeder, container, false);
        deviceStatusView = (TextView) view.findViewById(R.id.device_status);
        deviceNameView = (TextView) view.findViewById(R.id.device_name);
        connectB = (Button) view.findViewById(R.id.b_connect);
        connectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                    Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                } else if (mSerialService.getState() == BluetoothSerialService.STATE_CONNECTED) {
                    mSerialService.stop();
                    mSerialService.start();
                }
            }
        });
        logView = (TextView) view.findViewById(R.id.log);
        logView.setText(logView.getText(), TextView.BufferType.EDITABLE);
        editableLog = (Editable) logView.getText();

        logScrollerView = (ScrollView) view.findViewById(R.id.logScroller);

        mSerialService = new BluetoothSerialService(getActivity(), mHandlerBT);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            finishDialogNoBluetooth();
        }

        feedNowB = (Button) view.findViewById(R.id.b_feed_now);
        feedNowB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SERIAL", "f" + quantitySeekBar.getProgress());
                mSerialService.write(("f" + quantitySeekBar.getProgress() + " ").getBytes());
            }
        });

        syncManager = new SyncManager(mSerialService);

        syncB = (Button) view.findViewById(R.id.b_sync);
        syncB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Db.getScheduleManager().getListQuery().queryInBackground(new OnQueryCompleteListener() {
                    @Override
                    public void onQueryComplete(Cursor c, SQLException e) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            List<Schedule.Content> list = Db.getScheduleManager().getListFromResult(c);
                            syncManager
                                    .setRecordsToWrite(
                                            PreferenceManager
                                                    .getDefaultSharedPreferences(getActivity().getApplicationContext())
                                                    .getInt("pref_max_pending_schedules", 99) * 10)
                                    .setSyncLatency(
                                            PreferenceManager
                                                    .getDefaultSharedPreferences(getActivity().getApplicationContext())
                                                    .getInt("pref_sync_latency", 500) * 10)
                                    .setScheduleList(list);
                            syncB.setEnabled(false);
                            syncB.setText(R.string.syncing);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    syncManager.sync();
                                    syncB.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            syncB.setEnabled(true);
                                            syncB.setText(R.string.sync);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
            }
        });

        quantityView = (TextView) view.findViewById(R.id.quantity);

        quantitySeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        quantitySeekBar.setMax(PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getInt("pref_max_servings", 4));
        quantitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress == 0) {
                    seekBar.setProgress(1);
                    quantityView.setText(1 + " servings");
                } else {
                    quantityView.setText(progress + " servings");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

    public void finishDialogNoBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.alert_dialog_no_bt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_name)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:

                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mSerialService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    finishDialogNoBluetooth();
                }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
