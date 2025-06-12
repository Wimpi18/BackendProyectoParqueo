package backendProyectoParqueo.enums;

public enum TipoAccion {
    // Para Clientes (vía Parqueo)
    BLOQUEO_CLIENTE_POR_PARQUEO,      
    DESBLOQUEO_CLIENTE_POR_PARQUEO,   
    INACTIVACION_CLIENTE_POR_PARQUEO, 
    ACTIVACION_CLIENTE_POR_PARQUEO,   

    // Para Cajeros
    INACTIVACION_CUENTA_CAJERO,       
    ACTIVACION_CUENTA_CAJERO,         

    // Para Administradores
    INACTIVACION_CUENTA_ADMIN,        
    ACTIVACION_CUENTA_ADMIN          
}