package im;

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 700230983064187010L;

	public static enum Type {
		MSG, USERNAME, CLEAR;
	};

	Type type;
	String username;
	String hostname;
	String text;
	private boolean anonymous;

	public Message(String text) {
		anonymous = true;
		this.text = text;
	}

	public Message(Type type) {
		if (type == Type.CLEAR) {
			text = "";
		}
	}
	
	public Message(String username, String text, String hostname) {
		this.username = username;
		this.text = text;
		this.hostname = hostname;
	}

	public Message(String username, String text, Type type) {
		this.username = username;
		this.text = text;
	}

	public String toString() {

		while (text.contains("{{") && text.contains("}}")) {
			String secretMsg = text.substring(text.indexOf("{{"), text.indexOf("}}") + 2);
			text = text.replace(secretMsg, "");
			System.out.println(secretMsg);
		}
		
		if (anonymous) {
			return text;
		}

		if (username != null && text != null) {
			return hostname + "@" + username + ":$ " + text;
		} else {
			return "[Error 404: String not found]: There was a null value in either username or text in a message instance";
		}
	}
}
