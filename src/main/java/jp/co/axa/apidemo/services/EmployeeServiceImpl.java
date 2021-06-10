package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeRepository employeeRepository;

    
    /** 
     * @param employeeRepository
     */
    public void setEmployeeRepository(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    
    /** 
     * Get all Employees.
     * @return List<Employee>
     */
    public List<Employee> retrieveEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees;
    }

    
    /** 
     * Get one Employee and set the result in `Optional`. 
     * You must check if the value is actually in `Optional` before accessing the value.
     * @param employeeId
     * @return Optional<Employee>
     */
    public Optional<Employee> getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    
    /** 
     * Save the given Employee entity and returns the saved Employee.
     * CreatedAt and UpdatedAt are not included in json, but they can are accessible internally.
     * @param employee
     * @return Employee
     */
    @Transactional
    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    
    /** 
     * Delete one Employee with an Employee ID.
     * @param employeeId
     */
    @Transactional
    public void deleteEmployee(Long employeeId){
        employeeRepository.deleteById(employeeId);
    }

    
    /** 
     * Update one Employee with an Employee ID.
     * @param employee
     */
    @Transactional
    public void updateEmployee(Employee employeeFrom, Employee employeeTo) {
        employeeFrom.setDepartment(employeeTo.getDepartment());
        employeeFrom.setName(employeeTo.getName());
        employeeFrom.setSalary(employeeTo.getSalary());
        Instant instant = Instant.now();
        Timestamp timestamp = Timestamp.from(instant);
        employeeFrom.setUpdatedAt(timestamp);
        employeeRepository.save(employeeFrom);
    }
}