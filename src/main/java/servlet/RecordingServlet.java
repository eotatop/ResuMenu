package com.twilio;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.HashMap;
import com.rosaloves.bitlyj.*;
import static com.rosaloves.bitlyj.Bitly.*;

import com.twilio.sdk.verbs.*;

@WebServlet(
	name = "RecordingServlet", 
	urlPatterns = {"/recording"}
)
public class RecordingServlet extends HttpServlet {
	public static final String bitlyName = "eotatop";
	public static final String bitlyAuth = "R_e484e5b7a45224918a72d44d9012c9ef";
	
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(true);
		
        String recordingUrl = request.getParameter("RecordingUrl");
        String target = request.getParameter("Target");
        TwiMLResponse twiml = new TwiMLResponse();
        if (recordingUrl != null && target != "Cris") {
            try {
                twiml.append(new Say("Great! Here's what you recorded."));
                twiml.append(new Play(recordingUrl));
				String userName = (String)session.getAttribute("userName");
				Provider bitly = as(bitlyName, bitlyAuth);
				Url shortUrl = bitly.call(shorten(recordingUrl));
				String shortUrlString = shortUrl.getShortUrl();
				if (userName != "Guest") {
					twiml.append(new Sms("Thanks " + userName + ", here's a link to your recording: " + shortUrlString));
				} else {
					twiml.append(new Sms("Thanks! Listen again here: " + shortUrlString));
				}

                twiml.append(new Redirect("/ivr"));
            } catch (TwiMLException e) {
                e.printStackTrace();
            } catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect("/ivr");
			}
        } else if (recordingUrl != null && target == "Cris") {
			try {
                twiml.append(new Say("Great! Thanks for the message."));
				String userName = (String)session.getAttribute("userName");
				Provider bitly = as(bitlyName, bitlyAuth);
				Url shortUrl = bitly.call(shorten(recordingUrl));
				String shortUrlString = shortUrl.getShortUrl();
				
				String message = userName + " sent you a recording! " + shortUrlString;
				Sms sms = new Sms(message);
				sms.setTo("+13395269393");
				
				twiml.append(sms);
                twiml.append(new Redirect("/subMenu?Digits=5"));
            } catch (TwiMLException e) {
                e.printStackTrace();
            } catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect("/subMenu?Digits=5");
			}
		} else {
            response.sendRedirect("/ivr");
            return;
        }
		
        response.setContentType("application/xml");
        response.getWriter().print(twiml.toXML());
    }
}