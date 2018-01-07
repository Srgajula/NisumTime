package com.nisum.mytime.controllertest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.mytime.controller.UserController;
import com.nisum.mytime.model.Designation;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Shift;
import com.nisum.mytime.service.UserService;

public class UserControllerTest {

	@Mock
	UserService userService;

	@InjectMocks
	UserController userController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testgetEmployeeRole() throws Exception {
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d", null, null,
				"user@nisum.com", null,null,null,null,null,null,null, null,new Date(2017-11-20),new Date(2017-12-23));
		System.out.println(employeesRole);
		when(userService.getEmployeesRole("user@nisum.com")).thenReturn(employeesRole);
		mockMvc.perform(get("/user/employee").param("emailId", "user@nisum.com"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeesRole("user@nisum.com");
	}


	@Test
	public void testassigingEmployeeRole() throws Exception {
		EmployeeRoles employeeRole = new EmployeeRoles("5976ef15874c902c98b8a05c", "16135", "Monika",
				"user1@nisum.com", "HR",null, "06:00-09:00","Java/J2EE","Spring","8767893452","5687234567","user1@gmail.com",new Date(2017-11-20),new Date(2017-12-23));
		System.out.println(employeeRole);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeRole);
		System.out.println(jsonString);
		when(userService.assigingEmployeeRole(anyObject())).thenReturn(employeeRole);
		mockMvc.perform(
				post("/user/assignEmployeeRole").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).assigingEmployeeRole(anyObject());
	}

	@Test
	public void testupdateEmployeeRole() throws Exception {
		EmployeeRoles employeeRole2 = new EmployeeRoles("5976ef15874c902c98b8a05d", "67890", "Sonika",
				"user2@nisum.com", "Manager",null, "09:00am-06:00am","php","Hibernate","9878678956","9989782210","user2@gmail.com",new Date(2017-11-20),new Date(2017-12-23));
		System.out.println(employeeRole2);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeeRole2);
		System.out.println(jsonString);
		when(userService.updateEmployeeRole(anyObject())).thenReturn(employeeRole2);
		mockMvc.perform(
				post("/user/updateEmployeeRole").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).updateEmployeeRole(anyObject());
	}

	@Test
	public void testdeleteEmployee() throws Exception {
		mockMvc.perform(delete("/user/deleteEmployee").param("empId", "16127"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).deleteEmployee("16127");
	}

	@Test
	public void testgetUserRoles() throws Exception {
		List<EmployeeRoles> employeesRole3 = CreateUserRoles();
		System.out.println(employeesRole3);
		when(userService.getEmployeeRoles()).thenReturn(employeesRole3);
		mockMvc.perform(get("/user/getUserRoles").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeeRoles();
	}

	@Test
	public void testgetEmployeeRoleData() throws Exception {
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d", "16127",null, null, null,
				null, null,null,null,null,null,null,new Date(2017-11-13),new Date(2017-12-20));
		System.out.println(employeesRole);
		when(userService.getEmployeesRoleData("16127")).thenReturn(employeesRole);
		mockMvc.perform(get("/user/getEmployeeRoleData").param("empId", "16127").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(jsonPath("$.employeeId", is("16127")))
				.andDo(print());
		verify(userService).getEmployeesRoleData("16127");
	}

	@Test
	public void testgetManagers() throws Exception {
		List<EmployeeRoles> employeesRole4 = CreateUserRoles();
		System.out.println(employeesRole4);
		when(userService.getEmployeeRoles()).thenReturn(employeesRole4);
		mockMvc.perform(get("/user/getManagers")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeeRoles();
	}

	@Test
	public void testgetAllShifts() throws Exception{
		List<Shift> shifts = CreateShifts();
		System.out.println(shifts);
		when(userService.getAllShifts()).thenReturn(shifts);
		mockMvc.perform(get("/user/getAllShifts")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getAllShifts();
	}
	
	@Test
	public void testgetAllDesignations() throws Exception{
		 List<Designation> designation = CreateDesignations();
		 System.out.println(designation);
		 when(userService.getAllDesignations()).thenReturn(designation);
		 mockMvc.perform(get("/user/getAllDesignations")).andExpect(MockMvcResultMatchers.status().isOk());
		 verify(userService).getAllDesignations();
	}
	
	private List<Designation> CreateDesignations() {
    List<Designation> data = new ArrayList<>();
		
        Designation desig1=new Designation();
        desig1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
        desig1.setDesignationId("01");
        desig1.setDesignationName("Manager");
        desig1.setActiveStatus("Active");
		data.add(desig1);
		
		Designation desig2=new Designation();
		desig2.setId(new ObjectId("2476ef15874c902c98b8a05d"));
		desig2.setDesignationId("02");
		desig2.setDesignationName("Employee");
		desig2.setActiveStatus("Active");
		data.add(desig2);
		return data;
	}

	private List<Shift> CreateShifts() {
		List<Shift> data = new ArrayList<>();
		
		Shift shift1=new Shift();
		shift1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
		shift1.setShiftId("01");
		shift1.setShiftName("Morning Shift");
		shift1.setActiveStatus("Active");
		data.add(shift1);
		
		Shift shift2=new Shift();
		shift2.setId(new ObjectId("2476ef15874c902c98b8a05d"));
		shift2.setShiftId("02");
		shift2.setShiftName("Night Shift");
		shift2.setActiveStatus("Active");
		data.add(shift2);
		return data;
	}

	private List<EmployeeRoles> CreateUserRoles() {
		List<EmployeeRoles> data = new ArrayList<>();

		EmployeeRoles data1 = new EmployeeRoles();
		data1.setId("3976ef15874c902c98b8a05d");
		data1.setEmployeeId("16101");
		data1.setEmployeeName("Reshma");
		data1.setEmailId("user1@nisum.com");
		data1.setRole("HR");
		data1.setShift("09:00-06:00");
		data1.setBaseTechnology("Spring");
		data1.setTechnologyKnown("Jmeter");
		data1.setMobileNumber("9978567809");
		data1.setAlternateMobileNumber("7789092345");
		data1.setPersonalEmailId("user1@gmail.com");
		data1.setCreatedOn(new Date(2017-11-21));
		data1.setLastModifiedOn(new Date(2017-12-22));
				
		EmployeeRoles data2 = new EmployeeRoles();
		data2.setId("4976ef15874c902c98b8a05d");
		data2.setEmployeeId("16102");
		data2.setEmployeeName("Sasmita");
		data2.setEmailId("user2@nisum.com");
		data2.setRole("Manager");
		data2.setShift("03:00-12:00");
		data2.setBaseTechnology("Hibernate");
		data2.setTechnologyKnown("EJB");
		data2.setMobileNumber("9989087671");
		data2.setAlternateMobileNumber("9999786756");
		data2.setPersonalEmailId("user2@gmail.com");
		data2.setCreatedOn(new Date(2017-11-23));
		data2.setLastModifiedOn(new Date(2017-12-22));

		data.add(data1);
		data.add(data2);

		return data;
	}
}
