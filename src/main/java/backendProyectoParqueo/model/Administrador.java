package backendProyectoParqueo.model;
 import jakarta.persistence.*;
 import java.util.UUID;

 @Entity
 @Table (name = "administrador")
public class Administrador {
    @Id
     @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

     public UUID getId() {
         return id;
     }

     public void setId(UUID id) {
         this.id = id;
     }
}
