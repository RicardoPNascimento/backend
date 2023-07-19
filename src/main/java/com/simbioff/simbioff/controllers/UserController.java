package com.simbioff.simbioff.controllers;

import com.lowagie.text.DocumentException;
import com.simbioff.simbioff.dto.DaysLeftDto;
import com.simbioff.simbioff.dto.UpdatePasswordDto;
import com.simbioff.simbioff.dto.UserDto;
import com.simbioff.simbioff.dto.UserListDto;
import com.simbioff.simbioff.models.PermissionModel;
import com.simbioff.simbioff.models.UserModel;
import com.simbioff.simbioff.repositories.UserRepository;
import com.simbioff.simbioff.services.AuthServices;
import com.simbioff.simbioff.services.EventsServices;
import com.simbioff.simbioff.services.UserServices;
import com.simbioff.simbioff.services.UsersPermissions;
import com.simbioff.simbioff.view.ExcelGenerator;
import com.simbioff.simbioff.view.PdfGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/users")
public class UserController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserServices userServices;

    @Autowired
    EventsServices eventsServices;

    @Autowired
    UsersPermissions usersPermissions;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthServices authServices;

    @Operation(summary = "Listar todos colaboradores", description = "Listar todos colaboradores", tags = {"Employees"})
    @ApiResponse(
            responseCode = "200",
            description = "Operação realizada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserListDto.class)
            ))

    @GetMapping
    public ResponseEntity<Object> getAllUsers(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "25") int size,
                                              @RequestParam(value = "enabled", defaultValue = "true") boolean enabled,
                                              @RequestParam(value = "sortDirection", defaultValue = "ASC") String sort) {
        if (keyword != null && !keyword.isEmpty()) {
            // Retrieve only a specific user by its name

            Page<UserModel> userModel = userServices.findAllUserByName(page, size, sort, keyword, enabled);
            userModel.getContent().forEach(user -> {
                try {
                    user.setDayOffsAvailable(eventsServices.dayOffsLeft(eventsServices.daysWorking(user), eventsServices.countDayOffRequest(user)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            if (userModel.isEmpty()) {
                return ResponseEntity.status(404).body("Usuário não encontrado");
            }
            return ResponseEntity.status(200).body((userModel));
        }

        Page<UserModel> users = userServices.findAllPage(page, size, sort, enabled);
        users.getContent().forEach(user -> {

            try {
                user.setDayOffsAvailable(eventsServices.dayOffsLeft(eventsServices.daysWorking(user), eventsServices.countDayOffRequest(user)));
            } catch (ParseException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
        });

        return ResponseEntity.status(200).body(users);
    }


    @Operation(summary = "Listar colaborador por id", description = "Listar colaborador por id", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserListDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    //User get user by id
    public ResponseEntity<Object> getUserById(@PathVariable(value = "id") UUID id) {
       
    	Optional<UserModel> userModel = userServices.findById(id);
		Optional<UserModel> loggedUser = userServices.findByEmail(userServices.getLoggedUser());


		if (!userModel.get().getEmail().equals(loggedUser.get().getEmail())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
		}
    	
        if (userModel.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }
        return ResponseEntity.status(200).body((userServices.findById(id)));
    }

    @Operation(summary = "Exportar dados de colaboradores para pdf", tags = {"Export Files"})
    @ApiResponse(responseCode = "200", description = "Dados exportados com sucesso"
            , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/pdf")
    )
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/export-to-pdf")
    public void generatePdfFile(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy::HH::mm::ss");

        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";

        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";

        response.setHeader(headerKey, headerValue);

        List<UserListDto> listUsers = userServices.getUserLIst();
        PdfGenerator pdfGenerator = new PdfGenerator();
        pdfGenerator.generate(listUsers, response);
    }

    @Operation(summary = "Exportar dados de colaboradores para excel", tags = {"Export Files"})
    @ApiResponse(responseCode = "200", description = "Dados exportados com sucesso"
            , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/vnd.ms-excel")
    )
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/export-to-excel")
    public void generateExcelFile(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy::HH::mm::ss");

        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";

        String headerValue = "attachment; filename=users_" + currentDateTime + ".xls";

        response.setHeader(headerKey, headerValue);

        List<UserListDto> listUsers = userServices.getUserLIst();
        ExcelGenerator excelGenerator = new ExcelGenerator(listUsers);
        excelGenerator.generateExcelFile(response);
    }

    @Operation(summary = "Criar novo colaborador", description = "Criar novo colaborador", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador criado com sucesso"
                    , content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Colaborador já existe"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserDto userDto) {
    	
    	List<String> errors = new ArrayList<>();
    	
        if (userServices.existsByEmail(userDto.getEmail())) {
        	errors.add("Email em uso");
        }
        
        if(userServices.existsByCpf(userDto.getCpf())) {
        	errors.add("CPF em uso");
        }
        
        if(userServices.existsByPixKey(userDto.getPixKey())) {
        	errors.add("Chave pix em uso");
        }
        
        if(userServices.existsByPhone(userDto.getPhone())) {
        	errors.add("Número de celular em uso");
        }
        
        if(errors.size() > 0) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        }
        
       
        var userModel = new UserModel();

        BeanUtils.copyProperties(userDto, userModel);
        userModel.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(8)));
        userModel.setAccountNonExpired(true);
        userModel.setAccountNonLocked(true);
        userModel.setCredentialsNonExpired(true);
        userModel.setEnabled(true);
        userModel.setEmail(userDto.getEmail().toLowerCase());
        var user = userServices.save(userModel);
        var permission = new PermissionModel();
        permission.setId_permission(2L);
        usersPermissions.insertUserPermission(user.getIdUser(), permission.getId_permission());
        // This line send a email to the new user automatically
        //authServices.mailNewUser(user.getEmail());

        return ResponseEntity.status(201).body(userServices.save(userModel));
    }

    @Operation(summary = "Excluir colaborador", description = "Excluir colaborador", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador excluído com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{email}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "email") String email) {
        if (!userServices.existsByEmail(email)) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        userServices.deleteByEmail(email);
        return ResponseEntity.status(200).body("Usuário excluído");
    }

    @Operation(summary = "Atualizar colaborador", description = "Atualizar colaborador", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador atualizado com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@Valid @PathVariable(value = "id") UUID id, @RequestBody UserDto userDto) {
    	
    	
    	Optional<UserModel> userModel = userServices.findById(id);
		Optional<UserModel> loggedUser = userServices.findByEmail(userServices.getLoggedUser());


		if (!userModel.get().getEmail().equals(loggedUser.get().getEmail())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
		}
        if (userModel.isEmpty()) {
            return ResponseEntity.status(404).body("Colaborador não encontrado");
        }
        userModel.get().setFullName(userDto.getFullName());
        userModel.get().setPhone(userDto.getPhone());
        userModel.get().setZip(userDto.getZip());
        userModel.get().setStreet(userDto.getStreet());
        userModel.get().setDistrict(userDto.getDistrict());
        userModel.get().setCity(userDto.getCity());
        userModel.get().setState(userDto.getState());
        userModel.get().setCountry(userDto.getCountry());
        userModel.get().setNumber(userDto.getNumber());
        userModel.get().setNearbyAirport(userDto.getNearbyAirport());
        userModel.get().setMaritalState(userDto.getMaritalState());
        userModel.get().setHasChildren(userDto.getHasChildren());
        userModel.get().setPixKey(userDto.getPixKey());
        userModel.get().setChildrenQty(userDto.getChildrenQty());
        userModel.get().setChildrenNames(userDto.getChildrenNames());

        // Other Information
        userModel.get().setFunctionAtWork(userDto.getFunctionAtWork());
        userModel.get().setPed(userDto.getPed());
        userModel.get().setResponsiblePed(userDto.getResponsiblePed());
        userModel.get().setEnglish(userDto.getEnglish());
        userModel.get().setEnglishTeacher(userDto.getEnglishTeacher());
        userModel.get().setTherapy(userDto.getTherapy());
        userModel.get().setResponsibleTherapist(userDto.getResponsibleTherapist());
        userModel.get().setUndergraduate(userDto.getUndergraduate());
        userModel.get().setUndergraduateCourseName(userDto.getUndergraduateCourseName());
        userModel.get().setGraduate(userDto.getGraduate());
        userModel.get().setGraduateCourseName(userDto.getGraduateCourseName());

        // Personal Information
        userModel.get().setShirtSize(userDto.getShirtSize());
        userModel.get().setShoeSize(userDto.getShoeSize());
        userModel.get().setFavouriteColor(userDto.getFavouriteColor());
        userModel.get().setFavouriteFood(userDto.getFavouriteFood());
        userModel.get().setBeachOrCamp(userDto.getBeachOrCamp());
        userModel.get().setPets(userDto.getPets());
        userModel.get().setHobbies(userDto.getHobbies());

        userServices.save(userModel.get());

        return ResponseEntity.status(200).body("Colaborador atualizado com sucesso");
    }

    @Operation(summary = "Atualizar colaborador", description = "Atualizar colaborador", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador atualizado com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/admin/{id}")
    public ResponseEntity<Object> adminUpdateUser(@Valid @PathVariable(value = "id") UUID id, @RequestBody UserDto userDto) {
        var userModel = userServices.findById(id);
        if (userModel.isEmpty()) {
            return ResponseEntity.status(404).body("Colaborador não encontrado");
        }
        userModel.get().setFullName(userDto.getFullName());
        userModel.get().setPhone(userDto.getPhone());
        userModel.get().setZip(userDto.getZip());
        userModel.get().setStreet(userDto.getStreet());
        userModel.get().setDistrict(userDto.getDistrict());
        userModel.get().setCity(userDto.getCity());
        userModel.get().setState(userDto.getState());
        userModel.get().setCountry(userDto.getCountry());
        userModel.get().setNumber(userDto.getNumber());
        userModel.get().setNearbyAirport(userDto.getNearbyAirport());
        userModel.get().setMaritalState(userDto.getMaritalState());
        userModel.get().setHasChildren(userDto.getHasChildren());
        userModel.get().setPixKey(userDto.getPixKey());
        userModel.get().setChildrenQty(userDto.getChildrenQty());
        userModel.get().setChildrenNames(userDto.getChildrenNames());

        // Admin access
        userModel.get().setEmail(userDto.getEmail());
        userModel.get().setCpf(userDto.getCpf());
        userModel.get().setBirthDate(userDto.getBirthDate());
        userModel.get().setStartOnTeam(userDto.getStartOnTeam());
        userModel.get().setDaysOffWithdrawn(userDto.getDaysOffWithdrawn());
        userModel.get().setEnabled(userDto.getEnabled());

        // Other Information
        userModel.get().setFunctionAtWork(userDto.getFunctionAtWork());
        userModel.get().setPed(userDto.getPed());
        userModel.get().setResponsiblePed(userDto.getResponsiblePed());
        userModel.get().setEnglish(userDto.getEnglish());
        userModel.get().setEnglishTeacher(userDto.getEnglishTeacher());
        userModel.get().setTherapy(userDto.getTherapy());
        userModel.get().setResponsibleTherapist(userDto.getResponsibleTherapist());
        userModel.get().setUndergraduate(userDto.getUndergraduate());
        userModel.get().setUndergraduateCourseName(userDto.getUndergraduateCourseName());
        userModel.get().setGraduate(userDto.getGraduate());
        userModel.get().setGraduateCourseName(userDto.getGraduateCourseName());

        // Personal Information
        userModel.get().setShirtSize(userDto.getShirtSize());
        userModel.get().setShoeSize(userDto.getShoeSize());
        userModel.get().setFavouriteColor(userDto.getFavouriteColor());
        userModel.get().setFavouriteFood(userDto.getFavouriteFood());
        userModel.get().setBeachOrCamp(userDto.getBeachOrCamp());
        userModel.get().setPets(userDto.getPets());
        userModel.get().setHobbies(userDto.getHobbies());

        userServices.save(userModel.get());

        return ResponseEntity.status(200).body("Colaborador atualizado com sucesso");
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PatchMapping("/new-password")
    public ResponseEntity<Object> updateUser(@RequestBody UpdatePasswordDto updatePasswordDto,
                                             @RequestParam(value = "idUser") UUID idUser) {
    	
    	Optional<UserModel> userModel = userServices.findById(idUser);
		Optional<UserModel> loggedUser = userServices.findByEmail(userServices.getLoggedUser());


		if (!userModel.get().getEmail().equals(loggedUser.get().getEmail())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
		}

        if (userModel.isEmpty()) {
            return ResponseEntity.status(404).body("Colaborador não encontrado");
        }
        Boolean passwordMatches = passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), userModel.get().getPassword());
        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Senha atual incorreta");
        }
        if (!updatePasswordDto.getPassword().equals(updatePasswordDto.getPasswordConfirm())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nova senha e confirmar nova senha não coincidem");
        }

        if(updatePasswordDto.getPassword().length() < 6) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nova senha deve conter no mínimo 6 caracteres");
        }

        userModel.get().setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));

        userRepository.save(userModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Senha atualizada com sucesso");
    }

    @Operation(summary = "Listar dias trabalhados e dayoff por id do colaborador", description = "Listar dias trabalhados e dayoff por id do colaborador", tags = {"Employees"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Usuário ou senha inválido"
                    , content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{id_user}/dayoff-status")
    public ResponseEntity<Object> getDaysOffLeft(@PathVariable("id_user") UUID id_user) {
        try {
            Optional<UserModel> user = userServices.findById(id_user);

            if(user.isEmpty()) {
                return ResponseEntity.badRequest().body("Dayoff não registrado");
            }

            DaysLeftDto daysLeftDto1 = new DaysLeftDto();
            daysLeftDto1.setWorkingDays(eventsServices.daysWorking(user.get()));
            daysLeftDto1.setDayOffRequestds(eventsServices.countDayOffRequest(user.get()));
            daysLeftDto1.setDaysOffRemmaing(eventsServices.dayOffsLeft(daysLeftDto1.getWorkingDays(), daysLeftDto1.getDayOffRequestds()));
            return ResponseEntity.ok().body(daysLeftDto1);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Dayoff não registrado");
        }
    }

}
