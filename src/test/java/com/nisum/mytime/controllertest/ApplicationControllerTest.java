package com.nisum.mytime.controllertest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nisum.mytime.controller.ApplicationController;

public class ApplicationControllerTest {

	@InjectMocks
	ApplicationController applicationController;

	private MockMvc mockMvc; 

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
	}

	@Test
	public void testLogin() throws Exception {
		mockMvc.perform(get("/")).andExpect(MockMvcResultMatchers.forwardedUrl("templates/index"));	
	}

}
