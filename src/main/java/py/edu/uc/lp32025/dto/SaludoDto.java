package py.edu.uc.lp32025.dto;

public class SaludoDto extends BaseResponseDto {

    private String mensaje;

    // Constructor para respuestas exitosas
    public SaludoDto(String mensaje) {
        super(200, null, null); // 200 OK
        this.mensaje = mensaje;
    }

    // Constructor para respuestas con error
    public SaludoDto(int status, String error, String userError) {
        super(status, error, userError);
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}