package startgame.Objects;

import startgame.Position;
import startgame.RNG.Distribuicoes;

public abstract class Enemie extends Character {

    private float velocidade;
    private float tempoParaMudarDirecao = 0f;
    private float tempoDecorrido = 0f;
    private int direcaoAtual = 0; // 0=Parado, 1=Esq, 2=Dir, 3=Cima, 4=Baixo;

    public Enemie(Position position, int health, int atkD, float velocidade) {
     super(position, health, atkD);
     this.velocidade = velocidade;
    }

    public float getVelocidade() {return velocidade;}

    public void setVelocidade(float velocidade) {
        this.velocidade = velocidade;
    }

    public void mudarDirecao() {
        // 1. V.A. EXPONENCIAL: Quanto tempo vou andar nesta direção?
        tempoParaMudarDirecao = Distribuicoes.gerarExponencial(0.8);

        // 2. V.A. UNIFORME: Para onde vou?
        direcaoAtual = (int) Distribuicoes.gerarUniforme(0, 5);

        tempoDecorrido = 0f;
    }

    public int getDirecaoAtual() {
        return direcaoAtual;
    }

    public float getTempoDecorrido() {
        return tempoDecorrido;
    }

    public float getTempoParaMudarDirecao() {
        return tempoParaMudarDirecao;
    }

    public void setDirecaoAtual(int direcaoAtual) {
        this.direcaoAtual = direcaoAtual;
    }

    public void setTempoDecorrido(float tempoDecorrido) {
        this.tempoDecorrido = tempoDecorrido;
    }

    public void addTempoDecorrido(float tempoDecorrido) {
        this.tempoDecorrido += tempoDecorrido;
    }

    public void setTempoParaMudarDirecao(float tempoParaMudarDirecao) {
        this.tempoParaMudarDirecao = tempoParaMudarDirecao;
    }



    public abstract void move();
}
