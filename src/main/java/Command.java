import java.util.Map;
import java.util.Set;

public interface Command {
	public static final String SEPARATOR = " ";
	public static final Integer BOARD_SIZE = 3;

	public SlackResponse invoke(SlackRequest slackRequest,
			Map<String, TTT> channelGames, Set<String> slackUsers);
}
