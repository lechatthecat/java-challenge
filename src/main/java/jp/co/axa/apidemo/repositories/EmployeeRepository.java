package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    @Query("select e from Employee e order by id asc")
    List<Employee> findAll();
    @Query("select e from Employee e where id = :id")
    Employee findById(@Param("id") long id);
}
