/*
 * Copyright (c) 2002-2015 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.httpclient;

import static com.gargoylesoftware.htmlunit.BrowserVersionFeatures.HTTP_COOKIE_EXTENDED_DATE_PATTERNS;
import static com.gargoylesoftware.htmlunit.BrowserVersionFeatures.HTTP_COOKIE_START_DATE_1970;

import java.util.Date;

import org.apache.http.client.utils.DateUtils;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.BasicExpiresHandler;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * Customized BasicExpiresHandler for HtmlUnit.
 *
 * @version $Revision: 10270 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Noboru Sinohara
 * @author David D. Kilzer
 * @author Marc Guillemot
 * @author Brad Clarke
 * @author Ahmed Ashour
 * @author Nicolas Belisle
 * @author Ronald Brill
 * @author John J Murdoch
 */
final class HtmlUnitExpiresHandler extends BasicExpiresHandler {

    // simplified patterns from BrowserCompatSpec, with yy patterns before similar yyyy patterns
    static final String[] DEFAULT_DATE_PATTERNS = new String[] {
        "EEE dd MMM yy HH mm ss zzz",
        "EEE dd MMM yyyy HH mm ss zzz",
        "EEE MMM d HH mm ss yyyy",
        "EEE dd MMM yy HH mm ss z ",
        "EEE dd MMM yyyy HH mm ss z ",
        "EEE dd MM yy HH mm ss z ",
        "EEE dd MM yyyy HH mm ss z ",
    };
    static final String[] EXTENDED_DATE_PATTERNS = new String[] {
        "EEE dd MMM yy HH mm ss zzz",
        "EEE dd MMM yyyy HH mm ss zzz",
        "EEE MMM d HH mm ss yyyy",
        "EEE dd MMM yy HH mm ss z ",
        "EEE dd MMM yyyy HH mm ss z ",
        "EEE dd MM yy HH mm ss z ",
        "EEE dd MM yyyy HH mm ss z ",
        "d/M/yyyy",
    };

    private final BrowserVersion browserVersion_;

    HtmlUnitExpiresHandler(final BrowserVersion browserVersion) {
        super(DEFAULT_DATE_PATTERNS);
        browserVersion_ = browserVersion;
    }

    @Override
    public void parse(final SetCookie cookie, String value) throws MalformedCookieException {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        value = value.replaceAll("[ ,:-]+", " ");

        Date startDate = null;
        String[] datePatterns = DEFAULT_DATE_PATTERNS;

        if (null != browserVersion_) {
            if (browserVersion_.hasFeature(HTTP_COOKIE_START_DATE_1970)) {
                startDate = HtmlUnitBrowserCompatCookieSpec.DATE_1_1_1970;
            }

            if (browserVersion_.hasFeature(HTTP_COOKIE_EXTENDED_DATE_PATTERNS)) {
                datePatterns = EXTENDED_DATE_PATTERNS;
            }
        }

        final Date expiry = DateUtils.parseDate(value, datePatterns, startDate);
        cookie.setExpiryDate(expiry);
    }
}
