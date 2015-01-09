package com.techshroom.unplanned.util;

import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;

/**
 * Token class for any sort of generic token. It holds an ID string.
 * 
 * @author Kenzie Togami
 */
@AutoValue
public abstract class Token {
	/**
	 * Creates a new Token with the given ID.
	 * 
	 * @param id
	 *            - the ID
	 * @return a Token with the given ID
	 */
	public static final Token createToken(String id) {
		return new AutoValue_Token(Strings.nullToEmpty(id));
	}

	Token() {
	}

	/**
	 * Gets the ID of the token.
	 * 
	 * @return the ID of the token
	 */
	public abstract String getID();
}
