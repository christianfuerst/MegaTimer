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

public class EditMegaTimerActivityFragment extends Fragment implements HmsPickerDialogFragment.HmsPickerDialogHandlerV2 {

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

    @BindView(R.id.button_edit)
    Button button_edit;

    private MegaTimer megaTimer;
    private String title;
    private long length;
    private int color;

    public EditMegaTimerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_mega_timer, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getActivity().getIntent().getExtras();

        long id = bundle.getLong("id");

        megaTimer = MegaTimer.findById(MegaTimer.class, id);

        title = megaTimer.getTitle();
        length = megaTimer.getLength();
        color = megaTimer.getColor();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button_edit.setEnabled(true);

        editText_title.setText(title);
        textView_length_value.setText(DateUtils.formatElapsedTime(length / 1000));
        imageView_color.setBackgroundColor(color);

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
                    button_edit.setEnabled(true);
                } else {
                    button_edit.setEnabled(false);
                }
            }
        });

        textView_length_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HmsPickerBuilder hmsPickerBuilder = new HmsPickerBuilder()
                        .addHmsPickerDialogHandler(EditMegaTimerActivityFragment.this)
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
                        .addHmsPickerDialogHandler(EditMegaTimerActivityFragment.this)
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

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = editText_title.getText().toString();
                ColorDrawable colorDrawable = (ColorDrawable) imageView_color.getBackground();
                color = colorDrawable.getColor();

                megaTimer.setTitle(title);
                megaTimer.setLength(length);
                megaTimer.setColor(color);
                megaTimer.setStart(0);
                megaTimer.setStop(length);
                megaTimer.setStatus("initialised");
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
            button_edit.setEnabled(true);
        } else {
            button_edit.setEnabled(false);
        }
    }

    private boolean DataIsValid() {
        return !(title.equals("") || length == 0);
    }
}
