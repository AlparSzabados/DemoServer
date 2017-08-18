package com.alpar.szabados.hibernate.server.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum TaskStatus {
    COMPLETED,
    NOT_COMPLETED
}
