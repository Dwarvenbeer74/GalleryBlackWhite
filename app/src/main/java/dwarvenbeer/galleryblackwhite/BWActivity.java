package dwarvenbeer.galleryblackwhite;

import android.support.v4.app.Fragment;

public class BWActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return BWFragment.newInstance();
    }
}
