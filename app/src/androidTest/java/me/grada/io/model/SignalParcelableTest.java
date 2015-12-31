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

package me.grada.io.model;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by yavorivanov on 30/12/2015.
 */
public class SignalParcelableTest extends AndroidTestCase {

    private Signal expectedSignal;
    private Signal actualSignal;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        expectedSignal = new Signal();
        expectedSignal.setDateCreated("dateCreated");
        expectedSignal.setTitle("title");
        expectedSignal.setDescription("description");
        expectedSignal.setAddress("address");
        expectedSignal.setImages(new String[]{"image"});
        expectedSignal.setLocation(new double[]{Double.MAX_VALUE, Double.MIN_VALUE});
        expectedSignal.setStatus(Integer.MAX_VALUE);
        expectedSignal.setType("type");

        Parcel parcel = Parcel.obtain();
        expectedSignal.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        actualSignal = new Signal(parcel);
    }

    @SmallTest
    public void testDateCreated() {
        assertThat(expectedSignal.getDateCreated()).isEqualTo(actualSignal.getDateCreated());
    }

    @SmallTest
    public void testTitle() {
        assertThat(expectedSignal.getTitle()).isEqualTo(actualSignal.getTitle());
    }

    @SmallTest
    public void testDescription() {
        assertThat(expectedSignal.getDescription()).isEqualTo(actualSignal.getDescription());
    }

    @SmallTest
    public void testAddress() {
        assertThat(expectedSignal.getAddress()).isEqualTo(actualSignal.getAddress());
    }

    @SmallTest
    public void testImages() {
        assertThat(expectedSignal.getImages()).isNotNull();
        assertThat(expectedSignal.getImages().length).isEqualTo(1);
        assertThat(expectedSignal.getImages()[0]).isEqualTo(actualSignal.getImages()[0]);
    }

    @SmallTest
    public void testLocation() {
        assertThat(expectedSignal.getLocation()).isNotNull();
        assertThat(expectedSignal.getLocation().length).isEqualTo(2);
        assertThat(expectedSignal.getLocation()[0]).isEqualTo(actualSignal.getLocation()[0]);
        assertThat(expectedSignal.getLocation()[1]).isEqualTo(actualSignal.getLocation()[1]);
    }

    @SmallTest
    public void testStatus() {
        assertThat(expectedSignal.getStatus()).isEqualTo(actualSignal.getStatus());
    }

    @SmallTest
    public void testType() {
        assertThat(expectedSignal.getType()).isEqualTo(actualSignal.getType());
    }

}
