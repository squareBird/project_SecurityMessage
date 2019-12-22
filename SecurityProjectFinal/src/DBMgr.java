import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DBMgr {

	Connection conn;
	Statement stmt;
	PublicKey myPublickey; // �� ����Ű
	PrivateKey myPrivateKey; // �� ����Ű
	ArrayList<Message> mList;

	// �����ͺ��̽��� ����
	void DBConnect(Scanner scan) throws Exception {
		try {
			
			String me;
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/project?characterEncoding=UTF-8&serverTimezone=UTC", "fails12", "sphere12#");
			stmt = conn.createStatement();
			System.out.println("Success!");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	// �α׾ƿ��� �� ������ �ʱ�ȭ
	void initMe() {
		myPublickey = null;
		myPrivateKey = null;
		mList = null;
	}
		
	// �������� ������
	ResultSet sendExecuteQuery(String query) throws SQLException {
		
		return stmt.executeQuery(query);

	}
	
	// �������� ������
	int sendUpdateQuery(String query) throws SQLException {
		return stmt.executeUpdate(query);
	}
	
	// �α����� ���� ���� key���� �޾ƿ�.
	String Login(Scanner scan) throws NoSuchAlgorithmException, InvalidKeySpecException {
		mList= new ArrayList<>();
		ResultSet rs;
		String id, pw;
		
		Random rand = new Random(System.currentTimeMillis());
		int nonce = rand.nextInt();
		
		System.out.print("ID : ");
		id = scan.next();

		System.out.print("PW : ");
		pw = SHA256.makeHash(scan.next(), nonce);
		String query = "select * from account where id='"+id+"';";
		
		try {
			
			rs = sendExecuteQuery(query);
			while(rs.next()) {
				if(rs.getString(1).equals(id) && SHA256.makeHash(rs.getString(2), nonce).equals(pw)) {
					getMyKey(id);
					return id;
				}
			}
			return null;
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	// ȸ�������� �ϸ� DB�� �� �޼����ڽ��� �����ϰ�, ���������� ����Ű ����Ű�� DB�� ����.
	void SignUp(Scanner scan) throws NoSuchAlgorithmException, 
		InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, 
		BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		
		int rs=0;
		String id, pw = "0", pwcheck = "1";
		String query;
		
		System.out.print("ID : ");
		id = scan.next();		
		while (!pw.equals(pwcheck)) {
			System.out.println("PW : ");
			pw = scan.next();
			System.out.println("PW Check : ");
			pwcheck = scan.next();
		}
		
//		pw = SHA256.makeHash(pw);
		
		query = "insert account values('"+id+"','"+pw+"');";
		
		try {
			rs = sendUpdateQuery(query);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(rs);
			System.out.println("ȸ������ ����");
		}
		
		String msgBoxQuery = "create table " + id +
				"msgbox(number int(11) NOT NULL auto_increment Primary key, time timestamp default now() , sender char(50), receiver char(50),title varchar(1024), message varchar(10000), hash varchar(256))";

		try {
			rs = sendUpdateQuery(msgBoxQuery);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��������");
		}
		
		KeyPair keyPair = RSA.genRSAKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec rsaPublicKeySpec = keyFactory1.getKeySpec(publicKey, RSAPublicKeySpec.class);
        RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory1.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        
//        System.out.println("Public  key modulus : "
//                                         + rsaPublicKeySpec.getModulus());
//        System.out.println("Public  key exponent: "
//                                         + rsaPublicKeySpec.getPublicExponent());
//        System.out.println("Private key modulus : "
//                                         + rsaPrivateKeySpec.getModulus());
//        System.out.println("Private key exponent: "
//                                         + rsaPrivateKeySpec.getPrivateExponent());
		
        byte[] bPublicKey1 = publicKey.getEncoded();
        String sPublicKey1 = Base64.encodeBase64String(bPublicKey1);

        byte[] bPrivateKey1 = privateKey.getEncoded();
        String sPrivateKey1 = Base64.encodeBase64String(bPrivateKey1);
		
        String publicKeyQuery = "insert publickeytable values('"+id+"','"+sPublicKey1+"');";
        String privateKeyQuery = "insert privatekeytable values('"+id+"','"+sPrivateKey1+"');";
        
        try {
        	
			sendUpdateQuery(publicKeyQuery);
	        sendUpdateQuery(privateKeyQuery);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// �� Ű ������ �޾ƿ�
	void getMyKey(String id) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException{
		
		
		String publicKeyQuery = "select publicKey from publickeytable where id='"+id+"';";
		byte[] bPublicKey = null; 

		String privateKeyQuery = "select privateKey from privatekeytable where id='"+id+"';";
		byte[] bPrivateKey = null; 

		ResultSet purs = sendExecuteQuery(publicKeyQuery);

		while(purs.next()) {
			bPublicKey = Base64.decodeBase64(purs.getString(1).getBytes());
		}
		purs.close();
		
		ResultSet prrs = sendExecuteQuery(privateKeyQuery);

		while(prrs.next()) {
			bPrivateKey = Base64.decodeBase64(prrs.getString(1).getBytes());
		}
		prrs.close();
		
		KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec= new X509EncodedKeySpec(bPublicKey);
        myPublickey = keyFactory2.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec= new PKCS8EncodedKeySpec(bPrivateKey);
        myPrivateKey = keyFactory2.generatePrivate(privateKeySpec);

	}
	
	// �޼����� �����ϰ��� �ϴ� ����� ����Ű�� �޾ƿ�
	PublicKey getReceiverKey(String receiver) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
		
		String publicKeyQuery = "select publicKey from publickeytable where id='"+receiver+"';";
		byte[] bPublicKey = null; 

		ResultSet purs = sendExecuteQuery(publicKeyQuery);

		while(purs.next()) {
			bPublicKey = Base64.decodeBase64(purs.getString(1).getBytes());
		}
		purs.close();
		
		KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec= new X509EncodedKeySpec(bPublicKey);
        
        return keyFactory2.generatePublic(publicKeySpec);
        
	}
	
	// �޼����� ����
	void sendMsg(Scanner scan, String id) throws NoSuchAlgorithmException, Exception {

		PublicKey receiverKey = null;
		
		String receiver, msg, title;
		String receiverBoxQuery;
		String myBoxQuery;
		String hash;
		int r;

		System.out.print("�޴»�� : ");
		receiver = scan.next();
		scan.nextLine();
		System.out.print("���� : ");
		title = scan.nextLine();
		System.out.println("���� �޽��� : ");
		msg = scan.nextLine();
		receiverKey = getReceiverKey(receiver);
		
		// ��ȣȭ ��� ���� �� �޴³� �޽����ڽ��� �־���
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, receiverKey);
		
		byte[] btitle = cipher.doFinal(title.getBytes());
		String encryptTitle=Base64.encodeBase64String(btitle);
		
		byte[] bmsg = cipher.doFinal(msg.getBytes());
		String encryptMsg=Base64.encodeBase64String(bmsg);
		hash = SHA256.makeHash(msg);
		
		receiverBoxQuery = "insert into "+ receiver + "msgbox(time, sender, receiver, title, message, hash) values(" 
				+ "default" + ", '" 
				+ id + "', '" 
				+ receiver + "', '"
				+ encryptTitle + "', '"
				+ encryptMsg + "', '" 
				+ hash + "');";

		r = stmt.executeUpdate(receiverBoxQuery);
		
		// ��ȣȭ ��� ���� �� �� �޽����ڽ��� ����
		Cipher cipher2 = Cipher.getInstance("RSA");
		cipher2.init(Cipher.ENCRYPT_MODE, myPublickey);
		
		byte[] btitle2 = cipher2.doFinal(title.getBytes());
		String encryptTitle2=Base64.encodeBase64String(btitle2);
		
		byte[] bmsg2 = cipher2.doFinal(msg.getBytes());
		String encryptMsg2=Base64.encodeBase64String(bmsg2);
		
		myBoxQuery = "insert into "+ id + "msgbox(time, sender, receiver, title, message, hash) values(" 
				+ "default" + ", '" 
				+ id + "', '" 
				+ receiver + "', '"
				+ encryptTitle2 + "', '"
				+ encryptMsg2 + "', '" 
				+ hash + "');";
		
		r = stmt.executeUpdate(myBoxQuery);
		
	}
	
	// ���� �޼����� �޾ƿ�
	void getReceivedMsg(String id, Scanner scan) throws Exception {
		ResultSet rs;
		String query;
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
		String plainTitle, plaintext;

		query = "select time, sender, receiver, title, message, hash from "
					+ id +"msgbox where receiver='" + id + "'";
		rs = stmt.executeQuery(query);
			
		Message m;
		while (rs.next()) {
			m = new Message();
			m.setTime(rs.getString(1));
			m.setSender(rs.getString(2));
			m.setReceiver(rs.getString(3));
			byte[] bcipherTitle = Base64.decodeBase64(rs.getString(4).getBytes());
			byte[] bplainTitle = cipher.doFinal(bcipherTitle);
			plainTitle = new String(bplainTitle);
			m.setTitle(plainTitle);
			byte[] bcipher = Base64.decodeBase64(rs.getString(5).getBytes());
			byte[] bplain = cipher.doFinal(bcipher);
			plaintext = new String(bplain);
			m.setMsg(plaintext);
			m.setHash(rs.getString(6));
			
			mList.add(m);
		}
		
		int menu = 1;
		
		while(true) {
			if(mList.size()==0) {
				System.out.println("�������� ��� �ֽ��ϴ�.");
				break;
			}
			System.out.println("�������ϴ� ������ ��ȣ�� �Է� �� �ּ��� (���� �� 0��)");
			System.out.printf("%30s // %10s // %10s // %20s\n","==============����==============","===�۽���===","===������===","=========���۽ð�=========");
			int count = 1;
			for(Message ms : mList) {
				System.out.printf("%3d. ", count++);
				ms.printTitle();
			}
			System.out.println("==============================================================================================");
			menu = scan.nextInt();

			if(menu == 0)
				break;
			
			if(menu+1>count) {
				System.out.println("�߸��� ��ȣ�Դϴ�");
				count--;
				continue;
			}
			
			mList.get(menu-1).print();
			System.out.println("�ƹ��ų� �Է� �� �ּ���.");
			scan.next();
		}
		
		
		
		mList = new ArrayList<>();
		rs.close();
	}
	
	// ���� �޽����� �޾ƿ�
	void getSendMsg(String me, Scanner scan) throws Exception {
		
		ResultSet rs;
		String query;
		
		query = "select time, sender, receiver, title, message, hash from "+ me +"msgbox where sender='" + me + "'";
		rs = stmt.executeQuery(query);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
		String plainTitle, plaintext;
		
		Message m;
		while (rs.next()) {
			m = new Message();
			m.setTime(rs.getString(1));
			m.setSender(rs.getString(2));
			m.setReceiver(rs.getString(3));
			byte[] bcipherTitle = Base64.decodeBase64(rs.getString(4).getBytes());
			byte[] bplainTitle = cipher.doFinal(bcipherTitle);
			plainTitle = new String(bplainTitle);
			m.setTitle(plainTitle);
			byte[] bcipher = Base64.decodeBase64(rs.getString(5).getBytes());
			byte[] bplain = cipher.doFinal(bcipher);
			plaintext = new String(bplain);
			m.setMsg(plaintext);
			m.setHash(rs.getString(6));
			
			mList.add(m);
		}
		
		int menu = 1;
		
		while(true) {
			if(mList.size()==0) {
				System.out.println("�������� ��� �ֽ��ϴ�.");
				break;
			}
			System.out.println("�������ϴ� ������ ��ȣ�� �Է� �� �ּ��� (���� �� 0��)");
			System.out.printf("%30s // %10s // %10s // %20s\n","==============����==============","===�۽���===","===������===","=========���۽ð�=========");
			int count = 1;
			for(Message ms : mList) {
				System.out.printf("%3d. ", count++);
				ms.printTitle();
			}
			System.out.println("==============================================================================================");
			menu = scan.nextInt();

			if(menu+1>count) {
				System.out.println("�߸��� ��ȣ�Դϴ�");
				count--;
				continue;
			}
			
			if(menu == 0)
				break;
			
			mList.get(menu-1).print();
			System.out.println("�ƹ��ų� �Է� �� �ּ���.");
			scan.next();
		}
		
		mList = new ArrayList<>();
		rs.close();
	}
}
