package com.Garuda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private String packageName,contryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppUpdateChecker appUpdateChecker=new AppUpdateChecker(MainActivity.this);
        appUpdateChecker.checkForUpdate(false);
        AudienceNetworkAds.initialize(this);
        EditText Smobile = findViewById(R.id.mob);
        EditText Scode = findViewById(R.id.cc);
        EditText Stext = findViewById(R.id.text);
        CheckBox clip = findViewById(R.id.clip);
        CheckBox country = findViewById(R.id.code);
        RadioGroup group = findViewById(R.id.group);
        CountryCodePicker codePicker = findViewById(R.id.ccp);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loader);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        packageName = "com.whatsapp";
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.wa) {
                    packageName = "com.whatsapp";
                } else if(checkedId == R.id.wab) {
                    packageName = "com.whatsapp.w4b";
                } else if(checkedId == R.id.gbwa) {
                    packageName = "com.gbwhatsapp";
                }
            }
        });
        clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clip.isChecked()) {
                    codePicker.setVisibility(View.GONE);
                    Scode.setVisibility(View.GONE);
                    country.setChecked(false);
                } else {
                    codePicker.setVisibility(View.VISIBLE);
                    Scode.setVisibility(View.GONE);
                }
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(country.isChecked()) {
                    codePicker.setVisibility(View.GONE);
                    Scode.setVisibility(View.VISIBLE);
                    clip.setChecked(false);
                } else {
                    codePicker.setVisibility(View.VISIBLE);
                    Scode.setVisibility(View.GONE);
                }
            }
        });
        if(isConnect(this)) {
            AdView adView = new AdView(this, getResources().getString(R.string.banner), AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
            adContainer.addView(adView); adView.loadAd(); loadInter();
        } else {
            makeText(MainActivity.this,"No internet found",LENGTH_SHORT).show();
        }
        contryCode = codePicker.getDefaultCountryCode();
        codePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                contryCode = codePicker.getSelectedCountryCode();
            }
        });
        ((Button)findViewById(R.id.send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = Stext.getText().toString();
                String num = Smobile.getText().toString();
                String cod = Scode.getText().toString();
                if(num.equals("")) {
                    makeText(MainActivity.this,"Phone Number Blank",LENGTH_SHORT).show();
                } else if(msg.equals("")) {
                    makeText(MainActivity.this,"Message is Blank",LENGTH_SHORT).show();
                } else if(num.length()<10) {
                    makeText(MainActivity.this,"Invalid Phone Number",LENGTH_SHORT).show();
                } else {
                    if(clip.isChecked()) {
                        if (installed(packageName)) {
                            if(interstitialAd.isAdLoaded()) {
                                interstitialAd.show();
                            } else {
                                Uri uri = Uri.parse("http://api.whatsapp.com/send?phone=" + num + "&text=" + msg);
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                                sendIntent.setPackage(packageName);
                                startActivity(sendIntent);
                            }
                        } else {
                            makeText(MainActivity.this, "App not Installed", LENGTH_SHORT).show();
                            Uri uri = Uri.parse("market://details?id=" + packageName);
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(goToMarket);
                        }
                    } else if(country.isChecked()) {
                        if(cod.equals("")) {
                            makeText(MainActivity.this,"Invalid Country Code",LENGTH_SHORT).show();
                        } else {
                            String numb = cod + num;
                            if (installed(packageName)) {
                                if(interstitialAd.isAdLoaded()) {
                                    interstitialAd.show();
                                } else {
                                    Uri uri = Uri.parse("http://api.whatsapp.com/send?phone=" + numb + "&text=" + msg);
                                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                                    sendIntent.setPackage(packageName);
                                    startActivity(sendIntent);
                                }
                            } else {
                                makeText(MainActivity.this, "App not Installed", LENGTH_SHORT).show();
                                Uri uri = Uri.parse("market://details?id=" + packageName);
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(goToMarket);
                            }
                        }
                    } else {
                        String numb = contryCode + num;
                        if (installed(packageName)) {
                            if(interstitialAd.isAdLoaded()) {
                                interstitialAd.show();
                            } else {
                                Uri uri = Uri.parse("http://api.whatsapp.com/send?phone=" + numb + "&text=" + msg);
                                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                                sendIntent.setPackage(packageName);
                                startActivity(sendIntent);
                            }
                        } else {
                            makeText(MainActivity.this, "App not Installed", LENGTH_SHORT).show();
                            Uri uri = Uri.parse("market://details?id=" + packageName);
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(goToMarket);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.help) {
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://google.com/")));
            return true;
        } else if (menuItem.getItemId() == R.id.faq) {
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://google.com/")));
            return true;
        } else if (menuItem.getItemId() == R.id.more) {
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://play.google.com/store/apps/dev?id=7861050630849506029")));
            return true;
        } else if (menuItem.getItemId() == R.id.policy) {
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://google.com/")));
            return true;
        } else if (menuItem.getItemId() == R.id.about) {
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://google.com/")));
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private boolean isConnect(Context ctx) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean installed(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void loadInter() {
        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.interstitial));
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                //dialog.dismiss();
                //loadInter();
            }
            @Override
            public void onInterstitialDismissed(Ad ad) {
                //dialog.dismiss();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                //dialog.dismiss();
                loadInter();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                //dialog.dismiss();
                //interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                //dialog.dismiss();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                //dialog.dismiss();
            }
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

}