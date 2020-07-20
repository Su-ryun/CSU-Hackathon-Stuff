# In case if they've decided not to archive the winners.

<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/IWonSomething.png" height="300" width="500">

Q: I see 5. Why am I only seeing your commits in this repo? <br/>
A: The Unity portion of this project kinda broke, and I was the only Andy code monkey.

### Since, I got rid of the Firebase database and other API keys related to this project, there is no way of running this app. So, here are the pictures:

This is the first thing the users will see when they open the app. <br/>        
<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/1.png" height="500" width="300"> <br/><br/>

Essentially, you can use the app to place augmented reality objects
around the world (literally) to have them persist for 24 hours
(Google working on ARCore to make cloud anchors persist permanently).
<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/2.png" height="500" width="300"><br/><br/>

After the hackathon was over, one of the judge came over to us and said,
"we are currently thinking about creating an AR tour across our campus."
During the hackathon, the AR objects were static, you couldn't customize
the AR objects in anyway; <br/><br/>

therefore, I've decided to extend what I have, so the users can at least
put descriptions on'em.
<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/3.png" height="500" width="300">

<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/4.png" height="500" width="300">

<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/5.png" height="500" width="300"> <br/><br/>

As the API suggests, those AR objects are being persisted (as alphanumeric)
in the cloud service (Firebase). Since, I've created the anchor just now
with "Aerospace Engineering Building" as its id, I can generate the
same object I've created in front of me.<br>
<img src="https://github.com/dlee67/CSU-Hackathon-Stuff/blob/master/images/6.png" height="500" width="300">
 
# Learned new parent concepts.

eye odometry

pupil(?) odometry

# The belows are the links I've used.

https://developers.google.com/ar/develop/java/cloud-anchors/quickstart-android

https://developers.google.com/ar/reference/java/sceneform/reference/com/google/ar/sceneform/ux/ArFragment

https://developers.google.com/ar/develop/java/sceneform

https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#hostCloudAnchor(com.google.ar.core.Anchor)

https://stackoverflow.com/questions/26488840/where-to-put-png-files-and-how-to-refer-to-them-from-imageview-in-xml-layout

https://stackoverflow.com/questions/5576822/change-background-of-linearlayout-in-android

https://stackoverflow.com/questions/8969122/right-align-text-in-android-textview

https://stackoverflow.com/questions/13264794/font-size-of-textview-in-android-application-changes-on-changing-font-size-from

https://stackoverflow.com/questions/9290651/make-a-hyperlink-textview-in-android

### The below was the backbone of this project.

# https://github.com/zipper-studios/ARCoreCloudAnchors

# ARCoreCloudAnchors

<img src="https://i1.wp.com/androidcommunity.com/wp-content/uploads/2018/05/cloud-anchors.png?resize=696%2C383&ssl=1" height="250" width="500">

This app is based on Augmented Reality technology and uses ARCore Cloud Anchors to create multiplayer or collaborative AR experiences that Android and IOS users can share. A step-by-step guide to create this app is available on our article on [Medium](https://medium.com/p/16929723f693/edit "AR technology for Android - Part 4 : AR Cloud Anchors"), where you will learn how to host and resolve a Cloud Anchor as Android developer.

## Running 
To use Cloud Anchors, you'll need to add an API Key to your app for authentication with the ARCore Cloud Anchor Service. Follow [Steps 1 and 2](https://developers.google.com/ar/develop/java/cloud-anchors/quickstart-android#add_an_api_key) from these instructions to get an API Key.

Include the API key in your *AndroidManifest.xml* file as follows:
```
<meta-data
        android:name="com.google.android.ar.API_KEY"
        android:value="<YOUR API KEY HERE>" />
```
                
                
For sharing the Cloud Anchor IDs between different devices, we use the [Firebase Realtime Database](https://firebase.google.com/docs/database). You need to set up a Firebase Realtime Database with your Google account to use with this app. This is easy with the Firebase Assistant in Android Studio. See more details about how to connect your app with Firebase on [Medium- Step 4](https://medium.com/p/16929723f693/edit "AR technology for Android - Part 4 : AR Cloud Anchors").

## Requirements
- A supported ARCore device, connected via a USB cable to your development machine (and also connected to the Internet via WiFi).
- ARCore 1.8 or later.
- A development machine with Android Studio (v3.0 or later).

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/zipper-studios/ARCoreCloudAnchors/blob/master/LICENSE) file for details

