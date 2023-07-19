package com.simbioff.simbioff.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backblaze.b2.client.B2ListFilesIterable;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.contentHandlers.B2ContentFileWriter;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.contentSources.B2ContentTypes;
import com.backblaze.b2.client.contentSources.B2FileContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2AccountAuthorization;
import com.backblaze.b2.client.structures.B2DownloadAuthorization;
import com.backblaze.b2.client.structures.B2DownloadByNameRequest;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2GetDownloadAuthorizationRequest;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import com.simbioff.simbioff.models.EventsModel;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.UserRepository;

@Service
public class BackBlazeServices {

	@Autowired
	UserServices userServices;

	@Autowired
	EventsServices eventServices;

	static final String APP_KEY_ID = "004a004bd8719c00000000003";
	static final String APP_KEY = "K004FtAtaZDWHaW7TAnYNLrUy41jGdU";
	static final String USER_AGENT = "simbioff";
	static final String BUCKET_ID = "fad030a46b2db8f781590c10";

	public ResponseEntity<Object> uploadFile(MultipartFile file, boolean isProfilePicture, List<EventsModel> eventos)
			throws B2Exception, IllegalStateException, IOException {

		if (isProfilePicture && !file.getContentType().equals("image/jpeg")
				&& !file.getContentType().equals("image/png")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Formato do arquivo não suportado. Utilize  JPEG ou PNG .");
		}

		if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("application/pdf")
				&& !file.getContentType().equals("image/png")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Formato do arquivo não suportado. Utilize  JPEG ou PNG .");
		}

		B2StorageClient client = B2StorageClientFactory.createDefaultFactory().create(APP_KEY_ID, APP_KEY, USER_AGENT);
		B2ListFilesIterable files = client.fileNames(BUCKET_ID);
		String fileName = "";

		if (isProfilePicture) {

			String user = userServices.getLoggedUser();
			Optional<UserModel> userOptional = userServices.findByEmail(user);
			fileName = userOptional.get().getIdUser().toString();
			File newFile = new File("/app/" + fileName + "."
					+ extractExtension(file.getOriginalFilename()));

			try (OutputStream os = new FileOutputStream(newFile)) {
				os.write(file.getBytes());
			}
			final B2ContentSource source = B2FileContentSource.build(newFile);
			B2UploadFileRequest request = B2UploadFileRequest
					.builder(BUCKET_ID, newFile.getName(), B2ContentTypes.B2_AUTO, source).build();

			for (var fileNameExists : files) {

				if (fileNameExists.getFileName().length() == 40) {
					if (fileNameExists.getFileName()
							.startsWith(newFile.getName().substring(0, fileNameExists.getFileName().length() - 4))) {

						client.deleteFileVersion(fileNameExists);
					}
				}
			}

			client.uploadSmallFile(request);
			client.close();
			
		} else {
			if (eventos.size() > 0) {
				for (var dayoff : eventos) {

					fileName = Integer.toString(dayoff.getEventId()) + "__" + dayoff.getIdUser().toString();
					File newFile = new File("/app/" + fileName + "."
							+ extractExtension(file.getOriginalFilename()));

					try (OutputStream os = new FileOutputStream(newFile)) {
						os.write(file.getBytes());
					}
					final B2ContentSource source = B2FileContentSource.build(newFile);
					B2UploadFileRequest request = B2UploadFileRequest
							.builder(BUCKET_ID, newFile.getName(), B2ContentTypes.B2_AUTO, source).build();

					for (var fileNameExists : files) {
						if (fileNameExists.getFileName().startsWith(String.valueOf(dayoff.getEventId()))) {
							client.deleteFileVersion(fileNameExists);
						}
					}
					client.uploadSmallFile(request);
					newFile.delete();
				}
			}
			client.close();

		}
		return ResponseEntity.status(HttpStatus.OK).body("Arquivo enviado com sucesso");
	}

	public ResponseEntity<Object> getProfilePicture() throws B2Exception {

		String user = userServices.getLoggedUser();
		Optional<UserModel> userOptional = userServices.findByEmail(user);

		if (userOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
		}

		B2StorageClient client = B2StorageClientFactory.createDefaultFactory().create(APP_KEY_ID, APP_KEY, USER_AGENT);

		B2ListFilesIterable files = client.fileNames(BUCKET_ID);
		String extension = "";
		for (var arquivo : files) {

			if (arquivo.getFileName().startsWith(userOptional.get().getIdUser().toString())) {

				if (arquivo.getContentType().equals("image/jpeg")) {
					extension = ".jpg";
				} else if (arquivo.getContentType().equals("image/png")) {
					extension = ".png";
				}
			}

		}

		final B2GetDownloadAuthorizationRequest request = B2GetDownloadAuthorizationRequest
				.builder(BUCKET_ID, userOptional.get().getIdUser().toString(), 28800).build();
		B2DownloadAuthorization auth = client.getDownloadAuthorization(request);
		String downloadUrl = client.getDownloadByNameUrl("Simbioff-files",
				userOptional.get().getIdUser().toString() + extension);

		return ResponseEntity.status(HttpStatus.OK)
				.body(downloadUrl + "?Authorization=" + auth.getAuthorizationToken());
	}

	public ResponseEntity<Object> getAtestadosById(int eventId) throws B2Exception {

		Optional<EventsModel> eventsOptional = eventServices.findEventById(eventId);

		B2StorageClient client = B2StorageClientFactory.createDefaultFactory().create(APP_KEY_ID, APP_KEY, USER_AGENT);

		if (!eventsOptional.get().getHasAtestado()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dayoff não possui atetado");
		}

		B2ListFilesIterable files = client.fileNames(BUCKET_ID);
		String extension = "";

		for (var arquivo : files) {

			if (arquivo.getFileName().startsWith(String.valueOf(eventsOptional.get().getEventId()))) {

				if (arquivo.getContentType().equals("image/jpeg")) {
					extension = ".jpg";
				} else if (arquivo.getContentType().equals("image/png")) {
					extension = ".png";
				} else if (arquivo.getContentType().equals("application/pdf")) {
					extension = ".pdf";
				}
			}

		}

		final B2GetDownloadAuthorizationRequest request = B2GetDownloadAuthorizationRequest
				.builder(BUCKET_ID, String.valueOf(eventsOptional.get().getEventId()), 28800).build();

		B2DownloadAuthorization auth = client.getDownloadAuthorization(request);

		String downloadUrl = client.getDownloadByNameUrl("Simbioff-files",
				String.valueOf(eventsOptional.get().getEventId()) + "__" + eventsOptional.get().getIdUser()
						+ extension);
		return ResponseEntity.status(HttpStatus.OK)
				.body(downloadUrl + "?Authorization=" + auth.getAuthorizationToken());
	}

	public String extractExtension(String fileName) {
		int i = fileName.lastIndexOf(".");
		String extension = fileName.substring(i + 1);
		if(extension.equals("jpeg")) {
			extension = "jpg";
		}
		return extension;
	}
}
