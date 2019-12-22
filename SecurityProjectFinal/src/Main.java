import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


////// 무결성 확인코드를 넣기.

public class Main {
		
	String me;

	public static void main(String[] args) throws Exception {

//		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
//		Date time = new Date();
//		String time1 = format1.format(time);
//		System.out.println(time1);
		
		Main m = new Main();
		Scanner scan = new Scanner(System.in);

		m.doit(scan);

	}

	void doit(Scanner scan) throws Exception {

		DBMgr dmgr = new DBMgr();
		dmgr.DBConnect(scan);

		int num = 1;

		while (!(num == 0)) {

			System.out.println("어떤 기능을 수행 하시겠습니까?(1.로그인        2.회원가입        0.종료)");
			num = scan.nextInt();

			switch (num) {
			case 1:

				me = dmgr.Login(scan);
				if (me == null) {
					System.out.println("로그인 실패");
				} else {
					System.out.println(me + "님 반갑습니다.");
					userMenu(scan, dmgr);
				}
				break;

			case 2:
				dmgr.SignUp(scan);
				break;

			case 0:
				dmgr.initMe();
				System.out.println("종료합니다.");
				break;
			}
		}
	}
	
	void userMenu(Scanner scan, DBMgr dmgr) throws NoSuchAlgorithmException, Exception {

		int num = 99;
		while(true) {
			System.out.println("어떤 기능을 수행 하시겠습니까?(1.메시지 전송        2.받은 메시지        3.보낸 메세지        0.로그아웃)");
			
			num = scan.nextInt();
			
			if(num==0) {
				System.out.println("로그아웃...");
				break;
			}
			
			switch(num) {
			case 1:
				dmgr.sendMsg(scan, me);
				scan.nextLine();
				break;
			case 2:
				dmgr.getReceivedMsg(me, scan);
				break;
			case 3:
				dmgr.getSendMsg(me, scan);
				break;
			}
			
		}
		
	
	
	}

}