package br.com.tksoft.financeiro.di;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

/**
 * Marca classes que ter�o escopo DAO para inje��o do entity manager e outros objetos DAO.
 * 
 * <p>Esta anota��o � obrigat�ria para que o entity manager seja inicializado
 * corretamente durante a chamada aos m�todos dos DAO.</p>
 */
@Target({ TYPE }) @Retention(RUNTIME) @ScopeAnnotation
public @interface DAO {}
