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

import android.content.res.Resources;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.concurrent.TimeUnit;

import me.grada.GradaMeApp;
import me.grada.R;

/**
 * Created by yavorivanov on 28/12/2015.
 */
public class DateTimeUtils {

    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER =
            ISODateTimeFormat.dateTimeParser();

    private DateTimeUtils() {
    }

    /**
     * Calculates the elapsed time between a point in the past and now.
     *
     * @param before The point in the past represented as a valid ISO 8601 value.
     * @return Elapsed time using the following formatting convention: 1 min, 1 hour, 1 day, etc
     *
     */
    public static String getElapsedTime(String before) {
        String now = ISODateTimeFormat.dateTime().print(DateTime.now());
        return DateTimeUtils.getElapsedTime(now, before);
    }

    /**
     * Calculates the elapsed time between two points in time.
     * Both values ought to be compliant with ISO 8601.
     *
     * @param now A point in time.
     * @param before A point in time earlier in time than the first parameter.
     * @return Elapsed time using the following formatting convention: 1 min, 1 hour, 1 day, etc
     */
    public static String getElapsedTime(String now, String before) {

        Resources res = GradaMeApp.get().getResources();

        long beforeTimestamp = ISO_DATE_TIME_FORMATTER.parseDateTime(before).getMillis();
        long nowTimestamp = ISO_DATE_TIME_FORMATTER.parseDateTime(now).getMillis();

        if (beforeTimestamp >= nowTimestamp) {
            return res.getString(R.string.just_now);
        }

        long seconds = (nowTimestamp - beforeTimestamp) / 1000;
        long days = (int) TimeUnit.SECONDS.toDays(seconds);

        if (days > 0) {
            return res.getQuantityString(R.plurals.days, (int) days, (int) days);
        }

        long hours = TimeUnit.SECONDS.toHours(seconds) - (days * 24);

        if (hours > 0) {
            return res.getQuantityString(R.plurals.hours, (int) hours, (int) hours);
        }

        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);

        if (minute > 0) {
            return res.getQuantityString(R.plurals.minutes, (int) minute, (int) minute);
        }

        return res.getString(R.string.just_now);
    }

}