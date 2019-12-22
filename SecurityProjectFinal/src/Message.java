import java.security.PrivateKey;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Message {
	String time;
	String sender;
	String receiver;
	String title;
	String msg;
	String hash;
	
	void setTime(String time) {
		this.time = time;
	}
	
	void setSender(String sender) {
		this.sender = sender;
	}
	
	void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	void setTitle(String title) {
		this.title = title;
	}
	
	void setMsg(String msg) {
		this.msg = msg;
	}
	
	void setHash(String hash) {
		this.hash = hash;
	}
	
	void printTitle() {
		System.out.printf("%27s // %10s // %10s // %20s\n",title,sender,receiver,time);

	}
	
	void print() {
		System.out.println("===========================================================================");
		System.out.println("Sender : " + sender);
		System.out.println("Receiver : " + receiver);
		System.out.println("Title : " + title);
		System.out.println("Msg : " + msg);
		System.out.println("Time : " + time);
		if(this.hash.equals(SHA256.makeHash(msg)))
			System.out.println("Integrity Okay...");
		else
			System.out.println("Integrity Break...");
		System.out.println("===========================================================================");


	}
	
}
