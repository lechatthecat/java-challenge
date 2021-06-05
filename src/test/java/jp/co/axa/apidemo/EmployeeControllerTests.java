package jp.co.axa.apidemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jp.co.axa.apidemo.Util.TestUtil;
import jp.co.axa.apidemo.controllers.EmployeeController;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	EmployeeService empService;

    @Test
	public void testGetEmployees() throws Exception {
        // Define how EmployeeService works
        Employee emp1 = new Employee();
        emp1.setId(1l);
        emp1.setName("name_test_1");
        emp1.setSalary("10000");
        emp1.setDepartment("dep_test_1");
        Employee emp2 = new Employee();
        emp2.setId(2l);
        emp2.setName("name_test_2");
        emp2.setSalary("20000");
        emp2.setDepartment("dep_test_2");
        List<Employee> emps = new ArrayList<Employee>();
        emps.add(emp1);
        emps.add(emp2);
        when(empService.retrieveEmployees()).thenReturn(emps);

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .get("/api/v1/employees/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		// verify that service method was called once
		verify(empService).retrieveEmployees();

        // Verify returned Employees
        List<Employee> outputList = TestUtil.jsonToObjectList(result.getResponse().getContentAsString(), Employee.class);
        assertNotNull(outputList);
        Employee resEmp1 = outputList.get(0);
        assertEquals(1l, resEmp1.getId().longValue());
        assertEquals("name_test_1", resEmp1.getName());
        assertEquals("10000", resEmp1.getSalary());
        assertEquals("dep_test_1", resEmp1.getDepartment());
        Employee resEmp2 = outputList.get(1);
        assertEquals(2l, resEmp2.getId().longValue());
        assertEquals("name_test_2", resEmp2.getName());
        assertEquals("20000", resEmp2.getSalary());
        assertEquals("dep_test_2", resEmp2.getDepartment());
	}

    @Test
	public void testGetEmployee() throws Exception {
        // Define how EmployeeService works
        Employee emp1 = new Employee();
        emp1.setId(1l);
        emp1.setName("name_test");
        emp1.setSalary("80000");
        emp1.setDepartment("dep_test");
        Optional<Employee> opEmp = Optional.ofNullable(emp1); 
        when(empService.getEmployee(any(Long.class))).thenReturn(opEmp);

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .get("/api/v1/employees/1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		// verify that service method was called once
		verify(empService).getEmployee(any(Long.class));

        // Verify returned Employees
		Employee res = TestUtil.jsonToObject(result.getResponse().getContentAsString(), Employee.class);
		assertNotNull(res);
		assertEquals(1l, res.getId().longValue());
        assertEquals("name_test", res.getName());
        assertEquals("dep_test", res.getDepartment());
        assertEquals("80000", res.getSalary());
	}

	@Test
	public void testSaveEmployee() throws Exception {
        // Define how EmployeeService works
        Employee emp = new Employee();
        emp.setId(1l);
        emp.setName("name_test");
        emp.setSalary("80000");
        emp.setDepartment("dep_test");
        when(empService.saveEmployee(any(Employee.class))).thenReturn(emp);

		// prepare data and mock's behaviour
        Map<String, String> e = new HashMap<>();
        e.put("department", "dep_test");
        e.put("name", "name_test");
        e.put("salary", "80000");

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .post("/api/v1/employees/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(TestUtil.objectToJson(e))
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		// verify that service method was called once
		verify(empService).saveEmployee(any(Employee.class));

		Employee res = TestUtil.jsonToObject(result.getResponse().getContentAsString(), Employee.class);
		assertNotNull(res);
		assertEquals(1l, res.getId().longValue());
        assertEquals("name_test", res.getName());
        assertEquals("dep_test", res.getDepartment());
        assertEquals("80000", res.getSalary());
	}

    @Test
	public void testSaveEmployeeValidationFail() throws Exception {
        // Define how EmployeeService works
        Employee emp = new Employee();
        emp.setId(1l);
        emp.setName("name_test");
        emp.setSalary("80000");
        emp.setDepartment("dep_test");
        when(empService.saveEmployee(any(Employee.class))).thenReturn(emp);

		// prepare data and mock's behaviour
        Map<String, String> e = new HashMap<>();
        e.put("department", "dep_test");
        e.put("name", "name_test");
        e.put("salary", "salary_test");

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .post("/api/v1/employees/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(TestUtil.objectToJson(e))
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", HttpStatus.BAD_REQUEST.value(), status);

		String res = result.getResponse().getContentAsString();
        assertTrue(res.equals("{\"errors\":{\"salary\":\"Salary must be a number\"}}") 
            || res.equals("{\"errors\":{\"salary\":\"Salary must not be less than 0.\"}}"));
	}

    @Test
	public void testDeleteEmployee() throws Exception {
        // Define how EmployeeService works
        Employee emp1 = new Employee();
        emp1.setId(1l);
        emp1.setName("name_test");
        emp1.setSalary("80000");
        emp1.setDepartment("dep_test");
        Optional<Employee> opEmp = Optional.ofNullable(emp1); 
        when(empService.getEmployee(any(Long.class))).thenReturn(opEmp);

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .delete("/api/v1/employees/1")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", 200, status);

		// verify that service method was called once
		verify(empService).deleteEmployee(any(Long.class));

        // verify returned value
		String msg = result.getResponse().getContentAsString();
		assertEquals("{\"message\":\"Employee Deleted Successfully.\"}", msg);
	}

    @Test
	public void testUpdateEmployee() throws Exception {
        // Define how EmployeeService works
        Employee emp1 = new Employee();
        emp1.setId(1l);
        emp1.setName("name_test");
        emp1.setSalary("80000");
        emp1.setDepartment("dep_test");
        Optional<Employee> opEmp = Optional.ofNullable(emp1); 
        when(empService.getEmployee(any(Long.class))).thenReturn(opEmp);

		// prepare data and mock's behaviour
        Map<String, String> e = new HashMap<>();
        e.put("department", "dep_test");
        e.put("name", "name_test");
        e.put("salary", "80000");

		// execute
		MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                    .put("/api/v1/employees/1")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(TestUtil.objectToJson(e))
                )
                .andReturn();

		// verify
		int status = result.getResponse().getStatus();
		assertEquals("Incorrect Response Status", HttpStatus.OK.value(), status);

		// verify that service method was called once
		verify(empService).updateEmployee(any(Employee.class));

        // verify returned value
		String msg = result.getResponse().getContentAsString();
		assertEquals("{\"message\":\"Employee Updated Successfully.\"}", msg);

	}

}
