/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Obshtestvo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.grada.utils;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import me.grada.R;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by yavorivanov on 28/12/2015.
 */
public class DateTimeUtilsTest extends AndroidTestCase{

    @SmallTest
    public void testSameTimezoneElapsedSingleMinute(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-28T17:59:00+0200";

        final int expectedElapsedMinute = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.minutes, expectedElapsedMinute, expectedElapsedMinute);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedSingleMinute(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-28T17:59:00+0200";

        final int expectedElapsedMinute = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.minutes, expectedElapsedMinute, expectedElapsedMinute);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testSameTimezoneElapsedMultipleMinutes(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-28T17:58:00+0200";

        final int expectedElapsedMinutes = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.minutes, expectedElapsedMinutes, expectedElapsedMinutes);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedMultipleMinutes(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-28T17:58:00+0200";

        final int expectedElapsedMinutes = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.minutes, expectedElapsedMinutes, expectedElapsedMinutes);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testSameTimezoneElapsedSingleHour(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-28T17:00:00+0200";

        final int expectedElapsedHour = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.hours, expectedElapsedHour, expectedElapsedHour);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedSingleHour(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-28T17:00:00+0200";

        final int expectedElapsedHour = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.hours, expectedElapsedHour, expectedElapsedHour);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testSameTimezoneElapsedMultipleHours(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-28T16:00:00+0200";

        final int expectedElapsedHours = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.hours, expectedElapsedHours, expectedElapsedHours);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedMultipleHours(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-28T16:00:00+0200";

        final int expectedElapsedMinutes = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.hours, expectedElapsedMinutes, expectedElapsedMinutes);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testSameTimezoneElapsedSingleDay(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-27T17:00:00+0200";

        final int expectedElapsedDay = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.days, expectedElapsedDay, expectedElapsedDay);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedSingleDay(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-27T17:00:00+0200";

        final int expectedElapsedDay = 1;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.days, expectedElapsedDay, expectedElapsedDay);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testSameTimezoneElapsedMultipleDays(){
        final String now =    "2015-12-28T18:00:00+0200";
        final String before = "2015-12-26T18:00:00+0200";

        final int expectedElapsedDays = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.days, expectedElapsedDays, expectedElapsedDays);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

    @SmallTest
    public void testDifferentTimezoneElapsedMultipleDays(){
        final String now =    "2015-12-28T16:00:00+0000";
        final String before = "2015-12-26T17:00:00+0200";

        final int expectedElapsedDays = 2;

        final String actualElapsedTime = DateTimeUtils.getElapsedTime(now, before);
        final String expectedElapsedTime = getContext().getResources()
                .getQuantityString(R.plurals.days, expectedElapsedDays, expectedElapsedDays);

        assertThat(actualElapsedTime).isEqualTo(expectedElapsedTime);
    }

}
