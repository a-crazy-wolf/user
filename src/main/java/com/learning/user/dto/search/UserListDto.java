package com.learning.user.dto.search;

import com.learning.user.dto.UserDto;
import lombok.Data;

import java.util.List;

@Data
public class UserListDto {
    private List<UserDto> userList;
    private long totalSize;
    private int batchSize;
    private int batchOffset;
}
