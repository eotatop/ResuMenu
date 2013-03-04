package com.twilio;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.twilio.sdk.verbs.*;

@WebServlet(
	name = "PhoneMenuServlet", 
	urlPatterns = {"/ivr"}
)
public class PhoneMenuServlet extends HttpServlet{	
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
        String userName = (String)session.getAttribute("userName");
		
		Say greeting = null;
        if (userName == null) {
			// Create a dict of people we know.
			HashMap<String, String> callers = new HashMap<String, String>();
			callers.put("+13395269393", "Cris");
			callers.put("+14253468137", "Other Cris");
			callers.put("+14254183850", "Deirdre");
			
			String fromNumber = request.getParameter("From");
			String knownCaller = callers.get(fromNumber);
			String message;
			if (knownCaller == null) {
				knownCaller = "Guest";
				message = "Howdy stranger";
			} else {
				// Use the caller's name
				message = "Hello " + knownCaller;
			}
			message += ", Welcome to Cris Tarr's Twilio application.";
			greeting = new Say(message);
			session.setAttribute("userMtn", fromNumber);
			session.setAttribute("userName", knownCaller);
        }
        
		
        // Create a TwiML response and add our friendly message.
		TwiMLResponse twiml = new TwiMLResponse();
		
        Gather gather = new Gather();
        gather.setAction("/subMenu");
		gather.setTimeout(10);
        gather.setNumDigits(1);
        gather.setMethod("POST");
        Say sayInGather = new Say("To listen to some nice music, press 1. "
			+ "To make a voice recording, press 2. "
			+ "To go on an adventure, press 3. "
			+ "To discuss the weather, press 4. "
			+ "For contact details, press 5. "
			+ "Press any other key to start over.");
        try {
            gather.append(sayInGather);
			if(greeting != null) {
				twiml.append(greeting);
			}
            twiml.append(gather);
        } catch (TwiMLException e) {
            e.printStackTrace();
        }
		
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());
	}
}
