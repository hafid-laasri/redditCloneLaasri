package ma.formation.redditClone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long userId;
    @NotBlank(message = "le username est obligatoire")
    private String username;
    @NotBlank(message = "le password est obligatoire")
    private String password;
    @Email
    @NotEmpty(message = "Email est obligatoire")
    private String email;
    private Instant created;
    private boolean enabled;
}
