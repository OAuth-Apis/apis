package org.surfnet.oaaas.cas;

/**
 * Created by bourges on 07/08/14.
 */
public class CasUser {
    String uid;
    boolean isAdmin;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
