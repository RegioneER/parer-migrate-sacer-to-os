/**
 *
 */
package it.eng.parer.migrate.sacer.os.jpa.sacer.sequence;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

/**
 *
 * @author mbertuzzi
 */
public class NonMonotonicSequenceGenerator extends SequenceStyleGenerator {

    private static final long serialVersionUID = 1L;

    private static final String FORMAT = "%d%d";
    private SecureRandom rand;
    private static final int MAX = 9999;
    private static final int MIN = 1000;

    private int random() {
	return (rand.nextInt((MAX - MIN) + 1) + MIN);
    }

    public NonMonotonicSequenceGenerator() throws NoSuchAlgorithmException {
	rand = SecureRandom.getInstance("NativePRNGNonBlocking");
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
	    throws HibernateException {
	return Long.parseLong(String.format(FORMAT, random(), super.generate(session, object)));
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
	    throws MappingException {
	super.configure(
		new TypeConfiguration().getBasicTypeRegistry().getRegisteredType(Long.class),
		params, serviceRegistry);
    }
}
