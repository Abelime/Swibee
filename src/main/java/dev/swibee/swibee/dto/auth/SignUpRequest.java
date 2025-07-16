package dev.swibee.swibee.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    
    @NotBlank(message = "아이디는 필수입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문과 숫자만 사용 가능합니다.")
    private String memberId;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다.")
    private String password;
    
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;
    
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하여야 합니다.")
    private String name;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호는 10-11자리 숫자만 입력 가능합니다.")
    private String phone;
} 