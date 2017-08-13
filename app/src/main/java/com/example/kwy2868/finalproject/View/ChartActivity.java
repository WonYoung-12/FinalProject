package com.example.kwy2868.finalproject.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Retrofit.NetworkManager;
import com.example.kwy2868.finalproject.Retrofit.NetworkService;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kwy2868 on 2017-08-11.
 */

public class ChartActivity extends AppCompatActivity implements OnDateSelectedListener, EditText.OnFocusChangeListener{
    @BindView(R.id.materialCalendar)
    MaterialCalendarView materialCalendar;
    @BindView(R.id.treatmentDate)
    EditText inputTreatmentDate;
    @BindView(R.id.reTreatmentDate)
    EditText inputReTreatmentDate;
    @BindView(R.id.inputPetName)
    EditText inputPetName;
    @BindView(R.id.inputChartTitle)
    EditText inputChartTitle;
    @BindView(R.id.inputChartDescription)
    EditText inputChartDescription;
    @BindView(R.id.writeChartButton)
    Button writeChartButton;

    private static final int TREATMENT_FLAG = 0;
    private static final int RETREATMENT_FLAG = 1;

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
        setFocusChangeListener();
        initCalendar();
    }

    public void setFocusChangeListener(){
        inputTreatmentDate.setOnFocusChangeListener(this);
        inputReTreatmentDate.setOnFocusChangeListener(this);
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

    // 이거 두번 누르면 터짐.
    public void constructDateTimeDialog(final int flag){
        dateTimeDialogFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeDialogFragment == null){
            dateTimeDialogFragment
                    = SwitchDateTimeDialogFragment.newInstance(getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel));
        }
        else{
            dateTimeDialogFragment
                    = SwitchDateTimeDialogFragment.newInstance(getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel));
        }

        dateTimeDialogFragment.startAtCalendarView();
        dateTimeDialogFragment.set24HoursMode(false);
        dateTimeDialogFragment.setMinimumDateTime(minDate.getTime());
        dateTimeDialogFragment.setMaximumDateTime(maxDate.getTime());
        dateTimeDialogFragment.setDefaultDateTime(nowDate.getTime());

        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("Error", e.getMessage());
        }
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // 셋 해주자.
            @Override
            public void onPositiveButtonClick(Date date) {
                if(flag == TREATMENT_FLAG){
                    inputTreatmentDate.setText(sdf.format(date));
                    hideKeyBoard(inputTreatmentDate);
                }
                else{
                    inputReTreatmentDate.setText(sdf.format(date));
                    hideKeyBoard(inputReTreatmentDate);
                }
                // 선택한 날짜로 위의 달력이 이동하면서 선택됨.
                materialCalendar.setCurrentDate(CalendarDay.from(date), true);
                materialCalendar.setSelectedDate(date);
            }

            // 아무것도 안해줘도 됨.
            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });
        dateTimeDialogFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
    }

    public void hideKeyBoard(EditText editText){
        editText.clearFocus();
        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    // 작성 버튼 누르면 호출. DB에 써주자.
    @OnClick(R.id.writeChartButton)
    public void writeChart(){
        String petName = inputPetName.getText().toString();
        String treatmentDate = inputTreatmentDate.getText().toString();
        String reTreatmentDate = inputReTreatmentDate.getText().toString();
        String title = inputChartTitle.getText().toString();
        String description = inputChartDescription.getText().toString();

        Chart chart = new Chart(petName, GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag(), treatmentDate, reTreatmentDate, title, description);
        GlobalData.getChartDBHelper().addChart(chart);
        Toast.makeText(this, "정상적으로 차트를 등록하였습니다.", Toast.LENGTH_SHORT).show();


        NetworkService networkService = NetworkManager.getNetworkService();
        Call<BaseResult> call = networkService.writeChart(chart);
        call.enqueue(new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                if(response.isSuccessful()){
                    if(response.body().getResultCode() == 200)
                        Toast.makeText(ChartActivity.this, "차트 작성 성공", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {

            }
        });
        finish();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()){
            case R.id.treatmentDate:
                if(hasFocus) {
                    constructDateTimeDialog(TREATMENT_FLAG);
                }
                break;
            case R.id.reTreatmentDate:
                if(hasFocus) {
                    constructDateTimeDialog(RETREATMENT_FLAG);
                }
                break;
            default:
                break;
        }
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
//        constructDateTimeDialog(date);
    }
}
