package com.example.bmeisr.badiapp;

import android.app.Activity;
import android.app.NativeActivity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {


   @Rule
   public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);



    @Test
    public void testBadiNameLoad(){

        // Context of the app under Test
        Context appContext = InstrumentationRegistry.getTargetContext();

        MainActivity mainActivity = mActivityRule.getActivity();
        int numbers = 136;
        assertEquals(numbers,mainActivity.badiliste.getCount());

    }

     @Test
    public void checkFirstRow(){
         Context appContext = InstrumentationRegistry.getTargetContext();

         MainActivity mainActivity = mActivityRule.getActivity();
         String str ="Aarberg - Schwimmbad";
         assertEquals(str,mainActivity.badiliste.getItem(0));
     }







}
