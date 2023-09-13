package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.checkservice.CheckService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CheckService checkService;

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.makeDtoInUser(userDto);
        userRepository.save(user);
        return UserMapper.makeUserInDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> idCount, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (idCount == null) {
            return UserMapper.makeUserDtoList(userRepository.findAll(pageRequest));
        } else {
            return UserMapper.makeUserDtoList(userRepository.findByIdInOrderByIdAsc(idCount, pageRequest));
        }
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        checkService.checkUser(userId);
        userRepository.deleteById(userId);
    }
}