package com.example.kwy2868.finalproject.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kwy2868.finalproject.R;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by kwy2868 on 2017-08-11.
 */

public class ChartActivity extends AppCompatActivity implements OnDateSelectedListener{
    @BindView(R.id.materialCalendar)
    MaterialCalendarView materialCalendar;
    private Unbinder unbinder;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private SwitchDateTimeDialogFragment dateTimeDialogFragment;

    private Calendar nowDate;
    private Calendar minDate;
    private Calendar maxDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        unbinder = ButterKnife.bind(this);
        setTitle("진료 내역 차트 작성");
        initCalendar();
    }

    public void initCalendar(){
        // 현재 날짜 받아오자.
        nowDate = Calendar.getInstance();
//        materialCalendar.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        materialCalendar.setSelectedDate(nowDate.getTime());
        // 달력 최소 날짜랑 마지막 날짜 정하자.
        minDate = Calendar.getInstance();
        minDate.set(minDate.get(Calendar.YEAR), Calendar.JANUARY, 1);
        maxDate = Calendar.getInstance();
        // 최대 년수는 5년 후까지.
        maxDate.set(maxDate.get(Calendar.YEAR) + 5, Calendar.DECEMBER, 31);

        materialCalendar.setOnDateChangedListener(this);

        materialCalendar.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    // 날짜 선택하면.
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Log.d("달",date+" ");
        constructDateTimeDialog(date);
    }

    public void constructDateTimeDialog(CalendarDay date){
        dateTimeDialogFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeDialogFragment == null){
            dateTimeDialogFragment
                    = SwitchDateTimeDialogFragment.newInstance(getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel));
        }

        dateTimeDialogFragment.startAtTimeView();
        dateTimeDialogFragment.set24HoursMode(false);
        dateTimeDialogFragment.setMinimumDateTime(minDate.getTime());
        dateTimeDialogFragment.setMaximumDateTime(maxDate.getTime());
        dateTimeDialogFragment.setDefaultDateTime(date.getDate());

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Error", e.getMessage());
        }
        dateTimeDialogFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        // 현재 선택한 날짜 풀어주자.
        materialCalendar.clearSelection();
    }
}
