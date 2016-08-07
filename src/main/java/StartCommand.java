import java.util.Map;

public class StartCommand implements Command {
	public SlackResponse invoke(SlackRequest slackRequest,
			Map<String, TTT> channelGames) {
		if (slackRequest == null) {
			return new SlackResponse(SlackErrors.BAD_REQUEST.getValue(),
					ResponseType.EPHEMERAL.getValue());
		} else {
			final String[] tokens = slackRequest.getText().split(SEPARATOR);
			if (tokens == null || tokens.length < 2) {
				return new SlackResponse(SlackErrors.BAD_REQUEST.getValue(),
						ResponseType.EPHEMERAL.getValue());
			} else {
				if (channelGames.containsKey(slackRequest.getChannelId())) {
					return new SlackResponse(
							SlackErrors.GAME_IN_PROG.getValue(),
							ResponseType.EPHEMERAL.getValue());
				}
				final String opponent = tokens[1];
				TTT game = new TTT(BOARD_SIZE, slackRequest.getUserName(),
						opponent);
				channelGames.putIfAbsent(slackRequest.getChannelId(), game);

				return new SlackResponse(
						"New tic tac toe game initiated! Player 1 : "
								+ game.getPlayer1() + " Player 2 : "
								+ game.getPlayer2(),
						ResponseType.IN_CHANNEL.getValue());
			}
		}
	}
}
