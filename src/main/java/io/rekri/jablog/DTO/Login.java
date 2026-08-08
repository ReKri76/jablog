package io.rekri.jablog.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Login {
    @NotEmpty
    private String password;
    @NotEmpty
    private String nickname;
}
