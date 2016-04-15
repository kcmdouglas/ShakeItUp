# Shake It Up

#### An app for choosing your evening plans

##### Friday April 15th, 2016

#### By Midori Bowen, Kassidy Douglas, Illia Kuchko, Nathan Romike, Jeffrey Ruder, Christopher Siems

## Description

Shake It Up is an app where a user can enter a location and decide what they'd like to do in an evening. If users feel stuck in a rut, they can "shake up" their nightlife plans!

Users can:
* Search for a location or let Shake It Up use their current location
* Choose businesses for drinks, dinner, and a fun activity
  * If they don't like their options, on each choice they can 'shake to shuffle' to see three new options
* Once they have their choices, they can view them all on a map, share their choices with friends, and click 'Let's Go' to go to Google Navigation to their first, second, and third locations in turn

Shake It Up demonstrates some of the following Android development concepts:

* Animations
* User geolocation
* Gesture detection
* Implicit intents
* Fragments (and moving data between fragments and activities)
* Material design
* Using data from different APIs (Unsplash, Yelp, and Google Maps)
* Google map customization (info windows and markers)

## Setup/Installation Requirements
You will need the following programs installed on your computer.
* Android Studio
* Java JDK 8+
* Android SDK

### To Run Shake It Up
* In a terminal window, navigate to ~/AndroidStudioProjects
* Run `git clone https://github.com/midoribowen/ShakeItUp.git`
* Navigate to ~/AndroidStudioProjects/ShakeItUp
* Run on either an emulator or an Android OS Device connected to a computer

##### To set up an emulator
* Select Run > Run 'app'
* Click 'Create New Emulator'
* Select the device you would like to emulate (Recommended: Nexus 6)
* Select the API level you would like to run - click 'Download' if not available (Recommended: Marshmallow - ABI: x86)
* Select configuration settings for emulator
 * Recommended:
 * Scale: 4dp on device = 1px on screen
 * Camera - Front: Webcam()
 * Camera - Back: Webcam()
* Click 'Finish' and allow Emulator to run

##### To Run on an Android OS Device
* Connect the device to the computer through its USB port
* Make sure USB debugging is enabled (this may pop up in a window when you connect the device or it may need to be checked in the phone's settings)
* Select Run > Run 'app'
* Select the device (If it does not show, USB debugging is probably not enabled)
* Click 'OK'
* Wait... it takes a little while the first time

## Technologies Used

AndroidStudio, AndroidSDK, Java v1.8, Gradle

### License
Copyright (c) 2016 Midori Bowen, Kassidy Douglas, Illia Kuchko, Nathan Romike, Jeffrey Ruder, Christopher Siems

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
