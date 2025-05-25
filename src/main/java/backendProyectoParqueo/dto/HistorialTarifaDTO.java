package backendProyectoParqueo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import backendProyectoParqueo.enums.TipoCliente;
import backendProyectoParqueo.enums.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistorialTarifaDTO {

    private TipoVehiculo tipoVehiculo;
    private TipoCliente tipoCliente;
    private BigDecimal monto;
    private String nombreCompleto;
    private LocalDateTime fechaInicio;
    private Long cantidadTarifas;

    // Getters y Setters

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getCantidadTarifas() {
        return cantidadTarifas;
    }

    public void setCantidadTarifas(Long cantidadTarifas) {
        this.cantidadTarifas = cantidadTarifas;
    }
}
