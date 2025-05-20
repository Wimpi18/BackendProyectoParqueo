package backendProyectoParqueo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 10, nullable = false, unique = true)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVehiculo tipo;

    @Column(name = "foto_delantera", nullable = false)
    private byte[] fotoDelantera;

    @Column(name = "foto_trasera", nullable = false)
    private byte[] fotoTrasera;

    public enum TipoVehiculo {
        Auto,
        Moto
    }
}