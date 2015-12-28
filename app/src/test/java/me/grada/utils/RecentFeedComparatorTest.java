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

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.grada.io.model.Signal;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by yavorivanov on 28/12/2015.
 */
@SmallTest
public class RecentFeedComparatorTest {

    private List<Signal> signals;

    @Before
    public void setUp() {
        signals = new ArrayList<>();

        Signal signalOne = new Signal();
        signalOne.setDescription("Most recent signal");
        signalOne.setDateCreated("2015-12-28T18:00:00+0200");

        Signal signalTwo = new Signal();
        signalTwo.setDescription("Second most recent signal");
        signalTwo.setDateCreated("2015-12-28T16:00:00+0100");

        Signal signalThree = new Signal();
        signalThree.setDescription("Least recent signal");
        signalThree.setDateCreated("2015-12-28T15:00:00+0000");

        signals.add(signalTwo);
        signals.add(signalThree);
        signals.add(signalOne);

        Collections.sort(signals, new RecentSignalsComparator());
    }

    @Test
    public void testMostRecentSignal() {
        assertThat(signals.get(0).getDescription()).isEqualTo("Most recent signal");
    }

    @Test
    public void testSecondMostRecentSignal() {
        assertThat(signals.get(1).getDescription()).isEqualTo("Second most recent signal");
    }

    @Test
    public void testLeastSignal() {
        assertThat(signals.get(2).getDescription()).isEqualTo("Least recent signal");
    }
}
