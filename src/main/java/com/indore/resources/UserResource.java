package com.indore.resources;

import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.indore.api.SearchParameter;
import com.indore.services.UserService;

/**
 * User resource to create an entity for indexing the document, search request, getting doc by id. .
 *
 * @author Amit Khandelwal
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response createUser(JsonNode user) {
        try {
            log.debug("User doc is {}", user);
            userService.add(user);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Error indexing doc {} and error is", user, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response getUser(@PathParam("id") @NotEmpty String id) {
        log.debug("Get request for user id {}", id);
        try {
            return Response.ok(userService.get(id)).build();
        } catch (IOException e) {
            log.error("Error getting user doc {} and error is {}", id, e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/search")
    @Timed
    public Response searchUsers(SearchParameter searchParameter) {
        log.debug("Search term {}", searchParameter.getSearchTerm());
        try {
            return Response.ok(userService.search(searchParameter.getSearchTerm())).build();
        } catch (IOException e) {
            log.error("Error getting search results for search term {} ", searchParameter.getSearchTerm(), e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response deleteUser(@PathParam("userId") String userId) {
        log.debug("Delete request for message id {}", userId);
        try {
            userService.delete(userId);
            return Response.ok().build();
        } catch (IOException e) {
            log.error("Error deleting user document {} and error is {}", userId, e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/auth/password/{password}")
    public Response authUser(@PathParam("password") String password, @QueryParam("emailId") String emailId,
                             @QueryParam("mobileNumber") Long mobileNumber) throws IOException {
        String testeMAIL = emailId;
        Long mbl = mobileNumber;
        String pass = password;
        log.debug("Authenticate request for user");
        try {
            if(userService.authUser(emailId, mobileNumber, password)) {
                return Response.ok().build();
            } else{
                return Response.noContent().build(); //need to correct this later so as to show this user is not present
            }

        } catch (IOException e) {
            log.error("User does not exist and error is {}", e);
            return Response.serverError().build();
        }

    }

   /** public Response getAllUser(){
        log.debug("Get record of all users");
        try {
            return Response.ok(userService.getAll()).build();
        } catch (IOException e) {
            log.error("Error getting user doc and error is {}", e);
            return Response.serverError().build();
        }

    }*/

}








