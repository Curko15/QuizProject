package ppks.projekt.backend.dto.userdto;

public record SignUpDto(String firstName,
                        String lastName,
                        String login,
                        char[] password,
                        String role
                        ){}

