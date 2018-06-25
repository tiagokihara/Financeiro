package br.com.tksoft.financeiro.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;

import org.apache.commons.beanutils.PropertyUtils;

public class CriarObjetosResolver extends BeanELResolver {
	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		// Obtém o valor da propriedade
		Object value = null;
		try {
			value = super.getValue(context, base, property);
		} catch (PropertyNotFoundException e) {
			// Como este resolver é o primeiro da lista e a sua classe ancestral (BeanELResolver)
			// sempre tenta trazer o valor de qualquer objeto, exceções PropertyNotFoundException
			// são lançadas para objetos 'base' que normalmente seriam tratados antes por resolvers
			// mais específicos (ex: ResourceBundleELResolver). Nesses casos, marcamos
			// o valor como não resolvido para que a requisição seja resolvida pelo próximo resolver
			// da lista.
			context.setPropertyResolved(false);
			return null;
		}
		
		// Tenta criar o objeto se ele não existir
		if (value == null && base != null) {
			try {
				Class<?> classePropriedade = PropertyUtils.getPropertyType(base, (String) property);
				boolean ehInstanciavel = !(
						classePropriedade.isPrimitive() ||
						classePropriedade.isArray() ||
						classePropriedade.isEnum() ||
						classePropriedade.isInterface() ||
						classePropriedade.equals(Object.class) ||
						classePropriedade.equals(String.class) ||
						Date.class.isAssignableFrom(classePropriedade)
					);
				
				if (ehInstanciavel) {
					value = classePropriedade.newInstance();
					PropertyUtils.setProperty(base, (String) property, value);
				}
			} catch (IllegalAccessException e) {
				// Ignora
			} catch (InvocationTargetException e) {
				// Ignora
			} catch (NoSuchMethodException e) {
				// Ignora
			} catch (InstantiationException e) {
				// Ignora
			}
		}
		
		return value;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property,
			Object value) {
		super.setValue(context, base, property, value);
	}
}
