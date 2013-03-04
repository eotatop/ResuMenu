package com.twilio;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import com.twilio.sdk.verbs.*;

@WebServlet(
	name = "ContactInfoServlet", 
	urlPatterns = {"/contactInfo"}
)
public class ContactInfoServlet extends HttpServlet {
	
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String digits = request.getParameter("Digits");
        TwiMLResponse twiml = new TwiMLResponse();
		Redirect homeRedirect = new Redirect("/ivr");
		Redirect contactMenuRedirect = new Redirect("/subMenu?Digits=5");
		
		// Send links to resume
		if (digits != null && digits.equals("1")) {
			String resumeLinkMessage = "Here's Cris's resume! http://bit.ly/XEnKbu";
			
			// If the above dial failed, say an error message.
            Say say = new Say("Check your text messages for a link to Cris's resumay.");
            try { 
				twiml.append(new Sms(resumeLinkMessage));
                twiml.append(say);
				twiml.append(contactMenuRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
		}
		// Send link to code
		else if (digits != null && digits.equals("2")){
			String codeLinkMessage = "Take a look! https://github.com/eotatop/ResuMenu/";
			
			// If the above dial failed, say an error message.
            Say say = new Say("Check your text messages for a link to the repository.");
            try { 
				twiml.append(new Sms(codeLinkMessage));
                twiml.append(say);
				twiml.append(contactMenuRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
		}
		// Leave Cris a recorded message
		else if (digits != null && digits.equals("3")){
			HttpSession session = request.getSession(true);
			session.setAttribute("SendTo","Cris");
			Say leaveAMessage = new Say("You can leave a message after the beep, ending with the pound sign.");
            Record record = new Record();
            record.setMaxLength(60);
			record.setFinishOnKey("#");
            record.setAction("/recording");
            try {
                twiml.append(leaveAMessage);
                twiml.append(record);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
		}
		// Call Cris directly
		else if (digits != null && digits.equals("4")) {
            // Connect the weather number thing to the incoming caller.
            Dial dial = new Dial("+13395269393");
			
            // If the above dial failed, say an error message.
            Say say = new Say("The call failed, or Cris hung up.");
            try { 
                twiml.append(dial);
                twiml.append(say);
				twiml.append(homeRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        }
		else if (digits != null && digits.equals("5")) {
			try {
				twiml.append(homeRedirect);
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
				twiml.append(contactMenuRedirect);
            } catch (TwiMLException e) {
                e.printStackTrace();
            }
        }
		
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());
    }
}