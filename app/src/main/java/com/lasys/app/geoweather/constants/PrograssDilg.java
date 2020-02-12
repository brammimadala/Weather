package com.lasys.app.geoweather.constants;

import android.app.ProgressDialog;
import android.content.Context;

public class PrograssDilg {
    private com.kaopiz.kprogresshud.KProgressHUD hud;
    ProgressDialog progress;

    public PrograssDilg(Context context, String Msg) {

        hud = com.kaopiz.kprogresshud.KProgressHUD.create(context)
                .setStyle(com.kaopiz.kprogresshud.KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(Msg)
                .setCancellable(false);
        hud.show();

    }

    public void PrograssDilgDismiss() {
        //progress.dismiss();
        hud.dismiss();
    }
}
