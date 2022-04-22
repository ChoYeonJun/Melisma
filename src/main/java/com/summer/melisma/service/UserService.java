package com.summer.melisma.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.summer.melisma.model.dto.LoginReqUserDto;
import com.summer.melisma.model.dto.UserDto;
import com.summer.melisma.model.entity.UserEntity;
import com.summer.melisma.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void create(LoginReqUserDto reqUserDto) {
        // 1. 넘어온 LoginReqUserDto로 UserDto 생성. (이때 password + salt값으로 password데이터 생성) 
        // 2. 넘어온 UserDto의 username와 동일한 이름을 가진 유저 조회 -> UQ설정을 해놓았기 때문에 예외처리만 해주기
        // 3. 유저 생성
        UUID userId = UUID.randomUUID();
        UUID salt = UUID.randomUUID();

        // String saltedPassword = reqUserDto.getPassword() + salt;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(reqUserDto.getPassword());

        UserDto dto = UserDto.builder()
            .id(userId)
            .username(reqUserDto.getUsername())
            .password(encodedPassword)
            .salt(salt)
            .role("USER")
            .createdAt(LocalDateTime.now())
            .build();
        
        UserEntity entity = UserEntity.toEntity(dto);
        userRepository.save(entity);
    }

    public UserEntity login(LoginReqUserDto reqUserDto) {
        // 1. reqUserDto로 넘어온 username과 대응되는 데이터가 있는지 확인
        // 2. 비밀번호 확인
        UserEntity storedEntity = userRepository.findByUsername(reqUserDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(reqUserDto.getUsername()));

            UUID salt = storedEntity.getSalt();
//            String loginPassword = reqUserDto.getPassword() + salt;
            String loginPassword = reqUserDto.getPassword();

//            if(storedEntity.getPassword().equals(loginPassword)) {
//                return storedEntity.getId();
//            }

            if(!passwordEncoder.matches(loginPassword,storedEntity.getPassword())) {
                throw new BadCredentialsException("Password not matched");
            }

        return storedEntity;
//        throw new NullPointerException();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getUsername(), user.getPassword(), user.getAuthorities());

        // Optional<UserEntity> user = userRepository.findByUsername(username);

        // if(user.isPresent()) {
        //     return new User(user.get().getUsername(), user.get().getPassword(), user.get().getAuthorities());
        // }else {
        //     throw new UsernameNotFoundException(username);
        // }
    }

    public UserEntity authenticateByEmailAndPassword(String username, String password) {
        UserEntity member = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return member;
    }

    public UUID getUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        return userEntity.getId();
    }
}
