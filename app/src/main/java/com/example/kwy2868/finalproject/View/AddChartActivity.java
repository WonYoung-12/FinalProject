package com.example.kwy2868.finalproject.View;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kwy2868.finalproject.Model.BaseResult;
import com.example.kwy2868.finalproject.Model.Chart;
import com.example.kwy2868.finalproject.Model.GlobalData;
import com.example.kwy2868.finalproject.Model.Pet;
import com.example.kwy2868.finalproject.R;
import com.example.kwy2868.finalproject.Network.NetworkManager;
import com.example.kwy2868.finalproject.Network.NetworkService;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

public class AddChartActivity extends AppCompatActivity implements OnDateSelectedListener, EditText.OnFocusChangeListener{
    @BindView(R.id.materialCalendar)
    MaterialCalendarView materialCalendar;
    @BindView(R.id.treatmentDate)
    EditText inputTreatmentDate;
    @BindView(R.id.reTreatmentDate)
    EditText inputReTreatmentDate;
    @BindView(R.id.spinner)
    Spinner spinner;
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

    private List<Pet> petList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addchart);
        unbinder = ButterKnife.bind(this);
        setTitle("진료 내역 차트 작성");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getPetListFromServer();
        setFocusChangeListener();
        initCalendar();
    }

    public void setFocusChangeListener(){
        inputTreatmentDate.setOnFocusChangeListener(this);
        inputReTreatmentDate.setOnFocusChangeListener(this);
    }

    public void initSpinnerItem(){
        ArrayList<String> petNameList = new ArrayList<String>();
        for(int i=0; i<GlobalData.getPetList().size(); i++){
            Pet pet = GlobalData.getPetList().get(i);
            petNameList.add(pet.getName() + "(" + pet.getAge() + ", " + pet.getSpecies() + ")");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, petNameList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(spinner != null)
            spinner.setAdapter(arrayAdapter);
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
                    inputTreatmentDate.clearFocus();
                    inputReTreatmentDate.clearFocus();
                }

                // 아무것도 안해줘도 됨.
                @Override
                public void onNegativeButtonClick(Date date) {
                    inputTreatmentDate.clearFocus();
                    inputReTreatmentDate.clearFocus();
                }
            });
            dateTimeDialogFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        }
    }

    public void hideKeyBoard(EditText editText){
        editText.clearFocus();
        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    // 작성 버튼 누르면 호출. DB에 써주자.
    @OnClick(R.id.writeChartButton)
    public void writeChart(){
        String tempPetName = spinner.getSelectedItem().toString();
        int index = tempPetName.indexOf("(");
        String petName = tempPetName.substring(0, index);
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
                        Toast.makeText(AddChartActivity.this, "차트 작성 성공", Toast.LENGTH_SHORT).show();
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

    public void getPetListFromServer(){
        Log.d("펫 리스트 가져온다", "가져온다");
        final NetworkService networkService = NetworkManager.getNetworkService();
        Call<List<Pet>> call = networkService.getPetList(GlobalData.getUser().getUserId(), GlobalData.getUser().getFlag());
        call.enqueue(new Callback<List<Pet>>() {
            @Override
            public void onResponse(Call<List<Pet>> call, Response<List<Pet>> response) {
                if(response.isSuccessful()){
                    petList = response.body();
                    GlobalData.setPetList(petList);
                    initSpinnerItem();
                    Log.d("펫 리스트 받아옴", "펫 리스트 받아왔다");
                }
            }

            @Override
            public void onFailure(Call<List<Pet>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
