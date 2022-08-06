package hb.xvideoplayer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import java.util.List;

import mxvideoplayer.app.com.xvideoplayer.R;

public class NativeAdCustomPlayer {

    private RelativeLayout mAdParentView;
    private NativeAppInstallAdView mNativeAppInstallAdView;
    private ImageView mAdImage;
    private ImageView mAdIcon;
    private TextView mAdHeadline;
    private TextView mAdBody;
    private Button mAdButton;

    public NativeAdCustomPlayer(final Context context, final ViewGroup viewGroup) {
        viewGroup.setVisibility(View.GONE);
        View itemView = LayoutInflater.from(context).inflate(R.layout.native_ad, null);
        mAdParentView = itemView.findViewById(R.id.adCardView);
        mNativeAppInstallAdView = itemView.findViewById(R.id.nativeAppInstallAdView);
        mAdImage = itemView.findViewById(R.id.appinstall_image);
        mAdIcon = itemView.findViewById(R.id.appinstall_app_icon);
        mAdHeadline = itemView.findViewById(R.id.appinstall_headline);
        mAdBody = itemView.findViewById(R.id.appinstall_body);
        mAdButton = itemView.findViewById(R.id.appinstall_call_to_action);

        AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.nativeId));
        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                mNativeAppInstallAdView.setImageView(mAdImage);
                mNativeAppInstallAdView.setIconView(mAdIcon);
                mNativeAppInstallAdView.setHeadlineView(mAdHeadline);
                mNativeAppInstallAdView.setBodyView(mAdBody);
                mNativeAppInstallAdView.setCallToActionView(mAdButton);

                ((TextView) mNativeAppInstallAdView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
                ((TextView) mNativeAppInstallAdView.getBodyView()).setText(nativeAppInstallAd.getBody());
                ((Button) mNativeAppInstallAdView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
                if (nativeAppInstallAd.getIcon().getDrawable() != null) {
                    ((ImageView) mNativeAppInstallAdView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());
                }
                List<com.google.android.gms.ads.formats.NativeAd.Image> images = nativeAppInstallAd.getImages();

                if (images.size() > 0) {
                    if (images.get(0).getDrawable() != null)
                        ((ImageView) mNativeAppInstallAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
                }

                mNativeAppInstallAdView.setNativeAd(nativeAppInstallAd);

                mAdParentView.removeAllViews();
                mAdParentView.addView(mNativeAppInstallAdView);
                viewGroup.removeAllViews();
                viewGroup.addView(mAdParentView);
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                mAdParentView.setVisibility(View.VISIBLE);
                viewGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("", "onAdFailedToLoad: " + errorCode);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }
}
