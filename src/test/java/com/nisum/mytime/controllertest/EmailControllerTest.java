package com.nisum.mytime.controllertest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.nisum.mytime.controller.EmailController;
import com.nisum.mytime.model.EmailDomain;
import com.nisum.mytime.service.MailService;

public class EmailControllerTest {

	@Mock
	MailService mailService;

	@InjectMocks
	EmailController emailController;

	private MockMvc mockMvc; 
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
	}
	
	@Test
	public void testsendAttachmentMail_success() throws Exception{
		EmailDomain emailObj = new EmailDomain("12345","2017-11-18","2017-12-18",
				               new String[]{"to@nisum.com"},new String[]{"cc@nisum.com"},new String[]{});
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(emailObj);
		when(mailService.sendEmailWithAttachment(any())).thenReturn("Success");
		mockMvc.perform(post("/sendEmail").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(jsonString)).andExpect(MockMvcResultMatchers.status().isOk());		
	}
	
	@Test
	public void testsendAttachmentMail_failure() throws Exception{
		EmailDomain emailObj = new EmailDomain("1234567890","2017-11-18","2017-12-18",
	               new String[]{"to@nisum.com"},new String[]{"cc@nisum.com"},new String[]{});
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(emailObj);
		System.out.println(jsonString);
		when(mailService.sendEmailWithAttachment(any())).thenReturn("Failure");
		mockMvc.perform(post("/sendEmail")).andExpect(MockMvcResultMatchers.status().is4xxClientError());	
	}
	
	
	@Test
	public void testdeletePdfReport() throws Exception{
		when(mailService.deletePdfReport("eTimeTrackLite1")).thenReturn("Success");
		mockMvc.perform(get("/deleteReport/eTimeTrackLite1")).andExpect(MockMvcResultMatchers.status().isOk());
		verify(mailService).deletePdfReport("eTimeTrackLite1");
	}
	
}
