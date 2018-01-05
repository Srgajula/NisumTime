package com.nisum.mytime.controllertest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.nisum.mytime.controller.ProjectController;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.service.ProjectService;
import com.nisum.mytime.service.UserService;

public class ProjectControllerTest {

	@Mock
	UserService userService;
	
	@Mock
	ProjectService projectService;

	@InjectMocks
	ProjectController projectController;

	private MockMvc mockMvc; 
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
	}
	

	@Test
	public void testgetEmployeeRole() throws Exception{
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d",null,null,"user@nisum.com", null,null,null,null,null,null,null, new Date(2017-11-12),new Date(2017-12-12));
		System.out.println(employeesRole);
		when(userService.getEmployeesRole("user@nisum.com")).thenReturn(employeesRole);
		mockMvc.perform(get("/project/employee").param("emailId", "user@nisum.com")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeesRole("user@nisum.com");		
	}
	
	
	@Test
	public void testaddProject() throws Exception{
		List<String> list = new ArrayList<>();
		list.add("16101");
		list.add("16102");
		list.add("16103");
		Project employeeRole1 = new Project(new ObjectId ("9976ef15874c902c98b8a05d"),"102","OMS","16101","Srikanth","Macys","Billable",list);
		System.out.println(employeeRole1);
		String jsonvalue =(new ObjectMapper()).writeValueAsString(employeeRole1).toString();
		System.out.println(jsonvalue);
		when(projectService.addProject(employeeRole1)).thenReturn(employeeRole1);
		mockMvc.perform(post("/project/addProject").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvalue)).andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void testupdateEmployeeRole() throws Exception{
		List<String> list1 = new ArrayList<>();
		list1.add("16104");
		list1.add("16105");
		list1.add("16106");
		Project project = new Project(new ObjectId ("5976ef15874c902c98b8a05d"),"101","MOSAIC","16100","Rajeshekar","Gap","Billable",list1);
		System.out.println(project);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(project);
		System.out.println(jsonString);
		when(projectService.updateProject(any())).thenReturn(project);
		mockMvc.perform(post("/project/updateProject").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonString)).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).updateProject(any());
	}
	
	@Test
	public void testdeleteProject() throws Exception{
		mockMvc.perform(delete("/project/deleteProject").param("projectId", "101")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).deleteProject("101");
	}
	
	@Test
	public void testgetProjects() throws Exception{
		List<Project> projects = CreateProjectDetails();
		System.out.println(projects.toString());
		when(projectService.getProjects()).thenReturn(projects);
		mockMvc.perform(get("/project/getProjects").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getProjects();
	}
	
	
	@Test
	public void testgetEmployeeRoleData() throws Exception{
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d","16127",null,null,null, null,null,null,null,null,null,new Date(2017-11-18),new Date(2017-12-18));
		System.out.println(employeesRole);
		when(userService.getEmployeesRoleData("16127")).thenReturn(employeesRole);
		mockMvc.perform(get("/project/getEmployeeRoleData").param("empId", "16127").contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeesRoleData("16127");		
	}
	

	private List<Project> CreateProjectDetails() {
		List<Project> data = new ArrayList<>();
		
		Project data1 = new Project();
		data1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
		data1.setProjectId("101");
		data1.setProjectName("MOSAIC");
		data1.setManagerId("16110");
		data1.setManagerName("Rajeshekar");
		data1.setStatus("Billable");
		data1.setAccount("GAP");
		List<String> list = new ArrayList<>();
		list.add("16101");
		list.add("16102");
		list.add("16103");
		data1.setEmployeeIds(list);
		
		
		Project data2 = new Project();
		data2.setId(new ObjectId("9976ef15874c902c98b8a05d"));
		data2.setProjectId("102");
		data2.setProjectName("OMS");
		data2.setManagerId("16111");
		data2.setManagerName("Reshma");
		data2.setStatus("Non-Billable");
		data2.setAccount("MACYS");
		List<String> lists = new ArrayList<>();
		lists.add("16104");
		lists.add("16105");
		lists.add("16106");
		data2.setEmployeeIds(lists);
		
		data.add(data1);
		data.add(data2);
		
		return data;
	}
}
