package com.twilio;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import com.twilio.sdk.verbs.*;

@WebServlet(
	name = "CyoaServlet", 
	urlPatterns = {"/cyoa"}
)
public class CyoaServlet extends HttpServlet {
	
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		String cyoaPrevPos = (String)session.getAttribute("cyoaPrevPos");
		
		String choice = request.getParameter("Digits");
		TwiMLResponse twiml = new TwiMLResponse();
		
		if (cyoaPrevPos == null) {
			cyoaPrevPos = "0";
		}
		if(choice == null) {
			choice = "";
		}
		
		String step = cyoaPrevPos + choice;
		
		String stepType = cyoaOptions.get(step + ".type");
		String stepMsg = cyoaOptions.get(step + ".msg");
		
		session.setAttribute("cyoaPrevPos", step);
		
		// Tree element that has options to continue with
		if (stepType == "continue") {
			Gather gather = new Gather();
			gather.setAction("/cyoa");
			gather.setTimeout(10);
			gather.setNumDigits(1);
			gather.setMethod("POST");
			Say sayInGather = new Say(stepMsg);
			try {
				gather.append(sayInGather);
				twiml.append(gather);
			} catch (TwiMLException e) {
				e.printStackTrace();
			}
		} 
		// An end to the story
		else if (stepType == "endBad") {
			session.removeAttribute("cyoaPrevPos");
			Say say = new Say(stepMsg);
			try {
				twiml.append(say);
				twiml.append(new Redirect("/ivr"));
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		}
		// A victorious ending
		else if (stepType == "endGood") {
			Play music = new Play("/resources/Victory.mp3");
			session.removeAttribute("cyoaPrevPos");
			Say say = new Say(stepMsg);
			try {
				twiml.append(say);
				twiml.append(music);
				twiml.append(new Redirect("/ivr"));
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
			
		}
		// To jump from inside of one storyline to another
		else if (stepType != null && stepType.startsWith("redirect")) {
			String newStep = (stepType.split(","))[1];
			session.setAttribute("cyoaPrevPos", newStep);
            Say say = new Say(stepMsg);
			try {
				twiml.append(say);
				twiml.append(new Redirect("/cyoa"));
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		} 
		// Watch out for invalid options
		else {
			Say say = new Say("You've stepped off the adventure path and into the nether. You should be a little more careful next time.");
			session.removeAttribute("cyoaPrevPos");
			try {
				twiml.append(say);
				twiml.append(new Redirect("/ivr"));
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		}		
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());
    }
	
	// Local hardcoded story data
	public final static HashMap<String, String> cyoaOptions = new HashMap<String, String>();
	static
	{
		cyoaOptions.put("0.msg", "You're in the Twilio offices, where would you like to go? Press 1 to head into the lounge. Press 2 to explore the nearby woods. Press 3 to visit the creek. Press 4 to hire Cris Tarr.");
		cyoaOptions.put("0.type", "continue");
		cyoaOptions.put("01.msg", "What a comfy lounge! Press 1 to watch TV. Press 2 to take a nap. Press 3 to check out the computer.");
		cyoaOptions.put("01.type", "continue");
		cyoaOptions.put("011.msg", "Well, Wheel of Fortune is on. Press 1 to change the channel. Press 2 to keep watching. Press 3 to turn off TV and look over Cris's qualifications");
		cyoaOptions.put("011.type", "continue");
		cyoaOptions.put("0111.msg", "Now Star Trek is on. Press 1 to stay on the channel. Press 2 to change the channel again. Press 3 if you're starting the get bored.");
		cyoaOptions.put("0111.type", "continue");
		cyoaOptions.put("01111.msg", "You've found happiness. Enjoy the rest of your day.");
		cyoaOptions.put("01111.type", "endGood");
		cyoaOptions.put("01112.msg", "The TV explodes, and you die");
		cyoaOptions.put("01112.type", "endBad");
		cyoaOptions.put("01113.msg", "You've fallen asleep.");
		cyoaOptions.put("01113.type", "redirect,012");
		cyoaOptions.put("0112.msg", "Episode by episode, you fade into a vegetable state.");
		cyoaOptions.put("0112.type", "endBad");
		cyoaOptions.put("0113.msg", "Wow, this guy looks great. He came really well prepared. Press 1 to hire Cris. Press 2 not to hire Cris");
		cyoaOptions.put("0113.type", "continue");
		cyoaOptions.put("01131.msg", "You live long and prosper.");
		cyoaOptions.put("01131.type", "endGood");
		cyoaOptions.put("01132.msg", "The world ends.");
		cyoaOptions.put("01132.type", "endBad");
		cyoaOptions.put("012.msg", "Would you consider yourself a light or heavy sleeper? Press 1 for light. Press 2 for heavy.");
		cyoaOptions.put("012.type", "continue");
		cyoaOptions.put("0121.msg", "You wake suddenly. Press 1 if you're still tired. Press 2 if you want to look around.");
		cyoaOptions.put("0121.type", "continue");
		cyoaOptions.put("01211.msg", "You fall back asleep. Inception sound. Suddenly, you're awake.");
		cyoaOptions.put("01211.type", "redirect,02");
		cyoaOptions.put("01212.msg", "You get up, you go out, and it was earth all along. Unfortunately you get killed by apes.");
		cyoaOptions.put("01212.type", "endBad");
		cyoaOptions.put("0122.msg", "You never wake up from your slumber and you stay in the Matrix forever.");
		cyoaOptions.put("0122.type", "redirect,0");
		cyoaOptions.put("013.msg", "You boot up the computer. Press 1 to browse the internet. Press 2 to play an addicting game.");
		cyoaOptions.put("013.type", "continue");
		cyoaOptions.put("0131.msg", "Ah yes, the internet. Press 1 to look at Cris's qualifications. Press 2 to look at cats.");
		cyoaOptions.put("0131.type", "continue");
		cyoaOptions.put("01311.msg", "Woah, this guy looks like a great hire! Press 1 to hire Cris. Press 2 to make the silly mistake of not hiring Cris.");
		cyoaOptions.put("01311.type", "continue");
		cyoaOptions.put("013111.msg", "After hiring Cris all your dreams come true and you go on to live happily ever after. The end.");
		cyoaOptions.put("013111.type", "endGood");
		cyoaOptions.put("013112.msg", "You didn't hire Cris, and you regret it for years. Eventually it drives you to eating ice cream for dinner.");
		cyoaOptions.put("013112.type", "endBad");
		cyoaOptions.put("01312.msg", "Never stop browsing cats. Ever.");
		cyoaOptions.put("01312.type", "endBad");
		cyoaOptions.put("0132.msg", "You end up playing for four days straight and die from dehydration and bed sores.");
		cyoaOptions.put("0132.type", "endBad");
		cyoaOptions.put("02.msg", "You are now in the woods. Press 1 to set up camp. Press 2 to go hunting. Press 3 if you're starting to miss technology.");
		cyoaOptions.put("02.type", "continue");
		cyoaOptions.put("021.msg", "Your campsite gets raided, and you die.");
		cyoaOptions.put("021.type", "endBad");
		cyoaOptions.put("022.msg", "You're hunting! Press 1 to set a trap. Press 2 to scavenge for food.");
		cyoaOptions.put("022.type", "continue");
		cyoaOptions.put("0221.msg", "You trap some food! Press 1 to eat it raw. Press 2 to cook it.");
		cyoaOptions.put("0221.type", "continue");
		cyoaOptions.put("02211.msg", "You eat it, you get sick, and you die.");
		cyoaOptions.put("02211.type", "endBad");
		cyoaOptions.put("02212.msg", "Bears attack, and you die.");
		cyoaOptions.put("02212.type", "endBad");
		cyoaOptions.put("0222.msg", "You fail to find food, and die.");
		cyoaOptions.put("0222.type", "endBad");
		cyoaOptions.put("023.msg", "You head back no worse for the wear to hire Cris.");
		cyoaOptions.put("023.type", "endGood");
		cyoaOptions.put("03.msg", "You're now at the creek. Press 1 if you're bad at boating. Press 2 if you can boat like a boss.");
		cyoaOptions.put("03.type", "continue");
		cyoaOptions.put("031.msg", "Your boat capsizes. Press 1 if you don't know how to swim. Press 2 to swim towards shore.");
		cyoaOptions.put("031.type", "continue");
		cyoaOptions.put("0311.msg", "What? You can't swim? Obviously you die.");
		cyoaOptions.put("0311.type", "endBad");
		cyoaOptions.put("0312.msg", "You swim for safety, but hit your head on a rock and fall unconscious as you reach the shore.");
		cyoaOptions.put("0312.type", "redirect,012");
		cyoaOptions.put("032.msg", "Your boat was captured by pirates. And no. These are not good pirates. You die horribly.");
		cyoaOptions.put("032.type", "endBad");
		cyoaOptions.put("04.msg", "Upon hiring Cris, you have ushered in an age of world peace.");
		cyoaOptions.put("04.type", "endGood");
	}
}