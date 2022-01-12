# Introduction
AdManager is an Admob and Facebook and custom ads mediation library. AdManager supports Interstitial ad, native ad, Banner ad, icon ad, and exit dialog ad.AdManager support custom ad using firebase remote config.
AdManager is a fast, smooth, reliable, and easy-to-use ads mediation library.
# Version 1.6
  Now in version 1.6 support to monitor user events on ad's load, success,error and clicked by using Firebase Events.
  Some other changes:
  * Remove release key values from remote config.
  * Remove test parameter on initliazing method.
  * Now app library remove test ids in release mode.
  * fix some UI/UX issue and code bugs.
# What's new in version 1.5.2
   * All the Ad ids get from remote config. You don't need to pass any Ad ids in methods.
   * Update admob app id in manifest(Beta version, Don't forget to add metadata in manifest of admob app id)
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
  implementation 'com.github.CUBIIT:Ads-Manager:1.6'
}
```
### Step3: Connect App to Firebase
> Add Firebase to your android project.Visit [how to add firebase.](https://firebase.google.com/docs/android/setup)
### Step4: Also add other dependencies
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
# AdMob mediation(Optional)
You can use admob mediation with other different ads source network like facebook, unity etc.

Visit [admob mediation overview](https://developers.google.com/admob/android/mediate).

For facebook visit [Integrating Facebook Audience Network with Admob Mediation](https://developers.google.com/admob/android/mediation/facebook)

> **Note: Don't need to add any code in your app. Our library will handle this for you.**

### Change ads priority (Important!)

Now after setup every thing then change your priorities for all ads like this.

```java
   public static Integer[] KEY_PRIORITY_BANNER_AD = new Integer[]{

             MediationAdHelper.AD_ADMOB,
             MediationAdHelper.AD_FACEBOOK,
             MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_INTERSTITIAL_AD = new Integer[]{
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_CUBI_IT};

    public static Integer[] KEY_PRIORITY_NATIVE_AD = new Integer[]{
            MediationAdHelper.AD_ADMOB,
            MediationAdHelper.AD_FACEBOOK,
            MediationAdHelper.AD_CUBI_IT};
```

# Implementation
### Update v1.4.9
In this update library automatically update the ADMob app ID in manifest by fetching values from remote.
> **Note: Adding Admob app id in manifest is must, otherwise your app may be crash.**
### Menifest
Add your AdMob app ID to your app's AndroidManifest.xml file by adding a <meta-data> tag with **android:name="com.google.android.gms.ads.APPLICATION_ID"**
>  For android:value, insert your own AdMob app ID in quotes.
```java
   
   <manifest>
    <application>
        <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713"/>
    </application>
</manifest>
   
   ```
### Initialization
  > **Activate Events**
To monitor ads events pass the FirebaseAnalytics object.
 ```java
  AdHelperApplication.setFirebaseAnalytics(FirebaseAnalytics.getInstance(this));
  ```
> Below is full code

**Important to fetch the values from remote config file.Without this AdManager does not work properly**
```java
public class AdManager extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Important 
       AdHelperApplication.getValuesFromConfig(true,FirebaseRemoteConfig.getInstance(),AdManager.this,new OnFetchRemoteCallback() {
            @Override
            public void onFetchValuesSuccess() {
            /*
             * called when received success responce from remote config
            */
                Log.d(TAG, "onFetchValuesSuccess: ");
            }

            @Override
            public void onFetchValuesFailed() {
                Log.d(TAG, "onFetchValuesFailed: ");
            /*
             * called when  responce is failed from remote config
            */
            }

            @Override
            public void onUpdateSuccess(String appid) {
         /* always called,get default or last update values 
           */
                Log.d(TAG, "onUpdateSuccess: ");
                updateManifest(appid);
            }
        });
  //to support events
  AdHelperApplication.setFirebaseAnalytics(FirebaseAnalytics.getInstance(this));
    }
   //add this method to update the admob app id in manifest
    private void updateManifest(String app_id) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "Name Found: " + myApiKey);
            ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", app_id);
            String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
            Log.d(TAG, "ReNamed Found: " + ApiKey);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
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
        MediationAdInterstitial.initInterstitialAd(false, this, 
                KEY_PRIORITY_INTERSTITIAL_AD, null);

}

```
### Interstitial Ad
To load and show interstitial ads.
```java
  MediationAdInterstitial.showInterstitialAd(false, this, 
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
  MediationNativeAd nativeAd = new MediationNativeAd(false,ad_container, this, getString(R.string.app_name),
                new MediationAdHelper.ImageProvider() {
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
            KEY_PRIORITY_BANNER_AD,
            new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {
                        
                    }

                    @Override
                    public void onLoaded(int adType) {
                       
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
NativeBanner ad looks like a banner but it customizes the native ad layout. Mostly used in recycleview data.

```java

   MediationNativeBanner nativeAd = new MediationNativeBanner(false,ad_container, AdsActivity.this, getString(R.string.app_name), new MediationAdHelper.ImageProvider() {
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
                

            }

            @Override
            public void onLoaded(int adType) {
                

            }

            @Override
            public void onAdClicked(int adType) {

            }
        });

```
### Icon ad
   To show ads in icon type in your app.
   ```java
  
   MediationNativeBanner nativeBanner = new MediationNativeBanner(frameLayout, mContext.getApplicationContext(), mContext.getString(R.string.app_real_name),        true,null, new MediationAdHelper.ImageProvider() {
            @Override
            public void onProvideImage(ImageView imageView, String imageUrl) {
                Glide.with(mContext).
                        load(imageUrl)
                        .into(imageView);

            }
        });
        nativeBanner.loadAD(Constant.KEY_PRIORITY_NATIVE_AD, new OnNativeBannerListener() {
            @Override
            public void onError(String errorMessage) {
                Log.d(TAG, "onError: " + errorMessage);
            }

            @Override
            public void onLoaded(int adType) {
                Log.d(TAG, "onLoaded: ");
            }

            @Override
            public void onAdClicked(int adType) {

            }
        });
   ```
### Backpress Dialog:
Backpress dialog ad used when user exit from the app. Just call below code **onBackPressed()**.
> If you want to user review your app on exit then pass true in startDialog() method instead of false as given below


```java
@Override
    public void onBackPressed() {
        super.onBackPressed();
        MediationBackPressDialog.startDialog(MainActivity.this, getResources().getString(R.string.app_name)
               
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
* **Framelayout adContainer:** 
> framelayout container for native and banner ads.
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
* **boolean appReview:(Only used in MediationBackpressedDialog )**
> pass true value to show review button,so user can review your app,by default value is false.

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

//for NativeBanner ad Listener

new OnBannerAdListener() {
                    @Override
                    public void onError(String errorMessage) {
                      
                    }

                    @Override
                    public void onLoaded(int adType) {
                        
                    }

                    @Override
                    public void onAdClicked(int adType) {
                    }

                    @Override
                    public void onFacebookAdCreated(com.facebook.ads.AdView facebookBanner) {
                    }
                }
                
                
//for Banner ad Listener
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
       new OnNativeAdListener() {
            @Override
            public void onError(String errorMessage) {
               
            }

            @Override
            public void onLoaded(int adType) {
              
            }

            @Override
            public void onAdClicked(int adType) {

            }
        }
```
# Contributors
[@CUBIIT.](https://github.com/CUBIIT)

[@Alamgir Jahanzaib.](https://github.com/Almgirjhanzaib)

[@Nabeel Ahmed.](https://github.com/nabeel1o1)

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
