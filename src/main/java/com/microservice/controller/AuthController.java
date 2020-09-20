package com.microservice.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.exceptions.BadRequestException;
import com.microservice.exceptions.NotFoundException;
import com.microservice.model.ERole;
import com.microservice.payload.JwtResponse;
import com.microservice.payload.LoginRequest;
import com.microservice.payload.MessageResponse;
import com.microservice.payload.SignupRequest;
import com.microservice.payload.UpdateUserRequest;
import com.microservice.payload.UserDto;

import com.microservice.config.jwt.JwtUtils;
import com.microservice.model.Permission;
import com.microservice.model.Role;
import com.microservice.model.User;
import com.microservice.repository.PermissionRepository;
import com.microservice.repository.RoleRepository;
import com.microservice.repository.UserRepository;
import com.microservice.service.UserDetailsImpl;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PermissionRepository permissionRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@GetMapping("/user")
	public Principal user(Principal principal) {
		log.info("enter user for {}", principal);
		return principal;
	}
	
	@GetMapping("/info")
	public UserDto getUserInfo(Principal principal) {
		log.info("enter getUserInfo for {}", principal);
		User user = userRepository.findByUsername(principal.getName())
				.orElseThrow(() -> new NotFoundException("User is not found"));
		
		return User.toDto(user);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		log.info("enter authenticateUser for {}", loginRequest);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());	
		
		User user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new NotFoundException("User is not found"));
		String[] permissions = user.getPermissions()
				.stream()
				.map(p -> p.getName())
				.toArray(String[]::new);

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles, permissions));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		log.info("enter registerUser for {}", signUpRequest);
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new BadRequestException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new NotFoundException("User is not found"));
		
		Map<String, Role> roles = roleRepository.findAll()
				.stream()
				.collect(Collectors.toMap(t -> t.getName().name(), t -> t));
		
		
		Map<String, Permission> permissions = permissionRepository.findAll()
				.stream()
				.collect(Collectors.toMap(t -> t.getName(), t -> t));
		
		Set<Role> mappedRole = request.getRoles()
				.stream()
				.map(roleName -> roles.get(roleName.toUpperCase()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		Set<Permission> mappedPermission = Arrays.asList(request.getPermissions())
				.stream()
				.map(pName -> permissions.get(pName.toLowerCase()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		if (Objects.isNull(mappedRole) || mappedRole.isEmpty()) {
			throw new BadRequestException("Role is empty");
		}
		if (Objects.isNull(mappedPermission) || mappedPermission.isEmpty()) {
			throw new BadRequestException("Permission is empty");
		}
		
		user.setRoles(mappedRole);
		user.setPermissions(mappedPermission);
		userRepository.save(user);
				
		return ResponseEntity.ok("user is updated");
	}
}
