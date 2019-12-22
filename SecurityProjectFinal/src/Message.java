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
//		System.out.printf("%s\t\t\t // %s // %s // %s\n",title,sender,receiver,time);
//		System.out.println("�۽��� : " + sender);
//		System.out.println("������ : " + receiver);
//		System.out.println("���� : " + title);
//		System.out.println("���۽ð� : " + time);
	}
	
	void print() {
		System.out.println("===========================================================================");
		System.out.println("�۽��� : " + sender);
		System.out.println("������ : " + receiver);
		System.out.println("���� : " + title);
		System.out.println("�޽��� : " + msg);
		System.out.println("���۽ð� : " + time);
		if(this.hash.equals(SHA256.makeHash(msg)))
			System.out.println("���Ἲ ���� �Ϸ�...");
		else
			System.out.println("���Ἲ �ջ��...");
		System.out.println("===========================================================================");


	}
	
}
