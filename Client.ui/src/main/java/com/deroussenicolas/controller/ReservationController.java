package com.deroussenicolas.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.deroussenicolas.beans.BookBean;
import com.deroussenicolas.beans.CopyBean;
import com.deroussenicolas.beans.ReservationBean;
import com.deroussenicolas.proxies.MicroserviceBookProxy;
import com.deroussenicolas.proxies.MicroserviceCopyProxy;
import com.deroussenicolas.proxies.MicroserviceReservationProxy;
@Controller
@SessionAttributes("userEmail")
public class ReservationController {

	@Autowired
	private MicroserviceCopyProxy microServiceCopyProxy;
	@Autowired
	private MicroserviceBookProxy microServiceBookProxy;
	@Autowired
	private MicroserviceReservationProxy microserviceReservationProxy;

	
	
	@GetMapping("/extendReservation")
	public ModelAndView extendReservation(@SessionAttribute("userEmail") String userEmail, ModelAndView modelAndView, 
			@RequestParam(name="id") int reservation_id) {
		ModelAndView modelView = new ModelAndView();
		modelView.setViewName("errors/reservation_error");	
		try {
			CopyBean copyBean = microServiceCopyProxy.oneCopyOfReservationWithReservationId(reservation_id);
			Boolean confirmationUserIsCorrect = false;
			if(copyBean != null) {
				confirmationUserIsCorrect = microserviceReservationProxy.confirmReservationWithReservationIdAndUserEmail(reservation_id,userEmail);
			}	
			if(copyBean.getStatus() == '1' && confirmationUserIsCorrect == true) {
				ReservationBean reservationBean = microserviceReservationProxy.extendReservationWithId(reservation_id);
				modelView.addObject("date", "Date de début : " + reservationBean.getDate_begin() + " || Date de fin : " + reservationBean.getDate_end());
				modelView.setViewName("private/extendReservation");	
			}			
		} catch (Exception e) {
			System.err.println("error" + e);
		}
		return modelView;	
	}
	
	@GetMapping("/showAllReservation")
	public ModelAndView showAllReservation(@SessionAttribute("userEmail") String userEmail) {
		ModelAndView modelView = new ModelAndView();
		if(userEmail == null) {
			modelView.setViewName("errors/access_denied");
			return modelView;
		}
		List<ReservationBean> reservationBeans = microserviceReservationProxy.reservationWithUserEmail(userEmail);
		List<BookBean> bookBeanList = microServiceBookProxy.allBookWithUserEmail(userEmail);
		List<CopyBean> copyBeanList = microServiceCopyProxy.allCopiesWithUserEmail(userEmail);
		List<Boolean> statusList = new ArrayList<>();
		for (int i = 0 ; i < reservationBeans.size() ; i++) {
			reservationBeans.get(i).setBook_name(bookBeanList.get(i).getBook_name());
			if(copyBeanList.get(i).getStatus() == '1') {
				statusList.add(i, true);
			}
			else {
				statusList.add(i, false);
			}
		}	
		modelView.addObject("reservationlist", reservationBeans);
		modelView.addObject("statusList", statusList);
		modelView.setViewName("private/reservationlist");
		return modelView;	
	}
}
