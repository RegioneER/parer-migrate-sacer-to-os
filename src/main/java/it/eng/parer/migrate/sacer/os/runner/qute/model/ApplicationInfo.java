/**
 *
 */
package it.eng.parer.migrate.sacer.os.runner.qute.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class ApplicationInfo {

    @ConfigProperty(name = "quarkus.application.name")
    String name;

    @ConfigProperty(name = "quarkus.http.static-resources.index-page")
    String index;

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "-")
    Optional<String> version;

    @ConfigProperty(name = "quarkus.swagger-ui.path", defaultValue = "q/swagger-ui")
    Optional<String> swagger;

    Properties git;

    @PostConstruct
    public void init() throws IOException {
	try (InputStream input = getClass().getResourceAsStream("/git.properties")) {
	    git = new Properties();
	    // load a properties file
	    git.load(input);
	}
    }

    public Optional<String> getVersion() {
	return version;
    }

    public Optional<String> getSwagger() {
	return swagger;
    }

    public String getName() {
	return name;
    }

    public Properties getGit() {
	return git;
    }

    public String getIndex() {
	return index;
    }

}
