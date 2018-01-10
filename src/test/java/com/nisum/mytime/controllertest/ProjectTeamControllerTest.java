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
import com.nisum.mytime.controller.ProjectTeamController;
import com.nisum.mytime.model.EmployeeRoles;
import com.nisum.mytime.model.Project;
import com.nisum.mytime.model.ProjectTeamMate;
import com.nisum.mytime.model.TeamMateBilling;
import com.nisum.mytime.service.ProjectService;
import com.nisum.mytime.service.UserService;

public class ProjectTeamControllerTest {

	@Mock
	UserService userService;

	@Mock
	ProjectService projectService;

	@InjectMocks
	ProjectTeamController projectTeamController;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(projectTeamController).build();
	}

	@Test
	public void testgetEmployeeRole() throws Exception {
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d", null, null, null, null, null, null,
				null, null, "user@nisum.com", null, null, new Date(2017 - 11 - 20), new Date(2107 - 12 - 23));
		when(userService.getEmployeesRole("user@nisum.com")).thenReturn(employeesRole);
		mockMvc.perform(get("/projectTeam/employee").param("emailId", "user@nisum.com"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeesRole("user@nisum.com");
	}

	@Test
	public void testaddProject() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("16101");
		list.add("16102");
		list.add("16103");
		Project employeeRole1 = new Project(new ObjectId("9976ef15874c902c98b8a05d"), "102", "OMS", "16101", "Srikanth",
				"Gap", "Billable", list);
		String jsonvalue = (new ObjectMapper()).writeValueAsString(employeeRole1).toString();
		when(projectService.addProject(employeeRole1)).thenReturn(employeeRole1);
		mockMvc.perform(
				post("/projectTeam/addProject").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvalue))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testupdateEmployeeRole() throws Exception {
		EmployeeRoles employeesRoles2 = new EmployeeRoles("1976ef15874c902c98b8a05d", "16111", "Vinay Singh",
				"vsingh@nisum.com", "Manager", null, "09:00-06:00", "Java/J2EE", "Testing", "8755672341", "8800543678",
				"vsingh@gmail.com", new Date(2017 - 11 - 29), new Date(2017 - 12 - 20));
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(employeesRoles2);
		when(userService.updateEmployeeRole(any())).thenReturn(employeesRoles2);
		mockMvc.perform(post("/projectTeam/updateEmployeeRole").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(jsonString)).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).updateEmployeeRole(any());
	}

	@Test
	public void testdeleteEmployee() throws Exception {
		mockMvc.perform(delete("/projectTeam/deleteEmployee").param("empId", "16157"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).deleteEmployee("16157");
	}

	@Test
	public void testgetEmployeeRoleData() throws Exception {
		EmployeeRoles employeesRole = new EmployeeRoles("5976ef15874c902c98b8a05d", "16127", null, null, null, null,
				null, null, null, null, null, null, new Date(2017 - 11 - 20), new Date(2107 - 12 - 23));
		when(userService.getEmployeesRoleData("16127")).thenReturn(employeesRole);
		mockMvc.perform(get("/projectTeam/getEmployeeRoleData").param("empId", "16127")
				.contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
		verify(userService).getEmployeesRoleData("16127");
	}

	@Test
	public void testgetManagers() throws Exception {
		List<EmployeeRoles> employeesRoles = createEmployeeRoles();
		when(userService.getEmployeeRoles()).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getEmployeesToTeam")).andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testgetTeamDetails() throws Exception {
		List<ProjectTeamMate> employeesRoles = createProjectTeamMate();
		when(projectService.getTeamDetails("16127")).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getTeamDetails").param("employeeId", "16127"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getTeamDetails("16127");
	}

	@Test
	public void testaddEmployeeToTeam() throws Exception {
		ProjectTeamMate projectTeamMate = new ProjectTeamMate(new ObjectId("1976ef15874c902c98b8a05d"), "16127",
				"Monika Srivastava", "msrivastava@nisum.com", "Employee", "09:00-06:00", "101", "MOSAIC", "GAP",
				"16081", "Rajeshekar", "01", "Software Engineer", "Non-Billable", "8765588388",new Date(),new Date(), true);
		String jsonvalue = (new ObjectMapper()).writeValueAsString(projectTeamMate).toString();
		when(projectService.addProjectTeamMate(projectTeamMate)).thenReturn(projectTeamMate);
		mockMvc.perform(
				post("/projectTeam/addEmployeeToTeam").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvalue))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testupdateTeammate() throws Exception {
		ProjectTeamMate updatedTeammate = new ProjectTeamMate(new ObjectId("1976ef15874c902c98b8a05d"), "16127",
				"Monika Srivastava", "msrivastava@nisum.com", "Employee", "09:00-06:00", "101", "MOSAIC", "GAP",
				"16081", "Rajeshekar", "01", "Software Engineer", "Non-Billable", "8765588388",new Date(),new Date(), true);
		String jsonvalue = (new ObjectMapper()).writeValueAsString(updatedTeammate).toString();
		when(projectService.updateTeammate(updatedTeammate)).thenReturn(updatedTeammate);
		mockMvc.perform(
				post("/projectTeam/updateTeammate").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvalue))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	public void testdeleteTeammate() throws Exception {
		ProjectTeamMate deleteTeamMate = new ProjectTeamMate(new ObjectId("1976ef15874c902c98b8a05d"), "16127",
				"Monika Srivastava", "msrivastava@nisum.com", "Employee", "09:00-06:00", "101", "MOSAIC", "GAP",
				"16081", "Rajeshekar", "01", "Software Engineer", "Non-Billable", "8765588388",new Date(),new Date(), true);
		String jsonvalue = (new ObjectMapper()).writeValueAsString(deleteTeamMate).toString();
		mockMvc.perform(
				post("/projectTeam/deleteTeammate").contentType(MediaType.APPLICATION_JSON_VALUE).content(jsonvalue))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).deleteTeammate("16127", "101", new ObjectId("1976ef15874c902c98b8a05d"));
	}

	@Test
	public void testgetProjects() throws Exception {
		List<Project> projects = CreateProjectDetails();
		when(projectService.getProjects("16127")).thenReturn(projects);
		mockMvc.perform(get("/projectTeam/getProjects").param("employeeId", "16127"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getProjects("16127");
	}

	@Test
	public void testgetMyTeamDetails() throws Exception {
		List<ProjectTeamMate> employeesRoles = createProjectTeamMate();
		when(projectService.getMyTeamDetails("16127")).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getMyTeamDetails").param("employeeId", "16127"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getMyTeamDetails("16127");
	}

	@Test
	public void testgetUnAssignedEmployees() throws Exception {
		List<EmployeeRoles> employeesRoles = createEmployeeRoles();
		when(projectService.getUnAssignedEmployees()).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getUnAssignedEmployees")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getUnAssignedEmployees();
	}

	@Test
	public void testgetShiftDetails() throws Exception {
		List<ProjectTeamMate> employeesRoles = createProjectTeamMate();
		when(projectService.getShiftDetails("09:00-06:00")).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getShiftDetails").param("shift", "09:00-06:00"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getShiftDetails("09:00-06:00");
	}

	@Test
	public void testgetProjectAllocations() throws Exception {
		List<ProjectTeamMate> employeesRoles = createProjectTeamMate();
		when(projectService.getAllProjectDetails()).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getProjectAllocations")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getAllProjectDetails();
	}

	@Test
	public void testgetProjectDetails() throws Exception {
		List<ProjectTeamMate> employeesRoles = createProjectTeamMate();
		when(projectService.getProjectDetails("101")).thenReturn(employeesRoles);
		mockMvc.perform(get("/projectTeam/getProjectDetails").param("projectId", "101"))
				.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getProjectDetails("101");
	}
	
	@Test
	public void testgetMyProjectAllocations() throws Exception{
		List<ProjectTeamMate> projectAllocations=createProjectTeamMate();
		System.out.println(projectAllocations);
		when(projectService.getMyProjectAllocations("16127")).thenReturn(projectAllocations);
		mockMvc.perform(get("/projectTeam/getMyProjectAllocations").param("employeeId", "16127")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getMyProjectAllocations("16127");
	}

	@Test
	public void testgetEmployeeBillingDetails() throws Exception{
		List<TeamMateBilling> billings=CreateTeamMateBilling();	
		System.out.println(billings);
		when(projectService.getEmployeeBillingDetails("16127", "101")).thenReturn(billings);
		mockMvc.perform(get("/projectTeam/getEmployeeBillingDetails").param("employeeId", "16127").param("projectId", "101"))
		.andExpect(MockMvcResultMatchers.status().isOk());
		verify(projectService).getEmployeeBillingDetails("16127", "101");
	}
	
	private List<TeamMateBilling> CreateTeamMateBilling() {
		List<TeamMateBilling> data = new ArrayList<>();

		TeamMateBilling team1 = new TeamMateBilling();
		team1.setId(new ObjectId("5976ef15874c902c98b8a05d"));
		team1.setEmployeeId("16127");
		team1.setEmployeeName("Employee1");
		team1.setProjectId("101");
		team1.setProjectName("MOSAIC");
		team1.setBillingStartDate(new Date(2017-01-10));
		team1.setBillingEndDate(new Date(2017-02-10));
		team1.setActive(true);
		data.add(team1);

		TeamMateBilling team2 = new TeamMateBilling();
		team2.setId(new ObjectId("1976ef15874c902c98b8a05d"));
		team2.setEmployeeId("16128");
		team2.setEmployeeName("Employee2");
		team2.setProjectId("102");
		team2.setProjectName("OMS");
		team2.setBillingStartDate(new Date(2017-01-15));
		team2.setBillingEndDate(new Date(2017-02-15));
		team2.setActive(false);
		data.add(team2);

		return data;
		
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
		data1.setAccount("Gap");
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
		data2.setStatus("Billable");
		data2.setAccount("Macys");
		List<String> lists = new ArrayList<>();
		lists.add("16104");
		lists.add("16105");
		lists.add("16106");
		data2.setEmployeeIds(lists);

		data.add(data1);
		data.add(data2);

		return data;
	}

	private List<ProjectTeamMate> createProjectTeamMate() {
		List<ProjectTeamMate> data = new ArrayList<>();

		ProjectTeamMate record1 = new ProjectTeamMate();
		record1.setId(new ObjectId("3976ef15874c902c98b8a05d"));
		record1.setEmployeeId("16127");
		record1.setEmployeeName("Monika Srivastava");
		record1.setEmailId("msrivastava@nisum.com");
		record1.setRole("Employee");
		record1.setShift("09:00-06:00");
		record1.setProjectId("101");
		record1.setProjectName("Mosaic");
		record1.setAccount("Gap");
		record1.setManagerId("16081");
		record1.setManagerName("Rajeshekar");
		record1.setExperience("01 Year");
		record1.setDesignation("Software Engineer");
		record1.setBillableStatus("Non-Billable");
		record1.setMobileNumber("8765588388");

		ProjectTeamMate record2 = new ProjectTeamMate();
		record2.setId(new ObjectId("2976ef15874c902c98b8a05d"));
		record2.setEmployeeId("16111");
		record2.setEmployeeName("Vinay Singh");
		record2.setEmailId("vsingh@nisum.com");
		record2.setRole("Employee");
		record2.setShift("09:00-06:00");
		record2.setProjectId("101");
		record2.setProjectName("Mosaic");
		record2.setAccount("Gap");
		record2.setManagerId("16081");
		record2.setManagerName("Rajeshekar");
		record2.setExperience("07 Year");
		record2.setDesignation("Senoir Software Engineer");
		record2.setBillableStatus("Billable");
		record2.setMobileNumber("8765588399");

		data.add(record1);
		data.add(record2);

		return data;
	}

	private List<EmployeeRoles> createEmployeeRoles() {
		List<EmployeeRoles> data = new ArrayList<>();

		EmployeeRoles record1 = new EmployeeRoles();
		record1.setId("5976ef15874c902c98b8a05d");
		record1.setEmployeeId("16127");
		record1.setEmployeeName("Monika Srivastava");
		record1.setEmailId("msrivastava@nisum.com");
		record1.setRole("Employee");
		record1.setShift("09:00-06:00");
		record1.setBaseTechnology("Spring");
		record1.setTechnologyKnown("Java");
		record1.setMobileNumber("9900786755");
		record1.setAlternateMobileNumber("7689009876");
		record1.setPersonalEmailId("msrivastava@gmail.com");
		record1.setCreatedOn(new Date(2017 - 11 - 18));
		record1.setLastModifiedOn(new Date(2017 - 12 - 13));

		EmployeeRoles record2 = new EmployeeRoles();
		record2.setId("8976ef15874c902c98b8a05d");
		record2.setEmployeeId("16081");
		record2.setEmployeeName("Rajeshekar");
		record2.setEmailId("manager@nisum.com");
		record2.setRole("Manager");
		record2.setShift("03:00-12:00");
		record2.setBaseTechnology("Hibernate");
		record2.setTechnologyKnown("EJB");
		record2.setMobileNumber("9109897867");
		record2.setAlternateMobileNumber("9129098767");
		record2.setPersonalEmailId("manager@gmail.com");
		record2.setCreatedOn(new Date(2017 - 11 - 18));
		record2.setLastModifiedOn(new Date(2017 - 12 - 21));

		data.add(record1);
		data.add(record2);
		return data;
	}
}
