package com.learning.user.service;

import com.learning.user.dto.UserDto;
import com.learning.user.model.Role;
import com.learning.user.model.User;
import com.learning.user.repository.RoleRepository;
import com.learning.user.repository.UserRepository;
import com.learning.user.util.MapperUtil;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(UserDto userDto) throws Exception{
        Optional<User> optionalUser = userRepository.findByEmailId(userDto.getEmailId());
        if(optionalUser.isPresent()){
            throw new Exception("User Already Present!");
        }else{
            User user = mapperUtil.userDtoToUserEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            if(user.getUserType() == 1){
                user.setMfaSecret(generateMFASecret());
                List<Role> roles = new ArrayList<>();
                if(userDto.isSuperAdmin())
                    roles.add(roleRepository.findById(1l).get());
                else
                    roles.add(roleRepository.findById(2l).get());
                user.setRoles(roles);
            }else{
                List<Role> roles = new ArrayList<>();
                roles.add(roleRepository.findById(3l).get());
                user.setRoles(roles);
            }
            userRepository.save(user);
        }
    }

    public UserDto getUser(long userId) throws Exception{
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            return mapperUtil.userEntityToUserDto(optionalUser.get());
        }else {
            throw new Exception("User Not Found!");
        }
    }

    public void resetPassword(long userId, String password) throws Exception{
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }else {
            throw new Exception("User Not Found!");
        }
    }

    private String generateMFASecret(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
}
