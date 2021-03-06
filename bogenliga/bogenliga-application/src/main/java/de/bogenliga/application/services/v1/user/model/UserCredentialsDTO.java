package de.bogenliga.application.services.v1.user.model;

import de.bogenliga.application.common.service.types.DataTransferObject;

/**
 * Zur initialen Anlage einer neuen Anwenders werden Name und Password gehalten
 *
 * @author Andre Lehnert, eXXcellent solutions consulting & software gmbh
 */
public class UserCredentialsDTO implements DataTransferObject {

    private static final long serialVersionUID = 7100904135169446743L;
    private String username;
    private String password;
    private String code;
    private boolean isUsing2FA;


    public String getUsername() {
        return username;
    }
    public void setUsername(final String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(final String password) {
        this.password = password;
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    public boolean isUsing2FA() {
        return isUsing2FA;
    }


    public void setUsing2FA(boolean using2FA) {
        isUsing2FA = using2FA;
    }
}
