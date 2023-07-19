package com.simbioff.simbioff.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.simbioff.simbioff.services.BackBlazeServices;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class FileController {

	private final BackBlazeServices backBlazeServices;
	
	@PostMapping
	public ResponseEntity<Object> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam(value = "isProfilePicture") boolean isProfilePicture) throws B2Exception, IllegalStateException, IOException {
		return backBlazeServices.uploadFile(file, isProfilePicture, null);
		
	}
	
	@GetMapping("/profile")
	public ResponseEntity<Object> getProfilePicture() throws B2Exception {
		return backBlazeServices.getProfilePicture();
	}
	
	@GetMapping("/atestado")
	public ResponseEntity<Object> getAtestadoById(@RequestParam (value = "eventId") int eventId) throws B2Exception {
		return backBlazeServices.getAtestadosById(eventId);
	}
}
