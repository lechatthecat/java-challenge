package jp.co.axa.apidemo.entities;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_NAME")
    private String name;

    @Getter
    @Setter
    @Column(name="EMPLOYEE_SALARY")
    private Integer salary;

    @Getter
    @Setter
    @Column(name="DEPARTMENT")
    private String department;

    @Getter
    @Setter
    @ApiModelProperty(hidden=true)
    @ApiParam(access = "internal", required = false)
    @Column(name="UPDATED_AT")
    private Timestamp updatedAt;

    @Getter
    @ApiModelProperty(hidden=true)
    @ApiParam(access = "internal", required = false)
    @Column(name="CREATED_AT")
    private Timestamp createdAt;
}
