/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.util;

import com.icesoft.util.encoding.Base64;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * The <code>IdGenerator</code> is responsible for generating a unique ID based
 * on a counter, the current time in milliseconds, an arbitrary string, the IP
 * address of the localhost, and a random number. </p>
 */
public class IdGenerator {
    private String seed;
    private long counter;
    private String ipAddress;
    private static MessageDigest md5;


    public IdGenerator() {
        this(String.valueOf(new Random().nextInt()));
    }

    public IdGenerator(String seed) {
        this.seed = seed.trim();
        this.counter = 0;
        try {
            md5 = MessageDigest.getInstance("MD5");
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (NoClassDefFoundError e)  {
            //Google App Engine
            ipAddress = "GAE";
        }
    }

    /**
     * Creates a unique ID based on the specified <code>string</code>. </p>
     *
     * @return a unique ID.
     */
    public synchronized String newIdentifier() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(++counter);
        buffer.append(System.currentTimeMillis());
        buffer.append(seed);
        buffer.append(ipAddress);
        buffer.append(Math.random());
        byte[] digest = md5.digest(buffer.toString().getBytes());
        byte[] encodedDigest = Base64.encodeForURL(digest);
        return new String(encodedDigest);
    }
}
