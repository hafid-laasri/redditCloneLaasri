package ma.formation.redditClone.service;

import lombok.AllArgsConstructor;
import ma.formation.redditClone.dto.RegisterRequest;
import ma.formation.redditClone.exception.SpringRedditException;
import ma.formation.redditClone.model.NotificationEmail;
import ma.formation.redditClone.model.User;
import ma.formation.redditClone.model.VerificationToken;
import ma.formation.redditClone.repository.UserRepository;
import ma.formation.redditClone.repository.VerificationTokenRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user=new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("SVP activer votre adresse email",
                user.getEmail(),"merci d'avoir rejoint hafid Reddit, "
        +"cliquez sur le lien pour activer votre compte"
                + "http://localhost:8080/api/auth/accountVerification/"+token));

    }

    private String generateVerificationToken(User user){
       String token= UUID.randomUUID().toString();
        VerificationToken verificationToken =new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }


    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken= verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()->new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }
    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username= verificationToken.getUser().getUsername();
        User user =userRepository.findByUsername(username).orElseThrow(()->new SpringRedditException("utilisateur non disponible"+username));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
