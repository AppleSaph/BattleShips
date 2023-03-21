# BattleShipsClient

This is a client for the Battleships game.

## How to run
1. Clone the repository
2. Run the Main class
3. Enter the server address, port and username
4. Enjoy!

## Server Protocol
Communication between the client and the server is done using the following protocol:

### Client -> Server
* 'MOVE~x~y' - where x and y are the coordinates of the move
* 'EXIT' - to exit the game
* 'PING' - to check if the server is still alive
* 'PONG' - response to a PING from the server

### Server -> Client
* 'HIT~x~y~playerNumber' - where x and y are the coordinates of the hit, and playerNumber is the playerNumber of the player that is hit
* 'MISS~x~y' - where x and y are the coordinates of the miss
* 'WINNER~playerNumber' - where playerNumber is the playerNumber of the winner
* 'LOST~playerNumber' - where playerNumber is the playerNumber of the player who lost
* 'ERROR~message' - where message is the error message
* 'EXIT' - to exit the game
* 'TURN~playerNumber' - where playerNumber is the playerNumber of the player who's turn it is
* 'NEWGAME~playerNumber' - where playerNumber is the playerNumber of the player who's turn it is
* 'PING' - to check if a client is still alive
* 'PONG' - response to a PING from the client
