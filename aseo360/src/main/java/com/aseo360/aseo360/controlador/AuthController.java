package com.aseo360.aseo360.controlador;

import com.aseo360.aseo360.dto.AuthResponse;
import com.aseo360.aseo360.dto.ClienteRegistroDTO;
import com.aseo360.aseo360.dto.EmpleadoRegistroDTO;
import com.aseo360.aseo360.dto.LoginRequest;
import com.aseo360.aseo360.repositorio.IClienteRepositorio;
import com.aseo360.aseo360.repositorio.IEmpleadoRepositorio;
import com.aseo360.aseo360.security.JwtUtils;
import com.aseo360.aseo360.servicio.interfaz.IClienteServicio;
import com.aseo360.aseo360.servicio.interfaz.IEmpleadoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IEmpleadoRepositorio empleadoRepositorio;
    @Autowired
    private IClienteRepositorio clienteRepositorio;
    @Autowired
    private IClienteServicio clienteServicio;
    @Autowired
    private IEmpleadoServicio empleadoServicio;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        // Verificar si el correo existe en el sistema
        boolean existeEmpleado = empleadoRepositorio.findByCorreo(loginRequest.getCorreo()).isPresent();
        boolean existeCliente = clienteRepositorio.findByCorreo(loginRequest.getCorreo()).isPresent();

        if (!existeEmpleado && !existeCliente) {
            throw new Exception("El correo no esta registrado");
        }

        // Si el correo existe, validar la contrasena
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getCorreo(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("La contraseña es incorrecta");
        } catch (DisabledException e) {
            throw new Exception("La cuenta se encuentra deshabilitada");
        }

        String token = jwtUtils.generateToken(loginRequest.getCorreo());

        // Buscamos datos extras para el Frontend
        if (existeEmpleado) {
            var emp = empleadoRepositorio.findByCorreo(loginRequest.getCorreo()).get();
            return ResponseEntity.ok(new AuthResponse(token, emp.getNombreCompleto(), emp.getFotoPerfil(),
                    emp.getRol().getNombre(), "EMPLEADO"));
        }

        var cust = clienteRepositorio.findByCorreo(loginRequest.getCorreo()).get();
        return ResponseEntity
                .ok(new AuthResponse(token, cust.getNombreCompleto(), cust.getFotoPerfil(), "CLIENTE", "CLIENTE"));
    }

    @PostMapping("/registrar/empleado")
    public ResponseEntity<?> registrarEmpleado(@Valid @RequestBody EmpleadoRegistroDTO empleadoRegistro)
            throws Exception {
        return ResponseEntity.ok(this.empleadoServicio.registrarEmpleado(empleadoRegistro));
    }

    @PostMapping("/registrar/cliente")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRegistroDTO clienteRegistro) throws Exception {
        return ResponseEntity.ok(this.clienteServicio.registrarCliente(clienteRegistro));
    }
}
