package server.timeouts;

import java.util.TimerTask;

import server.generated.ErrorType;
import server.networking.Connection;

public class SendMessageTimeout extends TimerTask {

	private Connection con;

	public SendMessageTimeout(Connection con) {
		this.con = con;
	}

	@Override
	public void run() {
		this.con.disconnect(ErrorType.TIMEOUT);
	}

}
