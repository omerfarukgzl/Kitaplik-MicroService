package com.Omer.entity;


import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseClass implements Serializable {
    private Date createData;
    private String createdBy;
    private Date updateAt;
    private String updateBy;

}
