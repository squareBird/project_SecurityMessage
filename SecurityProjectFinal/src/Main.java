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


////// ���Ἲ Ȯ���ڵ带 �ֱ�.

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

			System.out.println("� ����� ���� �Ͻðڽ��ϱ�?(1.�α���        2.ȸ������        0.����)");
			num = scan.nextInt();

			switch (num) {
			case 1:

				me = dmgr.Login(scan);
				if (me == null) {
					System.out.println("�α��� ����");
				} else {
					System.out.println(me + "�� �ݰ����ϴ�.");
					userMenu(scan, dmgr);
				}
				break;

			case 2:
				dmgr.SignUp(scan);
				break;

			case 0:
				dmgr.initMe();
				System.out.println("�����մϴ�.");
				break;
			}
		}
	}
	
	void userMenu(Scanner scan, DBMgr dmgr) throws NoSuchAlgorithmException, Exception {

		int num = 99;
		while(true) {
			System.out.println("� ����� ���� �Ͻðڽ��ϱ�?(1.�޽��� ����        2.���� �޽���        3.���� �޼���        0.�α׾ƿ�)");
			
			num = scan.nextInt();
			
			if(num==0) {
				System.out.println("�α׾ƿ�...");
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