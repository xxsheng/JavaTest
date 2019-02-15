package com.warm.share.model.dto;


import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * if (!FormatUtils.phone(loginDto.getPhone())) {
 * return BaseModel.fail(302, "手机号格式不正确");
 * }
 * <p>
 * if (!FormatUtils.password(loginDto.getPassword())) {
 * return BaseModel.fail(301, "密码格式不对");
 * }
 */

@Getter
@Setter
public class LoginDto {

    @NotBlank(message = "{phone.notnull}")
    @Length(min = 11, max = 11, message = "{phone.notnull}")
    private String phone;
    @NotBlank(message = "{password.notnull}")
    private String password;

}
