package jp.co.axa.apidemo.controllers;

import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.payload.MessageResponse;
import jp.co.axa.apidemo.services.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private EmployeeService employeeService;

    /** 
     * @param employeeService
     */
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    /** 
     * Get all Employees and return them in json format.
     * @return ResponseEntity<?>
     */
    @Cacheable("employees")
    @GetMapping(value = "/employees", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message ="Returns all employees", response = Employee.class, responseContainer = "List"),
    })
    public ResponseEntity<?> getEmployees() {
        try {
            return new ResponseEntity<>(employeeService.retrieveEmployees(), HttpStatus.OK);
        } catch (Exception ex){
            logger.error("Employee Failed to Search.", ex);
            // To not return the detail, we just return a fixed string and a status number.
            MessageResponse error = this.getMessageMap("Sorry, server error. Please try again later.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /** 
     * Get one Employee by an EmployeeId which is given this way: /employees/{employeeId}.
     * Return it in json format when it is found.
     * 
     * When ID is not found, this returns "message" : "Employee not found." 
     * When employeeId is invalid, this returns "message" : "Employee ID is invalid." 
     * 
     * @param employeeId
     * @return ResponseEntity<?>
     */
    @Cacheable("employee")
    @GetMapping(value = "/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message ="Successfully found Employee. Returns one Employee selected by a given Employee ID.", response = Employee.class),
    })
    public ResponseEntity<?> getEmployee(@PathVariable(name="employeeId")String employeeId) {
        try {
             // Check if the employee ID is a valid long
            if (!this.isLong(employeeId)) {
                return ResponseEntity.badRequest().body(this.getMessageMap("Employee ID is invalid."));
            }
            // If Employee ID is valid, convert Employee ID to long.
            long longEmployeeId = Long.valueOf(employeeId);
            Optional<Employee> opEmp = employeeService.getEmployee(longEmployeeId);
            if (opEmp.isPresent()) {
                // If Employee was found, give it to the user.
                return new ResponseEntity<>(opEmp.get(), HttpStatus.OK);
            }
            // If Employee was not found, notify the user that the Employee was not found.
            return new ResponseEntity<>(this.getMessageMap("Employee Not Found."), HttpStatus.NOT_FOUND);
        } catch (Exception ex){
            // We don't return the error detail to the API caller.
            logger.error("Employee Failed to Search: " + employeeId, ex);
            // To not return the detail, we just return a fixed string and a status number.
            MessageResponse error = this.getMessageMap("Sorry, server error. Please try again later.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /** 
     * Save one Employee. 
     * When saving the entity is success, this returns the saved Employee as json.
     * If user input for Employee entity is invalid, this return validation errors in json format such as:
     * 
     * "errors": {
     *      "department": "Department cannot be blank.",
     *      "salary": "Salary must not be less than 0."
     *  }
     * 
     * @param employee
     * @param bindingResult
     * @return ResponseEntity<?>
     */
    @CacheEvict(cacheNames={"employees", "employee"})
    @PostMapping(value = "/employees", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message ="Successfully saved. Returns saved Employee.", response = Employee.class),
        @ApiResponse(code = 400, message = "Returns validation errors. Validation errors are stored in \"errors\".")
    })
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody Employee employee, BindingResult bindingResult){
        try {
            // Validate the user input. Save the data only when the data is valid.
            if (bindingResult.hasErrors()) {
                // If there is any error, return the errors.
                Map<String, Map<String, String>> errors = this.getErrors(bindingResult.getFieldErrors());
                return ResponseEntity.badRequest().body(errors);
            }
            // If there is no error, proceed to save the data.
            Employee emp = employeeService.saveEmployee(employee);
            logger.info("Employee Saved Successfully");
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception ex){
            // We don't return the error detail to the API caller.
            logger.error("Employee Failed to Save.", ex);
            // To not return the detail, we just return a fixed string and a status number.
            MessageResponse error = this.getMessageMap("Sorry, server error. Please try again later.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /** 
     * Delete one Employee by an EmployeeId which is given this way: /employees/{employeeId}.
     * 
     * When deletion is success, this returns "message" : "Employee Deleted Successfully." 
     * When ID is not found, this returns "message" : "Employee not found." 
     * When employeeId is invalid, this returns "message" : "Employee ID is invalid." 
     * 
     * @param employeeId
     * @return ResponseEntity<?>
     */
    @CacheEvict(cacheNames={"employees", "employee"})
    @DeleteMapping(value="/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message ="Successfully deleted. Returns a message \"Success.\".", response = MessageResponse.class),
        @ApiResponse(code = 400, message = "Returns validation errors. Validation errors are stored in \"errors\".")
    })
    public ResponseEntity<?> deleteEmployee(@Validated @PathVariable(name="employeeId")String employeeId){
        try {
            // Check if the employee ID is a valid long
            if (!this.isLong(employeeId)) {
                // If there is any error, return the errors.
                return ResponseEntity.badRequest().body(this.createError("id", "Employee ID is invalid."));
            }
            // If Employee ID is valid, convert Employee ID to long.
            long longEmployeeId = Long.valueOf(employeeId);
            Optional<Employee> opEmp = employeeService.getEmployee(longEmployeeId);
            if (opEmp.isPresent()) {
                // If Employee was found, proceed to delete the user.
                employeeService.deleteEmployee(longEmployeeId);
                logger.info("Employee Deleted Successfully: " + employeeId);
                return new ResponseEntity<>(this.getMessageMap("Success."), HttpStatus.OK);
            }
            // If Employee was not found, notify the user that the Employee was not found.
            return new ResponseEntity<>(this.getMessageMap("Employee Not Found."), HttpStatus.NOT_FOUND);
        } catch (Exception ex){
            // We don't return the error detail to the API caller.
            logger.error("Employee Failed to Delete: " + employeeId, ex);
            // To not return the detail, we just return a fixed string and a status number.
            MessageResponse error = this.getMessageMap("Sorry, server error. Please try again later.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /** 
     * Update one Employee by an EmployeeId which is given this way: /employees/{employeeId}.
     * 
     * When update is success, this returns "message" : "Employee Updated Successfully." 
     * When ID is not found, this returns "message" : "Employee not found." 
     * When employeeId is invalid, this returns "message" : "Employee ID is invalid." 
     * If user input for Employee entity is invalid, this return validation errors in json format such as:
     * 
     * "errors": {
     *      "department": "Department cannot be blank.",
     *      "salary": "Salary must not be less than 0."
     *  }
     * 
     * @param employee
     * @param bindingResult
     * @param employeeId
     * @return ResponseEntity<?>
     */
    @CacheEvict(cacheNames={"employees", "employee"})
    @PutMapping(value="/employees/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message ="Successfully updated. Returns a message \"Success.\".", response = MessageResponse.class),
        @ApiResponse(code = 400, message = "Returns validation errors. Validation errors are stored in \"errors\".")
    })
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody Employee employee,
                               BindingResult bindingResult,
                               @PathVariable(name="employeeId")String employeeId){
        try {
            // Check if the employee ID is a valid long
            if (!this.isLong(employeeId)) {
                return ResponseEntity.badRequest().body(this.createError("id", "Employee ID is invalid."));
            }
            // Validate the user input. Save the data only when the data is valid.
            if (bindingResult.hasErrors()) {
                // If there is any error, return the errors.
                Map<String, Map<String, String>> errors = this.getErrors(bindingResult.getFieldErrors());
                return ResponseEntity.badRequest().body(errors);
            }
            // If Employee ID is valid, convert Employee ID to long.
            long longEmployeeId = Long.valueOf(employeeId);
            Optional<Employee> opEmp = employeeService.getEmployee(longEmployeeId);
            // If Employee was found by the employeeId, we update the data
            if(opEmp.isPresent()){
                // If Employee was found, proceed to update the user.
                employeeService.updateEmployee(opEmp.get(), employee);
                logger.info("Employee Updated Successfully: " + employeeId);
                return new ResponseEntity<>(this.getMessageMap("Success."), HttpStatus.OK);
            }
            // If Employee was not found, notify the user that the Employee was not found.
            return new ResponseEntity<>(this.getMessageMap("Employee Not Found."), HttpStatus.NOT_FOUND);
        } catch (Exception ex){
            // We don't return the error detail to the API caller.
            logger.error("Employee Failed to Update", ex);
            // To not return the detail, we just return a fixed string and a status number.
            MessageResponse error = this.getMessageMap("Sorry, server error. Please try again later.");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /** 
     * Check if given string can be parsed to Long. 
     * Returns true when possible. 
     * Returns false when impossible. 
     * 
     * @param str
     * @return boolean
     */
    private boolean isLong(String str) {
        try{
            Long.parseLong( str );
            return true;
        } catch (NumberFormatException ex){
            return false;
        }
    }

    /** 
     * This function organizes validation errors so that the error information would look like in json format:
     * 
     * "errors": {
     *      "department": "Department cannot be blank.",
     *      "salary": "Salary must not be less than 0."
     *  }
     * 
     * This returns the same format as the format of createError()
     * 
     * @param errors
     * @return Map<String, Map<String, String>>
     */
    private Map<String, Map<String, String>> getErrors(List<FieldError> errors) {
        Map<String, String> errorList = new HashMap<>();
        for (FieldError error : errors ) {
            errorList.put(error.getField(), error.getDefaultMessage());
        }
        Map<String, Map<String, String>> result = new HashMap<>();
        result.put("errors", errorList);
        return result;
    }
    
    /** 
     * This function accepts one string and convert it to error message in json format:
     * 
     * "errors": {
     *      "id": "Employee ID is invalid.",
     *  }
     * 
     * This returns the same format as the format of getErrors()
     * 
     * @param propertyName
     * @param message
     * @return Map<String, Map<String, String>>
     */
    private Map<String, Map<String, String>> createError(String propertyName, String message) {
        Map<String, String> errorList = new HashMap<>();
        errorList.put(propertyName, message);
        Map<String, Map<String, String>> result = new HashMap<>();
        result.put("errors", errorList);
        return result;
    }

    /** 
     * This function organizes message information so that the information would look like in json format:
     * 
     * "message" : "My Message Here." 
     * 
     * @param msg
     * @return SuccessResponse
     */
    private MessageResponse getMessageMap(String msg) {
        MessageResponse result = new MessageResponse();
        result.setMessage(msg);
        return result;
    }
}
