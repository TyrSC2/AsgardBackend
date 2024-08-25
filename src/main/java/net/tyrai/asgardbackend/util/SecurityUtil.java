package net.tyrai.asgardbackend.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtil {
	private static SecureRandom secureRandom;
	static
	{
		try {
			secureRandom = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No algorithm found!");
		}
	}
	private SecurityUtil() {}

	public static String getHash(int length) {
		if (secureRandom == null) {
			System.out.println("secureRandom is null!");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			int digit = secureRandom.nextInt(16);
			if (digit < 10)
				sb.append(digit);
			else
				sb.append((char)('A' + digit - 10));
		}
		return sb.toString();
	}
}
