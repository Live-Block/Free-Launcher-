package moe.zl.freeshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.crossbowffs.remotepreferences.RemotePreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.internal.EdgeToEdgeUtils;
import com.google.android.material.slider.Slider;
import de.robv.android.xposed.XSharedPreferences;
import moe.zl.freelauncher.R;

public class Pref extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // EdgeToEdgeUtils.applyEdgeToEdge(this.getWindow(), true);
    setContentView(R.layout.layout_pref);

    SharedPreferences sp =
        new RemotePreferences(this, "moe.zl.freeshare.preferences", "main_prefs");
    Slider s = findViewById(R.id.freeValueSlider);
    TextView t1 = findViewById(R.id.t1);
    LinearLayout l = findViewById(R.id.l);
    MaterialButtonToggleGroup mbg = findViewById(R.id.modeButtonGroup);

    s.addOnChangeListener(
        (mSlider, value, fromUser) -> {
          SharedPreferences.Editor ed = sp.edit();
          ed.putFloat("freeValue", value);
          ed.apply();
        });
    s.setValue(sp.getFloat("freeValue", 2f));

    mbg.addOnButtonCheckedListener(
        (mGroup, id, isChecked) -> {
          if (isChecked) {
            MaterialButton mb = mGroup.findViewById(id);
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("freeMode", Integer.valueOf(mb.getTag().toString()));
            ed.apply();
          }
        });
  }
}
