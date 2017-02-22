package de.yourdot.megatimer;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codetroopers.betterpickers.hmspicker.HmsPickerBuilder;
import com.codetroopers.betterpickers.hmspicker.HmsPickerDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.yourdot.megatimer.model.MegaTimer;
import petrov.kristiyan.colorpicker.ColorPicker;

public class AddMegaTimerActivityFragment extends Fragment implements HmsPickerDialogFragment.HmsPickerDialogHandlerV2 {

    @BindView(R.id.editText_title)
    EditText editText_title;

    @BindView(R.id.textView_length_value)
    TextView textView_length_value;

    @BindView(R.id.button_length)
    Button button_length;

    @BindView(R.id.imageView_color)
    ImageView imageView_color;

    @BindView(R.id.button_color)
    Button button_color;

    @BindView(R.id.button_add)
    Button button_add;

    private String title;
    private long length;
    private int color;

    public AddMegaTimerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_mega_timer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        title = "";
        length = 0;

        button_add.setEnabled(false);

        textView_length_value.setText(DateUtils.formatElapsedTime(length));
        imageView_color.setBackgroundColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary)
        );

        editText_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                title = editText_title.getText().toString();

                if (DataIsValid()) {
                    button_add.setEnabled(true);
                } else {
                    button_add.setEnabled(false);
                }
            }
        });

        textView_length_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hmsPickerBuilder = new HmsPickerBuilder()
                        .addHmsPickerDialogHandler(AddMegaTimerActivityFragment.this)
                        .setFragmentManager(getFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragmentCustom)
                        .setTimeInMilliseconds(length);

                hmsPickerBuilder.show();
            }
        });

        button_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hmsPickerBuilder = new HmsPickerBuilder()
                        .addHmsPickerDialogHandler(AddMegaTimerActivityFragment.this)
                        .setFragmentManager(getFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragmentCustom)
                        .setTimeInMilliseconds(length);

                hmsPickerBuilder.show();
            }
        });

        imageView_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker colorPicker = new ColorPicker(getActivity());

                colorPicker.setTitle(getString(R.string.add_mega_timer_color_button));

                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        imageView_color.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel() {
                        imageView_color.setBackgroundColor(
                                ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary)
                        );
                    }
                }).show();
            }
        });

        button_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker colorPicker = new ColorPicker(getActivity());

                colorPicker.setTitle(getString(R.string.add_mega_timer_color_button));

                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        imageView_color.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel() {
                        imageView_color.setBackgroundColor(
                                ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary)
                        );
                    }
                }).show();
            }
        });

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = editText_title.getText().toString();
                ColorDrawable colorDrawable = (ColorDrawable) imageView_color.getBackground();
                color = colorDrawable.getColor();

                MegaTimer megaTimer = new MegaTimer(
                        title,
                        "initialised",
                        length,
                        color,
                        0,
                        length);
                megaTimer.save();

                getActivity().finish();
            }
        });

    }

    @Override
    public void onDialogHmsSet(int reference, boolean isNegative, int hours, int minutes, int seconds) {
        length = (seconds + (minutes * 60) + (hours * 3600)) * 1000;
        textView_length_value
                .setText(DateUtils.formatElapsedTime(seconds + (minutes * 60) + (hours * 3600)));

        if (DataIsValid()) {
            button_add.setEnabled(true);
        } else {
            button_add.setEnabled(false);
        }
    }

    private boolean DataIsValid() {
        return !(title.equals("") || length == 0);
    }

}
