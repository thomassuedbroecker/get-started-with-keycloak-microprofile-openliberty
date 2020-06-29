package com.ibm.authors;

// JAX-RS
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;

// Keycloak and security
import org.eclipse.microprofile.auth.LoginConfig;
import javax.annotation.security.DeclareRoles;

@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({"authors-role-cloud-native-starter"})
@ApplicationPath("v1")
public class AuthorsApplication extends Application {
    
    public AuthorsApplication(){
        System.out.println("... [Author] start AuthorsApplication");
	}
}