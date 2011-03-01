package co.hanul.mr.dev.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import biz.source_code.base64Coder.Base64Coder;
import co.hanul.mr.dev.cryptos.Cryptos;

/**
 * SingleSession
 * 
 * @author Mr. 하늘
 */
public class SingleSession {

	private static final String singleMapName = "LOTUS_CRYPTOS_SINGLE_SESSION_MAP";
	private static final String createTimeName = "CREATE_TIME";
	private static final String userIp = "USER_IP";
	private static final String securityErrorMsg = "보안 오류! 오류를 낸 IP : ";

	private HttpServletResponse response;

	private HashMap<String, Object> singleMap;

	private SingleSession() {
	}

	/**
	 * 세션을 불러온다. 없을 경우 생성한다.
	 */
	@SuppressWarnings("unchecked")
	public static SingleSession getSession(
			HttpServletRequest request,
			HttpServletResponse response) {

		SingleSession singleSession = new SingleSession();
		CookieBox cookies = new CookieBox(request);
		Cryptos cryptos = Cryptos.getInstance();
		String string = null;
		try {
			string = cookies.getValue(singleMapName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (string != null) {
			try {
				singleSession.singleMap = (HashMap<String, Object>) objectFromString(cryptos.decrypt(string));
			} catch (BadPaddingException e) { // 서버 재시작 후 암호화 값이 맞지 않는 문제
				singleSession.singleMap = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (singleSession.singleMap == null) {
			singleSession.singleMap = new HashMap<String, Object>();
			singleSession.singleMap.put(createTimeName, new Date());
			singleSession.singleMap.put(userIp, request.getRemoteAddr());
		} else {
			// IP로 사용자 판단
			if (!singleSession.getAttribute(userIp).equals(request.getRemoteAddr())) {
				throw new Error(securityErrorMsg + request.getRemoteAddr());
			}
		}
		singleSession.response = response;
		return singleSession;
	}

	private static Object objectFromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64Coder.decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
			data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	private static String objectToString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64Coder.encode(baos.toByteArray()));
	}

	/**
	 * SingleSession에 속성을 저장한다.
	 */
	public void setAttribute(String arg0, Object arg1) {
		singleMap.put(arg0, arg1);
		commit();
	}

	/**
	 * SingleSession에서 속성을 가져온다.
	 */
	public Object getAttribute(String arg0) {
		return singleMap.get(arg0);
	}

	/**
	 * SingleSession에서 속성을 삭제한다.
	 */
	public void removeAttribute(String arg0) {
		singleMap.remove(arg0);
		commit();
	}

	/**
	 * 속성의 이름 목록을 구한다.
	 */
	public Set<String> getAttributeNames() {
		return singleMap.keySet();
	}

	/**
	 * 세션 초기화
	 */
	public void invalidate() {
		try {
			response.addCookie(CookieBox.createCookie(singleMapName, "", "/", 0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 새로 만든 세션인가?
	 */
	public boolean isNew() {
		return singleMap.size() == 0;
	}

	/**
	 * 생성 시간을 반환한다.
	 */
	public Date getCreationTime() {
		return (Date) singleMap.get(createTimeName);
	}

	/**
	 * 쿠키에 저장되는 SingleSession ID를 반환한다.
	 */
	public String getId() {
		return singleMapName;
	}

	private void commit() {
		Cryptos cryptos = Cryptos.getInstance();
		try {
			response.addCookie(CookieBox.createCookie(
				singleMapName,
				cryptos.encrypt(objectToString(singleMap)),
				"/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
