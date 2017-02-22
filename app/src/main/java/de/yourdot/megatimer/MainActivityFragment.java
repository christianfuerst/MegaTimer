package de.yourdot.megatimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.yourdot.megatimer.model.MegaTimer;

public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getCanonicalName();

    private RecyclerView.Adapter adapter;
    private List<MegaTimer> megaTimerList;
    private MegaTimer megaTimer_selected;
    private int position_selected;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.fab_options_menu)
    FloatingActionMenu floatingActionMenu;

    @BindView(R.id.fab_menu_reset)
    com.github.clans.fab.FloatingActionButton floatingActionButtonMenuReset;

    @BindView(R.id.fab_menu_edit)
    com.github.clans.fab.FloatingActionButton floatingActionButtonMenuEdit;

    @BindView(R.id.fab_menu_delete)
    com.github.clans.fab.FloatingActionButton floatingActionButtonMenuDelete;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        megaTimerList = MegaTimer.listAll(MegaTimer.class);
        adapter = new MegaTimerAdapter(megaTimerList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        disableOptions();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AddMegaTimerActivity.class);
                startActivity(intent);
            }
        });

        floatingActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableOptions();
                adapter.notifyItemChanged(position_selected);
            }
        });

        floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableOptions();
                adapter.notifyItemChanged(position_selected);
            }
        });

        floatingActionButtonMenuReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                megaTimer_selected.setStatus("initialised");
                megaTimer_selected.setStart(0);
                megaTimer_selected.setStop(megaTimer_selected.getLength());
                megaTimer_selected.save();

                disableOptions();
                adapter.notifyItemChanged(position_selected);
            }
        });

        floatingActionButtonMenuEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EditMegaTimerActivity.class);
                intent.putExtra("id", megaTimer_selected.getId());
                startActivity(intent);
            }
        });

        floatingActionButtonMenuDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(
                        view,
                        getString(R.string.timer) + " " +
                                megaTimer_selected.getTitle() + " " +
                                getString(R.string.fab_menu_delete_confirmed),
                        Snackbar.LENGTH_LONG);

                snackbar.show();

                megaTimerList.remove(megaTimer_selected.getId());
                megaTimer_selected.delete();

                disableOptions();

                megaTimerList = MegaTimer.listAll(MegaTimer.class);
                adapter = new MegaTimerAdapter(megaTimerList);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Subscribe
    public void handleIconClick(MegaTimerAdapter.OnIconClickEvent onIconClickEvent) {

        MegaTimer megaTimer;
        megaTimer = onIconClickEvent.megaTimer;

        switch (megaTimer.getStatus()) {
            // Timer started by user interaction
            case "initialised" :
                PendingIntent pendingIntent;
            {
                megaTimer.setStatus("running");
                megaTimer.setStart(System.currentTimeMillis());
                megaTimer.setStop(System.currentTimeMillis() + megaTimer.getLength());
                megaTimer.save();

                Intent intent = new Intent(getActivity(), MegaTimerReceiver.class);
                intent.putExtra("megatimer_id", megaTimer.getId());
                pendingIntent = PendingIntent.getBroadcast(
                        getActivity(),
                        megaTimer.getId().intValue(),
                        intent,
                        0);

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC,
                        megaTimer.getStop(),
                        pendingIntent);

                adapter.notifyItemChanged(onIconClickEvent.position);

                break;
            }
            // Timer paused by user interaction
            case "running" : {
                megaTimer.setStatus("paused");
                megaTimer.save();

                Intent intent = new Intent(getActivity(), MegaTimerReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(
                        getActivity(),
                        megaTimer.getId().intValue(),
                        intent,
                        0);

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                adapter.notifyItemChanged(onIconClickEvent.position);

                break;
            }
            // Timer restarted by user interaction
            case "paused" : {
                long progress = megaTimer.getProgress();
                long start = System.currentTimeMillis();
                long stop = System.currentTimeMillis() + megaTimer.getLength() - progress;

                megaTimer.setStatus("running");
                megaTimer.setStart(start);
                megaTimer.setStop(stop);
                megaTimer.save();

                Intent intent = new Intent(getActivity(), MegaTimerReceiver.class);
                intent.putExtra("megatimer_id", megaTimer.getId());
                pendingIntent = PendingIntent.getBroadcast(
                        getActivity(),
                        megaTimer.getId().intValue(),
                        intent,
                        0);

                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC,
                        stop,
                        pendingIntent);

                adapter.notifyItemChanged(onIconClickEvent.position);

                break;
            }
            // Timer reset by user interaction
            case "finished" : {
                megaTimer.setStatus("initialised");
                megaTimer.setStart(0);
                megaTimer.setStop(megaTimer.getLength());
                megaTimer.save();

                adapter.notifyItemChanged(onIconClickEvent.position);

                break;
            }
        }
    }

    @Subscribe
    public void handleTimerClick(MegaTimerAdapter.OnTimerClickEvent onTimerClickEvent) {
        megaTimer_selected = onTimerClickEvent.megaTimer;
        position_selected = onTimerClickEvent.position;

        enableOptions();
    }

    private void enableOptions() {
        floatingActionButton.setVisibility(View.INVISIBLE);
        floatingActionMenu.setVisibility(View.VISIBLE);
        floatingActionMenu.open(true);
    }

    private void disableOptions() {
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionMenu.setVisibility(View.INVISIBLE);
        floatingActionMenu.close(true);
    }
}
