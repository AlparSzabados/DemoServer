package com.alpar.szabados.hibernate.server.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class UserAndActivityWrapper implements Serializable {
    private User user;
    private Activity activity;

    public UserAndActivityWrapper() {
    }

    public UserAndActivityWrapper(User user, Activity activity) {
        this.user = user;
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
