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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Getter
    @Setter
    @Id
    @ApiModelProperty(readOnly = true)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Getter
    @Setter
    @Size(max = 255, message = "Employee name cannot be more than 255 letters.")
    @NotBlank(message = "Employee name cannot be blank.")
    @Column(name="EMPLOYEE_NAME")
    private String name;

    @Getter
    @Setter
    @Min(value = 0, message = "Salary must not be less than 0.")
    @Size(max = 255, message = "Salary cannot be more than 255 letters.")
    @Pattern(message = "Salary must be a number", regexp="^-?[0-9]*$")
    @Column(name="EMPLOYEE_SALARY")
    private String salary;

    @Getter
    @Setter
    @Size(max = 255, message = "Department cannot be more than 255 letters.")
    @NotBlank(message = "Department cannot be blank.")
    @Column(name="DEPARTMENT")
    private String department;

    @Getter
    @Setter
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    @Column(name="UPDATED_AT")
    private Timestamp updatedAt;

    @Getter
    @JsonIgnore
    @ApiModelProperty(hidden=true)
    @Column(name="CREATED_AT")
    private Timestamp createdAt;
}
