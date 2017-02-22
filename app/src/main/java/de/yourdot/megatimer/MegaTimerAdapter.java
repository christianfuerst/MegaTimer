package de.yourdot.megatimer;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.yourdot.megatimer.model.MegaTimer;

public class MegaTimerAdapter extends RecyclerView.Adapter<MegaTimerAdapter.MegaTimerViewHolder> {

    private static final String TAG = MegaTimerAdapter.class.getCanonicalName();

    private Context context;

    private final List<MegaTimer> megaTimerList;
    private final List<MegaTimerViewHolder> megaTimerViewHolderList;
    private final Timer timer;

    private final Handler handler = new Handler();
    private final Runnable updateMegaTimerRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (megaTimerViewHolderList) {
                long currentTime = System.currentTimeMillis();
                for (MegaTimerViewHolder megaTimerViewHolder : megaTimerViewHolderList) {
                    megaTimerViewHolder.updateMegaTimer(currentTime);
                }
            }
        }
    };

    public MegaTimerAdapter(List<MegaTimer> megaTimerList) {
        this.megaTimerList = megaTimerList;
        this.megaTimerViewHolderList = new ArrayList<>();
        this.timer = new Timer();
    }

    public class OnIconClickEvent {
        public final MegaTimer megaTimer;
        public final int position;

        public OnIconClickEvent(MegaTimer megaTimer, int position) {
            this.megaTimer = megaTimer;
            this.position = position;
        }
    }

    public class OnTimerClickEvent {
        public final MegaTimer megaTimer;
        public final int position;

        public OnTimerClickEvent(MegaTimer megaTimer, int position) {
            this.megaTimer = megaTimer;
            this.position = position;
        }
    }

    @Override
    public MegaTimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.megatimer_list_item, parent, false);

        context = parent.getContext();

        return new MegaTimerViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        startMegaTimer();
    }

    @Override
    public void onBindViewHolder(MegaTimerViewHolder holder, int position) {

        final MegaTimer megaTimer = megaTimerList.get(position);
        final MegaTimerViewHolder megaTimerViewHolder = holder;

        holder.setData(megaTimer);
        synchronized (megaTimerViewHolderList) {
            megaTimerViewHolderList.add(holder);
        }

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.rcBackgroundColor));
        holder.progressBar.setProgressBackgroundColor(ContextCompat.getColor(context, R.color.rcBackgroundColor));
        holder.textView_title.setTextColor(ContextCompat.getColor(context, R.color.rcTextColor));
        holder.textView_length.setTextColor(ContextCompat.getColor(context, R.color.rcTextColor));
        holder.progressBar.setIconBackgroundColor(megaTimer.getColor());
        holder.progressBar.setProgressColor(megaTimer.getColor() + 10000);

        holder.textView_title.setText(megaTimer.getTitle());
        holder.textView_length.setText(DateUtils.formatElapsedTime(megaTimer.getElapsedTime()));

        holder.progressBar.setMax(megaTimer.getLength());
        holder.progressBar.setProgress(megaTimer.getProgress());

        holder.progressBar.setOnIconClickListener(new IconRoundCornerProgressBar.OnIconClickListener() {
            @Override
            public void onIconClick() {
                EventBus.getDefault().post(new OnIconClickEvent(megaTimer, megaTimerViewHolder.getAdapterPosition()));
            }
        });

        holder.textView_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! megaTimer.getStatus().equals("running")) {
                    handleOnTimerClick(megaTimer, megaTimerViewHolder, view);
                }
            }
        });

        holder.textView_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! megaTimer.getStatus().equals("running")) {
                    handleOnTimerClick(megaTimer, megaTimerViewHolder, view);
                }            }
        });

        holder.progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! megaTimer.getStatus().equals("running")) {
                    handleOnTimerClick(megaTimer, megaTimerViewHolder, view);
                }            }
        });
    }

    @Override
    public void onViewRecycled(MegaTimerViewHolder holder) {
        super.onViewRecycled(holder);

        synchronized (megaTimerViewHolderList) {
            megaTimerViewHolderList.remove(holder);
        }
    }

    @Override
    public int getItemCount() {
        return megaTimerList.size();
    }

    private void startMegaTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updateMegaTimerRunnable);
            }
        }, 250, 250);
    }

    public class MegaTimerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.textView_title)
        TextView textView_title;
        @BindView(R.id.textView_length)
        TextView textView_length;
        @BindView(R.id.progress_bar)
        IconRoundCornerProgressBar progressBar;

        MegaTimer megaTimerHolder;

        public MegaTimerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            megaTimerHolder = null;
        }

        public void setData(MegaTimer megaTimer) {
            megaTimerHolder = megaTimer;
            updateMegaTimer(System.currentTimeMillis());
        }

        public void updateMegaTimer(long currentTime) {

            switch (megaTimerHolder.getStatus()) {
                case "initialised" : {
                    progressBar.setIconImageResource(R.drawable.ic_action_av_play_arrow);
                    progressBar.setProgress(0);
                    textView_length.setText(DateUtils.formatElapsedTime(megaTimerHolder.getElapsedTime()));

                    break;
                }
                case "running" : {
                    progressBar.setIconImageResource(R.drawable.ic_action_av_pause);

                    if (currentTime < megaTimerHolder.getStop()) {
                        megaTimerHolder.setStart(System.currentTimeMillis());
                        progressBar.setProgress(megaTimerHolder.getProgress());
                        textView_length.setText(DateUtils.formatElapsedTime(megaTimerHolder.getElapsedTime()));
                    } else {
                        megaTimerHolder.setStatus("finished");
                        megaTimerHolder.setStart(megaTimerHolder.getStop());
                        progressBar.setProgress(megaTimerHolder.getLength());
                        textView_length.setText(DateUtils.formatElapsedTime(megaTimerHolder.getElapsedTime()));
                        progressBar.setIconImageResource(R.drawable.ic_action_av_replay);
                    }

                    break;
                }
                case "paused" : {
                    progressBar.setProgress(megaTimerHolder.getProgress());
                    textView_length.setText(DateUtils.formatElapsedTime(megaTimerHolder.getElapsedTime()));
                    progressBar.setIconImageResource(R.drawable.ic_action_av_play_arrow);
                    break;
                }
                case "finished" : {
                    progressBar.setProgress(megaTimerHolder.getLength());
                    textView_length.setText(DateUtils.formatElapsedTime(megaTimerHolder.getElapsedTime()));
                    progressBar.setIconImageResource(R.drawable.ic_action_av_replay);
                    break;
                }
            }
        }
    }

    private void handleOnTimerClick(MegaTimer megaTimer, MegaTimerViewHolder megaTimerViewHolder, View view) {
        EventBus.getDefault().post(new OnTimerClickEvent(megaTimer, megaTimerViewHolder.getAdapterPosition()));
        megaTimerViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
        megaTimerViewHolder.progressBar.setProgressBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
        megaTimerViewHolder.textView_title.setTextColor(ContextCompat.getColor(view.getContext(), R.color.rcBackgroundColor));
        megaTimerViewHolder.textView_length.setTextColor(ContextCompat.getColor(view.getContext(), R.color.rcBackgroundColor));
    }

}
