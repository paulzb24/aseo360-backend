package com.aseo360.aseo360.servicio.implementacion;

import com.aseo360.aseo360.dto.AsistenciaDTO;
import com.aseo360.aseo360.modelo.Asistencia;
import com.aseo360.aseo360.modelo.Empleado;
import com.aseo360.aseo360.repositorio.IAsistenciaRepositorio;
import com.aseo360.aseo360.repositorio.IEmpleadoRepositorio;
import com.aseo360.aseo360.servicio.interfaz.IAsistenciaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaServicio implements IAsistenciaServicio {
        private final IAsistenciaRepositorio asistenciaRepositorio;
        private final IEmpleadoRepositorio empleadoRepositorio;
        private final ConfiguracionSistemaServicio configuracionServicio;

        @Autowired
        public AsistenciaServicio(IAsistenciaRepositorio asistenciaRepositorio,
                        IEmpleadoRepositorio empleadoRepositorio,
                        ConfiguracionSistemaServicio configuracionServicio) {
                this.asistenciaRepositorio = asistenciaRepositorio;
                this.empleadoRepositorio = empleadoRepositorio;
                this.configuracionServicio = configuracionServicio;
        }

        @Override
        public List<AsistenciaDTO> listarAsistencias() throws Exception {
                List<Asistencia> asistencias = this.asistenciaRepositorio.findAll();
                List<AsistenciaDTO> asistenciaDTOS = asistencias.stream().map(
                                asistencia -> new AsistenciaDTO(
                                                asistencia.getIdAsistencia(),
                                                asistencia.getEmpleado().getNombreCompleto(),
                                                asistencia.getEmpleado().getCorreo(),
                                                asistencia.getEmpleado().getDni(),
                                                asistencia.getEmpleado().getNumeroCelular(),
                                                asistencia.getFecha(),
                                                asistencia.getHoraEntrada(),
                                                asistencia.getHoraSalida(),
                                                asistencia.getEstado(),
                                                asistencia.getComentarios()))
                                .toList();
                return asistenciaDTOS;
        }

        @Override
        public List<Asistencia> listarAsistenciasPorFecha(LocalDate fecha) throws Exception {
                return this.asistenciaRepositorio.findByFecha(fecha);
        }

        @Override
        public List<AsistenciaDTO> listarAsistenciasPorEmpleado(String correo) throws Exception {
                Empleado empleado = this.empleadoRepositorio.findByCorreo(correo)
                                .orElseThrow(() -> new Exception("Error: Empleado no encontrado"));
                List<Asistencia> asistencias = this.asistenciaRepositorio.findByEmpleado(empleado);
                List<AsistenciaDTO> asistenciaDTOS = asistencias.stream().map(
                                asistencia -> new AsistenciaDTO(
                                                asistencia.getIdAsistencia(),
                                                asistencia.getEmpleado().getNombreCompleto(),
                                                asistencia.getEmpleado().getCorreo(),
                                                asistencia.getEmpleado().getDni(),
                                                asistencia.getEmpleado().getNumeroCelular(),
                                                asistencia.getFecha(),
                                                asistencia.getHoraEntrada(),
                                                asistencia.getHoraSalida(),
                                                asistencia.getEstado(),
                                                asistencia.getComentarios()))
                                .toList();

                return asistenciaDTOS;
        }

        @Override
        public Asistencia registrarAsistencia(String correo) throws Exception {
                Asistencia asistencia = new Asistencia();
                LocalDate hoy = LocalDate.now();
                // Obtener la hora actual en la zona horaria de Peru (America/Lima)
                ZoneId zonePeru = ZoneId.of("America/Lima");
                ZonedDateTime horaActualPeru = ZonedDateTime.now(zonePeru);
                LocalTime horaLocal = horaActualPeru.toLocalTime();
                Time horaActualSql = Time.valueOf(horaLocal);

                Empleado empleado = this.empleadoRepositorio.findByCorreo(correo)
                                .orElseThrow(() -> new Exception(
                                                "Error: Empleado no encontrado con correo: " + correo));

                // Validar que no exista ya un registro de asistencia para hoy
                Optional<Asistencia> asistenciaExistente = this.asistenciaRepositorio.findByEmpleadoAndFecha(empleado,
                                hoy);
                if (asistenciaExistente.isPresent()) {
                        throw new Exception("Ya registraste tu asistencia el día de hoy");
                }

                asistencia.setFecha(hoy);
                asistencia.setHoraEntrada(horaActualSql);
                asistencia.setEmpleado(empleado);

                // Determinar estado segun configuracion de horarios
                String estado = determinarEstadoEntrada(horaLocal);
                asistencia.setEstado(estado);

                if ("Tardanza".equals(estado)) {
                        String horaEntradaConfig = this.configuracionServicio.obtenerValor("ASISTENCIA_HORA_ENTRADA");
                        asistencia.setComentarios("Llego tarde. Hora establecida: " + horaEntradaConfig);
                }

                return this.asistenciaRepositorio.save(asistencia);
        }

        @Override
        public Asistencia registrarAsistenciaAdmin(AsistenciaDTO dto) throws Exception {
                Empleado empleado = this.empleadoRepositorio.findByCorreo(dto.getCorreoEmpleado())
                                .orElseThrow(() -> new Exception(
                                                "Empleado no encontrado con correo: " + dto.getCorreoEmpleado()));

                LocalDate fecha = dto.getFecha() != null ? dto.getFecha() : LocalDate.now();

                Optional<Asistencia> existente = this.asistenciaRepositorio.findByEmpleadoAndFecha(empleado, fecha);
                if (existente.isPresent()) {
                        throw new Exception("Ya existe un registro de asistencia para este empleado en esta fecha");
                }

                Asistencia asistencia = new Asistencia();
                asistencia.setEmpleado(empleado);
                asistencia.setFecha(fecha);
                asistencia.setEstado(dto.getEstado() != null ? dto.getEstado() : "Falta");
                asistencia.setHoraEntrada(dto.getHoraEntrada());
                asistencia.setHoraSalida(dto.getHoraSalida());
                asistencia.setComentarios(dto.getComentario());

                return this.asistenciaRepositorio.save(asistencia);
        }

        @Override
        public Asistencia registrarSalida(Long idAsistencia) throws Exception {
                Asistencia asistencia = this.asistenciaRepositorio.findById(idAsistencia)
                                .orElseThrow(() -> new Exception("Error: no se puedo encontrar el registro"));

                // Validar que no se haya registrado salida ya
                if (asistencia.getHoraSalida() != null) {
                        throw new Exception("Ya registraste tu salida el día de hoy");
                }

                // Obtener la hora actual en la zona horaria de Peru (America/Lima)
                ZoneId zonePeru = ZoneId.of("America/Lima");
                ZonedDateTime horaActualPeru = ZonedDateTime.now(zonePeru);
                LocalTime horaLocal = horaActualPeru.toLocalTime();
                Time horaActualSql = Time.valueOf(horaLocal);

                asistencia.setHoraSalida(horaActualSql);

                // Verificar si salio antes de la hora configurada
                try {
                        String horaSalidaConfig = this.configuracionServicio.obtenerValor("ASISTENCIA_HORA_SALIDA");
                        LocalTime horaSalida = LocalTime.parse(horaSalidaConfig);
                        if (horaLocal.isBefore(horaSalida)) {
                                String comentarioActual = asistencia.getComentarios() != null
                                                ? asistencia.getComentarios() + " | "
                                                : "";
                                asistencia.setComentarios(comentarioActual + "Salio antes de la hora establecida: "
                                                + horaSalidaConfig);
                        }
                } catch (Exception e) {
                        throw new Exception(
                                        "No se encontro configuracion de ASISTENCIA_HORA_SALIDA. El administrador debe configurarla.");
                }

                return this.asistenciaRepositorio.save(asistencia);
        }

        @Override
        public Asistencia modificarAsistencia(AsistenciaDTO modificarAsistencia) throws Exception {
                Asistencia asistencia = this.asistenciaRepositorio.findById(modificarAsistencia.getIdAsistencia())
                                .orElseThrow(() -> new Exception("Error : No existe la asistencia"));
                asistencia.setFecha(modificarAsistencia.getFecha());
                asistencia.setHoraEntrada(modificarAsistencia.getHoraEntrada());
                asistencia.setHoraSalida(modificarAsistencia.getHoraSalida());
                asistencia.setComentarios(modificarAsistencia.getComentario());
                asistencia.setEstado(modificarAsistencia.getEstado());
                return this.asistenciaRepositorio.save(asistencia);
        }

        @Override
        public Asistencia modificarComentario(AsistenciaDTO modificarComentario) throws Exception {
                Asistencia asistencia = this.asistenciaRepositorio.findById(modificarComentario.getIdAsistencia())
                                .orElseThrow(() -> new Exception("Error : No existe la asistencia"));
                asistencia.setComentarios(modificarComentario.getComentario());
                return this.asistenciaRepositorio.save(asistencia);
        }

        @Override
        public Asistencia modificarEstado(AsistenciaDTO modificarEstado) throws Exception {
                Asistencia asistencia = this.asistenciaRepositorio.findById(modificarEstado.getIdAsistencia())
                                .orElseThrow(() -> new Exception("Error : No existe la asistencia"));
                asistencia.setEstado(modificarEstado.getEstado());
                return this.asistenciaRepositorio.save(asistencia);
        }

        // Metodo privado para determinar el estado de entrada segun la configuracion
        private String determinarEstadoEntrada(LocalTime horaActual) throws Exception {
                try {
                        String horaEntradaConfig = this.configuracionServicio.obtenerValor("ASISTENCIA_HORA_ENTRADA");
                        String toleranciaConfig = this.configuracionServicio.obtenerValor("ASISTENCIA_TOLERANCIA");

                        LocalTime horaEntrada = LocalTime.parse(horaEntradaConfig);
                        int tolerancia = Integer.parseInt(toleranciaConfig);
                        LocalTime horaLimite = horaEntrada.plusMinutes(tolerancia);

                        if (!horaActual.isAfter(horaLimite)) {
                                return "Presente";
                        } else {
                                return "Tardanza";
                        }
                } catch (Exception e) {
                        throw new Exception(
                                        "No se encontro configuracion de horarios. El administrador debe configurar ASISTENCIA_HORA_ENTRADA y ASISTENCIA_TOLERANCIA.");
                }
        }
}
