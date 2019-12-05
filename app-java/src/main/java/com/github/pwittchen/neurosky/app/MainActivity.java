package com.github.pwittchen.neurosky.app;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.pwittchen.neurosky.library.NeuroSky;
import com.github.pwittchen.neurosky.library.exception.BluetoothNotEnabledException;
import com.github.pwittchen.neurosky.library.listener.ExtendedDeviceMessageListener;
import com.github.pwittchen.neurosky.library.message.enums.BrainWave;
import com.github.pwittchen.neurosky.library.message.enums.Signal;
import com.github.pwittchen.neurosky.library.message.enums.State;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

  private final static String LOG_TAG = "NeuroSky";
  private NeuroSky neuroSky;

  @BindView(R.id.tv_state) TextView tvState;
  @BindView(R.id.tv_poorsignal) TextView tvPoorsignal;
  @BindView(R.id.tv_attention) TextView tvAttention;
  @BindView(R.id.tv_meditation) TextView tvMeditation;
  @BindView(R.id.tv_blink) TextView tvBlink;
  @BindView(R.id.tv_alphalow) TextView tvAlphalow;
  @BindView(R.id.tv_alphahigh) TextView tvAlphahigh;
  @BindView(R.id.tv_betalow) TextView tvBetalow;
  @BindView(R.id.tv_betahigh) TextView tvBetahigh;
  @BindView(R.id.tv_gammalow) TextView tvGammalow;
  @BindView(R.id.tv_gammamid) TextView tvGammamid;
  @BindView(R.id.tv_delta) TextView tvDelta;
  @BindView(R.id.tv_theta) TextView tvTheta;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    neuroSky = createNeuroSky();
  }

  @Override protected void onResume() {
    super.onResume();
    if (neuroSky != null && neuroSky.isConnected()) {
      neuroSky.start();
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (neuroSky != null && neuroSky.isConnected()) {
      neuroSky.stop();
    }
  }

  @NonNull private NeuroSky createNeuroSky() {
    return new NeuroSky(new ExtendedDeviceMessageListener() {
      @Override public void onStateChange(State state) {
        handleStateChange(state);
      }

      @Override public void onSignalChange(Signal signal) {
        handleSignalChange(signal);
      }

      @Override public void onBrainWavesChange(Set<BrainWave> brainWaves) {
        handleBrainWavesChange(brainWaves);
      }
    });
  }

  private void handleStateChange(final State state) {
    if (neuroSky != null && state.equals(State.CONNECTED)) {
      neuroSky.start();
    }

    tvState.setText(state.toString());
    Log.d(LOG_TAG, state.toString());
  }
    StringBuilder data = new StringBuilder();

  private void handleSignalChange(final Signal signal) {

    switch (signal) {
      case POOR_SIGNAL:
        tvPoorsignal.setText(getFormattedMessage("Poorsignal: %d", signal));
        int psignal=signal.getValue();
       data.append("\n"+String.valueOf(signal.getValue()));
          data.append("Poor");
        if(psignal>0)
        {
          final MediaPlayer mp = MediaPlayer.create(this,R.raw.beep);
          mp.start();
        }
        break;
        case ATTENTION:
        tvAttention.setText(getFormattedMessage("attention: %d", signal));

        break;
      case MEDITATION:
        tvMeditation.setText(getFormattedMessage("meditation: %d", signal));
        break;
      case BLINK:
        tvBlink.setText(getFormattedMessage("blink: %d", signal));
        break;
    }

    //   Log.d(LOG_TAG, String.format("%s: %d", signal.toString(), signal.getValue()));
  }

  private String getFormattedMessage(String messageFormat, Signal signal) {
    return String.format(Locale.getDefault(), messageFormat, signal.getValue());
  }


  private void handleBrainWavesChange(final Set<BrainWave> brainWaves) {

      for (BrainWave brainWave : brainWaves) {
      switch (brainWave) {
          case DELTA:
          // int alph = brainWave.getValue()t;
          tvDelta.setText(String.format("Delta:%d", brainWave.getValue()));
        data.append(","+String.valueOf(brainWave.getValue()));

          break;
          case MID_GAMMA:
          tvGammamid.setText(String.format("Mid Gamma:%d", brainWave.getValue()));
          data.append(","+String.valueOf(brainWave.getValue()));

            break;
          case THETA:
              tvTheta.setText(String.format("Theta:%d", brainWave.getValue()));
            data.append(","+String.valueOf(brainWave.getValue()));

              break;
          case HIGH_BETA:
              tvBetahigh.setText(String.format("High Beta:%d", brainWave.getValue()));
              data.append(","+String.valueOf(brainWave.getValue()));

              break;
          case LOW_GAMMA:
              tvGammalow.setText(String.format("Low Gamma:%d", brainWave.getValue()));
              data.append(","+String.valueOf(brainWave.getValue()));

              break;
          case HIGH_ALPHA:
              tvAlphahigh.setText(String.format("High Alpha:%d", brainWave.getValue()));
              data.append(","+String.valueOf(brainWave.getValue()));

              break;
          case LOW_ALPHA:
              tvAlphalow.setText(String.format("Low Alpha:%d", brainWave.getValue()));
              data.append(","+String.valueOf(brainWave.getValue()));

              break;
          case LOW_BETA:
              tvBetalow.setText(String.format("Low Beta:%d", brainWave.getValue()));
              data.append(","+String.valueOf(brainWave.getValue()));

              break;
      }
//     Log.d(LOG_TAG, String.format("%s: %d", brainWave.toString(), brainWave.getValue()));
    }

  }

  @OnClick(R.id.btn_connect) void connect() {
      data.append("PoorSignal,Delta,MidGamma,Theta,HighBeta,LowGamma,HighAlpha,LowAlpha,LowBeta");
      //data.append("\n"+"PoorSignal,LowAlpha,HighAlpha,LowBeta,HighBeta,LowGamma,MidGamma,Delta,Theta");
      try {
      neuroSky.connect();
    } catch (BluetoothNotEnabledException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
      Log.d(LOG_TAG, e.getMessage());
    }
  }

  @OnClick(R.id.btn_disconnect) void disconnect() {
    neuroSky.disconnect();
    try{
      //saving
FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
out.write((data.toString()).getBytes());
out.close();
//export
      Context context = getApplicationContext();
      File filelocation = new File(getFilesDir(),"data.csv");
      Uri path = FileProvider.getUriForFile(context, "com.github.pwittchen.neurosky.app.fileprovider", filelocation);
      Intent fileIntent = new Intent(Intent.ACTION_SEND);
      fileIntent.setType("text/csv");
      fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
      fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      fileIntent.putExtra(Intent.EXTRA_STREAM, path);
      startActivity(Intent.createChooser(fileIntent, "Send mail"));
    }
    catch (Exception e) {
    e.printStackTrace();
    }
    }


  @OnClick(R.id.btn_start_monitoring) void startMonitoring() {

      neuroSky.start();

  }

  @OnClick(R.id.btn_stop_monitoring) void stopMonitoring() {
    neuroSky.stop();
  }
}
