package com.peacecorps.malaria.ui.user_profile.second_analysis;

import android.content.Context;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.data.db.entities.AlarmTime;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.user_profile.second_analysis.SecondAnalysisContract.SecondAnalysisMvpPresenter;
import com.peacecorps.malaria.ui.user_profile.second_analysis.SecondAnalysisContract.SecondAnalysisMvpView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Anamika Tripathi on 7/8/18.
 */
public class SecondAnalysisPresenter<V extends SecondAnalysisMvpView> extends BasePresenter<V> implements SecondAnalysisMvpPresenter<V> {
    private int mDate;
    private int mYear;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    private String[] monthList;

    private List<AnalysisModel> dataList;

    SecondAnalysisPresenter(AppDataManager manager, Context context) {
        super(manager, context);
        dataList = new ArrayList<>();
        monthList = getContext().getResources().getStringArray(R.array.array_month);
    }

    @Override
    public void getDataForProgressBar() {

        getDataManager().getAlarmData(new DbHelper.LoadAlarmDataCallback() {
            @Override
            public void onDataLoaded(AlarmTime time) {
                final int setUpMonth = time.getMonth();
                final int setUpYear = time.getYear();
                getAnalysisData(setUpMonth, setUpYear);
            }
        });
    }

    private void getAnalysisData(final int setUpMonth, final int setUpYear) {
        final int date = Calendar.getInstance().get(Calendar.MONTH);
        final String choice;
        final boolean isWeekly = getDataManager().isDosesWeekly();
        //checking choice of pill whether weekly or daily
        if (isWeekly) {
            choice = "weekly";
        } else {
            choice = "daily";
        }

        fourthMonthData(setUpMonth, setUpYear, date, choice, isWeekly);
        thirdMonthData(setUpMonth, setUpYear, date, choice, isWeekly);
        secondMonthData(setUpMonth, setUpYear, date, choice, isWeekly);
        firstMonthData(setUpMonth, setUpYear, date, choice, isWeekly);

    }

    private void fourthMonthData(final int setUpMonth, final int setUpYear, final int date, String choice, final boolean isWeekly) {
        final String fourthMonth = getMonth(date);
        getDataManager().getCountForProgressBar(mDate, mYear, "yes", choice, new DbHelper.LoadIntegerCallback() {
            @Override
            public void onDataLoaded(int count) {
                float progressPercentage;
                if (!isWeekly) {
                    progressPercentage = (float) count / getNumberOfDaysInMonth(mDate) * 100;
                } else {
                    progressPercentage = count * 25;
                }
                String fourthPer;
                if ((date - 1) >= setUpMonth || mYear != setUpYear || progressPercentage != 0) {
                    fourthPer = "" + (int) progressPercentage + "%";
                } else {
                    fourthPer = "N.A";
                }
                dataList.add(new AnalysisModel(fourthMonth, fourthPer));
            }
        });
    }

    private void thirdMonthData(final int setUpMonth, final int setUpYear, final int date, final String choice, final boolean isWeekly) {
        final String thirdMonth = getMonth(date - 1);
        getDataManager().getCountForProgressBar(mDate, mYear, "yes", choice, new DbHelper.LoadIntegerCallback() {
            @Override
            public void onDataLoaded(int count) {
                float progressPercentage;
                if (!isWeekly) {
                    progressPercentage = (float) count / getNumberOfDaysInMonth(mDate) * 100;
                } else {
                    progressPercentage = count * 25;
                }
                String thirdPer;
                if ((date - 1) >= setUpMonth || mYear != setUpYear || progressPercentage != 0) {
                    thirdPer = "" + (int) progressPercentage;
                } else {
                    thirdPer = "N.A";
                }
                dataList.add(new AnalysisModel(thirdMonth, thirdPer));
            }
        });
    }

    private void secondMonthData(final int setUpMonth, final int setUpYear, final int date, final String choice, final boolean isWeekly) {
        final String secondMonth = getMonth(date - 2);
        getDataManager().getCountForProgressBar(mDate, mYear, "yes", choice, new DbHelper.LoadIntegerCallback() {
            @Override
            public void onDataLoaded(int count) {
                float progressPercentage;
                if (!isWeekly) {
                    progressPercentage = (float) count / getNumberOfDaysInMonth(mDate) * 100;
                } else {
                    progressPercentage = count * 25;
                }
                String secondPer;
                if ((date - 2) >= setUpMonth || mYear != setUpYear || progressPercentage != 0) {
                    secondPer = "" + (int) progressPercentage;
                } else {
                    secondPer = "N.A";
                }
                dataList.add(new AnalysisModel(secondMonth, secondPer));
            }
        });
    }

    private void firstMonthData(final int setUpMonth, final int setUpYear, final int date, final String choice, final boolean isWeekly) {
        final String firstMonth = getMonth(date - 3);

        getDataManager().getCountForProgressBar(mDate, mYear, "yes", choice,
                new DbHelper.LoadIntegerCallback() {
                    @Override
                    public void onDataLoaded(int count) {
                        float progressPercentage;
                        if (!isWeekly) {
                            progressPercentage = (float) count / getNumberOfDaysInMonth(mDate) * 100;
                        } else {
                            progressPercentage = count * 25;
                        }
                        String firstPer;
                        if ((date - 3) >= setUpMonth || mYear != setUpYear || progressPercentage != 0) {
                            firstPer = "" + (int) progressPercentage;
                        } else {
                            firstPer = "N.A";
                        }
                        dataList.add(new AnalysisModel(firstMonth, firstPer));
                        getView().startRecyclerView();

                    }
                });
    }

    //finding month from its integer
    private String getMonth(int date) {
        int d;
        if (date == -1) {
            d = 11;
            mYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -2) {
            d = 10;
            mYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else if (date == -3) {
            d = 9;
            mYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
        } else {
            d = date;
            mYear = Calendar.getInstance().get(Calendar.YEAR);
            mDate = date;
        }
        return monthList[d];
    }

    /*Finding No. of Days in Month*/
    private int getNumberOfDaysInMonth(int month) {
        return daysOfMonth[month];
    }

    public List<AnalysisModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<AnalysisModel> dataList) {
        this.dataList = dataList;
    }
}
