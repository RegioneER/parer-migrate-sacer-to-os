package it.eng.parer;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.*;

public class Profiles {

    public static class Lab implements QuarkusTestProfile {
	@Override
	public Set<String> tags() {
	    return new HashSet<>(Arrays.asList("lab"));
	}
    }

    public static class H2 implements QuarkusTestProfile {
	@Override
	public Set<String> tags() {
	    return new HashSet<>(Arrays.asList("unit"));
	}

	@Override
	public String getConfigProfile() {
	    return "h2";
	}

	@SuppressWarnings({
		"rawtypes", "unchecked" })
	@Override
	public Map getConfigOverrides() {
	    /*
	     * da yml non si riesce a impostare quarkus.hibernate-orm.database.generation e
	     * quarkus.hibernate-orm.database.generation.create-schemas quindi devo forzarlo da qua
	     */
	    return Collections.singletonMap("quarkus.hibernate-orm.database.generation",
		    "drop-and-create");
	}
    }

    public static class EndToEnd implements QuarkusTestProfile {
	@Override
	public Set<String> tags() {
	    return new HashSet<>(Arrays.asList("e2e"));
	}

	@Override
	public String getConfigProfile() {
	    return "h2";
	}

	@SuppressWarnings({
		"rawtypes", "unchecked" })
	@Override
	public Map getConfigOverrides() {
	    /*
	     * da yml non si riesce a impostare quarkus.hibernate-orm.database.generation e
	     * quarkus.hibernate-orm.database.generation.create-schemas quindi devo forzarlo da qua
	     */
	    return Collections.singletonMap("quarkus.hibernate-orm.database.generation",
		    "drop-and-create");
	}
    }

}
