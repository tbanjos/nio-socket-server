# Numbers

This application accepts inputs of 9-digit numbers via socket clientConnection. The input is stored in a log file after deduplication.  
Connection is limited to 5 concurrent clients at time.

---

## Usage

### Starting the server
Application can be on the command line by providing the port argument to start the server.  
If no port is provided, the server will start on the default port (4000). 
Example:

```
java Server 4000
```


### Client connection

Use telnet protocol to connect to server on the configured port. 
Every new line corresponds to a new input to the server.  
The server will save valid non-existent 9-digit inputs at the log file.  
Invalid inputs are discarded.

If the keyword `terminate` is sent, the server will disconnect from all clients and shutdown.

Example of usage:

```
telnet localhost 4000
314159265
007007009
terminate

```