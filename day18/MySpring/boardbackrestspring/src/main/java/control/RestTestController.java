package control;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.my.exception.ModifyException;
import com.my.service.RepBoardService;
import com.my.vo.RepBoard;

import lombok.extern.log4j.Log4j;

@RestController
@Log4j
public class RestTestController {
	
	@Autowired
	private RepBoardService service;
	@PostMapping("/reqjson")
	public RepBoard one(@RequestBody RepBoard b) {
		log.info(b.getBoard_title());
		return b;
//		return data;
	}
//	@PutMapping(value="/{board_no}")//, consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
//	public RepBoard put(
//			@PathVariable int board_no,
//			@RequestBody RepBoard b) throws ModifyException {
//		return b;
//	}
}
