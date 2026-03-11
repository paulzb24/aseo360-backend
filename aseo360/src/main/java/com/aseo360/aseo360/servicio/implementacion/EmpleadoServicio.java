package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.EmpleadoRegistroDTO;
import com.aseo360.aseo360.modelo.Empleado;
import com.aseo360.aseo360.modelo.Rol;
import com.aseo360.aseo360.repositorio.IEmpleadoRepositorio;
import com.aseo360.aseo360.repositorio.IRolRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IEmpleadoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpleadoServicio implements IEmpleadoServicio {
    private final IEmpleadoRepositorio empleadoRepositorio;
    private final IRolRepositorio rolRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmpleadoServicio(IEmpleadoRepositorio empleadoRepositorio, PasswordEncoder passwordEncoder,
            IRolRepositorio rolRepositorio) {
        this.empleadoRepositorio = empleadoRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.rolRepositorio = rolRepositorio;
    }

    @Override
    public List<Empleado> listarEmpleados() {
        return this.empleadoRepositorio.findAll();
    }

    @Override
    public Empleado registrarEmpleado(EmpleadoRegistroDTO empleadoRegistro) throws Exception {
        Empleado newEmpleado = new Empleado();
        if (empleadoRegistro.getPassword() == null || empleadoRegistro.getPassword().isEmpty()) {
            throw new Exception("La contrasena es obligatoria para registrar un empleado");
        }
        if (this.empleadoRepositorio.existsByDni(empleadoRegistro.getDni())) {
            throw new Exception("Ya existe un empleado con el DNI: " + empleadoRegistro.getDni());
        }
        if (empleadoRegistro.getNumeroCelular() != null
                && this.empleadoRepositorio.existsByNumeroCelular(empleadoRegistro.getNumeroCelular())) {
            throw new Exception(
                    "Ya existe un empleado con el numero de celular: " + empleadoRegistro.getNumeroCelular());
        }
        Rol rol = this.rolRepositorio.findById(empleadoRegistro.getRolId())
                .orElseThrow(() -> new Exception("Rol no encontrado con id: " + empleadoRegistro.getRolId()));
        newEmpleado.setNombreCompleto(empleadoRegistro.getNombreCompleto());
        newEmpleado.setFotoPerfil(empleadoRegistro.getFotoPerfil());
        newEmpleado.setRol(rol);
        newEmpleado.setDni(empleadoRegistro.getDni());
        newEmpleado.setNumeroCelular(empleadoRegistro.getNumeroCelular());
        newEmpleado.setPassword(this.passwordEncoder.encode(empleadoRegistro.getPassword()));
        newEmpleado.setCorreo("AS" + empleadoRegistro.getDni() + "@gmail.com");
        newEmpleado.setFechaRegistro(LocalDate.now());
        newEmpleado.setEstado(empleadoRegistro.getEstado());
        return this.empleadoRepositorio.save(newEmpleado);
    }

    @Override
    public Empleado modificarEmpleado(EmpleadoRegistroDTO empleadoRegistroDTO) throws Exception {
        if (empleadoRegistroDTO.getId() == null || empleadoRegistroDTO.getId() <= 0) {
            throw new Exception("El id es necesario para modificar empleado!");
        }

        // CORRECCIÓN 1: Usar getId() en lugar de getRolId() para buscar al empleado
        Empleado empleado = this.empleadoRepositorio.findById(empleadoRegistroDTO.getId())
                .orElseThrow(() -> new Exception("No se encontró usuario"));

        empleado.setEstado(empleadoRegistroDTO.getEstado());

        // Lógica para contraseña opcional
        if (empleadoRegistroDTO.getPassword() != null && !empleadoRegistroDTO.getPassword().isEmpty()) {
            empleado.setPassword(this.passwordEncoder.encode(empleadoRegistroDTO.getPassword()));
        }
        if (this.empleadoRepositorio.existsByDniAndIdEmpleadoNot(empleadoRegistroDTO.getDni(),
                empleado.getIdEmpleado())) {
            throw new Exception("Este dni ya esta en uso");
        }
        if (this.empleadoRepositorio.existsByNumeroCelularAndIdEmpleadoNot(empleadoRegistroDTO.getNumeroCelular(),
                empleado.getIdEmpleado())) {
            throw new Exception("Este numero ya esta en uso");
        }
        empleado.setNombreCompleto(empleadoRegistroDTO.getNombreCompleto());
        empleado.setFotoPerfil(empleadoRegistroDTO.getFotoPerfil());
        empleado.setDni(empleadoRegistroDTO.getDni());
        empleado.setNumeroCelular(empleadoRegistroDTO.getNumeroCelular());

        // Aquí sí está bien usar getRolId para buscar el rol
        empleado.setRol(this.rolRepositorio.findById(empleadoRegistroDTO.getRolId())
                .orElseThrow(() -> new Exception("Error: no se encontró el rol")));

        return this.empleadoRepositorio.save(empleado);
    }

    @Override
    public Empleado buscarPorId(Long id) throws Exception {
        return this.empleadoRepositorio.findById(id)
                .orElseThrow(() -> new Exception("Empleado con id " + id + " no encontrado"));
    }

    @Override
    public void eliminarPorId(Long id) {
        this.empleadoRepositorio.deleteById(id);
    }
}
