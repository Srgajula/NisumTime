/**
 * 
 */
package com.nisum.mytime.service;

import java.util.List;

import com.nisum.mytime.model.EmployeeVisa;
import com.nisum.mytime.model.TravelRequest;
import com.nisum.mytime.model.Visa;

/**
 * @author nisum
 *
 */
public interface VisaService {

	List<Visa> getAllVisas();

	List<EmployeeVisa> getAllEmployeeVisas();

	EmployeeVisa addEmployeeVisas(EmployeeVisa e);

	EmployeeVisa updateEmployeeVisas(EmployeeVisa e);

	void deleteEmployeeVisas(EmployeeVisa e);

	List<TravelRequest> getAllTravels();

	TravelRequest addTravelRequest(TravelRequest e);

	TravelRequest updateTravelRequest(TravelRequest e);

	void deleteEmployeeVisas(TravelRequest e);
}
