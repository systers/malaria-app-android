package com.peacecorps.malaria.ui.user_profile.first_ananlysis;

import android.content.Context;

import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.data.db.DbHelper;
import com.peacecorps.malaria.ui.base.BasePresenter;
import com.peacecorps.malaria.ui.user_profile.first_ananlysis.FirstAnalysisContract.FirstAnalysisMvpPresenter;
import com.peacecorps.malaria.ui.user_profile.first_ananlysis.FirstAnalysisContract.FirstAnalysisMvpView;
import com.peacecorps.malaria.utils.CalendarFunction;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anamika Tripathi on 7/8/18.
 */
public class FirstAnalysisPresenter<V extends FirstAnalysisMvpView> extends BasePresenter<V> implements FirstAnalysisMvpPresenter<V> {

    FirstAnalysisPresenter(AppDataManager manager, Context context) {
        super(manager, context);
    }

    /**
     * gets data from db, calls view's setLastTakenTime method to set on text
     */
    @Override
    public void updateMediLastTime() {
        getDataManager().getLastTaken(new DbHelper.LoadStringCallback() {
            @Override
            public void onDataLoaded(String data) {
                getView().setLastTakenTime(data);
            }
        });
    }

    /**
     * Updating Doses in a Row for Weekly and Daily Pill separately
     */
    @Override
    public void updateDoses() {
        boolean isWeekly = getDataManager().isDosesWeekly();
        if (isWeekly) {
            getDataManager().getDosesInaRowWeekly(new DbHelper.LoadIntegerCallback() {
                @Override
                public void onDataLoaded(int value) {
                    getDataManager().setDosesWeekly(value);
                    getView().setDosesText(value);
                }
            });
        } else {
            getDataManager().getDosesInaRowDaily(new DbHelper.LoadIntegerCallback() {
                @Override
                public void onDataLoaded(int value) {
                    getDataManager().setDosesDaily(value);
                    getView().setDosesText(value);
                }
            });
        }
    }

    // Calculating Adherence & calls setAdherenceText
    @Override
    public void getAdherenceData() {

        //finding the interval of time between today and the 'time'
        long today = new Date().getTime();
        final Date date = Calendar.getInstance().getTime();
        date.setTime(today);

        getDataManager().getFirstTimeByTimeStamp(new DbHelper.LoadLongCallback() {
            @Override
            public void onDataLoaded(Long takenDate) {
                // calculate interval first on the basis of weekly or daily
                final long interval;
                if (takenDate != 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(takenDate);

                    cal.add(Calendar.MONTH, 1);
                    Date start = cal.getTime();
                    int weekDay = cal.get(Calendar.DAY_OF_WEEK);

                    //calculating no. of weekdays for weekly drug
                    boolean isWeekly = getDataManager().isDosesWeekly();
                    if (isWeekly) {
                        interval = CalendarFunction.getIntervalWeekly(start, date, weekDay);
                    } else {
                        //for daily drug only the no. of days
                        interval = CalendarFunction.getIntervalDaily(start, date);
                    }
                    getDataManager().setFirstRunTime(takenDate);

                } else {
                    interval = 1;
                }
                getDataManager().getMedicineCountTaken(new DbHelper.LoadIntegerCallback() {
                    @Override
                    public void onDataLoaded(int count) {
                        double adherence;
                        if (interval != 1) {
                            adherence = ((double) count / (double) interval) * 100;
                        } else {
                            adherence = 100;
                        }
                        String adherenceText = String.valueOf((new DecimalFormat("#.##").format(adherence)));
                        getView().setAdherenceRate(adherenceText);
                    }
                });
            }
        });
    }
}
