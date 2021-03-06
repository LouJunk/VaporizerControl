package org.ligi.vaporizercontrol.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.ligi.vaporizercontrol.model.Settings;
import org.ligi.vaporizercontrol.model.VaporizerCommunicator;
import org.ligi.vaporizercontrol.util.TemperatureFormatter;
import org.ligi.vaporizercontrol.wiring.App;

public class ChangeDialogs {

    public static void setBooster(Context ctx, final VaporizerCommunicator comm) {
        final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(ctx);
        discreteSeekBar.setMax((2100 - comm.getData().setTemperature) / 10);
        discreteSeekBar.setIndicatorFormatter("%d°C");

        if (comm.getData().boostTemperature != null) {
            discreteSeekBar.setProgress(comm.getData().boostTemperature / 10);
        }

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(final DiscreteSeekBar discreteSeekBar, final int i, final boolean b) {
                comm.setBoosterTemperature(i * 10);
            }
        });

        new AlertDialog.Builder(ctx).setMessage("Set booster temperature").setView(discreteSeekBar).setPositiveButton("OK", null).show();
    }

    public static void showLEDPercentageDialog(Context ctx, final VaporizerCommunicator comm) {
        final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(ctx);
        discreteSeekBar.setMax(100);
        discreteSeekBar.setIndicatorFormatter("%d%%");

        if (comm.getData().ledPercentage != null) {
            discreteSeekBar.setProgress(comm.getData().ledPercentage);
        }

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(final DiscreteSeekBar discreteSeekBar, final int i, final boolean b) {
                comm.setLEDBrightness(i);
            }
        });

        new AlertDialog.Builder(ctx).setMessage("Set LED brightness").setView(discreteSeekBar).setPositiveButton("OK", null).show();
    }
/** RELEVANT? */
/** More useful set temperatures. Temp changes only not descriptions */
    static List<TemperatureSetting> temperatureList = new ArrayList<TemperatureSetting>() {{
        // src http://web.archive.org/web/20100223183517/http://en.wikipedia.org/wiki/Vaporizer
        add(new TemperatureSetting(1750, "<a href='http://en.wikipedia.org/wiki/Syzygium_aromaticum'>Clove</a> Dried flower buds"));
        add(new TemperatureSetting(1800,
                                   "<a href='http://en.wikipedia.org/wiki/Eucalyptus_globulus'>Eucapyptus</a> " +
                                   "or <a href='http://en.wikipedia.org/wiki/Lavandula_angustifolia'>Lavender</a> Leaves"));
        add(new TemperatureSetting(1850, "<a href='http://en.wikipedia.org/wiki/Ginkgo_biloba'>Ginkgo</a> Leaves or seeds"));
        add(new TemperatureSetting(1900, "<a href='http://en.wikipedia.org/wiki/Melissa_officinalis'>Lemon balm</a> Leaves"));
        add(new TemperatureSetting(1950, "<a href='http://en.wikipedia.org/wiki/Humulus_lupulus'>Hops</a> Cones"));
        add(new TemperatureSetting(2000, "<a href='http://en.wikipedia.org/wiki/Aloe_vera'>Aloe_vera</a> Gelatinous fluid from leaves"));
        add(new TemperatureSetting(2100, "<a href='http://en.wikipedia.org/wiki/Chamomilla_recutita'>Chamomile</a> Flowers " +
                                         "or <a href='http://en.wikipedia.org/wiki/Salvia_officinalis'>Sage</a> Leaves " +
                                         "or  <a href='http://en.wikipedia.org/wiki/Thymus_vulgaris'>Thyme</a> Herb"));


    }};

    public static void showTemperatureDialog(final Context ctx, final VaporizerCommunicator comm) {

        final Settings settings = ((App) ctx.getApplicationContext()).getSettings();

        final ScrollView scrollView = new ScrollView(ctx);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        final LinearLayout lin = new LinearLayout(ctx);
        lin.setOrientation(LinearLayout.VERTICAL);

        final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(ctx);
        discreteSeekBar.setMin(40);
        discreteSeekBar.setMax(210);
        discreteSeekBar.setIndicatorFormatter("%d°");

        if (comm.getData().setTemperature != null) {
            discreteSeekBar.setProgress(comm.getData().setTemperature / 10);
        }

        lin.addView(discreteSeekBar);


        for (final TemperatureSetting temperatureSetting : temperatureList) {
            LinearLayout grp = new RadioGroup(ctx);
            grp.setOrientation(LinearLayout.HORIZONTAL);
            grp.setPadding(16, 16, 16, 16);
            TextView txt = new TextView(ctx);
            txt.setText(Html.fromHtml(temperatureSetting.htmlDescription));
            txt.setMovementMethod(new LinkMovementMethod());

            final Button button = new Button(ctx);
            button.setText(TemperatureFormatter.INSTANCE.getFormattedTemp(settings, temperatureSetting.temp, true));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    discreteSeekBar.setProgress(temperatureSetting.temp / 10);
                }
            });
            grp.addView(button);
            grp.addView(txt);
            lin.addView(grp);
        }
        scrollView.addView(lin);

        new AlertDialog.Builder(ctx).setView(scrollView).setMessage("Set Temperature").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                comm.setTemperatureSetPoint(discreteSeekBar.getProgress() * 10);
            }
        }).show();
    }

    private static class TemperatureSetting {
        public final int temp;
        public final String htmlDescription;

        private TemperatureSetting(final int temp, final String htmlDescription) {
            this.temp = temp;
            this.htmlDescription = htmlDescription;
        }
    }
}
