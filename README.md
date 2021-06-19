# Introduction
AdManager is an Admob and Facebook and custom ads mediation library. AdManager supports Interstitial ad, native ad, Banner ad, icon ad, and exit dialog ad.AdManager support custom ad using firebase remote config.

AdManager is a fast, smooth, reliable, and easy-to-use ads mediation library.
# Download
you can download the sample app and source code from [release page.](https://github.com/CUBIIT/Ads-Manager/releases/tag/v1.0)
# Get Started
###  Step1: Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
   repositories {
      maven { url 'https://jitpack.io' }
  }
}
```
### step2: Add the dependency
Add dependency in your build.gradle(App level) file.

```gradle
dependencies {
  implementation 'com.github.CUBIIT:Ads-Manager:v1.0'
}
```
### Step3: Also add other dependencies
```gradle
dependencies {
  // Firebase
    implementation platform('com.google.firebase:firebase-bom:26.2.0')
    implementation 'com.google.firebase:firebase-messaging'
    implementation 'com.google.firebase:firebase-analytics'
    implementation 'com.google.firebase:firebase-config'
  //facebook audience 
    implementation 'com.facebook.android:audience-network-sdk:6.2.0'
  //Glide support
    implementation ('com.github.bumptech.glide:glide:4.11.0') {
        exclude(group: "com.android.support")
    }

}
```
# Implementation
### Initialization
add these lines in your Application **onCreate** methode.

**Important to fetch the values from remote config file.Without this AdManager does not work properly**
```java
public class AdManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Important 
       AdHelperApplication.getValuesFromConfig(FirebaseRemoteConfig.getInstance(),AdManager.this);
    }
}
```
### Initialize the Interstitial ads.
Add the following code in your launcher activity( mostly splash screen) to initialize the interstitial ad for pre and fast loading.

```java

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        MediationAdInterstitial.initInterstitialAd(false, this, getString(R.string.fb_interstitial_id),
                getString(R.string.admob_interstitial_id),
                KEY_PRIORITY_INTERSTITIAL_AD, null);

}

```
### Interstitial Ad
To load and show interstitial ads.
```java
  MediationAdInterstitial.showInterstitialAd(false, this, getString(R.string.fb_interstitial_id),
                    getString(R.string.admob_interstitial_id),
                    KEY_PRIORITY_INTERSTITIAL_AD,
                    new OnInterstitialAdListener() {
                        @Override
                        public void onDismissed(int adType) {

                        }

                        @Override
                        public void onError(String errorMessage) {


                        }

                        @Override
                        public void onLoaded(int adType){

                        }

                        @Override
                        public void onBeforeAdShow() {
                        }

                        @Override
                        public void onAdClicked(int adType) {

                        }

                        @Override
                        public void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD) {
                        }
                    });
       
```

### Native ad
To show native add.


```java
  MediationNativeAd nativeAd = new MediationNativeAd(ad_container, this, getString(R.string.app_name),
                getString(R.string.fb_native_id),
                getString(R.string.admob_native_id), new MediationAdHelper.ImageProvider() {
            @Override
            public void onProvideImage(ImageView imageView, String imageUrl) {
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .into(imageView);
            }
        });
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                
            }

            @Override
            public void onLoaded(int adType) {
                
            }

            @Override
            public void onAdClicked(int adType) {

            }
        });

```
### Banner ad
To show banner ad.
```java

   MediationAdBanner.showBanner(false, AdsActivity.this, ad_container,
            getString(R.string.fb_banner_id),
            getString(R.string.admob_banner_id),
            KEY_PRIORITY_BANNER_AD,
            new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {
                        hideDialog();
                    }

                    @Override
                    public void onLoaded(int adType) {
                        hideDialog();
                    }

                    @Override
                    public void onAdClicked(int adType) {
                    }

                    @Override
                    public void onFacebookAdCreated(com.facebook.ads.AdView facebookBanner) {
                    }
                });

```
### NativeBanner ad
NativeBanner ad looks like a banner but it customizes the native ad layout. Mostly used in recycler view.

```java

   MediationNativeBanner nativeAd = new MediationNativeBanner(ad_container, AdsActivity.this, getString(R.string.app_name), getString(R.string.nativeBanner),
                getString(R.string.admob_native_id), new MediationAdHelper.ImageProvider() {
            @Override
            public void onProvideImage(ImageView imageView, String imageUrl) {
                Glide.with(AdsActivity.this)
                        .load(imageUrl)
                        .into(imageView);
            }
        });
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD, new OnNativeBannerListener() {
            @Override
            public void onError(String errorMessage) {
                hideDialog();

            }

            @Override
            public void onLoaded(int adType) {
                hideDialog();

            }

            @Override
            public void onAdClicked(int adType) {

            }
        });

```
### Backpress Dialog:
Backpress dialog ad used when user exit from the app. Just call below code **onBackPressed()**.

```java
@Override
    public void onBackPressed() {
        super.onBackPressed();
        MediationBackPressDialog.startDialog(MainActivity.this, getResources().getString(R.string.app_name)
                , getString(R.string.fb_native_id)
                , getString(R.string.admob_native_id)
                , KEY_PRIORITY_NATIVE_AD
                , false
                , new OnBackPressListener() {
                    @Override
                    public void onReviewClick() {

                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }

                    @Override
                    public void onLoaded(int adType) {

                    }

                    @Override
                    public void onAdClicked(int adType) {

                    }
                });
    }

```
# Method Parameters
* **boolean isAppPurchase:** 

> pass true or false value, to check if a user purchases your app or not. Ads are not showing on true value.
* **Context:**

> pass current activity's/fragment  context.
* **Facebook Ad Ids:** 
> put facebook's native, banner, interstitial ads Ids.
* **AdMob Ad ids:** 
> pass Admob native, banner, interstitial ads ids.
*  **Ads Priority:**
>  pass the integer priority list of ad networks like Facebook, AdMob, and custom(by default Cubi-it). Currently, support three priorities. You can reorder the priorities.
Example:
```java
   public static Integer[] KEY_PRIORITY_BANNER_AD = new Integer[]{

            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_NATIVE_AD = new Integer[]{
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_CUBI_IT};

```
* **Listener:** 
> Pass the listener object according to ad type.
```java
//for Interstitial
new OnInterstitialAdListener() {
                        @Override
                        public void onDismissed(int adType) {

                        }

                        @Override
                        public void onError(String errorMessage) {


                        }

                        @Override
                        public void onLoaded(int adType){

                        }

                        @Override
                        public void onBeforeAdShow() {
                        }

                        @Override
                        public void onAdClicked(int adType) {

                        }

                        @Override
                        public void onFacebookAdCreated(com.facebook.ads.InterstitialAd facebookFrontAD) {
                        }
                    });

//for Banner ad
new OnNativeBannerListener() {
            @Override
            public void onError(String errorMessage) {
              

            }

            @Override
            public void onLoaded(int adType) {
                

            }

            @Override
            public void onAdClicked(int adType) {

            }
        });

// Native ad listener
MediationNativeAd nativeAd = new MediationNativeAd(ad_container, this, getString(R.string.app_name),
                getString(R.string.fb_native_id),
                getString(R.string.admob_native_id), new MediationAdHelper.ImageProvider() {
            @Override
            public void onProvideImage(ImageView imageView, String imageUrl) {
                Glide.with(getApplicationContext())
                        .load(imageUrl)
                        .into(imageView);
            }
        });
        nativeAd.loadAD(KEY_PRIORITY_NATIVE_AD, new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
                hideDialog();
            }

            @Override
            public void onLoaded(int adType) {
                hideDialog();
            }

            @Override
            public void onAdClicked(int adType) {

            }
        });
```
# Contributors
[@CUBIIT.](https://github.com/CUBIIT)

[@Nabeel Ahmed.]https://github.com/nabeel1o1

[@Alamgir Jahanzaib.](https://github.com/Almgirjhanzaib)
# License
```
MIT License

Copyright (c) 2021 Cubiit

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
