# TicTacToe using slack custom slash commands

## General

Using slash commands create a game of tic-tac-toe within Slack.
Custom slash command for the game : /tictactoe

Initialize the board with -1s which denotes empty. There are two players 1 and 2 - 1 corresponds to ‘X’ and 2 corresponds to ‘0’.
For responses that need to be visible to all users of that channel we use 'in_channel' response_type e.g. publicly display the board.
For other responses we use the 'ephemeral' response_type.

Deployed on heroku : https://hidden-gorge-10416.herokuapp.com/

## Commands
    (Syntax : /tictactoe command_name)

### 1. start

####Usage
    /tictactoe start opponent_username
Creates a new game of tictactoe within a channel with the specified opponent and specifies whose turn it is (The person who initiated the game).
Constrains include a channel can have at most one game being played at a time. Hence use a ConcurrentHashMap to keep track of tictactoe games for specific channels.

#### Error Handling :
           
Check if a game of tictactoe is already in progress within a given channel. If so error out saying a game of tictactoe is already in progress.
If the opponentUsername is not a valid username of the channel show an error. This could be an ‘invalid user’ error - however to make sure to protect against malicious phishing attacks only display a generic ‘Bad Request’ error for this.

### 2. move

#### Usage
    /tictactoe move row col
This allows users specify their next move, which also publicly displays the board in the channel after the move with a reminder of whose turn it is.
Constrains include only the user whose turn it is can make the next move - handling that.
There are two ways to end the game - either someone wins or it is a draw. If this turn ends the game display that and who won if applicable. Otherwise display what move was made and whose turn it is next.
This command also displays the status of the board after the move is made.

#### Error Handling :

If a user whose turn it is not attempts to make a move display an error saying it is not your turn.
If a user specifies a position that is out of bounds for the 3*3 board display that error.
If a user attempts to move to a position that is already filled display that error.
If a user attempts to move to a position that is invalid (e.g. a letter instead of a number) display that error.

### 3. status

#### Usage
    /tictactoe status

Anyone in the channel can run this command to display the current board and list whose turn it is.
If no game if currently in progress display that.

#### Error Handling :
If a status request is made when no game is currently in progress then display that.

## General Errors and Data Model :

A general error handling check on the request is also made to display bad request.

Different errors are specified in the enum SlackErrors. The slack request and response are specified in the SlackRequest and SlackResponse classes.
Game status is specified in the TTTStatus enum - game can be in progress, player 1 won, player 2 won or draw.

## Possible extensions :

Board size assumed to be 3*3 since it is a game of tictactoe. If a larger board is needed we can extend this to maybe pass in board size as another argument. We can accordingly create a board with the appropriate size. 
Addition of other commands such as ‘terminate’ which ends the current game. We could have it so that the command can only be exercised by the player whose turn it is. This prevents a game hanging for a long time in case a user has to step away from the channel for some time since a channel can only have one game in progress at a time. 
The current tests are pretty basic - just testing that the functions do the right thing. We could have a more extensive testing framework where a mock server can be setup with mock Slack request and responses.

## Minor cleanup :

Used the Slack channel.list and user info API to get the list of users within a channel. It works if I use it to query a particular hardcoded channel (tested on my local). After parsing the request however and calling the same method results in a null users list for the channel. If there is a more compact way of getting the users for a channel or any existing extensions available already those can be used instead. 





