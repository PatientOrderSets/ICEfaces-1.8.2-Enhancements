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

package com.icesoft.faces.env;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.HttpSessionContextIntegrationFilter;
import org.acegisecurity.context.SecurityContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.Principal;
import java.util.Map;

public class AcegiAuthWrapper implements Authorization {
    private final static Log Log = LogFactory.getLog(AcegiAuthWrapper.class);
    private final Authentication authentication;

    public AcegiAuthWrapper(Principal principal) {
        this.authentication = (Authentication) principal;
    }

    public boolean isUserInRole(String role) {
        if (null == authentication) {
            return false;
        }
        Log.trace("isUserInRole ROLE: " + role);

        GrantedAuthority[] authorities = authentication.getAuthorities();
        if (authentication.getPrincipal() == null || authorities == null) {
            return false;
        }

        for (int i = 0; i < authorities.length; i++) {
            if (role.equals(authorities[i].getAuthority())) {
                return true;
            }
        }

        return false;
    }

    public static Authorization getVerifier(Principal principal, Map sessionMap) {
        if (principal instanceof Authentication) {
            return new AcegiAuthWrapper(principal);
        } else {
            SecurityContext sc = (SecurityContext) sessionMap.get(HttpSessionContextIntegrationFilter.ACEGI_SECURITY_CONTEXT_KEY);
            return new AcegiAuthWrapper(sc == null ? null : sc.getAuthentication());
        }
    }

}
