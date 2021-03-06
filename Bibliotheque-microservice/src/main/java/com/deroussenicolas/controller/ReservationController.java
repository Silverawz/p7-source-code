package com.deroussenicolas.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.deroussenicolas.entities.Copy;
import com.deroussenicolas.entities.Reservation;
import com.deroussenicolas.service.CopyService;
import com.deroussenicolas.service.ReservationService;
import com.deroussenicolas.service.UserService;

@RestController
public class ReservationController {

	
	@Autowired
	private CopyService copyService;
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private UserService userService;
	
	@GetMapping(value="/Reservations")
    public @ResponseBody List<Reservation> listOfAllReservation() {
		List<Reservation> listReservation = reservationService.findAll();
        return listReservation;
    }

	@GetMapping(value="/ReservationsNotArchived")
    public @ResponseBody List<Reservation> listOfAllReservationNotArchived() {
		List<Reservation> listReservation = reservationService.reservationListNotArchived(false);
        return listReservation;
    }
	
    @GetMapping(value = "/Reservation/{id}")
    public Reservation oneReservationWithId(@PathVariable int id) {
    	return reservationService.findById(id);
    }
    
    @GetMapping(value = "/ConfirmReservation/{id}/{email}")
    public Boolean confirmReservationWithReservationIdAndUserEmail(@PathVariable int id, @PathVariable String email) {
    	boolean result;
    	if(reservationService.findById(id).getUser().getEmail().equals(email)) {
    		result = true;
    	}
    	else {
    		result = false;
    	}
    	return result;
    }
     
    @GetMapping(value = "/ExtendReservation/{id}")
    public Reservation extendReservationWithId(@PathVariable int id) {
    	Reservation reservation = reservationService.findById(id);
    	Copy copyOfTheReservation = copyService.findById(reservation.getCopy().getId_copy());
    	char status = copyOfTheReservation.getStatus();   
    	if(status == '1') {
    		reservation = reservationService.saveExtendReservation(id);
    		copyOfTheReservation.setStatus('2');
    		copyService.save(copyOfTheReservation);
    	}
    	else {
    		reservation = reservationService.findById(id);
    	}
    	return reservation;
    }
    
    @GetMapping(value = "/ReservationUser/{id}")
    public List<Reservation> reservationWithUserId(@PathVariable int id) {
    	List<Reservation> reservationOfUser = reservationService.reservationListOfUser(id);
    	return reservationOfUser;
    }
    
    @GetMapping(value = "/ReservationUserWithEmail/{email}")
    public List<Reservation> reservationWithUserEmail(@PathVariable String email) {
    	List<Reservation> reservationOfUser = reservationService.reservationListOfUser(userService.findByEmail(email).getId_user());
    	return reservationOfUser;
    }
    
    
  
}
