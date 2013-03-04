package com.twilio;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import com.twilio.sdk.verbs.*;

@WebServlet(
	name = "PhoneSubMenuServlet", 
	urlPatterns = {"/subMenu"}
)
public class PhoneSubMenuServlet extends HttpServlet {
	
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String digits = request.getParameter("Digits");
        TwiMLResponse twiml = new TwiMLResponse();
		Redirect homeRedirect = new Redirect("/ivr");
		
		// Play a nice track
		if (digits != null && digits.equals("1")) {
			Play music = new Play("/resources/Music.mp3");
			try {
				twiml.append(music);
				twiml.append(homeRedirect);
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		}
		// Record and send a bitly link to the caller
		else if (digits != null && digits.equals("2")){
			Say leaveAMessage = new Say("Make your recording after the tone, ending with the pound sign.");
            Record record = new Record();
            record.setMaxLength(30);
			record.setFinishOnKey("#");
            record.setAction("/recording");
            try {
                twiml.append(leaveAMessage);
                twiml.append(record);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
		}
		// Choose your own adventure!
		else if (digits != null && digits.equals("3")){
			
            Say say = new Say("Okay... good luck.");
			try {
				twiml.append(say);
				twiml.append(new Redirect("/cyoa"));
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		}
		// Call the Seattle weather
		else if (digits != null && digits.equals("4")) {
            // Connect the weather number thing to the incoming caller.
            Dial dial = new Dial("+12065268530");
			
            // If the above dial failed, say an error message.
            Say say = new Say("The call failed, or the remote party hung up.");
            try { 
                twiml.append(dial);
                twiml.append(say);
				twiml.append(homeRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        }
		// Bring up the developers contact details
		else if (digits != null && digits.equals("5")) {
			Gather gather = new Gather();
			gather.setAction("/contactInfo");
			gather.setTimeout(10);
			gather.setNumDigits(1);
			gather.setMethod("POST");
			Say sayInGather = new Say("To be sent a link to Cris's resumay, press 1. "
									  + "To get a link to the code for this application, press 2. "
									  + "To leave Cris a recorded message, press 3. "
									  + "To call Cris directly, press 4. "
									  + "To return to the main menu, press 5. ");
			try {
				gather.append(sayInGather);
				twiml.append(gather);
			} catch (TwiMLException e) {
				e.printStackTrace();
			}        }
		// Hidden menu option?
		else if (digits != null && digits.equals("0")){
            Say say = new Say("Hey! Nobody told you to press that.");
			Play music = new Play("/resources/Hidden.mp3");
			try {
				twiml.append(say);
				twiml.append(music);
			}
			catch (TwiMLException e) {
				e.printStackTrace();
			}
		}
		// If they didn't press press a valid response, redirect them home
		else {
            Say say = new Say("Sorry, that wasn't a valid option.");
            try { 
                twiml.append(say);
				twiml.append(homeRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        }
		
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());
    }
}