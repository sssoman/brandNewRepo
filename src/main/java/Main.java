import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.*;
import org.json.JSONObject;

public class Main extends AbstractHandler {

    private Map<String, TTT> channelGames = new ConcurrentHashMap<>();
    private StartCommand startCommandInv;
    private MoveCommand markCommandInv;
    private StatusCommand statusCommandInv;
    private String expectedToken = "IPp6IULLVFsycYkfAT8Vogg4";
    
    /* Request parameters*/
    private static final String TOKEN = "token";
    private static final String TEAM_ID = "team_id";
    private static final String TEAM_DOMAIN = "team_domain";
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String COMMAND = "command";
    private static final String TEXT = "text";
    private static final String RESPONSE_URL = "response_url";

    /* Response parameters*/
    private static final String RESPONSE_TYPE = "response_type”;
    private static final String TEXT = "text";

    Main(){
    	channelGames = new ConcurrentHashMap<>();
    	startCommandInv = new StartCommand();
    	markCommandInv = new MoveCommand();
    	statusCommandInv = new StatusCommand();
       expectedToken = System.getenv("SLACK_AUTH_TOKEN");
    }
  
  /*  @SuppressWarnings("resource")
	public static void main(String args[]) throws IOException{
    	Main mn = new Main();
    	SlackResponse slackResponse = new SlackResponse("fake", ResponseType.EPHEMERAL.getValue());
    	while(!slackResponse.getText().contains("end") && !slackResponse.getText().contains("draw")){
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String s = br.readLine();
        final String[] tokens = s.split(Command.SEPARATOR);
        SlackRequest slackCommand = new SlackRequest();
        final String cmd = tokens[0];
        slackCommand.setChannelId("1");
        slackCommand.setText(s);
        slackCommand.setUserName("test");
		switch(cmd){
            case "start":
            	slackResponse = mn.startCommandInv.invoke(slackCommand, mn.channelGames);
                break;
            case "move":
            	slackResponse =  mn.markCommandInv.invoke(slackCommand, mn.channelGames);
                break;
            case "status":
            	slackResponse = mn.statusCommandInv.invoke(slackCommand, mn.channelGames);
                break;
            default:
            	slackResponse = new SlackResponse(SlackErrors.BAD_COMMAND.getValue(), ResponseType.EPHEMERAL.getValue());       
          }

        System.out.println(slackResponse.getText());
    	}
    }*/

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		SlackRequest sRequest = parseRequest(request);
		SlackResponse slackResponse;
		if (sRequest == null || sRequest.getText() == null) {
			slackResponse = new SlackResponse(
					SlackErrors.BAD_REQUEST.getValue(),
					ResponseType.EPHEMERAL.getValue());
		} else {
			final String actualToken = sRequest.getToken();
			if (actualToken == null || actualToken.isEmpty()
					|| !actualToken.equals(expectedToken)) {
				slackResponse = new SlackResponse(
						SlackErrors.BAD_TOKEN.getValue(),
						ResponseType.EPHEMERAL.getValue());
			} else {
				String text = sRequest.getText();
				if (text != null && !text.isEmpty()) {
					final String[] tokens = text.split(Command.SEPARATOR);
					String command = tokens[0];
					switch (command) {
					case "start":
						slackResponse = startCommandInv.invoke(sRequest,
								channelGames);
						break;
					case "move":
						slackResponse = markCommandInv.invoke(sRequest,
								channelGames);
						break;
					case "status":
						slackResponse = statusCommandInv.invoke(sRequest,
								channelGames);
						break;
					default:
						slackResponse = new SlackResponse(
								SlackErrors.BAD_COMMAND.getValue(),
								ResponseType.EPHEMERAL.getValue());
					}
					if (slackResponse != null) {
						createResponse(slackResponse, response);
					}
				}
			}
		}
		baseRequest.setHandled(true);
	}

	/*
	 * Request of the form 
	 * token=gIkuvaNzQIHg97ATvDxqgjtO
       team_id=T0001
       team_domain=example
       channel_id=C2147483705
       channel_name=test
       user_id=U2147483697
       user_name=Steve
       command=/weather
       text=94070
       response_url=https://hooks.slack.com/commands/1234/5678
	 */
	SlackRequest parseRequest(HttpServletRequest request){
		final SlackRequest slackRequest = new SlackRequest();
		if (request != null) {
			slackRequest.setToken(request.getParameter(TOKEN));
			slackRequest.setTeamId(request.getParameter(TEAM_ID));
			slackRequest.setTeamDomain(request.getParameter(TEAM_DOMAIN));
			slackRequest.setChannelId(request.getParameter(CHANNEL_ID));
			slackRequest.setChannelName(request.getParameter(CHANNEL_NAME));
			slackRequest.setUserId(request.getParameter(USER_ID));
			slackRequest.setUserName(request.getParameter(USER_NAME));
			slackRequest.setCommand(request.getParameter(COMMAND));
			slackRequest.setText(request.getParameter(TEXT));
			slackRequest.setResponseUrl(request.getParameter(RESPONSE_URL));
		}
		return slackRequest;
	}
	
	void createResponse(SlackResponse slackResponse, HttpServletResponse response){
               JSONObject jResp = new JSONObject();
               obj.put(RESPONSE_TYPE, slackResponse.getResponseType());
               obj.put(TEXT, slackResponse.getText());
		response.setContentType("application/json");
		response.setStatus(HttpStatus.OK_200);
		try {
			response.getWriter().print(jResp.toString());
		} catch (IOException e) {
			new SlackResponse(
					SlackErrors.BAD_REQUEST.getValue(),
					ResponseType.EPHEMERAL.getValue());
		}
	}

    public static void main( String[] args ) throws Exception
    {
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(new Main());
        server.start();
        server.join();
    }
}
