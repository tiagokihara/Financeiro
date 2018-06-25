package br.com.tksoft.financeiro.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografia {

	public static String criptografar(String senha) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest md5 = MessageDigest.getInstance("MD5");

		BigInteger hash = new BigInteger(1, md5.digest((senha + "F1nanc3ir0").getBytes("UTF-8")));
		return hash.toString(16);
	}

	public static Boolean compararSenha(String senha, String senhaInformada) throws UnsupportedEncodingException, NoSuchAlgorithmException {

		return senha.equals(criptografar(senhaInformada));
	}
}
