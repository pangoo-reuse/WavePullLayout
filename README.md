**License**

    Copyright 2017 Yone Hsiung
    
   
       Licensed under the Apache License, Version 2.0 (the "License");
       you may not use this file except in compliance with the License.
       You may obtain a copy of the License at
    
           http://www.apache.org/licenses/LICENSE-2.0
    
       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.



**`project  is a library for pull to refresh,use  Seibel line`**

    Supports Pulling Down from the top.
    Animated Scrolling for all devices.
    Over Scroll supports for devices on Android v2.2+.

    
**`Currently works with:`**
    
    ListView
    ExpandableListView
    GridView
    WebView
    ScrollView
    ViewPager

**`this project compile : support-v4 , nineoldandroids`**
so  if you use this library ,you unnecessary compile "support-v4 , nineoldandroids" 
for your project

**`new method added `**

    app:xy_mode="single"

**`how to use it`**
    xml :
    
    <com.xy.open.wavepulllayout. PullLayout 
         android:id="@+id/pull"
         android:layout_width="match_parent"
         android:layout_height="match_parent"
         app:xy_headCenterImage="@mipmap/main_gold"
         app:xy_headCenterSuccessImage="@mipmap/main_success"
         app:xy_headLeftImage="@mipmap/main_left_huaxing"
         app:xy_headRightImage="@mipmap/main_right_xinlang"
         app:xy_textColor="@color/colorAccent"
         app:xy_textSize="15sp"
         app:xy_waveColor="@color/colorAccent"
         app:xy_bgColor="@color/colorPrimaryDark"
         app:xy_pullHeight="180dp"
         app:xy_headerHeight="100dp"
         >
         
         <ListView
             android:layout_width="match_parent"
             android:layout_height="match_parent"
             android:background="@color/colorPrimary" />
             
     </com.xy.open.wavepulllayout.PullLayout> 
     
     
     
**`thanks for NineOldAndroids:`** https://github.com/JakeWharton/NineOldAndroids


	Supports Pulling Down from the top.
	Animated Scrolling for all devices.
	Over Scroll supports for devices on Android v2.2+.

	Currently works with:
	ListView
	ExpandableListView
	GridView
	WebView
	ScrollView
	HorizontalScrollView
	ViewPager

	this project compile : support-v4 , nineoldandroids ,so  if you use this library ,
	you unnecessary compile "support-v4 , nineoldandroids" for your project

	thanks for NineOldAndroids :https://github.com/JakeWharton/NineOldAndroids
**`Gradle:`**
this can install newest library
	
	compile 'com.xy.open.wavepulllayout:wavepulllayout:latest.integration'
	
	
**`this is GIF for you :`**
  ![image](https://github.com/YongHsiung/WavePullLayout/blob/master/anim.gif)





