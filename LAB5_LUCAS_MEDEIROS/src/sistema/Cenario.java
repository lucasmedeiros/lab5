package sistema;

import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * Representação de um cenário disponível para aposta no sistema.
 * 
 * @author Lucas de Medeiros Nunes Fernandes
 */
public class Cenario {
    private final int numeracao;
    private String descricao;
    private String status;
    private int caixaCenario;
    private HashSet<Aposta> apostas;
    private HashSet<ApostaSegura> apostasSeguras;
    
    private Validacoes val = new Validacoes();
    
    /**
     * Construtor de Cenario.
     * 
     * @param numeracao numeracao do cenário.
     * @param descricao descricao do cenário.
     */
    public Cenario(int numeracao, String descricao) {
        val.validaString(descricao, "Erro no cadastro de cenario: "
                + "Descricao nao pode ser vazia");
        
        this.descricao = descricao;
        this.numeracao = numeracao;
        this.status = "Nao finalizado";
        this.apostas = new HashSet<>();
        this.caixaCenario = 0;
    }

    /**
     * Método acessório da descrição de um cenário.
     * 
     * @return descrição do cenário.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Método acessório da numeração de um cenário.
     * 
     * @return numeração do cenário.
     */
    public int getNumeracao() {
        return numeracao;
    }

    /**
     * Método acessório do status de um cenário.
     * 
     * @return status atual do cenário (finalizado ou não, e se ocorreu ou não).
     */
    public String getStatus() {
        return status;
    }

    /**
     * Método acessório do caixa final de um cenário.
     * 
     * @return total a ser adicionado ao caixa do sistema.
     */
    public int getCaixa() {
        return caixaCenario;
    }
    
    /**
     * Método para adicionar uma aposta no conjunto de apostas de um cenário.
     * 
     * @param apostador nome do apostador.
     * @param aposta previsão de aposta.
     * @param valor valor em centavos da aposta.
     * @return <tt>true</tt> se foi corretamente inserido.
     */
    public boolean criaAposta(String apostador, String aposta, int valor) {
        Aposta a = new Aposta(apostador, aposta, valor);
        
        return apostas.add(a);
    }
    
    /**
     * Método para definir o ID de uma aposta segura.
     * 
     * @return numero correspondente ao ID da aposta assegurada.
     */
    private int idSegura() {
        return apostasSeguras.size() + 1;
    }
    
    /**
     * Método para adicionar uma aposta assegurada por valor no conjunto de
     * apostas de um cenário.
     * 
     * @param apostador nome do apostador.
     * @param aposta previsão de aposta.
     * @param valor valor em centavos da aposta.
     * @param valorSeguro valor a ser assegurado.
     * @return <tt>true</tt> se foi corretamente inserido.
     */
    public boolean criaAposta(String apostador, String aposta, int valor,
            int valorSeguro) {
        TipoValor tipoValor = new TipoValor(valorSeguro);
        ApostaSegura a = new ApostaSegura(apostador, aposta, valor, idSegura(),
                tipoValor);
        
        return apostasSeguras.add(a);
    }
    
    /**
     * Método para adicionar uma aposta assegurada por taxa no conjunto de
     * apostas de um cenário.
     * 
     * @param apostador nome do apostador.
     * @param aposta previsão de aposta.
     * @param valor valor em centavos da aposta.
     * @param taxaSeguro taxa a ser assegurada.
     * @return <tt>true</tt> se foi corretamente inserido.
     */
    public boolean criaAposta(String apostador, String aposta, int valor,
            double taxaSeguro) {
        TipoTaxa tipoTaxa = new TipoTaxa(taxaSeguro);
        ApostaSegura a = new ApostaSegura(apostador, aposta, valor, idSegura(),
                tipoTaxa);
        
        return apostasSeguras.add(a);
    }
    
    /**
     * Método para alterar o tipo de aposta assegurada por taxa para valor.
     * 
     * @param apostaAssegurada id da aposta assegurada.
     * @param valor valor a ser passado.
     * @return id da aposta.
     */
    public int alterarSeguroValor(int apostaAssegurada, int valor){
        val.validaNumeroMenorIgualZero(apostaAssegurada, 
                "Erro na alteraçao de seguro valor: Aposta invalida");
        
        for (ApostaSegura a: apostasSeguras) {
            if (a.getId() == apostaAssegurada) {
                TipoValor tipoValor = new TipoValor(valor);
                a.setSeguro(tipoValor);
                
                return apostaAssegurada;
            }
        }
        
        throw new NoSuchElementException("Erro na alteraçao de seguro valor:"
                + " Aposta nao existe");
    }
    
    /**
     * Método para mostrar, em centavos, quanto dinheiro foi apostado em um
     * cenário.
     * 
     * @return valor monetário, em centavos, total apostado em um cenário.
     */
    public int valorTotalApostas() {
        int soma = 0;
        
        for (Aposta a: apostas) {
            soma += a.getCentavosAposta();
        }
        
        for (ApostaSegura as: apostasSeguras) {
            soma += as.getCentavosAposta();
        }
        
        return soma;
    }
    
    /**
     * Método para exibir todas as apostas feitas em um cenário.
     * 
     * @return a representação em string das apostas feitas em um cenário.
     */
    public String getTodasApostas() {
        String strApostas = "Apostas: " + System.lineSeparator();
        
        for (Aposta a: apostas) {
            strApostas += a.toString() + System.lineSeparator();
        }
        
        for (ApostaSegura as: apostasSeguras) {
            strApostas += as.toString() + System.lineSeparator();
        }
        
        return strApostas;
    }
    
    /**
     * Método para retornar a quantidade de apostas feitas em um cenário.
     * 
     * @return tamanho do conjunto de apostas de um cenário.
     */
    public int totalApostas() {
        return apostas.size() + apostasSeguras.size();
    }
    
    /**
     * Método para finalizar um cenário.
     * 
     * @param ocorreu booleano que representa se o evento representado pelo
     * cenário se concretizou na vida real ou não.
     */
    public void finalizaCenario(boolean ocorreu) {
        if (ocorreu)
            status = "Finalizado (ocorreu)";
        else
            status = "Finalizado (n ocorreu)";
        
        defineGanhadores(ocorreu);
    }
    
    /**
     * Método para definir as apostas vencedoras de um cenário.
     * 
     * @param ocorreu booleano que representa se o evento representado pelo
     * cenário se concretizou na vida real ou não.
     */
    private void defineGanhadores(boolean ocorreu) {
        for (Aposta a: apostas) {
            if (a.getPrevisao().trim().toUpperCase().equals("VAI ACONTECER"))
                a.setGanhou(ocorreu);
            else
                a.setGanhou(!ocorreu);
        }
    }
    
    /**
     * Retorna o quanto foi apostado pelos apostadores que perderam.
     * 
     * @return o valor, em centavos, apostado pelos apostadores perdedores.
     */
    public int valorApostadoPerdedores() {
        int soma = 0;
        for (Aposta a: apostas) {
            if (!(a.ganhou()))
                soma += a.getCentavosAposta();
        }
        return soma;
    }
    
    /**
     * Define o valor de caixa de um cenário, de acordo com os apostadores que
     * perderam.
     * 
     * @param taxa taxa do sistema.
     */
    public void definirValorCaixa(double taxa) {
        caixaCenario = (int) Math.floor(valorApostadoPerdedores() * taxa);
    }
    
    /**
     * Método para definir o valor a ser repassado para os vencedores.
     * 
     * @return valor em centavos para as apostas vencedoras.
     */
    public int totalRateio() {
        return (valorApostadoPerdedores() - caixaCenario);
    }

    /**
     * Sobrescrita do método toString().
     * 
     * @return representação em string de um cenário (com numeração, descrição
     * e status do cenário).
     */
    @Override
    public String toString() {
        return numeracao + " - " + descricao + " - " + status;
    }

    /**
     * Sobrescrita do método hashCode().
     * 
     * @return hashCode da numeração.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + numeracao;
        return hash;
    }

    /**
     * Sobrescrita do método equals() de cenário.
     * 
     * @param obj objeto a ser comparado com essa instância.
     * @return <tt>true</tt> se os objetos forem iguais.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cenario otherCenario = (Cenario) obj;
        return numeracao == otherCenario.numeracao;
    }    
}
