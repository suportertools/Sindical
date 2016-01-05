package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Filial;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conf_arrecadacao")
public class ConfiguracaoArrecadacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Filial filial;
    @Column(name = "is_certificado_faturamento_bruto_anual", columnDefinition = "boolean default false", nullable = false)
    private Boolean certificadoFaturementoBrutoAnual;

    public ConfiguracaoArrecadacao() {
        this.id = -1;
        this.filial = new Filial();
        this.certificadoFaturementoBrutoAnual = false;
    }

    public ConfiguracaoArrecadacao(Integer id, Filial filial, Boolean certificadoFaturementoBrutoAnual) {
        this.id = id;
        this.filial = filial;
        this.certificadoFaturementoBrutoAnual = certificadoFaturementoBrutoAnual;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getCertificadoFaturementoBrutoAnual() {
        return certificadoFaturementoBrutoAnual;
    }

    public void setCertificadoFaturementoBrutoAnual(Boolean certificadoFaturementoBrutoAnual) {
        this.certificadoFaturementoBrutoAnual = certificadoFaturementoBrutoAnual;
    }

}
