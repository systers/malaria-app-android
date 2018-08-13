package com.peacecorps.malaria.ui.home_screen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.peacecorps.malaria.R;
import com.peacecorps.malaria.data.AppDataManager;
import com.peacecorps.malaria.utils.InjectionClass;
import com.peacecorps.malaria.utils.ToastLogSnackBarUtil;

/**
 * Created by DELL on 1/16/2016.
 */
public class ReminderToneActivity extends Activity implements View.OnClickListener{

    private EditText path;
    private Uri audioFileUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remainder_tone_dialog);
        Button btnBrowse = findViewById(R.id.browse);
        path= findViewById(R.id.tone_path);
        Button btnOK = findViewById(R.id.dialogButtonOKReminder);
        Button btnCancel = findViewById(R.id.dialogButtonCancelReminder);
        btnBrowse.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.browse:
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Select Via..."), 1);
                break;

            case R.id.dialogButtonOKReminder:

                if(path.toString().isEmpty() || !isAudioFile()){
                    path.setError("Specify valid path");
                    break;
                }
                AppDataManager dataManager = InjectionClass.provideDataManager(this);
                dataManager.setToneUri(audioFileUri.toString());
                ToastLogSnackBarUtil.showToast(this, "Reminder Tone Set");
                dataManager = null;
                this.finish();
                break;

            case R.id.dialogButtonCancelReminder:
                this.finish();
                break;
            default: ToastLogSnackBarUtil.showErrorLog("ReminderToneActivity: Invalid id");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            audioFileUri = data.getData();
            if(audioFileUri!=null) {
                String MP3Path = audioFileUri.getPath();
                path.setText(MP3Path);
            }
        }
    }
    public boolean isAudioFile() {
        if(audioFileUri!=null) {
            String type= getContentResolver().getType(audioFileUri);
            return "audio/mpeg".equals(type);
        }
        return false;
    }
}
