package poker;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import core.Hand;
import lombok.Data;


/*
 * curl http://localhost:8080/poka/scores -X POST -H "Content-Type: application/json" -d '{"cards": ["S1 S2 S3 S4 S5"]}'
 */
@RestController
public class PokerController {

	@Autowired
	PokerService pokerService;

	@RequestMapping(path="/poker/hand", method=RequestMethod.POST)
	public PokerResponseDao getPokerRole(@RequestBody PokerRequestDao request) throws Exception {
		Hand hand = pokerService.getHand(request.cards);
		return new PokerResponseDao(hand.getCardsAsString(), hand.getName().getName());
	}

	@RequestMapping(path="/poker/hands", method=RequestMethod.POST)
	public PokerListResponseDao getPokerRole(@RequestBody PokerListRequestDao request) throws Exception {
		PokerListResponseDao response = new PokerListResponseDao();
		
		List<Hand> handList = new ArrayList<>();
		for( PokerRequestDao handSrc : request.hands) {
			handList.add(Hand.create(handSrc.cards));
		}
		
		handList = pokerService.getHands(handList);

		response.result = new ArrayList<>();
		for(Hand h : handList) {
			response.result.add(new CardDao(h.getCardsAsString(), h.getName().getName(), false));			
		}
		if(handList.size() == 1) {
			response.result.get(0).best = true;
		}
		else if(handList.size() > 1) {
			if(handList.get(0).getScore() != handList.get(1).getScore()) {
				response.result.get(0).best = true;
			}
		}
		return response;
	}
}

@Data
class PokerRequestDao {
	String cards;
	public PokerRequestDao() {
		
	}
}

@Data
class PokerListRequestDao {
	List<PokerRequestDao> hands;
	public PokerListRequestDao() {
		
	}
}

@Data 
class PokerResponseDao {
	String cards;
	String hand;

	public PokerResponseDao() {
	}

	public PokerResponseDao(String cards, String hand) {
		this.hand = hand;
		this.cards = cards;
	}
}


@Data
class CardDao {
	String card;
	String hand;
	boolean best;
	public CardDao() {;}
	public CardDao(String card, String hand, boolean best) {
		this.card = card;
		this.best = best;
		this.hand = hand;
	}
}

@Data 
class PokerListResponseDao {

	List<CardDao> result; 

	public PokerListResponseDao() {
	}
	
}

@Data
class ErrorResponseDao {
	int code = 3;
	String description = "xxx";
	public ErrorResponseDao() {}
}

@ControllerAdvice
class EmployeeNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code=HttpStatus.BAD_REQUEST)
	ErrorResponseDao employeeNotFoundHandler(Exception ex) {
		return new ErrorResponseDao();
	}
}