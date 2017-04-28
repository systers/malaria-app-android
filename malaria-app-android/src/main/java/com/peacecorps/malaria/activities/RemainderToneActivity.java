package com.peacecorps.malaria.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peacecorps.malaria.R;
/**
 * Created by DELL on 1/16/2016.
 */
public class RemainderToneActivity extends Activity implements View.OnClickListener{

    Button btnBrowse;
    EditText path;
    Button btnOK ;
    Button btnCancel;
    Ringtone ringtone;
    String MP3Path;
    Uri audioFileUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remainder_tone_dialog);
        btnBrowse=(Button)findViewById(R.id.browse);
        path=(EditText)findViewById(R.id.tone_path);
        btnOK=(Button)findViewById(R.id.dialogButtonOKReminder);
        btnCancel= (Button)findViewById(R.id.dialogButtonCancelReminder);
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
                SharedPreferences.Editor editor = getSharedPreferences("ringtone", MODE_PRIVATE).edit();
                editor.putString("toneUri", audioFileUri.toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "Reminder Tone Set", Toast.LENGTH_SHORT).show();
                this.finish();
                break;

            case R.id.dialogButtonCancelReminder:
                this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                audioFileUri = data.getData();
                MP3Path = audioFileUri.getPath();
                path.setText(MP3Path);
            }
        }
    }
    public boolean isAudioFile() {
        if(audioFileUri!=null) {
            String type= getContentResolver().getType(audioFileUri);
            boolean isAudio= "audio/mpeg".equals(type);
            return isAudio;
        }
        return false;
    }
}
