package com.nisum.mytime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nisum.mytime.exception.handler.MyTimeException;
import com.nisum.mytime.model.EmployeeVisa;
import com.nisum.mytime.model.TravelRequest;
import com.nisum.mytime.model.Visa;
import com.nisum.mytime.service.VisaService;

@RestController
@RequestMapping("/visa")
public class VisaController {

	@Autowired
	private VisaService visaService;

	@RequestMapping(value = "/getAllVisas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Visa>> getAllVisas() throws MyTimeException {
		List<Visa> visas = visaService.getAllVisas();
		return new ResponseEntity<>(visas, HttpStatus.OK);
	}

	@RequestMapping(value = "/getAllEmployeeVisas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<EmployeeVisa>> getAllEmployeeVisas() throws MyTimeException {
		List<EmployeeVisa> employeeVisas = visaService.getAllEmployeeVisas();
		return new ResponseEntity<>(employeeVisas, HttpStatus.OK);
	}

	@RequestMapping(value = "/addEemployeeVisa", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeVisa> addEemployeeVisa(@RequestBody EmployeeVisa employeeVisa)
			throws MyTimeException {
		EmployeeVisa visa = visaService.addEmployeeVisas(employeeVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}
 
	@RequestMapping(value = "/updateEemployeeVisa", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EmployeeVisa> updateEemployeeVisa(@RequestBody EmployeeVisa eVisa) throws MyTimeException {
		EmployeeVisa visa = visaService.updateEmployeeVisas(eVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteEemployeeVisa", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteEemployeeVisa(@RequestBody EmployeeVisa eVisa) throws MyTimeException {
		visaService.deleteEmployeeVisas(eVisa);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}
	@RequestMapping(value = "/getAllTravelRequests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TravelRequest>> getAllTravelRequests() throws MyTimeException {
		List<TravelRequest> employeeVisas = visaService.getAllTravels();
		return new ResponseEntity<>(employeeVisas, HttpStatus.OK);
	}

	@RequestMapping(value = "/addTravelRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TravelRequest> addTravelRequest(@RequestBody TravelRequest employeeVisa)
			throws MyTimeException {
		TravelRequest visa = visaService.addTravelRequest(employeeVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTravelRequest", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TravelRequest> updateTravelRequest(@RequestBody TravelRequest eVisa) throws MyTimeException {
		TravelRequest visa = visaService.updateTravelRequest(eVisa);
		return new ResponseEntity<>(visa, HttpStatus.OK);
	}

	@RequestMapping(value = "/deleteTravelRequest", method = RequestMethod.DELETE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> deleteTravelRequest(@RequestBody TravelRequest eVisa) throws MyTimeException {
		visaService.deleteEmployeeVisas(eVisa);
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

}