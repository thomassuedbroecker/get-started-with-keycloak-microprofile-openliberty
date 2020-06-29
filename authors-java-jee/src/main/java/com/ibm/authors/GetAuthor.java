package com.ibm.authors;

import javax.enterprise.context.RequestScoped;

// Verify Strings
import java.util.Objects;

// JAX-RS
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

// JSON-B
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

// OPEN API
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

// Security with keycloak and MicroProfile "Java Web Token"
import javax.annotation.security.RolesAllowed;
import org.eclipse.microprofile.jwt.JsonWebToken;
import javax.inject.Inject;

@RequestScoped
@Path("/getauthor")
@OpenAPIDefinition(info = @Info(title = "Authors Service - keycloak", version = "1.0", description = "Authors Service APIs", contact = @Contact(url = "https://github.com/nheidloff/cloud-native-starter", name = "Niklas Heidloff"), license = @License(name = "License", url = "https://github.com/IBM/cloud-native-starter/blob/master/LICENSE")))
public class GetAuthor {
	
	@Inject private JsonWebToken tokenInformation;
	// usage of microprofile-config.properties file in src/main/resources/META-INF
	@RolesAllowed({"authors-role-cloud-native-starter"})
	@GET
	@APIResponses(value = {
		@APIResponse(
	      responseCode = "404",
	      description = "Author Not Found"
	    ),
	    @APIResponse(
	      responseCode = "200",
	      description = "Author with requested name",
	      content = @Content(
	        mediaType = "application/json",
	        schema = @Schema(implementation = Author.class)
	      )
	    ),
	    @APIResponse(
	      responseCode = "500",
	      description = "Internal service error"  	      
	    )
	})
	@Operation(
		    summary = "Get specific author",
		    description = "Get specific author"
	)

	public Response getAuthor(@Parameter(
            description = "The unique name of the author",
            required = true,
            example = "Niklas Heidloff",
            schema = @Schema(type = SchemaType.STRING))
			@QueryParam("name") String name) {

			if (tokenInformation != null){
				System.out.println("... [Author] MP JWT getIssuedAtTime " + tokenInformation.getIssuedAtTime() );
				System.out.println("... [Author] getIssuer: " + tokenInformation.getIssuer());
				System.out.println("... [Author] getRawToken: " + tokenInformation.getRawToken());
				System.out.println("... [Author] getTokenID: " + tokenInformation.getTokenID());
			}

			Author author = null;
			System.out.println("... [Author] Requested input name: " + name);
			
			if (Objects.equals("Thomas", name)) {
				System.out.println("... [Author] Requested name: " + name);
				author = new Author("Thomas Suedbroecker", 
				"https://twitter.com/tsuedbroecker", 
				"https://suedbroecker.net");
			} else {
				System.out.println("... [Author] Requested name: " + name);
				author = new Author(
				"Niklas Heidloff", 
				"https://twitter.com/nheidloff", 
				"http://heidloff.net");
			};

			Jsonb jsonb = JsonbBuilder.create();
			String author_json = jsonb.toJson(author); 

			System.out.println("... [Author] send getAuthor response: " + author_json.toString());

			return Response.ok(author_json).header("Access-Control-Allow-Origin", "*").build();
	}
}