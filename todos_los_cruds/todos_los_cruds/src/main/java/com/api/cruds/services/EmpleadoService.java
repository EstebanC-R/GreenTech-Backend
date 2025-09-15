package com.api.cruds.services;

import com.api.cruds.models.EmpleadoModel;
import com.api.cruds.models.EstadoEmpleado;
import com.api.cruds.repositories.IEmpleadosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private IEmpleadosRepository empleadosRepository;

    public ArrayList<EmpleadoModel> getEmpleado(){
        return (ArrayList<EmpleadoModel>) empleadosRepository.findAll();
    }

    public Optional<EmpleadoModel> getEmpleadoById(Long id) {
        return empleadosRepository.findById(id);
    }

    public List<EmpleadoModel> buscarConFiltros(String nombre, String cedula, EstadoEmpleado estado) {
        return empleadosRepository.buscarConFiltros(nombre, cedula, estado);
    }

    public EmpleadoModel saveEmpleado(EmpleadoModel empleado) {
        return empleadosRepository.save(empleado);
    }

    public EmpleadoModel updateById(EmpleadoModel request, Long id) {
        EmpleadoModel empleado = empleadosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + id));

        empleado.setNombre(request.getNombre());
        empleado.setAsignacion(request.getAsignacion());
        empleado.setCedula(request.getCedula());
        empleado.setFechaNacimiento(request.getFechaNacimiento());
        empleado.setFechaDeIngreso(request.getFechaDeIngreso());
        empleado.setCelular(request.getCelular());
        empleado.setCorreo(request.getCorreo());
        empleado.setEstado(request.getEstado());

        return empleadosRepository.save(empleado);
    }

    public Boolean deleteEmpleado(Long id) {
        try {
            if (empleadosRepository.existsById(id)) {
                empleadosRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Métodos utilitarios adicionales
    public boolean existsByUniqueFields(String cedula, String correo, String celular) {
        return empleadosRepository.existsByCedula(cedula) ||
                empleadosRepository.existsByCorreo(correo) ||
                empleadosRepository.existsByCelular(celular);
    }

    public List<EmpleadoModel> getEmpleadosByEstado(EstadoEmpleado estado) {
        return empleadosRepository.findByEstado(estado);
    }
}