package com.script.controller;

@RestController
public class RestController {
	
	// @RequestMapping(value = "/users", method = RequestMethod.GET, produces =
	// {
	// MediaType.APPLICATION_JSON_VALUE })
	// public List<User> getUsers() {
	//
	// System.out.println("Rest Controller");
	// List<User> listOfUsers = new ArrayList<User>();
	// listOfUsers = createUserList();
	// return listOfUsers;
	// }
	
	// Utiliy method to create user list.
	// public List<User> createUserList() {
	//
	// User user = new User();
	// user.setUserId(1);
	// user.setFirstName("First");
	// user.setLastName("Last");
	// user.setEmail("email@email.com");
	// user.setRecruiterId(1);
	// user.setSalesforce(1);
	// Role role = new Role(1, "Title");
	// user.setRole(role);
	// String now = "";
	// user.setDatePassIssued(now);
	//
	// List<User> listOfUsers = new ArrayList<User>();
	// listOfUsers.add(user);
	// return listOfUsers;
	// }
}
