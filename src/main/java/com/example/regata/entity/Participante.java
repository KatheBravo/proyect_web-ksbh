package com.example.regata.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "participantes",
       uniqueConstraints = {
           @UniqueConstraint(name = "uq_partida_usuario", columnNames = {"partida_id", "usuario_id"}),
           @UniqueConstraint(name = "uq_partida_barco", columnNames = {"partida_id", "barco_id"})
       })
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_participante_partida"))
    private Partida partida;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_participante_usuario"))
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "barco_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_participante_barco"))
    private Barco barco;

    // Estado en partida (independiente del Barco global)
    @Column(name = "pos_x", nullable = false)
    private int posX;

    @Column(name = "pos_y", nullable = false)
    private int posY;

    @Column(name = "vel_x", nullable = false)
    private int velX;

    @Column(name = "vel_y", nullable = false)
    private int velY;

    @Column(nullable = false)
    private boolean vivo = true;

    @Column(name = "llego_meta", nullable = false)
    private boolean llegoMeta = false;

    @Column(nullable = false)
    private int orden = 0; // por si luego aplicas orden de turnos

    // Getters / Setters
    public Long getId() { return id; }
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Barco getBarco() { return barco; }
    public void setBarco(Barco barco) { this.barco = barco; }
    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }
    public int getPosY() { return posY; }
    public void setPosY(int posY) { this.posY = posY; }
    public int getVelX() { return velX; }
    public void setVelX(int velX) { this.velX = velX; }
    public int getVelY() { return velY; }
    public void setVelY(int velY) { this.velY = velY; }
    public boolean isVivo() { return vivo; }
    public void setVivo(boolean vivo) { this.vivo = vivo; }
    public boolean isLlegoMeta() { return llegoMeta; }
    public void setLlegoMeta(boolean llegoMeta) { this.llegoMeta = llegoMeta; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
