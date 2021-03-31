package com.my.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RepBoardControllerAdvice {
	@ExceptionHandler
	public String exept(Exception e, Model model) {
		model.addAttribute("exception", e);
		e.printStackTrace();
		return "board/error";
	}
}
