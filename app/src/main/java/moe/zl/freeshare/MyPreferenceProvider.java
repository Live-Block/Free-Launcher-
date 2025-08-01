package moe.zl.freeshare;
import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

public class MyPreferenceProvider extends RemotePreferenceProvider {
    public MyPreferenceProvider() {
        
        super("moe.zl.freeshare.preferences", new String[] {"main_prefs"});
    }
}
