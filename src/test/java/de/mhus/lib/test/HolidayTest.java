/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.mhus.lib.core.calendar.Holidays;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.tests.TestCase;

public class HolidayTest extends TestCase {

    @SuppressWarnings("deprecation")
    @Test
    public void testGermanHolidays() throws NotFoundException {
        Holidays holidays = Holidays.getHolidaysForLocale(Locale.GERMANY);
        assertNotNull(holidays);

        {
            Map<Date, String> h = holidays.getHolidays(null, 2018, "Bayern");
            System.out.println(h);
            // Karfreitag - good friday
            assertNotNull(h.get(new Date(2018 - 1900, 2, 30, 0, 0, 0)));
        }
    }
}
