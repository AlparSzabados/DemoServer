package com.alpar.szabados.hibernate.server.entities;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public enum TaskStatus implements Serializable {
    COMPLETED,
    NOT_COMPLETED
}
