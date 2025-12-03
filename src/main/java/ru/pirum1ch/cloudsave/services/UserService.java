package ru.pirum1ch.cloudsave.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pirum1ch.cloudsave.dto.UserDto;
import ru.pirum1ch.cloudsave.models.User;
import ru.pirum1ch.cloudsave.repositories.UserRepo;

@Service
@Log4j2
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, CustomUserDetailService customUserDetailService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
    }

    public User addNewUser(String email, String login, String name, String surname, String password)  {
        log.info("Создаем нового пользователя");
        if (isUserExists(email))
            throw new BadCredentialsException ("Такой пользователь уже зарегистрирован");

        User user = new User();
        try {
            UserDto userDto = UserDto.builder()
                    .id(userRepo.getLastIds() + 1)
                    .email(email)
                    .name(name)
                    .surname(surname)
                    .login(login)
                    .password(passwordEncoder.encode(password))
                    .build();
            BeanUtils.copyProperties(userDto, user);
            customUserDetailService.saveUser(user);
            log.info("Новый пользователь c ID {} и логином {} успешно создан", user.getId(), user.getEmail());
        }catch (BeansException exception){
            log.info("Ошибка при сопоставлении данных пользователя");
            exception.fillInStackTrace();
        }
        return user;
    }

    private boolean isUserExists(String email){
        boolean isUserExists = false;
                User user = getUser(email);
            if (user != null)
                isUserExists = user.getEmail().equals(email);
        return isUserExists;
    }

    public User getUser(String email){
        User user = userRepo.findByEmail(email);
        return user;
    }
}
